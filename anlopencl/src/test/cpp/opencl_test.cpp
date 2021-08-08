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
#include <opencv2/highgui.hpp>
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
		cv::Mat input = cv::Mat::zeros(t.imageSize, 2, CV_32F);
    	fill2dSpace(input, t.imageWidth);
    	cl::Buffer inputBuffer(input.begin<float>(), input.end<float>(), false);
		auto output = std::make_shared<std::vector<float>>(t.imageSize, 0.0f);
	    cl::Buffer outputBuffer(std::begin(*output), std::end(*output), false);
	    cl::DeviceCommandQueue defaultDeviceQueue;
	    this->output = output;
	    this->outputBuffer = std::make_shared<cl::Buffer>(outputBuffer);
	    defaultDeviceQueue = cl::DeviceCommandQueue::makeDefault();
		logger->trace("Start kernel with size {}", t.imageSize);
		cl_int error;
		kernelf(
				cl::EnqueueArgs(cl::NDRange(t.imageSize)),
				inputBuffer,
				outputBuffer,
				error);
		logger->info("Created kernel error={}", error);
		return t.imageSize;
	}
};

TEST_P(value_noise2D_fixture, opencl_value_noise2D) {
	auto t = GetParam();
	cv::Mat m = cv::Mat(cv::Size(t.imageWidth, t.imageHeight), CV_32F);
    std::memcpy(m.data, output->data(), output->size() * sizeof(float));
    std::string w = "Grey Image Vec Copy";
    cv::namedWindow(w, cv::WINDOW_NORMAL);
    cv::resizeWindow(w, 1024, 1024);
    cv::imshow(w, m);
    cv::waitKey(0);
    cv::destroyAllWindows();
}

const size_t size = pow(2, 12);

INSTANTIATE_TEST_SUITE_P(opencl, value_noise2D_fixture,
		Values(
				KernelContext("value_noise2D_with_linearInterp_test",
						R"EOT(
kernel void value_noise2D_with_linearInterp_test(
global float2 *input,
global float *output) {
	int id = get_global_id(0);
	output[id] = value_noise2D(input[id], 200, noInterp);
}
)EOT",
						size), //
				KernelContext("value_noise2D_with_linearInterp_test",
						R"EOT(
		kernel void value_noise2D_with_linearInterp_test(
		global float2 *input,
		global float *output) {
			int id = get_global_id(0);
			output[id] = value_noise2D(input[id], 200, linearInterp);
		}
		)EOT",
						size), //
				KernelContext("value_noise2D_with_noInterp_test",
						R"EOT(
				kernel void value_noise2D_with_noInterp_test(
				global float2 *input,
				global float *output) {
					int id = get_global_id(0);
					output[id] = gradient_noise2D(input[id], 200, noInterp);
				}
				)EOT",
						size) //
						));
