#!/bin/bash

cd "$(dirname "$0")"

# Set the model file names including the models directory
ggml_model="../models/ggml-model-q5_k.gguf"
mmproj_model="../models/mmproj-model-f16.gguf"

# Check if the ggml_model exists
if [ -f "${ggml_model}" ]; then
    # Check if the mmproj_model exists
    if [ -f "${mmproj_model}" ]; then
        ./server -m "${ggml_model}" --mmproj "${mmproj_model}" -c 2048 --port 8080
    else
        echo "ERROR: No ${mmproj_model} detected. SOLUTION: Put ${mmproj_model} in the current directory"
        read -p "Press any key to continue..."
    fi
else
    echo "ERROR: No ${ggml_model} detected. SOLUTION: Put ${ggml_model} in the current directory"
    read -p "Press any key to continue..."
fi
