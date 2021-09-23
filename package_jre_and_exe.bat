@echo off

echo Before running this script make sure to run maven build 'mvn clean compile assembly:single' and have target/slike-1.x.jar created!
echo Or in Eclipse create new 'Run Configurations - Maven Build' and in field 'Goals' put 'clean compile assembly:single'

REM Clean build directory
rmdir /Q /S build

echo Built-in JRE from version 1.2 is created using OpenJDK17 and is custom tailored for this application, other versions can work but are not tested. 
echo In order to have working jlink please install OpenJDK17 (or some other version that has it) and setup environment variables

echo Checking jlink location:
which jlink
echo "If the previous line is empty is means that JDK is not installed correctly and next command will fail."


echo List of dependencies is acquired using "jdeps --list-deps slike-1.2.jar"
echo Please read https://medium.com/azulsystems/using-jlink-to-build-java-runtimes-for-non-modular-applications-9568c5e70ef4
echo Invoking jlink to create custom jre for this program
jlink --no-header-files --no-man-pages --compress=2 --strip-debug --add-modules java.base,java.desktop,java.logging --output build\jre-17

REM http://launch4j.sourceforge.net/docs.html
echo For this step you need to have launch4j directory in the project root (same location as this script), for example directory "slike" should contain:
echo src/, target/, launch4j/, package_jre_and_exe.bat...
echo launchj4 needs working Java on system in order to run

START /WAIT launch4j\launch4j.exe ..\launch4j_config_jre-17.xml

cp program_icon.png build\

echo \n
REM For whatever reason final check if slike.exe exists will fail without at least a litle bit of waiting
TIMEOUT /T 3 /NOBREAK

if exist ".\build\slike.exe" (
	echo SUCCESS - found build\slike.exe, seems that everything worked fine.
) else (
    echo ERROR - file build\slike.exe not found! Please check this log to find error details.
)
