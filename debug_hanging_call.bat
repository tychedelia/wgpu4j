@echo off
echo Starting Java process in background...

rem Start the Java process in background
start /B "Java-WGPU" "C:\Program Files\Common Files\Oracle\Java\javapath\java.exe" -jar gradle/wrapper/gradle-wrapper.jar test --tests MinimalDeviceTest

echo Waiting 10 seconds for hang to occur...
timeout /t 10 /nobreak > nul

echo Getting thread dump of hanging process...
for /f "tokens=2" %%i in ('tasklist /FI "IMAGENAME eq java.exe" /FO CSV ^| find "java.exe"') do (
    set PID=%%i
    set PID=!PID:"=!
    goto :found
)
:found

if defined PID (
    echo Found Java PID: %PID%
    echo Getting thread dump...
    "C:\Program Files\Common Files\Oracle\Java\javapath\jstack.exe" %PID% > thread_dump.txt
    echo Thread dump saved to thread_dump.txt
    
    echo Killing process...
    taskkill /PID %PID% /F
) else (
    echo No Java process found
)

pause