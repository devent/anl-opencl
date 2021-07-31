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

#ifdef __APPLE__
#include <OpenCL/opencl.h>
#else
#include <CL/cl.h>
#endif

#define CL_HPP_ENABLE_EXCEPTIONS
#include <CL/cl2.hpp>

#include "program_ex.hpp"

using namespace cl;
using ::testing::TestWithParam;
using ::testing::Values;

typedef std::vector<cl_mem> (*createBuffer)(cl_context);

struct OpenCL_Context {
	string kernel;
	string source;
	createBuffer createBuffer;
};

class OpenCL_Context_Fixture: public ::testing::TestWithParam<OpenCL_Context> {
public:
	std::vector<string> input_headers;
	std::vector<string> input_header_names;
protected:
	OpenCL_Context_Fixture() { // @suppress("Class members should be properly initialized")
		input_headers.push_back(readFile("src/main/cpp/use_opencl.h"));
		input_header_names.push_back("use_opencl.h");
		input_headers.push_back(readFile("src/main/cpp/opencl_utils.h"));
		input_header_names.push_back("opencl_utils.h");
		input_headers.push_back(readFile("src/main/cpp/utility.h"));
		input_header_names.push_back("utility.h");
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

	bool loadPlatform() {
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
	        std::cout << "No OpenCL 3 platform found.";
	        return false;
	    }
	    Platform newP = Platform::setDefault(plat);
	    if (newP != plat) {
	        std::cout << "Error setting default platform.";
	        return false;
	    }
	    return true;
	}

	virtual void SetUp() {
		EXPECT_TRUE(loadPlatform()) << "Unable to load platform";
		vector<Program> programs;
		vector<Program> input_headers_p;
		for (auto s : input_headers) {
			Program p(s);
			try {
				std::cerr << s << std::endl << std::endl;
				p.compile();
			} catch (...) {
				 cl_int buildErr = CL_SUCCESS;
				 auto buildInfo = p.getBuildInfo<CL_PROGRAM_BUILD_LOG>(&buildErr);
				 for (auto &pair : buildInfo) {
				 	std::cerr << pair.second << std::endl << std::endl;
				 }
			}
			input_headers_p.push_back(p);
		}
		auto t = GetParam();
		ProgramEx psource(t.source);
		psource.compile(input_headers_p, input_header_names);
		programs.push_back(psource);
		Program pfinal = linkProgram(programs);

		int numElements = 64;
	    std::vector<int> output(numElements, 0xdeadbeef);
	    cl::Buffer outputBuffer(begin(output), end(output), false);
	    cl::Pipe aPipe(sizeof(cl_int), numElements / 2);

	    cl::DeviceCommandQueue defaultDeviceQueue;
        defaultDeviceQueue = cl::DeviceCommandQueue::makeDefault();


	    auto vectorAddKernel =
	        cl::KernelFunctor<
	            cl::Buffer,
	            cl::DeviceCommandQueue
	        >(pfinal, "updateGlobal");

	    cl_int error;
    	vectorAddKernel(
            cl::EnqueueArgs(
                cl::NDRange(numElements/2),
                cl::NDRange(numElements/2)),
            outputBuffer,
            defaultDeviceQueue,
    		error
        );

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
#define USE_OPENCL
global int globalA;
kernel void updateGlobal() {
	globalA = 75;
}
)EOT", &value_noise2D_buffers})
		);
