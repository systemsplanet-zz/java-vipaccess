@if defined _sdk_debug ( @echo on ) else ( @echo off )

@echo %~n0      commandLine         [%~f0]            [%~1]             [%~2]                       Description
rem             USAGE:              7zipbackup.cmd    [src-drive-path]  [dest-drive-path]           recursive backup src-drive-path to dest-drive-path-yyyy_mm_dd-hh_mm_ss_ms.7z
rem             EXAMPLE:            7zipbackup.cmd                                                  recursive backup current working path to c:\backup\path-yyyy_mm_dd-hh_mm_ss_ms.7z
rem             EXAMPLE:            7zipbackup.cmd    d:\                                           recursive backup d:\ to c:\backup\drive_d-yyyy_mm_dd-hh_mm_ss_ms.7z
rem             EXAMPLE:            7zipbackup.cmd    d:\dell                                       recursive backup d:\dell to c:\backup\dell-yyyy_mm_dd-hh_mm_ss_ms.7z
rem             EXAMPLE:            7zipbackup.cmd    d:\dell\Logs                                  recursive backup d:\dell\Logs to c:\backup\Logs-yyyy_mm_dd-hh_mm_ss_ms.7z
rem             EXAMPLE:            7zipbackup.cmd    d:\dell\Logs      e:\repo                     recursive backup d:\dell\Logs to e:\repo\Logs-yyyy_mm_dd-hh_mm_ss_ms.7z

@setlocal DisableDelayedExpansion
REM BACKUP LOCATION
if not defined dstDrivePath         set "dstDrivePath=%BACKUP_HOME%"
if not defined dstDrivePath         set "dstDrivePath=c:\backup"

REM 7Z OPTIONS                      
REM                                 -t7z        7z archive instead of zip file
REM                                 -m0=lzma    lzma compression method
REM                                 -mx=0       level of compression = 0-None 3-fast 9-Ultra
REM                                 -mfb=64     number of fast bytes for LZMA = 64
REM                                 -md=32m     dictionary size = 32 megabytes
REM                                 -ms=on      solid archive enabled     
REM                                 -mmt=1      1 thread
REM                                 -p          Encrypt
REM                                 -ssw        compress shared files
REM                                 -aoa        Overwrite All existing files without prompt
REM                                 -bt         Time stats
if not defined  _7zPwd              (set "_7zPwd=Starts123")
if not defined  _7zOpts             (set "_7zOpts=-t7z -m0=lzma -mx=9 -mfb=64 -md=32m -ms=on -mmt=32 -p%_7zPwd% -ssw -aoa -bt -scrcSHA256")
@echo %~n0      _7zOpts             [%_7zOpts%]
                                    
REM Timestamp
set "hhmmssSS=%time: =0%"
set "yyyymmddhhmmssSS=%date:~10,4%_%date:~4,2%_%date:~7,2%-%hhmmssSS:~0,2%_%hhmmssSS:~3,2%_%hhmmssSS:~6,2%_%hhmmssSS:~9,2%"

REM EXAMPLE                         c:\$RECYCLE.BIN>e:\ProgramFiles\7zbackup-sendto.cmd d:\dell\Logs  c:\backup

REM SRC DRIVE PATH ARG              [d:\dell\Logs]
set "srcDrvPathArg=%~1"
REM                                 default to current path
if defined srcDrvPathArg            goto :setSrcDriveArg
set "srcDrvPathArg=%cd%"
set "srcDrvArg=%srcDrvPathArg:~0,1%"

:setSrcDriveArg
REM SRC DRIVE ARG                   [d:]
if not defined  srcDrvArg           set "srcDrvArg=%~d1"
if not defined  srcDrvArg           echo %~n0       ERROR: Expected 1st argument to have a drive letter && exit /b 1
set "srcDrvArg=%srcDrvArg:~0,1%"
@echo %~n0      srcDrvArg           [%srcDrvArg%] 

REM SRC PATH ARG                    [Logs] (if no folder, eg d:\ becomes "d_drive")
set startPath=%cd%
cd /d "%srcDrvPathArg%"
for /f "delims=\" %%a in ("%cd%") do set "srcPathArg=%%~nxa"
cd /d "%startPath%"
if not defined  srcPathArg          set "srcPathArg=%srcDrvArg%_drive"
@echo %~n0      srcPathArg          [%srcPathArg%] 

                                    REM remove trailing slash from path 
IF "%srcDrvPathArg:~-1%"=="\"       SET "srcDrvPathArg=%srcDrvPathArg:~0,-1%"
@echo %~n0      srcDrvPathArg       [%srcDrvPathArg%]
if not defined  srcDrvPathArg       echo %~n0       ERROR: Expected 1st argument [%srcDrvPathArg%] to be src-drive-path && exit /b 1
if not exist   "%srcDrvPathArg%\."  echo %~n0       ERROR: Expected 1st argument [%srcDrvPathArg%] to be existing src-drive-path && exit /b 1

REM DST DRIVE PATH                  [c:\backup]
set "dstDrivePathArg=%~2"
if defined      dstDrivePathArg     set "dstDrivePath=%dstDrivePathArg%"
                                    REM remove trailing slash from path 
IF "%dstDrivePath:~-1%"=="\"        SET "dstDrivePath=%dstDrivePath:~0,-1%"
@echo %~n0      dstDrivePath        [%dstDrivePath%]
if not exist   "%dstDrivePath%\."   echo %~n0       ERROR: Expected 2nd argument to be existing dest-drive-path or null && exit /b 1


REM SKIP FILES
if not defined  _7zSkipfile         set _7zSkipfile="-xr!*war.original" "-xr!stats.json" "-xr!*SNAPSHOT.zip" "-xr!*phantomjs.exe" "-xr!hiberfil.sys" "-xr!pagefile.sys" "-xr!nexus-maven-repository-index.gz" "-xr!*.cfs"  "-xr!dell.sdr" "-xr!*.war"   "-xr!*.class" "-xr!ntuser.dat.LOG1" "-xr!NTUSER.DAT" "-xr!*.LOG" "-xr!*.TMP"
@echo %~n0      _7zSkipfile         [%_7zSkipfile%]

REM SKIP PATHS
REM "-xr!dependencies\"
if not defined  _7zSkipPath         set _7zSkipPath="-xr!Documents and Settings" "-xr!ProgramData" "-xr!Program Files" "-xr!Program Files (x86)"  "-xr!PerfLogs" "-xr!PerfLogs" "-xr!System Recovery" "-xr!Windows" "-xr!Users" "-xr!temp"  "-xr!tmp\"   "-xr!$RECYCLE.BIN\"  "-xr!Application Data\" "-xr!System Volume Information\"   "-xr!nexus\"  "-xr!Symantec\" "-xr!Package Cache\"  "-xr!syntevo\"    
@echo %~n0      _7zSkipPath         [%_7zSkipPath%]


REM 7Z APP
if not defined  _7zApp              (set "_7zApp=%~dp07z.exe")
if not exist    "%_7zApp%"          @echo.7zbackup ERROR - cant find z7ip executable. Please set _7zApp to the location of 7z.exe  && goto :error

REM  DST ZIP FILE                   [c:\backup\Logs-yyyy_mm_dd-hh_mm_ss_ms.7z]
if not defined  dstZipFile          (set "dstZipFile=%dstDrivePath%\%srcPathArg%-%yyyymmddhhmmssSS%.7z")
@echo %~n0      dstZipFile          [%dstZipFile%]

rem CREATE ZIP
@echo %~n0      srcDrvPathArg       [%srcDrvPathArg%]
@echo %~n0      ---------------------------------------------------------------------------------------------------------------------------------------------------------------
@echo %~n0      Create Archive      "%_7zApp%"          %_7zOpts%           %_7zSkipfile%          %_7zSkipPath%     a        %dstZipFile%         %srcDrvPathArg%    
                                    "%_7zApp%"          %_7zOpts%           %_7zSkipfile%          %_7zSkipPath%     a        %dstZipFile%         %srcDrvPathArg%    
@if ERRORLEVEL 255 @echo %~n0    Create 7z ERROR: User stopped the process && goto error
@if ERRORLEVEL 8 @echo %~n0      Create ERROR: Not enough memory for operation  && goto error
@if ERRORLEVEL 7 @echo %~n0      Create ERROR: Command line error  && goto error
@if ERRORLEVEL 2 @echo %~n0      Create ERROR: Fatal error && goto error
@if ERRORLEVEL 1 @echo %~n0      Create WARN: Backup completed with warnings 


rem VERIFYING ZIP IS UNENCRYPTABLE
@echo %~n0      ---------------------------------------------------------------------------------------------------------------------------------------------------------------
@echo %~n0       Test Archive       "%_7zApp%"       t     -p%_7zPwd%     %dstZipFile%       
                                    "%_7zApp%"       t     -p%_7zPwd%     %dstZipFile%       
@if ERRORLEVEL 255 @echo %~n0    Verify 7z ERROR: User stopped the process && goto error
@if ERRORLEVEL 8 @echo %~n0      Verify ERROR: Not enough memory for operation  && goto error
@if ERRORLEVEL 7 @echo %~n0      Verify ERROR: Command line error  && goto error
@if ERRORLEVEL 2 @echo %~n0      Verify ERROR: Fatal error && goto error
@if ERRORLEVEL 1 @echo %~n0      Verify WARN: Backup completed with warnings 

rem SUCCESS EXIT
:success
@echo. & @echo. & @echo. & @echo. & @echo.
@echo %~n0               start   [%hhmmssSS%]
@echo %~n0               end     [%time: =0%] 
@echo %~n0      SUCCESS  output  [%dstZipFile%]
@echo. & @echo.
@endlocal
EXIT /B 0

rem ERROR EXIT 
:error
pause
@echo.7zbackup               [%~0] 
@echo.7zbackup               script drive            %%~d0       =[%~d0]
@echo.7zbackup               script file             %%~0        =[%~0]
@echo.7zbackup               script file wo ext      %%~n0       =[%~n0]
@echo.7zbackup               script path             %%~p0       =[%~p0]
@echo.7zbackup               script drive path       %%~dp0      =[%~dp0]
@echo.7zbackup               script path+file dos8.3 %%~fs0      =[%~fs0]
@echo.7zbackup               script path+file        %%~f0       =[%~f0]
@echo.7zbackup               path resolved name      %%~$PATH:0  =[%~$PATH:0]
@shift
@echo.7zbackup
@echo.7zbackup               PATH                 [%PATH%]
@echo.7zbackup               CURRENT DIRECTORY    [%cd%]
@echo.7zbackup          
@echo.7zbackup             *******************************************************************************
@echo.7zbackup
:dumpArgs
@set "ARG=%~1"
@if not defined ARG goto error_exit
@echo.7zbackup             ARGUMENT [%ARG%]
@echo.7zbackup               drive            %%~d1       =[%~d1]
@echo.7zbackup               file             %%~1        =[%~1]
@echo.7zbackup               file wo ext      %%~n1       =[%~n1]
@echo.7zbackup               path             %%~p1       =[%~p1]
@echo.7zbackup               drive path       %%~dp1      =[%~dp1]
@echo.7zbackup               path+file dos8.3 %%~fs1      =[%~fs1]
@echo.7zbackup               path+file        %%~f1       =[%~f1]
@echo.7zbackup               path resolved    %%~$PATH:1  =[%~$PATH:1]
@echo.7zbackup       
@echo.7zbackup             *******************************************************************************
@shift     
@goto dumpArgs
:error_exit
@endlocal
EXIT /B 1



REM NOTES
http://www.7-zip.org/faq.html
7-Zip treats *.* as any file that has an extension. treats * as all files
-x[r[-|0]]]{@listfile|!wildcard}: eXclude filenames
-r:   switch: only compress files with specific extension. Eg   7z a -r c:\a.zip c:\dir\*.txt 
-t7z: use 7z file type (less compatible and smaller results)
-mx0: no compression 
-mx3: Fast compression 
-ms:  create solid archive (default)
-mmt: multithread the operation (faster
ssw: compress locked files
-aoa: overwrites all destination files. 
-x!file1.txt  excludes a txt file
    
REM                             [e:\ProgramFiles\]
@echo.7zbackup       drivePath0     [%~dp0]         
REM                             [d:\dell\]
@echo.7zbackup       drivePath1     [%~dp1]         
REM                             [e:]
@echo.7zbackup       drive0         [%~d0]          
REM                             [c:\$RECYCLE.BIN]
@echo.7zbackup       cwd            [%cd%]          
