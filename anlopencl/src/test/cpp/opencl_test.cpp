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
 * Flag to run only this tests:
 * --gtest_filter="opencl_value_noise2D*"
 *
 *  Created on: Jul 27, 2021
 *      Author: Erwin Müller
 */

#include <memory>
#include <strings.h>
#include <bits/stdc++.h>

#define CL_HPP_ENABLE_EXCEPTIONS
#include <CL/opencl.hpp>

#include <spdlog/spdlog.h>
#include <spdlog/sinks/stdout_color_sinks.h>

#include <opencv2/core.hpp>
#include <opencv2/highgui.hpp>
#include <opencv2/core/mat.hpp>

#include "OpenCLTestFixture.h"
#include "imaging.h"

using ::testing::TestWithParam;
using ::testing::Values;
using ::spdlog::info;
using ::spdlog::error;

class value_noise2D_fixture: public OpenCL_Context_Fixture {
protected:

	void fill2dSpace(cv::Mat & v, size_t width) {
		std::vector<vector3> v3(width * width);
		map2D(v3.data(), calc_seamless_none, create_ranges_default(), width, width, 0);
		size_t i = 0;
		for (size_t x = 0; x < width; ++x) {
			for (size_t y = 0; y < width; ++y) {
				v.at<float>(i, 0) = v3[i].x;
				v.at<float>(i, 1) = v3[i].y;
				++i;
			}
		}
		//std::cout << "M = " << std::endl << " " << v << std::endl << std::endl;
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

	std::string mat_to_s(cv::Mat & m) {
	    std::string ms;
	    ms << m;
	    return ms;
	}
};

TEST_P(value_noise2D_fixture, opencl_value_noise2D) {
	auto t = GetParam();
	cv::Mat m = cv::Mat(cv::Size(t.imageWidth, t.imageHeight), CV_32F);
    float min = *std::min_element(output->begin(), output->end());
    float max = *std::max_element(output->begin(), output->end());
    scaleToRange(output->data(), output->size(), min, max, 0, 1);
    std::memcpy(m.data, output->data(), output->size() * sizeof(float));
    //logger->debug("min={}, max={}, m=\n{}", min, max, mat_to_s(m));
    std::string w = "Grey Image Vec Copy";
    cv::namedWindow(w, cv::WINDOW_NORMAL);
    cv::resizeWindow(w, 1024, 1024);
    cv::imshow(w, m);
    cv::waitKey(0);
    cv::destroyAllWindows();
}

const size_t size = pow(2, 10);

INSTANTIATE_TEST_SUITE_P(opencl_value_noise2D, value_noise2D_fixture,
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
