# slike
## Short description
A simple image viewer intended to be as simple and fast as possible.
Intention is to create something to be used instead of default Photos application on Windows 10 as the [problem with arrow keys](https://answers.microsoft.com/en-us/windows/forum/all/windows-10-photos-app-why-cant-i-use-my-arrow-keys/790e786d-b701-48ef-93c5-23d204b4fba4) could not be solved.

## Feature list
* Portable, no installation required
* View, zoom and rotate images
* Background thread for image loading

## How to build and use
A standard Maven project, you can build it from cmd using maven or use Eclipse and launch it from there. Currently only buildable as jar but [launch4j](http://launch4j.sourceforge.net/docs.html) config will be added later in order to get .exe for Windows users. If you have Maven installed (and on PATH) you can build the project from cmd using `mvn clean verify`.

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

## Credits
Image drag and zoom code taken from [this example](https://stackoverflow.com/questions/58021358/java-how-to-do-image-manipulation-pan-zoom-flip-and-rotate).
Copy image to clipboard [example](https://stackoverflow.com/questions/4552045/copy-bufferedimage-to-clipboard) , copy file to clipboard [example](https://stackoverflow.com/questions/31798646/can-java-system-clipboard-copy-a-file)
