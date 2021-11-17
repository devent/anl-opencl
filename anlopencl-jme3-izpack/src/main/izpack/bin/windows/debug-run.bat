@REM
@REM Copyright (C) 2021 Erwin Müller <erwin@muellerpublic.de>
@REM Released as open-source under the Apache License, Version 2.0.
@REM
@REM ****************************************************************************
@REM ANL-OpenCL :: IzPack Installation
@REM ****************************************************************************
@REM
@REM Copyright (C) 2021 Erwin Müller <erwin@muellerpublic.de>
@REM
@REM Licensed under the Apache License, Version 2.0 (the "License");
@REM you may not use this file except in compliance with the License.
@REM You may obtain a copy of the License at
@REM
@REM      http://www.apache.org/licenses/LICENSE-2.0
@REM
@REM Unless required by applicable law or agreed to in writing, software
@REM distributed under the License is distributed on an "AS IS" BASIS,
@REM WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
@REM See the License for the specific language governing permissions and
@REM limitations under the License.
@REM
@REM ****************************************************************************
@REM ANL-OpenCL :: IzPack Installation is a derivative work based on Josua Tippetts' C++ library:
@REM http://accidentalnoise.sourceforge.net/index.html
@REM ****************************************************************************
@REM
@REM Copyright (C) 2011 Joshua Tippetts
@REM
@REM   This software is provided 'as-is', without any express or implied
@REM   warranty.  In no event will the authors be held liable for any damages
@REM   arising from the use of this software.
@REM
@REM   Permission is granted to anyone to use this software for any purpose,
@REM   including commercial applications, and to alter it and redistribute it
@REM   freely, subject to the following restrictions:
@REM
@REM   1. The origin of this software must not be misrepresented; you must not
@REM      claim that you wrote the original software. If you use this software
@REM      in a product, an acknowledgment in the product documentation would be
@REM      appreciated but is not required.
@REM   2. Altered source versions must be plainly marked as such, and must not be
@REM      misrepresented as being the original software.
@REM   3. This notice may not be removed or altered from any source distribution.
@REM

set bashpath=%~dp0
set bashpath=%bashpath:~0,-1%
set apppath=%bashpath%/../..
set lib="%apppath%"\lib\*
set log="-Dlogback.configurationFile=file:///%apppath%/etc/logback-debug.xml"
set args=
set mainClass="${project.custom.app.mainclass}"

java -version >nul 2>&1 && ( set found=true ) || ( set found=false )
if %found% EQU false (
    cscript "%apppath%/bin/windows/MessageBox.vbs" "Java is not correctly installed."
    exit 1
)

java %log% -cp %lib% %mainClass% %args% %*
