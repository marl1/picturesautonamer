@echo off
cd /d "%~dp0"
IF EXIST "models\ggml-model-q5_k.gguf" (
	IF EXIST "models\llava-13B-mmproj-model-f16.gguf" (
	"%~dp0"server.exe -m models\ggml-model-q5_k.gguf --mmproj models\llava-13B-mmproj-model-f16.gguf -c 2048 --port 8080
	) ELSE (
	  echo "ERROR: No llava-13B-mmproj-model-f16.gguf detected. SOLUTION: Put llava-13B-mmproj-model-f16.gguf in the %~dp0models folder"
	  pause
	)
) ELSE (
  echo "ERROR: No ggml-model-q5_k.gguf detected. SOLUTION: Put ggml-model-q5_k.gguf in the %~dp0models folder"
  pause
)