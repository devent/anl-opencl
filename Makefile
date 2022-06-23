#!/bin/bash
#
# Copyright (C) 2021 Erwin Müller <erwin@muellerpublic.de>
# Released as open-source under the Apache License, Version 2.0.
#
# ****************************************************************************
# ANL-OpenCL :: Bundle POM
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
# ANL-OpenCL :: Bundle POM is a derivative work based on Josua Tippetts' C++ library:
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
#
# ****************************************************************************
# ANL-OpenCL :: Bundle POM bundles and uses the RandomCL library:
# https://github.com/bstatcomp/RandomCL
# ****************************************************************************
#
# BSD 3-Clause License
#
# Copyright (c) 2018, Tadej Ciglarič, Erik Štrumbelj, Rok Češnovar. All rights reserved.
#
# Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
#
# * Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
#
# * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
#
# * Neither the name of the copyright holder nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
#
# THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
#

SHELL := /bin/bash
.DEFAULT_GOAL := help

.PHONY: update-versions
update-versions: ##@targets Updates the versions of all sub-modules
	$(call check_defined, VERSION, VERSION must be set)
	mvn versions:set -DnewVersion=$(VERSION) && mvn versions:commit
	cd anlopencl-jme3-izpack && mvn versions:set -DnewVersion=$(VERSION) && mvn versions:commit
	cd anlopencl-jme3-izpack-fat && mvn versions:set -DnewVersion=$(VERSION) && mvn versions:commit

#COLORS
GREEN  := $(shell tput -Txterm setaf 2)
WHITE  := $(shell tput -Txterm setaf 7)
YELLOW := $(shell tput -Txterm setaf 3)
RED := $(shell tput -Txterm setaf 1)
RESET  := $(shell tput -Txterm sgr0)

# Add the following 'help' target to your Makefile
# And add help text after each target name starting with '\#\#'
# A category can be added with @category
HELP_FUN = \
    %help; \
    while(<>) { push @{$$help{$$2 // 'options'}}, [$$1, $$3] if /^([0-9a-zA-Z\-]+)\s*:.*\#\#(?:@([a-zA-Z\-]+))?\s(.*)$$/ }; \
    print "USAGE\n\nmake [target]\n\n"; \
    for (sort keys %help) { \
    print "${WHITE}$$_:${RESET}\n"; \
    for (@{$$help{$$_}}) { \
    $$sep = " " x (32 - length $$_->[0]); \
    print "  ${YELLOW}$$_->[0]${RESET}$$sep${GREEN}$$_->[1]${RESET}\n"; \
    }; \
    print "\n"; }

help: ##@other Show this help.
	@perl -e '$(HELP_FUN)' $(MAKEFILE_LIST)

# Check that given variables are set and all have non-empty values,
# die with an error otherwise.
#
# Author: http://stackoverflow.com/questions/10858261/abort-makefile-if-variable-not-set
#
# Params:
#   1. Variable name(s) to test.
#   2. (optional) Error message to print.
check_defined = \
    $(foreach 1,$1,$(__check_defined))
__check_defined = \
    $(if $(value $1),, \
      $(error ${RED}Undefined $1$(if $(value 2), ($(strip $2)))))
