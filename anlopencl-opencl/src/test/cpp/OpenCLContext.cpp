//
// Copyright (C) 2021 Erwin Müller <erwin@muellerpublic.de>
// Released as open-source under the Apache License, Version 2.0.
//
// ****************************************************************************
// ANL-OpenCL :: OpenCL
// ****************************************************************************
//
// Copyright (C) 2021 Erwin Müller <erwin@muellerpublic.de>
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//
// ****************************************************************************
// ANL-OpenCL :: OpenCL is a derivative work based on Josua Tippetts' C++ library:
// http://accidentalnoise.sourceforge.net/index.html
// ****************************************************************************
//
// Copyright (C) 2011 Joshua Tippetts
//
//   This software is provided 'as-is', without any express or implied
//   warranty.  In no event will the authors be held liable for any damages
//   arising from the use of this software.
//
//   Permission is granted to anyone to use this software for any purpose,
//   including commercial applications, and to alter it and redistribute it
//   freely, subject to the following restrictions:
//
//   1. The origin of this software must not be misrepresented; you must not
//      claim that you wrote the original software. If you use this software
//      in a product, an acknowledgment in the product documentation would be
//      appreciated but is not required.
//   2. Altered source versions must be plainly marked as such, and must not be
//      misrepresented as being the original software.
//   3. This notice may not be removed or altered from any source distribution.
//
//
// ****************************************************************************
// ANL-OpenCL :: OpenCL bundles and uses the RandomCL library:
// https://github.com/bstatcomp/RandomCL
// ****************************************************************************
//
// BSD 3-Clause License
//
// Copyright (c) 2018, Tadej Ciglarič, Erik Štrumbelj, Rok Češnovar. All rights reserved.
//
// Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
//
// * Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
//
// * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
//
// * Neither the name of the copyright holder nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
//

/*
 * OpenCLContext.cpp
 *
 *  Created on: Aug 29, 2021
 *      Author: Erwin Müller
 */

#include "OpenCLContext.h"
#include <spdlog/sinks/stdout_color_sinks.h>
#include <fstream>
#include <sstream>

std::string readFile(std::string fileName) {
	std::ifstream f(fileName);
	std::string s((std::istreambuf_iterator<char>(f)), std::istreambuf_iterator<char>());
	return s;
}

#define COMPILE_PROGRAM_ERR "Error compile program"

class ProgramEx : public cl::Program {
public:
	ProgramEx(const std::string &source, bool build = false, cl_int *err = NULL) :
			cl::Program { source, build, err } {
	}

	ProgramEx(const cl::Context &context, const std::string &source, bool build = false,
			cl_int *err = NULL) :
			cl::Program { source, build, err } {
	}

	ProgramEx(const Sources &sources, cl_int *err = NULL) : cl::Program { sources, err } {
	}

	ProgramEx(const cl::Context &context, const Sources &sources,
			cl_int *err = NULL) :
			cl::Program { context, sources, err } {
	}

#if CL_HPP_TARGET_OPENCL_VERSION >= 210 || (CL_HPP_TARGET_OPENCL_VERSION==200 && defined(CL_HPP_USE_IL_KHR))
	/**
	 * cl::Program constructor to allow construction of program from SPIR-V or another IL.
	 * Valid for either OpenCL >= 2.1 or when CL_HPP_USE_IL_KHR is defined.
	 */
	ProgramEx(const std::vector<char> &IL, bool build = false, cl_int *err = NULL) :
			cl::Program { IL, build, err } {
	}

	/**
	 * cl::Program constructor to allow construction of program from SPIR-V or another IL
	 * for a specific context.
	 * Valid for either OpenCL >= 2.1 or when CL_HPP_USE_IL_KHR is defined.
	 */
	ProgramEx(const cl::Context &context, const std::vector<char> &IL,
			bool build = false, cl_int *err = NULL) :
			cl::Program { context, IL, build, err } {
	}
#endif // #if CL_HPP_TARGET_OPENCL_VERSION >= 210

#if CL_HPP_TARGET_OPENCL_VERSION >= 120
    cl_int compile(
    		std::vector<cl::Program> input_headers,
			std::vector<std::string> input_header_names,
			const char* options = NULL,
			void (CL_CALLBACK * notifyFptr)(cl_program, void *) = NULL,
			void* data = NULL) const {
    	std::vector<cl_program> programs(input_headers.size());
        for (unsigned int i = 0; i < input_headers.size(); i++) {
            programs[i] = input_headers[i]();
        }
        cl::size_type n = input_header_names.size();
        std::vector<const char*> input_header_names_s(n);
        for (cl::size_type i = 0; i < n; ++i) {
        	input_header_names_s[i] = input_header_names[i].data();
        }
        cl_int error = ::clCompileProgram(
            object_,
            0,
            NULL,
            options,
			programs.size(),
			programs.data(),
			input_header_names_s.data(),
            notifyFptr,
            data);
        return cl::detail::buildErrHandler(error, COMPILE_PROGRAM_ERR,
				getBuildInfo<CL_PROGRAM_BUILD_LOG>());
	}
#endif // CL_HPP_TARGET_OPENCL_VERSION >= 120

};

#ifdef ANLOPENCL_USE_DOUBLE
static const std::string COMPILE_OPTIONS = "-DANLOPENCL_USE_DOUBLE -DANLOPENCL_USE_OPENCL";
#else
static const std::string COMPILE_OPTIONS = "-DANLOPENCL_USE_OPENCL";
#endif // ANLOPENCL_USE_DOUBLE

#if CL_HPP_TARGET_OPENCL_VERSION >= 120

cl::Program compileProgram(
		std::shared_ptr<spdlog::logger> logger,
		std::string s,
		std::vector<cl::Program> inputHeaders = std::vector<cl::Program>(),
		std::vector<std::string> inputHeaderNames = std::vector<std::string>()) {
	ProgramEx p(s);
	try {
		logger->debug("Compiling {}", s.substr(0, 60));
		p.compile(inputHeaders, inputHeaderNames, COMPILE_OPTIONS.c_str());
	} catch (const cl::Error &ex) {
		 cl_int buildErr = CL_SUCCESS;
		 auto buildInfo = p.getBuildInfo<CL_PROGRAM_BUILD_LOG>(&buildErr);
		 for (auto &pair : buildInfo) {
			 logger->error("Error compile {}", std::string(pair.second));
		 }
		 throw ex;
	}
	return std::move(p);
}

#endif // CL_HPP_TARGET_OPENCL_VERSION >= 120

#if CL_HPP_TARGET_OPENCL_VERSION >= 120

#define LINK_PROGRAM_ERR "Link program error"

inline cl::Program linkProgram(
    cl::Program input,
    const char* options = NULL,
    void (CL_CALLBACK * notifyFptr)(cl_program, void *) = NULL,
    void* data = NULL,
    cl_int* err = NULL)
{
    cl_int error_local = CL_SUCCESS;

    cl_program programs[1] = { input() };

    cl::Context ctx = input.getInfo<CL_PROGRAM_CONTEXT>(&error_local);
    if(error_local!=CL_SUCCESS) {
        cl::detail::errHandler(error_local, LINK_PROGRAM_ERR);
    }

    cl_program prog = ::clLinkProgram(
        ctx(),
        0,
        NULL,
        options,
        1,
        programs,
        notifyFptr,
        data,
        &error_local);

    cl::detail::errHandler(error_local,COMPILE_PROGRAM_ERR);
    if (err != NULL) {
        *err = error_local;
    }

    return cl::Program(prog);
}

inline cl::Program linkPrograms(
	std::vector<cl::Program> programs,
    const char* options = NULL,
    void (CL_CALLBACK * notifyFptr)(cl_program, void *) = NULL,
    void* data = NULL,
    cl_int* err = NULL)
{
	if (programs.size() == 0) {
		return cl::Program();
	}
    cl_int error_local = CL_SUCCESS;

    auto input = programs[0];
    cl::Context ctx = input.getInfo<CL_PROGRAM_CONTEXT>(&error_local);
    if(error_local!=CL_SUCCESS) {
        cl::detail::errHandler(error_local, LINK_PROGRAM_ERR);
    }

    auto clprograms = std::vector<cl_program>(programs.size());
    for (int i = 0; i < programs.size(); ++i) {
    	clprograms[i] = programs[i]();
	}
    cl_program prog = ::clLinkProgram(
        ctx(),
        0,
        NULL,
        options,
		clprograms.size(),
        clprograms.data(),
        notifyFptr,
        data,
        &error_local);

    cl::detail::errHandler(error_local,COMPILE_PROGRAM_ERR);
    if (err != NULL) {
        *err = error_local;
    }

    return cl::Program(prog);
}
#endif // CL_HPP_TARGET_OPENCL_VERSION >= 120

std::shared_ptr<spdlog::logger> OpenCL_Context::logger = []() -> std::shared_ptr<spdlog::logger> {
	logger = spdlog::stderr_color_mt("OpenCL_Context", spdlog::color_mode::automatic);
	logger->set_level(spdlog::level::trace);
	logger->flush_on(spdlog::level::debug);
	logger->flush_on(spdlog::level::err);
	return logger;
}();

OpenCL_Context::OpenCL_Context() {
	this->kiss09cl = readFile("src/main/cpp/extern/RandomCL/generators/kiss09.cl");
	this->randomcl = readFile("src/main/cpp/random.cl");
	std::stringstream ss;
	//ss << kiss09cl;
	ss << readFile("src/main/cpp/opencl_utils.h");
	ss << readFile("src/main/cpp/opencl_utils.c");
	ss << readFile("src/main/cpp/qsort.h");
	ss << readFile("src/main/cpp/qsort.c");
	ss << readFile("src/main/cpp/utility.h");
	ss << readFile("src/main/cpp/utility.c");
	ss << readFile("src/main/cpp/hashing.h");
	ss << readFile("src/main/cpp/hashing.c");
	ss << readFile("src/main/cpp/noise_lut.h");
	ss << readFile("src/main/cpp/noise_lut.c");
	ss << readFile("src/main/cpp/noise_gen.h");
	ss << readFile("src/main/cpp/noise_gen.c");
	ss << readFile("src/main/cpp/imaging.h");
	ss << readFile("src/main/cpp/imaging.c");
	ss << readFile("src/main/cpp/kernel.h");
	ss << readFile("src/main/cpp/kernel.c");
	sources = ss.str();
}

bool OpenCL_Context::loadPlatform() {
    std::vector<cl::Platform> platforms;
    cl::Platform::get(&platforms);
    cl::Platform plat;
    for (auto &p : platforms) {
        std::string platver = p.getInfo<CL_PLATFORM_VERSION>();
        if (platver.find("OpenCL 3.") != std::string::npos) {
            plat = p;
        }
    }
    if (plat() == 0)  {
    	logger->error("No OpenCL 3 platform found.");
        return false;
    }
    cl::Platform newP = cl::Platform::setDefault(plat);
    if (newP != plat) {
    	logger->error("Error setting default platform.");
        return false;
    }
    return true;
}

void OpenCL_Context::loadInputHeaders() {
	inputHeaders = std::vector<cl::Program>();
	inputHeaders.push_back(cl::Program(readFile("src/main/cpp/opencl_utils.h")));
	inputHeaders.push_back(cl::Program(readFile("src/main/cpp/qsort.h")));
	inputHeaders.push_back(cl::Program(readFile("src/main/cpp/utility.h")));
	inputHeaders.push_back(cl::Program(readFile("src/main/cpp/hashing.h")));
	inputHeaders.push_back(cl::Program(readFile("src/main/cpp/noise_lut.h")));
	inputHeaders.push_back(cl::Program(readFile("src/main/cpp/noise_gen.h")));
	inputHeaders.push_back(cl::Program(readFile("src/main/cpp/imaging.h")));
	inputHeaders.push_back(cl::Program(readFile("src/main/cpp/kernel.h")));
	inputHeaderNames = std::vector<std::string>();
	inputHeaderNames.push_back("opencl_utils.h");
	inputHeaderNames.push_back("qsort.h");
	inputHeaderNames.push_back("utility.h");
	inputHeaderNames.push_back("hashing.h");
	inputHeaderNames.push_back("noise_lut.h");
	inputHeaderNames.push_back("noise_gen.h");
	inputHeaderNames.push_back("imaging.h");
	inputHeaderNames.push_back("kernel.h");
}

cl::Program OpenCL_Context::createLibrary() {
	cl::Program p = compileProgram(logger, sources.c_str());
	logger->debug("Successfully compiled sources.");
	try {
		auto library = linkProgram(p, "-create-library");
		logger->debug("Successfully created library.");
		return library;
	} catch (const cl::Error &ex) {
		logger->error("Link library error {}: {}", ex.err(), ex.what());
		cl_int buildErr = CL_SUCCESS;
		auto buildInfo = p.getBuildInfo<CL_PROGRAM_BUILD_LOG>(&buildErr);
		for (auto &pair : buildInfo) {
			logger->error("Error link {}", std::string(pair.second));
		}
		throw ex;
	}
}

cl::Program OpenCL_Context::createKernel(cl::Program lib, const std::string kernel) {
	std::stringstream ss;
	ss << kiss09cl;
	ss << randomcl;
	ss << kernel;
	ProgramEx p(ss.str());
	try {
		logger->debug("Compiling {}", ss.str().substr(0, 60));
		p.compile(inputHeaders, inputHeaderNames, COMPILE_OPTIONS.c_str());
		auto programs = std::vector<cl::Program>();
		programs.push_back(lib);
		programs.push_back(p);
		auto kernel = linkPrograms(programs);
		return kernel;
	} catch (const cl::Error &ex) {
		cl_int buildErr = CL_SUCCESS;
		auto buildInfo = p.getBuildInfo<CL_PROGRAM_BUILD_LOG>(&buildErr);
		for (auto &pair : buildInfo) {
			logger->error("Error create kernel {}", std::string(pair.second));
		}
		throw ex;
	}
}

cl::Program OpenCL_Context::createPrograms(const std::string kernel) {
	std::stringstream ss;
	ss << sources;
	ss << kernel;
	cl::Program p = compileProgram(logger, ss.str());
	logger->debug("Successfully compiled sources.");
	//logger->debug(ss.str());
	try {
		auto kernel = linkProgram(p);
		logger->debug("Successfully linked sources");
		return kernel;
	} catch (const cl::Error &ex) {
		logger->error("Link library error {}: {}", ex.err(), ex.what());
		cl_int buildErr = CL_SUCCESS;
		auto buildInfo = p.getBuildInfo<CL_PROGRAM_BUILD_LOG>(&buildErr);
		for (auto &pair : buildInfo) {
			logger->error("Error link {}", std::string(pair.second));
		}
		throw ex;
	}
}

