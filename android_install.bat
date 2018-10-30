@echo off
set android_path=C:\android_sdk\platform-tools
"%android_path%\adb.exe" install -r "%~1"
ECHO %ERRORLEVEL%
if /I %ERRORLEVEL% NEQ 0 pause

pause