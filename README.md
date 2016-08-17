# GalleryManager

Standalone Java application for managing your personal pictures. Create and manage picture galleries and synchronize them with your NAS.

## The Concept

You might know that problem: You and your family just returned from Holiday, everyone took some snapshots with his smartphone or photo camera and now saves them on his computer. How can you be sure everyone gets the best images? This is where you need GalleryManager.

You can easily create galleries and push them to your NAS. You can as well download an existing gallery and add images to it. This way, everyone can keep his image collections up to date.

## Core Features
Already implemented:
* Basic and easy-to-use user interface, written in JavaFX
* Can run on any Desktop OS that supports Java 8 (Windows, Linux, Mac OS)
* Images are stored in your computer's images directory - you can still manage them with your file browser if youn want to
* The remote server can be any directory in your file system, you just have to mount a network drive
* Create galleries and add pictures, create collections
* Rename or delete galleries or collections
* Download galleries from the server and synchronize them on demand
* Export existing galleries to the server

What could be added in some hopefully not too distant future:
* Basic image manipulation tools (brightness, colors, contrast, crop, rotate, ...)
* Synchronizeation with popular cloud services (eg. Google Drive, Dropbox, ...)

## How to build
To run GalleryManager, you need to install Java 8 (which should support JavaFX). To build the project, use ant and the build fxbuild.xml build script.

```
ant -f fxbuild.xml
```

Of course, you can also build GalleryManager directly from your IDE, I use NetBeans.

## How to run
When you start GalleryManager for the first time, a dialogue appears asking you where your images are saved on your computer and with which directory you want to synchronize. After saving these settings, simply restart the program.
