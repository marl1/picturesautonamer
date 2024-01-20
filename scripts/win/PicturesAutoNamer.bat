@echo off
set JLINK_VM_OPTIONS=
set DIR=%~dp0

start "" "%DIR%\bin\javaw" %JLINK_VM_OPTIONS% -m fr.pan/fr.pan.main.Main %* && exit 0