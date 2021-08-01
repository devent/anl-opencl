/*
 * opencl_test.cpp
 *
 *  Created on: Jul 27, 2021
 *      Author: Erwin Müller
 */

#include <gtest/gtest.h>
#include <stdio.h>
#include <stdlib.h>
#include <string>
#include <unistd.h>
#include <fstream>
#include <streambuf>
#include <algorithm>
#include <memory>

#ifdef __APPLE__
#include <OpenCL/opencl.h>
#else
#include <CL/cl.h>
#endif

#define CL_HPP_ENABLE_EXCEPTIONS
#include <CL/opencl.hpp>

#include "program_ex.hpp"

#include <spdlog/spdlog.h>
#include <spdlog/sinks/stdout_color_sinks.h>

using namespace cl;
using ::testing::TestWithParam;
using ::testing::Values;
using ::spdlog::info;
using ::spdlog::error;

typedef std::vector<cl_mem> (*OpenCL_Context_createBuffer)(cl_context);

struct OpenCL_Context {
	std::string kernel;
	std::string source;
	OpenCL_Context_createBuffer createBuffer;
};

class OpenCL_Context_Fixture: public ::testing::TestWithParam<OpenCL_Context> {
public:
	std::shared_ptr<spdlog::logger> logger;
	Program library;
protected:
	OpenCL_Context_Fixture() { // @suppress("Class members should be properly initialized")
	};

	static std::string readFile(std::string fileName) {
		std::ifstream f(fileName);
		std::string s((std::istreambuf_iterator<char>(f)), std::istreambuf_iterator<char>());
		return s;
	}

	std::vector<const char*> strlist(std::vector<string> &input) {
	    std::vector<const char*> result;
	    // remember the nullptr terminator
	    result.reserve(input.size()+1);
	    transform(begin(input), end(input),
	                   back_inserter(result),
	                   [](string &s) { return s.data(); }
	                  );
	    result.push_back(nullptr);
	    return result;
	}

	void createPrograms() {
		std::stringstream ss;
		ss << readFile("src/main/cpp/opencl_utils.h");
		ss << readFile("src/main/cpp/utility.h");
		ss << readFile("src/main/cpp/hashing.h");
		ss << readFile("src/main/cpp/hashing.c");
		ss << readFile("src/main/cpp/noise_gen.h");
		ss << readFile("src/main/cpp/noise_gen.c");
		Program p = compileProgram(logger, ss.str());
		logger->debug("Successfully compiled sources.");
		logger->debug(ss.str());
		try {
			library = linkProgram(p, "-create-library");
			logger->debug("Successfully linked sources");
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

	virtual void SetUp() {
		logger = spdlog::stderr_color_mt("opencl_test", spdlog::color_mode::automatic);
		logger->set_level(spdlog::level::debug);
		logger->flush_on(spdlog::level::err);
		EXPECT_TRUE(loadPlatform(logger)) << "Unable to load platform";
		createPrograms();

		auto t = GetParam();
		Program kernel = compileProgram(logger, t.source);
		Program pfinal;
		try {
			pfinal = linkProgram(library, kernel);
		} catch (const cl::Error &ex) {
			logger->error("Link kernel error {}: {}", ex.err(), ex.what());
			throw ex;
		}

		int numElements = 64;
	    std::vector<float> output(numElements, 0.0f);
	    cl::Buffer outputBuffer(begin(output), end(output), false);

	    cl::DeviceCommandQueue defaultDeviceQueue;
        defaultDeviceQueue = cl::DeviceCommandQueue::makeDefault();


	    auto vectorAddKernel =
	        cl::KernelFunctor<
	            cl::Buffer
	        >(pfinal, "updateGlobal");

	    try {
			cl_int error;
			vectorAddKernel(
				cl::EnqueueArgs(
					cl::NDRange(numElements)),
				outputBuffer,
				error
			);
			logger->info("Created kernel error={}", error);
	    } catch (const cl::Error &ex) {
	    	logger->error("Created kernel error {}: {}", ex.err(), ex.what());
	    	throw ex;
	    }

    	cl::copy(outputBuffer, begin(output), end(output));

		cl::Device d = cl::Device::getDefault();
		std::cout << "Max pipe args: " << d.getInfo<CL_DEVICE_MAX_PIPE_ARGS>() << "\n";
		std::cout << "Max pipe active reservations: " << d.getInfo<CL_DEVICE_PIPE_MAX_ACTIVE_RESERVATIONS>() << "\n";
		std::cout << "Max pipe packet size: " << d.getInfo<CL_DEVICE_PIPE_MAX_PACKET_SIZE>() << "\n";
		std::cout << "Device SVM capabilities: " << d.getInfo<CL_DEVICE_SVM_CAPABILITIES>() << "\n";
		std::cout << "\tCL_DEVICE_SVM_COARSE_GRAIN_BUFFER = " << CL_DEVICE_SVM_COARSE_GRAIN_BUFFER << "\n";
		std::cout << "\tCL_DEVICE_SVM_FINE_GRAIN_BUFFER = " << CL_DEVICE_SVM_FINE_GRAIN_BUFFER << "\n";
		std::cout << "\tCL_DEVICE_SVM_FINE_GRAIN_SYSTEM = " << CL_DEVICE_SVM_FINE_GRAIN_SYSTEM << "\n";
		std::cout << "\tCL_DEVICE_SVM_ATOMICS = " << CL_DEVICE_SVM_ATOMICS << "\n";

		auto v = pfinal.getInfo<CL_PROGRAM_BINARIES>();
		auto v2 = pfinal.getInfo<CL_PROGRAM_BINARY_SIZES>();
		std::vector<std::vector<unsigned char>> v3;
		std::vector<size_t> v4;
		pfinal.getInfo(CL_PROGRAM_BINARIES, &v3);
		pfinal.getInfo(CL_PROGRAM_BINARY_SIZES, &v4);

		std::cout << "Binaries: " << v.size() << "\n";
		std::cout << "Binary sizes: " << v2.size() << "\n";
		for (size_t s : v2) {
			std::cout << "\t" << s << "\n";
		}

		std::cout << "Output:\n";
		for (int i = 1; i < numElements; ++i) {
			std::cout << "\t" << output[i] << "\n";
		}
	}

	virtual void TearDown() {
	}
};

TEST_P(OpenCL_Context_Fixture, opencl_value_noise2D) {
	auto t = GetParam();
	//EXPECT_GT(availableDevices, 0);
	//EXPECT_FLOAT_EQ(value_noise2D(t.x, t.y, t.seed, t.interp), t.noise);
}

std::vector<cl_mem> value_noise2D_buffers(cl_context context) {
	int NUM_ENTRIES = 1024;
	std::vector<cl_mem> memobjs(2);
	memobjs.push_back(clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR, sizeof(float) * 2 * NUM_ENTRIES, NULL, NULL));
	memobjs.push_back(clCreateBuffer(context, CL_MEM_READ_WRITE, sizeof(float) * 2 * NUM_ENTRIES, NULL, NULL));
	return memobjs;
}

INSTANTIATE_TEST_SUITE_P(opencl, OpenCL_Context_Fixture,
		Values(OpenCL_Context {"value_noise2D_test", R"EOT(
kernel void updateGlobal(global float *output) {
	int id = get_global_id(0);
	output[id] = value_noise2D(10.0, 10.0, 200, linearInterp);
}
)EOT", &value_noise2D_buffers})
		);
