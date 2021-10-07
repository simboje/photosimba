@echo off

REM Colour tags for build output
REM         START   END
REM RED     [31m   [0m
REM GREEN   [32m   [0m

REM Check if we can find mvn (maven)
CALL mvn -version >nul 2>&1 && (
    echo Find maven:[32m OK [0m
) || (
    echo Find maven:[31m ERROR [0m
    REM Repeat command to get error message in terminal
    mvn -version
    goto :error
)

REM Build jar with maven
CALL mvn clean compile assembly:single >nul 2>&1 && (
    echo Build jar with maven:[32m OK [0m
) || (
    echo Build jar with maven:[31m ERROR [0m
    REM Repeat command to get error message in terminal
    mvn clean compile assembly:single
    goto :error
)

REM Run junit tests with maven
CALL mvn test >nul 2>&1 && (
    echo Run junit tests:[32m OK [0m
) || (
    echo Run junit tests:[31m ERROR [0m
    REM Repeat command to get error message in terminal
    mvn test
    goto :error
)

REM Clean build directory
if exist build (
CALL rmdir /Q /S build >nul 2>&1 && (
    echo Clean build/ dir:[32m OK [0m
) || (
    echo Clean build/ dir:[31m ERROR [0m
    rmdir /Q /S build
    goto :error
)
)

REM Built-in JRE starting from version 1.2 is created using OpenJDK17 and is custom tailored for this application, other versions can work but are not tested.
REM In order to have working jlink please install OpenJDK17 (or some other version that has it) and setup environment variables

REM Check if we can find jlink
CALL jlink -h >nul 2>&1 && (
    echo Find jlink:[32m OK [0m
) || (
    echo Find jlink:[31m ERROR [0m
    jlink -h
    goto :error
)

REM List of dependencies is acquired using "jdeps --list-deps slike-1.X.jar"
REM Please read https://medium.com/azulsystems/using-jlink-to-build-java-runtimes-for-non-modular-applications-9568c5e70ef4
REM Invoking jlink to create custom jre for this program

CALL jlink --no-header-files --no-man-pages --compress=2 --strip-debug --add-modules java.base,java.desktop,java.logging --output build\jre-17 >nul 2>&1 && (
    echo jlink create JRE:[32m OK [0m
) || (
    echo jlink create JRE:[31m ERROR [0m
    jlink --no-header-files --no-man-pages --compress=2 --strip-debug --add-modules java.base,java.desktop,java.logging --output build\jre-17
    goto :error
)
REM http://launch4j.sourceforge.net/docs.html
REM For this step you need to have launch4j directory in the project root (same location as this script), for example directory "slike" should contain:
REM src/, target/, launch4j/, package_jre_and_exe.bat...
REM launchj4 needs working Java on system in order to run

REM START /WAIT launch4j\launch4j.exe ..\launch4j_config_jre-17.xml
CALL launch4j\launch4j.exe ..\launch4j_config_jre-17.xml >nul 2>&1 && (
    echo launch4j create exe:[32m OK [0m
) || (
    echo launch4j create exe:[31m ERROR [0m
    launch4j\launch4j.exe ..\launch4j_config_jre-17.xml
    goto :error
)

CALL cp program_icon.png build\ >nul 2>&1 && (
    echo cp program_icon.png:[32m OK [0m
) || (
    echo cp program_icon.png:[31m ERROR [0m
    cp program_icon.png build\
    goto :error
)

CALL cp target/slike-*.jar build\ >nul 2>&1 && (
    echo cp target/slike-*.jar build/:[32m OK [0m
) || (
    echo cp target/slike-*.jar build/:[31m ERROR [0m
    cp target/slike-*.jar build/
    goto :error
)

REM For whatever reason final check if slike.exe exists will fail without at least a litle bit of waiting
TIMEOUT /T 3 /NOBREAK

if exist ".\build\slike.exe" (
	echo [32m SUCCESS [0m - found build\slike.exe, seems that everything worked fine.
    exit /b 0
) else (
    echo [31m ERROR [0m - file build\slike.exe not found! Please check this log to find error details.
    exit /b 1
)

:error
echo [31mFailed with error:[0m %errorlevel%.
exit /b %errorlevel%
