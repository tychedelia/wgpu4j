plugins {
    java
    application
}

description = "WGPU4J Examples and Demos"

repositories {
    mavenCentral()
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(24)
    }
}

dependencies {

    implementation(project(":"))


    val lwjglVersion = "3.3.6"
    implementation(platform("org.lwjgl:lwjgl-bom:$lwjglVersion"))
    implementation("org.lwjgl:lwjgl")
    implementation("org.lwjgl:lwjgl-glfw")
    runtimeOnly("org.lwjgl:lwjgl::natives-windows")
    runtimeOnly("org.lwjgl:lwjgl-glfw::natives-windows")
    runtimeOnly("org.lwjgl:lwjgl::natives-macos")
    runtimeOnly("org.lwjgl:lwjgl-glfw::natives-macos")
    runtimeOnly("org.lwjgl:lwjgl::natives-macos-arm64")
    runtimeOnly("org.lwjgl:lwjgl-glfw::natives-macos-arm64")
    runtimeOnly("org.lwjgl:lwjgl::natives-linux")
    runtimeOnly("org.lwjgl:lwjgl-glfw::natives-linux")
    runtimeOnly("org.lwjgl:lwjgl::natives-linux-arm64")
    runtimeOnly("org.lwjgl:lwjgl-glfw::natives-linux-arm64")


    implementation("org.slf4j:slf4j-simple:2.0.9")
}


tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<Test> {
    jvmArgs("--enable-native-access=ALL-UNNAMED")
    useJUnitPlatform()
}


application {
    mainClass = "org.wgpu4j.examples.TriangleExample"

    val jvmArgs = mutableListOf("--enable-native-access=ALL-UNNAMED")


    if (System.getProperty("os.name").lowercase().contains("mac")) {
        jvmArgs += "-XstartOnFirstThread"
    }

    applicationDefaultJvmArgs = jvmArgs
}