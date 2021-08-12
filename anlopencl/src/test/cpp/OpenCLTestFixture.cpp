/**
 * Copyright (C) 2021 Erwin Müller <erwin@muellerpublic.de>
 * Released as open-source under the Apache License, Version 2.0.
 *
 * ****************************************************************************
 * ANL-OpenCL :: Core
 * ****************************************************************************
 *
 * Copyright (C) 2021 Erwin Müller <erwin@muellerpublic.de>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ****************************************************************************
 * ANL-OpenCL :: Core is a derivative work based on Josua Tippetts' C++ library:
 * http://accidentalnoise.sourceforge.net/index.html
 * ****************************************************************************
 *
 * Copyright (C) 2011 Joshua Tippetts
 *
 *   This software is provided 'as-is', without any express or implied
 *   warranty.  In no event will the authors be held liable for any damages
 *   arising from the use of this software.
 *
 *   Permission is granted to anyone to use this software for any purpose,
 *   including commercial applications, and to alter it and redistribute it
 *   freely, subject to the following restrictions:
 *
 *   1. The origin of this software must not be misrepresented; you must not
 *      claim that you wrote the original software. If you use this software
 *      in a product, an acknowledgment in the product documentation would be
 *      appreciated but is not required.
 *   2. Altered source versions must be plainly marked as such, and must not be
 *      misrepresented as being the original software.
 *   3. This notice may not be removed or altered from any source distribution.
 */
/*
 * OpenCLTestFixture.cpp
 *
 *  Created on: Aug 3, 2021
 *      Author: Erwin Müller
 */

#include <fstream>
#include <algorithm>
#include <stdlib.h>
#include <spdlog/sinks/stdout_color_sinks.h>
#include "OpenCLTestFixture.h"

using namespace cl;
using ::testing::TestWithParam;
using ::testing::Values;
using ::spdlog::info;
using ::spdlog::error;

bool loadPlatform(std::shared_ptr<spdlog::logger> logger) {
    std::vector<Platform> platforms;
    Platform::get(&platforms);
    Platform plat;
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
    Platform newP = Platform::setDefault(plat);
    if (newP != plat) {
    	logger->error("Error setting default platform.");
        return false;
    }
    return true;
}

#define COMPILE_PROGRAM_ERR "Error compile program"

class ProgramEx : public Program {
public:
	ProgramEx(const string &source, bool build = false, cl_int *err = NULL) :
			Program { source, build, err } {
	}

	ProgramEx(const Context &context, const string &source, bool build = false,
			cl_int *err = NULL) :
			Program { source, build, err } {
	}

	ProgramEx(const Sources &sources, cl_int *err = NULL) : Program { sources, err } {
	}

	ProgramEx(const Context &context, const Sources &sources,
			cl_int *err = NULL) :
			Program { context, sources, err } {
	}

#if CL_HPP_TARGET_OPENCL_VERSION >= 210 || (CL_HPP_TARGET_OPENCL_VERSION==200 && defined(CL_HPP_USE_IL_KHR))
	/**
	 * Program constructor to allow construction of program from SPIR-V or another IL.
	 * Valid for either OpenCL >= 2.1 or when CL_HPP_USE_IL_KHR is defined.
	 */
	ProgramEx(const vector<char> &IL, bool build = false, cl_int *err = NULL) :
			Program { IL, build, err } {
	}

	/**
	 * Program constructor to allow construction of program from SPIR-V or another IL
	 * for a specific context.
	 * Valid for either OpenCL >= 2.1 or when CL_HPP_USE_IL_KHR is defined.
	 */
	ProgramEx(const Context &context, const vector<char> &IL,
			bool build = false, cl_int *err = NULL) :
			Program { context, IL, build, err } {
	}
#endif // #if CL_HPP_TARGET_OPENCL_VERSION >= 210

#if CL_HPP_TARGET_OPENCL_VERSION >= 120
    cl_int compile(
    		vector<Program> input_headers,
			vector<string> input_header_names,
			const char* options = NULL,
			void (CL_CALLBACK * notifyFptr)(cl_program, void *) = NULL,
			void* data = NULL) const {
    	vector<cl_program> programs(input_headers.size());
        for (unsigned int i = 0; i < input_headers.size(); i++) {
            programs[i] = input_headers[i]();
        }
        size_type n = input_header_names.size();
        vector<const char*> input_header_names_s(n);
        for (size_type i = 0; i < n; ++i) {
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
        return detail::buildErrHandler(error, COMPILE_PROGRAM_ERR,
				getBuildInfo<CL_PROGRAM_BUILD_LOG>());
	}
#endif // CL_HPP_TARGET_OPENCL_VERSION >= 120

};

#if CL_HPP_TARGET_OPENCL_VERSION >= 120

Program compileProgram(
		std::shared_ptr<spdlog::logger> logger,
		std::string s,
		std::vector<Program> inputHeaders = std::vector<Program>(),
		std::vector<std::string> inputHeaderNames = std::vector<std::string>()) {
	ProgramEx p(s);
	try {
		logger->debug("Compiling {}", s.substr(0, 60));
		p.compile(inputHeaders, inputHeaderNames, "-D USE_OPENCL");
	} catch (const Error &ex) {
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

inline Program linkProgram(
    Program input,
    const char* options = NULL,
    void (CL_CALLBACK * notifyFptr)(cl_program, void *) = NULL,
    void* data = NULL,
    cl_int* err = NULL)
{
    cl_int error_local = CL_SUCCESS;

    cl_program programs[1] = { input() };

    Context ctx = input.getInfo<CL_PROGRAM_CONTEXT>(&error_local);
    if(error_local!=CL_SUCCESS) {
        detail::errHandler(error_local, LINK_PROGRAM_ERR);
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

    detail::errHandler(error_local,COMPILE_PROGRAM_ERR);
    if (err != NULL) {
        *err = error_local;
    }

    return Program(prog);
}
#endif // CL_HPP_TARGET_OPENCL_VERSION >= 120

std::string readFile(std::string fileName) {
	std::ifstream f(fileName);
	std::string s((std::istreambuf_iterator<char>(f)), std::istreambuf_iterator<char>());
	return s;
}

std::shared_ptr<spdlog::logger> OpenCL_Context_Fixture::logger = []() -> std::shared_ptr<spdlog::logger> {
	logger = spdlog::stderr_color_mt("OpenCL_Context_Fixture", spdlog::color_mode::automatic);
	logger->set_level(spdlog::level::trace);
	logger->flush_on(spdlog::level::err);
	return logger;
}();

Program createPrograms(std::shared_ptr<spdlog::logger> logger, const KernelContext& t) {
	std::stringstream ss;
	ss << readFile("src/main/cpp/opencl_utils.h");
	ss << readFile("src/main/cpp/utility.h");
	ss << readFile("src/main/cpp/hashing.h");
	ss << readFile("src/main/cpp/hashing.c");
	ss << readFile("src/main/cpp/noise_lut.h");
	ss << readFile("src/main/cpp/noise_lut.c");
	ss << readFile("src/main/cpp/noise_gen.h");
	ss << readFile("src/main/cpp/noise_gen.c");
	ss << readFile("src/main/cpp/imaging.h");
	ss << readFile("src/main/cpp/imaging.c");
	ss << t.source;
	Program p = compileProgram(logger, ss.str());
	logger->debug("Successfully compiled sources.");
	//logger->debug(ss.str());
	try {
		auto kernel = linkProgram(p);
		logger->debug("Successfully linked sources");
		return kernel;
    } catch (const Error &ex) {
    	logger->error("Link library error {}: {}", ex.err(), ex.what());
		 cl_int buildErr = CL_SUCCESS;
		 auto buildInfo = p.getBuildInfo<CL_PROGRAM_BUILD_LOG>(&buildErr);
		 for (auto &pair : buildInfo) {
			 logger->error("Error link {}", std::string(pair.second));
		 }
    	throw ex;
    }
}

void OpenCL_Context_Fixture::SetUpTestSuite() {
	Device d = Device::getDefault();
	logger->debug("Max compute units: {}", d.getInfo<CL_DEVICE_MAX_COMPUTE_UNITS>());
	logger->debug("Max dimensions: {}", d.getInfo<CL_DEVICE_MAX_WORK_ITEM_DIMENSIONS>());
	//std::cout << "Max work item sizes: " << d.getInfo<CL_DEVICE_MAX_WORK_ITEM_SIZES>() << "\n";
	logger->debug("Max work group sizes: {}", d.getInfo<CL_DEVICE_MAX_WORK_GROUP_SIZE>());
	logger->debug("Max pipe args: {}", d.getInfo<CL_DEVICE_MAX_PIPE_ARGS>());
	logger->debug("Max pipe active reservations: {}", d.getInfo<CL_DEVICE_PIPE_MAX_ACTIVE_RESERVATIONS>());
	logger->debug("Max pipe packet size: {}", d.getInfo<CL_DEVICE_PIPE_MAX_PACKET_SIZE>());
	logger->debug("Device SVM capabilities: {}", d.getInfo<CL_DEVICE_SVM_CAPABILITIES>());
}

void OpenCL_Context_Fixture::SetUp() {
	EXPECT_TRUE(loadPlatform(logger)) << "Unable to load platform";
	auto t = GetParam();
	auto kernel = createPrograms(logger, GetParam());
	size_t numElements;
	try {
		numElements = runKernel(kernel);
	} catch (const cl::Error &ex) {
		logger->error("Run kernel error {}: {}", ex.err(), ex.what());
		throw ex;
	}
	try {
		cl::copy(*outputBuffer, std::begin(*output), std::end(*output));
	} catch (const cl::Error &ex) {
		logger->error("Output buffer error {}: {}", ex.err(), ex.what());
		throw ex;
	}

//	std::cout << "Output:\n";
//	for (int i = 0; i < numElements; ++i) {
//		std::cout << "\t" << (*output)[i] << "\n";
//	}
}

void OpenCL_Context_Fixture::TearDown() {
}

