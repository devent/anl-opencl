#!/bin/bash
#
# Copyright (C) 2021 Erwin Müller <erwin@muellerpublic.de>
# Released as open-source under the Apache License, Version 2.0.
#
# ****************************************************************************
# ANL-OpenCL :: IzPack Installation :: Fat Jar
# ****************************************************************************
#
# Copyright (C) 2021 Erwin Müller <erwin@muellerpublic.de>
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
# ****************************************************************************
# ANL-OpenCL :: IzPack Installation :: Fat Jar is a derivative work based on Josua Tippetts' C++ library:
# http://accidentalnoise.sourceforge.net/index.html
# ****************************************************************************
#
# Copyright (C) 2011 Joshua Tippetts
#
#   This software is provided 'as-is', without any express or implied
#   warranty.  In no event will the authors be held liable for any damages
#   arising from the use of this software.
#
#   Permission is granted to anyone to use this software for any purpose,
#   including commercial applications, and to alter it and redistribute it
#   freely, subject to the following restrictions:
#
#   1. The origin of this software must not be misrepresented; you must not
#      claim that you wrote the original software. If you use this software
#      in a product, an acknowledgment in the product documentation would be
#      appreciated but is not required.
#   2. Altered source versions must be plainly marked as such, and must not be
#      misrepresented as being the original software.
#   3. This notice may not be removed or altered from any source distribution.
#

## change the directory to the start directory of the application.
function changeBinDirectory() {
    symlink=`find "$0" -printf "%l"`
    cd "`dirname "${symlink:-$0}"`"
}

## Returns the no-java-runtime text based on the current locale.
## It is expected to find the text in a file under:
##     etc/[LANG]/no_java_runtime.txt
function noJavaRuntime() {
    file="../../etc/${lang[0]}/no_java_runtime.txt"
    if [ ! -f $file ]; then
        file="../../etc/en_US/no_java_runtime.txt"
    fi
    if [ -f $file ]; then
        noJavaRuntimeText="`cat $file`"
    fi
}

## Checks that the Java runtime is installed and shows an error dialog if
## it is not the case.
function checkJavaRuntime() {
    if [ -z "$javaCommand" ]; then
        noJavaRuntime
        type zenity >/dev/null 2>&1
        if [ $? = 0 ]; then
            zenity --error --text="$noJavaRuntimeText"
        else
            echo "$noJavaRuntimeText"
        fi
        exit 1
    fi
}

currentDir=$(pwd)
changeBinDirectory
javaCommand=`type -P java`
mainClass="${project.custom.app.mainclass}"
lib="../../lib/*"
IFS='.' read -a lang <<< "$LANG"
log="-Dlogback.configurationFile=file:///$PWD/../../etc/logback.xml"
logArgs="-Dproject.custom.log.prefix=$currentDir"
args=""
noJavaRuntimeText="No Java Runtime found."
checkJavaRuntime
export _JAVA_OPTIONS="-Dawt.useSystemAAFontSettings=on"
"$javaCommand" "$logArgs" "$log" -cp "$lib" "$mainClass" $args $*
