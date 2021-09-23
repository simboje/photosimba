# slike
## Short description
A simple image viewer intended to be as simple and fast as possible.
Intention is to create something to be used instead of default Photos application on Windows 10 as the [problem with arrow keys](https://answers.microsoft.com/en-us/windows/forum/all/windows-10-photos-app-why-cant-i-use-my-arrow-keys/790e786d-b701-48ef-93c5-23d204b4fba4) could not be solved.

## Feature list
* Portable, no installation required
* View, zoom, rotate, delete images
* Copy image or file to clipboard
* Background thread for image loading<br>
Please see mouse and keyboard controls section for more information.

## Prebuilt binaries
Versions with bundled JRE (better)<br/>
[Version 1.2](https://1drv.ms/u/s!AhATAQSs_IENgpleIHFsemLA3WTRBQ?e=Vm0zy3)

Old versions using system JRE (here for reference, but have much higher memory usage)<br/>
[Version 1.1](https://1drv.ms/u/s!AhATAQSs_IENgpld0ZSnAXS7OlxzzQ?e=pKtqqR)<br/>
[Version 1.0](https://1drv.ms/u/s!AhATAQSs_IENgplcxLilqBBrOUnUAw?e=yciwup)

## How to open/modify or build jar
A standard Maven project, you can build it from cmd using maven or use Eclipse and launch it from there. If you have Maven installed (and on PATH) you can build the project from cmd using `mvn clean compile assembly:single`. Or in Eclipse create new 'Run Configurations - Maven Build' and in field 'Goals' put 'clean compile assembly:single'

## Packaging jar to exe with custom JRE
For this you will need [launch4j](http://launch4j.sourceforge.net/docs.html). Version 3.14 is tested so far.<br>
Basic steps:
1. Have a working OpenJDK17 on your system (other versions will work but this one is tested)
2. Download launchj4 and unpack it so that you have the following structure 'slike/launch4j/launch4j.exe'
3. Run 'package_jre_and_exe.bat' from cmd, it will:<br>
  3.1 Delete build/* dir if it exists (clean)<br>
  3.2 Check jlink location and invoke jlink and create build/jre-17 (custom jre that contains only modules required for this program)<br>
  3.3 Invoke launch4j with config file launch4j_config_jre-17.xml to create appropriate executable build/slike.exe.

Note: entire build/ directory should be packed as zip for a release and it should contain build/slike.exe and build/jre-17 directory.
Program will run even if build/jre-17 is removed and will use system JRE but this results in slower performance and higher memory usage.


## Mouse controls
* Left button drag - drag image
* Scroll wheel - zoom in and out
* Scroll click - reset zoom level and image position to default

## Keyboard Controls
* A or Left arrow - previous image
* D or Right Arrow - next image
* W or Up arrow - rotate image counter-clockwise (does not save changes to image file)
* S or Down arrow - rotate image clockwise (does not save changes to image file)
* Ctrl + C - copies image to clipboard (can be pasted to other image/chat programs)
* Ctrl + Shift + C - copies image file to clipboard (can be pasted in file explorer)
* Del - send file to Recycle Bin
* Q - open message and error log window

## Credits
Library for reading metadata about image orientation [metadata-extractor](https://github.com/drewnoakes/metadata-extractor).<br/>
Java Native Access (JNA) used for sending files to recycle bin [JNA GitHub page](https://github.com/java-native-access/jna)<br/>
Image drag and zoom code taken from [this example](https://stackoverflow.com/questions/58021358/java-how-to-do-image-manipulation-pan-zoom-flip-and-rotate).<br/>
Copy image to clipboard [example](https://stackoverflow.com/questions/4552045/copy-bufferedimage-to-clipboard) , copy file to clipboard [example](https://stackoverflow.com/questions/31798646/can-java-system-clipboard-copy-a-file)<br/>
