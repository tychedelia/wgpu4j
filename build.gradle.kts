import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.net.URL
import java.util.*

plugins {
    `java-library`
    `maven-publish`
    signing
}

group = "org.wgpu4j"
version = "0.1.0-SNAPSHOT"
description = "Java bindings for wgpu-native using Project Panama"

repositories {
    mavenCentral()
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(24)
    }
    withSourcesJar()
    withJavadocJar()
}

dependencies {
    api("org.slf4j:slf4j-api:2.0.9")

    testImplementation("org.junit.jupiter:junit-jupiter:5.10.1")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testRuntimeOnly("org.slf4j:slf4j-simple:2.0.9")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<Test> {
    jvmArgs("--enable-native-access=ALL-UNNAMED")
    useJUnitPlatform()
}

tasks.withType<Javadoc> {
    if (JavaVersion.current().isJava9Compatible) {
        options {
            this as StandardJavadocDocletOptions
            addStringOption("-source", "24")
            addBooleanOption("html5", true)
        }
    }
}

sourceSets {
    main {
        java {
            setSrcDirs(listOf("src/main/java", "src/generated/java"))
        }
    }
}

val wgpuVersion = "25.0.2.1"
val buildType = "release"

val supportedPlatforms = listOf(
    "macos-aarch64",
    "macos-x86_64",
    "linux-aarch64",
    "linux-x86_64",
    "windows-aarch64-msvc",
    "windows-x86_64-msvc"
)

tasks.register("downloadAllWgpuNatives") {
    description = "Downloads wgpu-native for all supported platforms"

    inputs.property("wgpuVersion", wgpuVersion)
    inputs.property("supportedPlatforms", supportedPlatforms)
    outputs.dir("native")

    doLast {
        val nativeDir = file("native")
        nativeDir.mkdirs()

        // Always download at least one platform for header files (preferring current platform)
        val currentPlatform = getCurrentPlatform()
        val platformsToDownload = if (supportedPlatforms.contains(currentPlatform)) {
            listOf(currentPlatform) + (supportedPlatforms - currentPlatform)
        } else {
            supportedPlatforms
        }

        // Download and extract all platforms
        platformsToDownload.forEach { platform ->
            val fileName = "wgpu-$platform-$buildType.zip"
            val downloadUrl = "https://github.com/gfx-rs/wgpu-native/releases/download/v$wgpuVersion/$fileName"
            val zipFile = file("native/$fileName")
            val platformDir = file("native/$platform")

            if (!zipFile.exists()) {
                println("Downloading wgpu-native for $platform from $downloadUrl")
                try {
                    zipFile.outputStream().use { output ->
                        URL(downloadUrl).openStream().use { input: InputStream ->
                            input.copyTo(output)
                        }
                    }
                } catch (e: Exception) {
                    println("Warning: Failed to download $platform: ${e.message}")
                    return@forEach // Skip this platform
                }
            }

            if (zipFile.exists() && !platformDir.exists()) {
                println("Extracting wgpu-native for $platform")
                copy {
                    from(zipTree(zipFile))
                    into(platformDir)
                }
            }
        }

        // Copy headers from first available platform
        if (!file("native/include").exists()) {
            var headersCopied = false
            for (platform in platformsToDownload) {
                val platformDir = file("native/$platform")
                val includeDir = file("$platformDir/include")
                if (includeDir.exists()) {
                    println("Copying headers from $platform")
                    copy {
                        from(includeDir)
                        into("native/include")
                    }
                    headersCopied = true
                    break
                }
            }

            if (!headersCopied) {
                throw GradleException("Failed to find header files from any platform")
            }
        }
    }
}

tasks.register("generateBindings") {
    dependsOn("downloadAllWgpuNatives")
    description = "Generates Java bindings using jextract"

    inputs.file("native/include/webgpu/webgpu.h")
    inputs.file("native/include/webgpu/wgpu.h")
    outputs.dir("src/generated/java")

    doLast {
        val jextractPath = findJextract()
            ?: run {
                val jextractExe = if (System.getProperty("os.name").lowercase().contains("windows")) {
                    "jextract.bat"
                } else {
                    "jextract"
                }
                val path = "${gradle.gradleUserHomeDir}/jextract-22/bin/$jextractExe"
                if (!file(path).exists()) {
                    throw GradleException("jextract not found at $path. Please run './gradlew downloadJextract' first.")
                }
                path
            }

        val outputDir = file("src/generated/java")
        outputDir.mkdirs()

        // Generate bindings for standard WebGPU (webgpu.h)
        val webgpuHeaderFile = file("native/include/webgpu/webgpu.h")
        if (webgpuHeaderFile.exists()) {
            println("Generating Java bindings from $webgpuHeaderFile")
            project.exec {
                commandLine(
                    jextractPath,
                    "--output", outputDir,
                    "--target-package", "org.wgpu4j.bindings",
                    "--include-dir", file("native/include").absolutePath,
                    webgpuHeaderFile.absolutePath
                )
            }
        } else {
            throw GradleException("Header file not found: $webgpuHeaderFile")
        }

        // Generate bindings for wgpu-native extensions (wgpu.h)
        val wgpuHeaderFile = file("native/include/webgpu/wgpu.h")
        if (wgpuHeaderFile.exists()) {
            println("Generating Java bindings from $wgpuHeaderFile")
            project.exec {
                commandLine(
                    jextractPath,
                    "--output", outputDir,
                    "--target-package", "org.wgpu4j.bindings",
                    "--include-dir", file("native/include").absolutePath,
                    wgpuHeaderFile.absolutePath
                )
            }
        } else {
            throw GradleException("Header file not found: $wgpuHeaderFile")
        }
    }
}

tasks.register("copyAllNativeLibraries") {
    dependsOn("downloadAllWgpuNatives")
    description = "Copies all platform native libraries to JAR resources"

    inputs.dir("native")
    outputs.dir("src/main/resources/native")

    doLast {
        val resourcesNativeDir = file("src/main/resources/native")
        resourcesNativeDir.mkdirs()

        supportedPlatforms.forEach { platform ->
            val platformDir = file("native/$platform")
            val libDir = file("$platformDir/lib")
            val targetDir = file("$resourcesNativeDir/$platform")

            if (libDir.exists()) {
                targetDir.mkdirs()
                copy {
                    from(libDir)
                    into(targetDir)
                    include("*.dylib", "*.so", "*.dll")
                }
                println("Copied $platform libraries to $targetDir")
            } else {
                println("Warning: No libraries found for $platform at $libDir")
            }
        }
    }
}

fun getCurrentPlatform(): String {
    val os = System.getProperty("os.name").lowercase()
    val arch = System.getProperty("os.arch").lowercase()

    return when {
        os.contains("mac") -> if (arch.contains("aarch64") || arch.contains("arm")) "macos-aarch64" else "macos-x86_64"
        os.contains("linux") -> if (arch.contains("aarch64") || arch.contains("arm")) "linux-aarch64" else "linux-x86_64"
        os.contains("windows") -> if (arch.contains("aarch64") || arch.contains("arm")) "windows-aarch64-msvc" else "windows-x86_64-msvc"
        else -> throw GradleException("Unsupported OS/Architecture: $os/$arch")
    }
}

fun findJextract(): String? {
    val jextractHome = System.getenv("JEXTRACT_HOME")
    if (jextractHome != null) {
        val jextractPath = "$jextractHome/bin/jextract"
        if (file(jextractPath).exists()) {
            return jextractPath
        }
    }

    try {
        val output = ByteArrayOutputStream()
        val result = project.exec {
            commandLine("which", "jextract")
            standardOutput = output
            isIgnoreExitValue = true
        }
        if (result.exitValue == 0) {
            return output.toString().trim()
        }
    } catch (e: Exception) {
        // Ignore
    }

    return null
}

tasks.jar {
    manifest {
        attributes(
            "Implementation-Title" to project.name,
            "Implementation-Version" to project.version,
            "Implementation-Vendor" to "WGPU4J",
            "Built-By" to System.getProperty("user.name"),
            "Built-Date" to Date().toString(),
            "Built-JDK" to System.getProperty("java.version"),
            "Multi-Release" to "true"
        )
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])

            pom {
                name = project.name
                description = project.description
                url = "https://github.com/tychedelia/wgpu4j"

                licenses {
                    license {
                        name = "The Apache License, Version 2.0"
                        url = "http://www.apache.org/licenses/LICENSE-2.0.txt"
                    }
                }

                developers {
                    developer {
                        id = "tychedelia"
                        name = "tychedelia"
                        email = "tychedelia@users.noreply.github.com"
                    }
                }

                scm {
                    connection = "scm:git:git://github.com/tychedelia/wgpu4j.git"
                    developerConnection = "scm:git:ssh://github.com:tychedelia/wgpu4j.git"
                    url = "https://github.com/tychedelia/wgpu4j/tree/main"
                }
            }
        }
    }

    repositories {
        maven {
            name = "sonatype"
            val releasesRepoUrl = "https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/"
            val snapshotsRepoUrl = "https://s01.oss.sonatype.org/content/repositories/snapshots/"
            url = uri(if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl)

            credentials {
                username = project.findProperty("ossrhUsername") as String? ?: System.getenv("OSSRH_USERNAME")
                password = project.findProperty("ossrhPassword") as String? ?: System.getenv("OSSRH_PASSWORD")
            }
        }
    }
}

signing {
    setRequired { gradle.taskGraph.hasTask("publish") }
    sign(publishing.publications["maven"])
}

tasks.processResources {
    dependsOn("copyAllNativeLibraries")
}

tasks.compileJava {
    dependsOn("generateBindings")
}

tasks.named("sourcesJar") {
    dependsOn("generateBindings", "copyAllNativeLibraries")
}

tasks.publishToMavenLocal {
    dependsOn("jar")
}

// Legacy task aliases for backwards compatibility
tasks.register("downloadWgpuNative") {
    dependsOn("downloadAllWgpuNatives")
    description = "Downloads WGPU native libraries"
}

tasks.register("copyNativeLibraries") {
    dependsOn("copyAllNativeLibraries")
    description = "Copies native libraries to resources"
}

tasks.register("downloadJextract") {
    description = "Downloads and sets up jextract in Gradle user home"

    val jextractVersion = "22-jextract+6-47"
    val jextractDir = file("${gradle.gradleUserHomeDir}/jextract-22")

    inputs.property("jextractVersion", jextractVersion)
    outputs.dir(jextractDir)

    doLast {
        println("Starting jextract download task")
        println("Target directory: $jextractDir")
        println("Directory exists: ${jextractDir.exists()}")

        val platform = getJextractPlatform()
        val fileName = "openjdk-$jextractVersion" + "_${platform}_bin.tar.gz"
        val downloadUrl = "https://download.java.net/java/early_access/jextract/22/6/$fileName"
        val tarFile = file("${gradle.gradleUserHomeDir}/$fileName")

        if (!tarFile.exists()) {
            println("Downloading jextract from $downloadUrl")
            try {
                tarFile.outputStream().use { output ->
                    URL(downloadUrl).openStream().use { input: InputStream ->
                        input.copyTo(output)
                    }
                }
            } catch (e: Exception) {
                throw GradleException("Failed to download jextract: ${e.message}")
            }
        }

        // Check if jextract binary actually exists, not just the directory
        val jextractExe =
            if (System.getProperty("os.name").lowercase().contains("windows")) "jextract.bat" else "jextract"
        val jextractBinary = file("$jextractDir/bin/$jextractExe")

        if (!jextractBinary.exists()) {
            println("Extracting jextract to ${gradle.gradleUserHomeDir}")
            copy {
                from(tarTree(tarFile))
                into(gradle.gradleUserHomeDir)
            }

            // The extracted directory is named jextract-22, which matches our target
            val extractedDir = file("${gradle.gradleUserHomeDir}/jextract-22")
            if (!extractedDir.exists()) {
                // List what was actually extracted to debug
                val gradleHome = file(gradle.gradleUserHomeDir)
                println("Extraction completed. Contents of gradle home:")
                gradleHome.listFiles()?.forEach { f ->
                    if (f.name.contains("jdk") || f.name.contains("jextract")) {
                        println("  Found: ${f.name} (${if (f.isDirectory) "directory" else "file"})")
                    }
                }
                throw GradleException("Expected extracted directory $extractedDir not found")
            } else {
                println("Successfully extracted jextract to $extractedDir")
            }
        }

        // Make jextract executable on Unix systems
        if (!System.getProperty("os.name").lowercase().contains("windows")) {
            val jextractBin = file("$jextractDir/bin/jextract")
            if (jextractBin.exists()) {
                project.exec {
                    commandLine("chmod", "+x", jextractBin.absolutePath)
                }
            }
        }

        println("jextract installed to: $jextractDir")
        println("You can also set JEXTRACT_HOME=$jextractDir to use it globally")
    }
}

fun getJextractPlatform(): String {
    val os = System.getProperty("os.name").lowercase()
    val arch = System.getProperty("os.arch").lowercase()

    return when {
        os.contains("linux") -> if (arch.contains("aarch64") || arch.contains("arm")) "linux-aarch64" else "linux-x64"
        os.contains("mac") -> if (arch.contains("aarch64") || arch.contains("arm")) "macos-aarch64" else "macos-x64"
        os.contains("windows") -> "windows-x64"
        else -> throw GradleException("Unsupported platform for jextract: $os/$arch")
    }
}