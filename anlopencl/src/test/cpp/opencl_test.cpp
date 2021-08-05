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

#include <opencv2/core.hpp>
#include <opencv2/core/mat.hpp>

#include <strings.h>

using ::testing::TestWithParam;
using ::testing::Values;
using ::spdlog::info;
using ::spdlog::error;

class value_noise2D_fixture: public OpenCL_Context_Fixture {
protected:

	void fill2dSpace(cv::Mat & v, size_t imageWidth) {
		float increment = 2.0 / imageWidth;
		float vx = -1.0;
		float vy = -1.0;
		size_t i = 0;
		for (size_t y = 0; y < imageWidth; ++y) {
			for (size_t x = 0; x < imageWidth; ++x) {
				v.at<float>(i, 0) = vx;
				v.at<float>(i, 1) = vy;
				++i;
				vx += increment;
			}
			vx = -1.0;
			vy += increment;
		}
		//std::cout << "M = " << std::endl << " " << cv::Formatter::get(cv::Formatter::FMT_PYTHON)->format(v) << std::endl << std::endl;
	}

	virtual size_t runKernel(cl::Program & kernel) {
		auto t = GetParam();
		auto kernelf = cl::KernelFunctor<cl::Buffer, cl::Buffer>(kernel, t.kernel);
		size_t imageWidth = 4, imageHeight = imageWidth, imageSize = imageWidth * imageHeight;
		cv::Mat input = cv::Mat::zeros(imageSize, 2, CV_32F);
    	fill2dSpace(input, imageWidth);
    	cl::Buffer inputBuffer(input.begin<float>(), input.end<float>(), false);
		auto output = std::make_shared<std::vector<float>>(imageSize, 0.0f);
	    cl::Buffer outputBuffer(std::begin(*output), std::end(*output), false);
	    cl::DeviceCommandQueue defaultDeviceQueue;
	    this->output = output;
	    this->outputBuffer = std::make_shared<cl::Buffer>(outputBuffer);
	    defaultDeviceQueue = cl::DeviceCommandQueue::makeDefault();
		logger->trace("Start kernel with size {}", imageSize);
		cl_int error;
		kernelf(
				cl::EnqueueArgs(cl::NDRange(imageSize)),
				inputBuffer,
				outputBuffer,
				error);
		logger->info("Created kernel error={}", error);
		return imageSize;
	}
};

TEST_P(value_noise2D_fixture, opencl_value_noise2D) {
	auto t = GetParam();
	for (int i = 0; i < t.expected.size(); ++i) {
		EXPECT_NEAR((*output)[i], t.expected[i], 0.00001);
	}
}

INSTANTIATE_TEST_SUITE_P(opencl, value_noise2D_fixture,
		Values(
				KernelContext { "value_noise2D_with_linearInterp_test",
						R"EOT(
kernel void value_noise2D_with_linearInterp_test(
global float2 *input,
global float *output) {
	int id = get_global_id(0);
	output[id] = value_noise2D(input[id], 200, noInterp);
}
)EOT",
						{
								0.152941, //
								0.152941, //
								-0.0352941, //
								-0.0352941, //
								0.152941, //
								0.152941, //
								-0.0352941, //
								-0.0352941, //
								-0.0274509, //
								-0.0274509, //
								0.333333, //
								0.333333, //
								-0.0274509, //
								-0.0274509, //
								0.333333, //
								0.333333 //
						} }, //
				KernelContext { "value_noise2D_with_linearInterp_test",
						R"EOT(
		kernel void value_noise2D_with_linearInterp_test(
		global float2 *input,
		global float *output) {
			int id = get_global_id(0);
			output[id] = value_noise2D(input[id], 200, linearInterp);
		}
		)EOT",
						{
								0.152941, //
								0.0588236, //
								-0.0352941, //
								0.403922, //
								0.0627452, //
								0.105882, //
								0.14902, //
								0.1, //
								-0.0274509, //
								0.152941, //
								0.333333, //
								-0.203922, //
								0.180392, //
								-0.0372549, //
								-0.254902, //
								-0.37451 //
						} }, //
				KernelContext { "value_noise2D_with_noInterp_test",
						R"EOT(
				kernel void value_noise2D_with_noInterp_test(
				global float2 *input,
				global float *output) {
					int id = get_global_id(0);
					output[id] = gradient_noise2D(input[id], 200, noInterp);
				}
				)EOT",
						{
								0, //
								-0.395285, //
								0, //
								-0.395285, //
								-0.158114, //
								-0.553399, //
								-0.158114, //
								-0.553399, //
								0, //
								0.158114, //
								0, //
								0.395285, //
								0.395285, //
								0.553399, //
								-0.158114, //
								0.237171, //
						} }
//
				));
