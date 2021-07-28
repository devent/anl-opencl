/*
 * opencl_test.cpp
 *
 *  Created on: Jul 27, 2021
 *      Author: Erwin Müller
 */

#ifndef USE_OPENCL
#include <gtest/gtest.h>
#include <stdio.h>
#include <stdlib.h>
#include <string>
#include <unistd.h>
#include <fstream>
#include <streambuf>

#ifdef __APPLE__
#include <OpenCL/opencl.h>
#else
#include <CL/cl.h>
#endif

using namespace std;
using ::testing::TestWithParam;
using ::testing::Values;

typedef vector<cl_mem> (*createBuffer)(cl_context);

struct OpenCL_Context {
	string kernel;
	string source;
	createBuffer createBuffer;
};

class OpenCL_Context_Fixture: public ::testing::TestWithParam<OpenCL_Context> {
public:
	cl_uint availablePlatforms;
	cl_uint availableDevices;
	cl_platform_id platform_id = NULL;
	cl_device_id device_id = NULL;
	cl_context context = NULL;
	cl_command_queue queue = NULL;
	vector<cl_mem> memobjs;
	cl_program program = NULL;
	cl_kernel kernel = NULL;
	size_t *global_work_offset;
	size_t *global_work_size;
	size_t *local_work_size;
	vector<string> libraries;
protected:
	OpenCL_Context_Fixture() { // @suppress("Class members should be properly initialized")
		libraries.push_back("src/main/cpp/opencl_utils.h");
		libraries.push_back("src/main/cpp/utility.h");
	};

	virtual void SetUp() {
		cl_int err;
		clGetPlatformIDs(1, &platform_id, &availablePlatforms);
		EXPECT_GT(availablePlatforms, 0) << "No OpenCL platform available.";
		clGetDeviceIDs(platform_id, CL_DEVICE_TYPE_DEFAULT, 1, &device_id, &availableDevices);
		EXPECT_GT(availableDevices, 0) << "No OpenCL device available.";
		context = clCreateContext(NULL, 1, &device_id, NULL, NULL, &err);
		if (err != CL_SUCCESS) {
			switch (err) {
				case CL_INVALID_PLATFORM:
					EXPECT_EQ(err, CL_SUCCESS) << "Create context error: CL_INVALID_PLATFORM";
					break;
				case CL_INVALID_PROPERTY:
					EXPECT_EQ(err, CL_SUCCESS) << "Create context error: CL_INVALID_PROPERTY";
					break;
				case CL_INVALID_VALUE:
					EXPECT_EQ(err, CL_SUCCESS) << "Create context error: CL_INVALID_VALUE";
					break;
				case CL_INVALID_DEVICE_TYPE:
					EXPECT_EQ(err, CL_SUCCESS) << "Create context error: CL_INVALID_DEVICE_TYPE";
					break;
				case CL_DEVICE_NOT_AVAILABLE:
					EXPECT_EQ(err, CL_SUCCESS) << "Create context error: CL_DEVICE_NOT_AVAILABLE";
					break;
				case CL_DEVICE_NOT_FOUND:
					EXPECT_EQ(err, CL_SUCCESS) << "Create context error: CL_DEVICE_NOT_FOUND";
					break;
				case CL_OUT_OF_RESOURCES:
					EXPECT_EQ(err, CL_SUCCESS) << "Create context error: CL_OUT_OF_RESOURCES";
					break;
				case CL_OUT_OF_HOST_MEMORY:
					EXPECT_EQ(err, CL_SUCCESS) << "Create context error: CL_OUT_OF_HOST_MEMORY";
					break;
				default:
					EXPECT_EQ(err, CL_SUCCESS) << "Create context error";
					break;
			}
		}

		cl_device_id devices[1];
		clGetDeviceIDs(NULL, CL_DEVICE_TYPE_GPU, 1, devices, NULL);
		queue = clCreateCommandQueueWithProperties(context, devices[0], 0, &err);
		EXPECT_EQ(err, 0) << "Create command queue error.";

		vector<const char*> sources;
		for (auto library : libraries) {
			ifstream f(library.c_str());
			string s((istreambuf_iterator<char>(f)), istreambuf_iterator<char>());
			printf("%s\n", s.c_str());
			sources.push_back(s.c_str());
		}

		auto t = GetParam();
		sources.push_back(t.source.c_str());

		program = clCreateProgramWithSource(context, sources.size(), sources.data(), NULL, &err);
		if (err != CL_SUCCESS) {
			switch (err) {
				case CL_INVALID_CONTEXT:
					EXPECT_EQ(err, CL_SUCCESS) << "Create program error: CL_INVALID_CONTEXT";
					break;
				case CL_INVALID_VALUE:
					EXPECT_EQ(err, CL_SUCCESS) << "Create program error: CL_INVALID_VALUE";
					break;
				case CL_OUT_OF_RESOURCES:
					EXPECT_EQ(err, CL_SUCCESS) << "Create program error: CL_OUT_OF_RESOURCES";
					break;
				case CL_OUT_OF_HOST_MEMORY:
					EXPECT_EQ(err, CL_SUCCESS) << "Create program error: CL_OUT_OF_HOST_MEMORY";
					break;
				default:
					EXPECT_EQ(err, CL_SUCCESS) << "Create program error";
					break;
			}
		}

		err = clBuildProgram(program, 1, &device_id, NULL, NULL, NULL);
		if (err != CL_SUCCESS) {
			switch (err) {
				case CL_INVALID_PROGRAM:
					EXPECT_EQ(err, CL_SUCCESS) << "Build program error: CL_INVALID_PROGRAM";
					break;
				default:
					EXPECT_EQ(err, CL_SUCCESS) << "Build program error";
					break;
			}
		}

		kernel = clCreateKernel(program, t.kernel.c_str(), &err);
		if (err != CL_SUCCESS) {
			switch (err) {
				default:
					EXPECT_EQ(err, CL_SUCCESS) << "Create kernel error";
					break;
			}
		}

		size_t global_work_size[1] = { 256 };
		global_work_size[0] = 1024;
		local_work_size[0] = 64; //Nvidia: 192 or 256
		clEnqueueNDRangeKernel(queue, kernel, 1, NULL, global_work_size,
				local_work_size, 0, NULL, NULL);

		err = clFinish(queue);
		if (err != CL_SUCCESS) {
			switch (err) {
				default:
					EXPECT_EQ(err, CL_SUCCESS) << "Finish error";
					break;
			}
		}
	}

	virtual void TearDown() {
		for (cl_mem m : memobjs) {
			clReleaseMemObject(m);
		}
		if (kernel != NULL) {
			clReleaseKernel(kernel);
		}
		if (program != NULL) {
			clReleaseProgram(program);
		}
		if (queue != NULL) {
			clReleaseCommandQueue(queue);
		}
		if (context != NULL) {
			clReleaseContext(context);
		}
	}
};

TEST_P(OpenCL_Context_Fixture, opencl_value_noise2D) {
	auto t = GetParam();
	EXPECT_GT(availableDevices, 0);
	//EXPECT_FLOAT_EQ(value_noise2D(t.x, t.y, t.seed, t.interp), t.noise);
}

vector<cl_mem> value_noise2D_buffers(cl_context context) {
	int NUM_ENTRIES = 1024;
	vector<cl_mem> memobjs(2);
	memobjs.push_back(clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR, sizeof(float) * 2 * NUM_ENTRIES, NULL, NULL));
	memobjs.push_back(clCreateBuffer(context, CL_MEM_READ_WRITE, sizeof(float) * 2 * NUM_ENTRIES, NULL, NULL));
	return memobjs;
}

INSTANTIATE_TEST_SUITE_P(opencl, OpenCL_Context_Fixture,
		Values(OpenCL_Context {"value_noise2D_test", R"EOT(
#define USE_OPENCL
__kernel void value_noise2D_test(__global float2 *in, __global float2 *out,
                          __local float *sMemx, __local float *sMemy) {
	int tid = get_local_id(0);
	printf("%d\n", tid);
}
)EOT", &value_noise2D_buffers})
		);

#endif
