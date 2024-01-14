# PicturesAutoNamer

A Java frontend using llamacpp and local LLaVA LLM to **rename pictures according to their content**.

![](demo.mp4)



## FEATURES
* Provide a JavaFX Graphical User Interface to select a folder
* Connecte to a local llamacpp server running LLaVA
* Resize internally each picture (using [thumbnailator](https://github.com/coobird/thumbnailator) lib)
* Send each picture to the server
* Rename the file after having cleaned up LLaVA's answer and accounted for duplicate

## INSTALLATION
* To write