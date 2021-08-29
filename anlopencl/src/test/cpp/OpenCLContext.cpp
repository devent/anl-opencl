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

#if CL_HPP_TARGET_OPENCL_VERSION >= 120

cl::Program compileProgram(
		std::shared_ptr<spdlog::logger> logger,
		std::string s,
		std::vector<cl::Program> inputHeaders = std::vector<cl::Program>(),
		std::vector<std::string> inputHeaderNames = std::vector<std::string>()) {
	ProgramEx p(s);
	try {
		logger->debug("Compiling {}", s.substr(0, 60));
		p.compile(inputHeaders, inputHeaderNames, "-D USE_OPENCL");
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
#endif // CL_HPP_TARGET_OPENCL_VERSION >= 120

OpenCL_Context::OpenCL_Context() {
	this->logger = spdlog::stderr_color_mt("OpenCL_Context", spdlog::color_mode::automatic);
	logger->set_level(spdlog::level::trace);
	logger->flush_on(spdlog::level::debug);
	logger->flush_on(spdlog::level::err);
	std::stringstream ss;
	ss << readFile("src/main/cpp/extern/RandomCL/generators/kiss09.cl");
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
	ss << readFile("src/main/cpp/random.h");
	ss << readFile("src/main/cpp/random.c");
	ss << readFile("src/main/cpp/kernel.h");
	ss << readFile("src/main/cpp/kernel.c");
	this->sources = ss.str();
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

cl::Program OpenCL_Context::createLibrary() {
	cl::Program p = compileProgram(logger, sources.c_str());
	logger->debug("Successfully compiled sources.");
	try {
		auto library = linkProgram(p, "-create-library");
		logger->debug("Successfully linked sources");
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

