@echo off
rem Copyright (C) 2007 The Android Open Source Project
rem
rem Licensed under the Apache License, Version 2.0 (the "License");
rem you may not use this file except in compliance with the License.
rem You may obtain a copy of the License at
rem
rem      http://www.apache.org/licenses/LICENSE-2.0
rem
rem Unless required by applicable law or agreed to in writing, software
rem distributed under the License is distributed on an "AS IS" BASIS,
rem WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
rem See the License for the specific language governing permissions and
rem limitations under the License.

rem Useful links:
rem Command-line reference:
rem   http://technet.microsoft.com/en-us/library/bb490890.aspx

rem don't modify the caller's environment
setlocal enableextensions

rem Set up prog to be the path of this script, including following symlinks,
rem and set up progdir to be the fully-qualified pathname of its directory.
set prog=%~f0

rem Grab current directory before we change it
set work_dir=%cd%

rem Change current directory and drive to where the script is, to avoid
rem issues with directories containing whitespaces.
cd /d %~dp0


rem Check we have a valid Java.exe in the path.
set java_exe=
call lib\find_java.bat
if not defined java_exe goto :EOF

set jar_path=lib\sdkmanager.jar;lib\swtmenubar.jar

rem Set SWT.Jar path based on current architecture (x86 or x86_64)
for /f "delims=" %%a in ('"%java_exe%" -jar lib\archquery.jar') do set swt_path=lib\%%a

:MkTempCopy
    rem Copy android.bat and its required libs to a temp dir.
    rem This avoids locking the tool dir in case the user is trying to update it.

    set tmp_dir=%TEMP%\temp-android-tool
    xcopy "%swt_path%" "%tmp_dir%\%swt_path%" /I /E /C /G /R /Y /Q > nul
    copy /B /D /Y lib\common.jar         "%tmp_dir%\lib\"          > nul
    copy /B /D /Y lib\commons-codec*     "%tmp_dir%\lib\"          > nul
    copy /B /D /Y lib\commons-compress*  "%tmp_dir%\lib\"          > nul
    copy /B /D /Y lib\commons-logging*   "%tmp_dir%\lib\"          > nul
    copy /B /D /Y lib\dvlib.jar          "%tmp_dir%\lib\"          > nul
    copy /B /D /Y lib\gson*              "%tmp_dir%\lib\"          > nul
    copy /B /D /Y lib\guava*             "%tmp_dir%\lib\"          > nul
    copy /B /D /Y lib\httpclient*        "%tmp_dir%\lib\"          > nul
    copy /B /D /Y lib\httpcore*          "%tmp_dir%\lib\"          > nul
    copy /B /D /Y lib\httpmime*          "%tmp_dir%\lib\"          > nul
    copy /B /D /Y lib\layoutlib-api.jar  "%tmp_dir%\lib\"          > nul
    copy /B /D /Y lib\org-eclipse-*      "%tmp_dir%\lib\"          > nul
    copy /B /D /Y lib\sdk*               "%tmp_dir%\lib\"          > nul
    copy /B /D /Y lib\swtmenubar.jar     "%tmp_dir%\lib\"          > nul

    rem jar_path and swt_path are relative to PWD so we don't need to adjust them, just change dirs.
    set tools_dir=%cd%
    cd /d "%tmp_dir%"

:EndTempCopy

rem The global ANDROID_SWT always override the SWT.Jar path
if defined ANDROID_SWT set swt_path=%ANDROID_SWT%

if exist "%swt_path%" goto SetPath
    echo ERROR: SWT folder '%swt_path%' does not exist.
    echo Please set ANDROID_SWT to point to the folder containing swt.jar for your platform.
    goto :EOF

:SetPath
rem Finally exec the java program and end here.
REM set REMOTE_DEBUG=-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=8000
call "%java_exe% %REMOTE_DEBUG%" "-Dcom.android.sdkmanager.toolsdir=%tools_dir%" "-Dcom.android.sdkmanager.workdir=%work_dir%" -classpath "%jar_path%;%swt_path%\swt.jar" com.android.sdkmanager.Main %*

rem EOF
