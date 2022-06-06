@REM
@REM Copyright (C) 2021 Erwin Müller <erwin@muellerpublic.de>
@REM Released as open-source under the Apache License, Version 2.0.
@REM
@REM ****************************************************************************
@REM ANL-OpenCL :: IzPack Installation :: Fat Jar
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
@REM ANL-OpenCL :: IzPack Installation :: Fat Jar is a derivative work based on Josua Tippetts' C++ library:
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
@REM
@REM ****************************************************************************
@REM ANL-OpenCL :: IzPack Installation :: Fat Jar bundles and uses the RandomCL library:
@REM https://github.com/bstatcomp/RandomCL
@REM ****************************************************************************
@REM
@REM BSD 3-Clause License
@REM
@REM Copyright (c) 2018, Tadej Ciglarič, Erik Štrumbelj, Rok Češnovar. All rights reserved.
@REM
@REM Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
@REM
@REM * Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
@REM
@REM * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
@REM
@REM * Neither the name of the copyright holder nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
@REM
@REM THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
@REM

set bashpath=%~dp0
set bashpath=%bashpath:~0,-1%
set apppath=%bashpath%/../..
set lib="%apppath%"\lib\*
set log="-Dlogback.configurationFile=file:///%apppath%/etc/logback-debug.xml"
set args=
set mainClass="${globalpom.custom.app.mainclass}"

java -version >nul 2>&1 && ( set found=true ) || ( set found=false )
if %found% EQU false (
    cscript "%apppath%/bin/windows/MessageBox.vbs" "Java is not correctly installed."
    exit 1
)

java %log% -cp %lib% %mainClass% %args% %*
