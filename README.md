# GalleryManager

Standalone Java application for managing your personal pictures. Create and manage picture galleries and synchronize them with your NAS.

## The Concept

You might know that problem: You and your family just returned from Holiday, everyone took some snapshots with his smartphone or photo camera and now saves them on his computer. How can you be sure everyone gets the best images? This is where you need GalleryManager.

You can easily create galleries and push them to your NAS or to another server. You can as well download an existing gallery and add images to it. This way, everyone can keep his image collections up to date.

## Core Features
Already implemented:
* Basic and easy-to-use user interface, written in JavaFX
* Can run on any Desktop OS that supports Java 8 (Windows, Linux, Mac OS)
* Images are stored in your computer's images directory - you can still manage them with your file browser if youn want to
* The remote server can be any directory in your file system, you just have to mount a network drive
* Create galleries and add pictures
* Download galleries from the server and synchronize on demand

On my TODO-list:
* Export existing galleries to the server
* Create directories for galleries and improve gallery management
* Tag images with categories, add images to favourites or hide them
* Create GUI for first the setup of GalleryManager

What could be added in some hopefully not too distant future:
* Basic image manipulation tools (brightness, colors, contrast, crop, rotate, ...)
* Synchronizeation with popular cloud services (eg. Google Drive, Dropbox, ...)

## How to build
To run GalleryManager, you need to install Java 8 (which should support JavaFX). To build the project, there are two ways:
 1. Build it with NetBeans (when i tried it, NetBeans missed to include some dependencies into the jar file).
 2. Use the fxbuild.xml script to build this project with ant. The default task compiles and creates a jar file.

## How to run
I hve not yet added a GUI that creates the basic configuration file that GalleryManager needs to run. You have to create a "config.json" file that contains references to the directory where you save your pictures in your computer and (optionally) the path to the server to synchronize gallieries.

ˋˋˋ
{
    "localGalleryLocation": "/path/to/images",
    "remoteGalleryLocation": "/path/to/server"
}
ˋˋˋ
If you want to use GalleryManager without a remote location or NAS, leave the value for "remoteGalleryLocation" blank (empty string).
I will add some examples for the configuration within the next days.