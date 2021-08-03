/*
 * opencl_test.cpp
 *
 *  Created on: Jul 27, 2021
 *      Author: Erwin Müller
 */

#include <memory>

#define CL_HPP_ENABLE_EXCEPTIONS
#include <CL/opencl.hpp>

#include "OpenCLTestFixture.h"

#include <spdlog/spdlog.h>
#include <spdlog/sinks/stdout_color_sinks.h>

using ::testing::TestWithParam;
using ::testing::Values;
using ::spdlog::info;
using ::spdlog::error;

class value_noise2D_fixture: public OpenCL_Context_Fixture {
protected:
	virtual size_t runKernel(cl::Program kernel) {
		auto t = GetParam();
		auto kernelf = cl::KernelFunctor<cl::Buffer, cl::Buffer>(kernel, t.kernel);
		int numElements = 64;
		std::vector<float> input(numElements * 2, 0.0f);
		cl::Buffer inputBuffer(begin(input), end(input), false);
	    std::vector<float> output(numElements, 0.0f);
	    cl::Buffer outputBuffer(begin(output), end(output), false);
	    cl::DeviceCommandQueue defaultDeviceQueue;
	    this->output = std::make_shared<std::vector<float>>(output);
	    this->outputBuffer = std::make_shared<cl::Buffer>(outputBuffer);
	    defaultDeviceQueue = cl::DeviceCommandQueue::makeDefault();
		try {
			cl_int error;
			kernelf(
					cl::EnqueueArgs(cl::NDRange(numElements)),
					inputBuffer,
					outputBuffer,
					error);
			logger->info("Created kernel error={}", error);
		} catch (const cl::Error &ex) {
			logger->error("Created kernel error {}: {}", ex.err(), ex.what());
			throw ex;
		}
		return numElements;
	}
};

TEST_P(value_noise2D_fixture, opencl_value_noise2D) {
	auto t = GetParam();
	//EXPECT_GT(availableDevices, 0);
	//EXPECT_FLOAT_EQ(value_noise2D(t.x, t.y, t.seed, t.interp), t.noise);
}

INSTANTIATE_TEST_SUITE_P(opencl, value_noise2D_fixture,
		Values(KernelContext {"value_noise2D_with_linearInterp_test", R"EOT(
kernel void value_noise2D_with_linearInterp_test(
global float2 *input,
global float *output) {
	int id = get_global_id(0);
	output[id] = value_noise2D(input[id], 200, linearInterp);
}
)EOT"})
		);
