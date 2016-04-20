# Config File Examples

This folder contains some examples for GalleryManager config files. Below is a brief description of these configurations. To use one of these files, you have to rename it to "config.json".

If you want to use GalleryManager without a remote location or NAS, leave the value for "remoteGalleryLocation" blank (empty string).

## example1.json

A very basic configuration for testing GalleryManager. Just create two folders named "galleries" and "remote" in the same directory where the GalleryManager jar file and the config file is located. If you create galleries, they will be stored in the "galleries" folder and can be synchronized with the "remote" folder.
```
{
    "localGalleryLocation": "galleries",
    "remoteGalleryLocation": "remote"
}
```

## example2.json

Configuration example for Linux.

```
{
    "localGalleryLocation": "/home/username/Pictures",
    "remoteGalleryLocation": "/home/username/Documents/remoteServerMockup"
}
```

## example3.json

Configuration example for Windows.

```
{
    "localGalleryLocation": "C://Users/Username/Pictures",
    "remoteGalleryLocation": "Z://Pictures"
}
```
