# PicturesAutoNamer

A Java frontend using llamacpp and local LLaVA LLM to **rename pictures according to their content**.

![](demo.mp4)



## FEATURES
* Show a window to choose a folder
* Resize each picture (using [thumbnailator](https://github.com/coobird/thumbnailator) lib)
* Connect to a local [llamacpp](https://github.com/ggerganov/llama.cpp) server running [LLaVA](https://llava.hliu.cc/)
* Send each picture to the server
* Rename the file after having cleaned up LLaVA's answer and accounted for duplicate

## INSTALLATION
* Download the latest package [on the release page](https://gitlab.com/marclv/picturesautonamer/-/releases).
* Download [mmproj-model-f16.gguf](https://huggingface.co/PsiPi/liuhaotian_llava-v1.5-13b-GGUF/blob/main/mmproj-model-f16.gguf) and rename it to llava-13B-mmproj-model-f16.gguf
* Download [ggml-model-q5_k.gguf] and rename it to ggml-model-q5_k.gguf
* Unzip the package.
* Put the 2 renamed models in llama/models
* Launch PicturesAutoNamer.bat
* Select a folder, press "Analyse and covert"

## TO DO
* Handle the case where the name chosen by LLaVA collide with an already existing name
* More images formats
* "About" popup