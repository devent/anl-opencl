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
 * opencl_simpleFractalLayer_test.cpp
 *
 * Flag to run only this tests:
 * --gtest_filter="opencl_simpleFractalLayer_test*"
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

class value_simpleFractalLayer_fixture: public OpenCL_Context_Fixture {
protected:

	/**
	 * float3 have 4 floats.
	 */
	const size_t dim = 4;

	std::string mat_to_s(cv::Mat & m) {
	    std::string ms;
	    ms << m;
	    return ms;
	}

	void fill2dSpace(std::vector<float> & v, size_t width) {
		std::vector<vector3> vv(width * width);
		map2D(vv.data(), calc_seamless_none, create_ranges_map2D(-10, 10, -10, 10), width, width, 0);
		size_t j = 0;
		for (size_t i = 0; i < width * width * dim; i += dim) {
			v[i] = vv[j].x;
			v[i + 1] = vv[j].y;
			v[i + 2] = vv[j].z;
			++j;
		}
	}

	virtual size_t runKernel(cl::Program & kernel) {
		auto t = GetParam();
		auto kernelf = cl::KernelFunctor<cl::Buffer, cl::Buffer>(kernel, t.kernel);
		auto input = std::vector<float>(t.imageSize * dim, 0.0f);
    	fill2dSpace(input, t.imageWidth);
    	cl::Buffer inputBuffer(std::begin(input), std::end(input), false);
    	this->output = std::make_shared<std::vector<float>>(t.imageSize, 0.0f);
		this->outputBuffer = std::make_shared<cl::Buffer>(std::begin(*output), std::end(*output), false);
	    cl::DeviceCommandQueue defaultDeviceQueue;
	    defaultDeviceQueue = cl::DeviceCommandQueue::makeDefault();
		logger->trace("Start kernel with size {}", t.imageSize);
		cl_int error;
		kernelf(
				cl::EnqueueArgs(cl::NDRange(t.imageSize)),
				inputBuffer,
				*outputBuffer,
				error);
		logger->info("Created kernel error={}", error);
		return t.imageSize;
	}

};

TEST_P(value_simpleFractalLayer_fixture, show_image) {
	auto t = GetParam();
	cv::Mat m = cv::Mat(cv::Size(t.imageWidth, t.imageHeight), CV_32F);
    float min = *std::min_element(output->begin(), output->end());
    float max = *std::max_element(output->begin(), output->end());
    scaleToRange(output->data(), output->size(), min, max, 0, 1);
    std::memcpy(m.data, output->data(), output->size() * sizeof(float));
//    logger->debug("min={}, max={}, m=\n{}", min, max, mat_to_s(m));
    std::string w = "Grey Image Vec Copy";
    cv::namedWindow(w, cv::WINDOW_NORMAL);
    cv::resizeWindow(w, 512, 512);
    cv::imshow(w, m);
    cv::waitKey(0);
    cv::destroyAllWindows();
}

const size_t size = pow(2, 10);

INSTANTIATE_TEST_SUITE_P(opencl_simpleFractalLayer_test, value_simpleFractalLayer_fixture,
		Values(
				KernelContext("simpleFractalLayer_with_value_noise3D_noInterp_norot_test",
						R"EOT(
kernel void simpleFractalLayer_with_value_noise3D_noInterp_norot_test(
global float3 *input,
global float *output) {
	int id = get_global_id(0);
	output[id] = simpleFractalLayer(input[id],
		value_noise3D, 200, noInterp,
		1, 0.125, false, 0.0, 0.0, 0.0, 0.0);
}
)EOT",
						size), //
				KernelContext("simpleFractalLayer_with_value_noise3D_noInterp_rot_test",
						R"EOT(
kernel void simpleFractalLayer_with_value_noise3D_noInterp_rot_test(
global float3 *input,
global float *output) {
	int id = get_global_id(0);
	output[id] = simpleFractalLayer(input[id],
		value_noise3D, 200, noInterp,
		1, 0.125, true, 1.57, 1.0, 0.0, 0.0);
}
)EOT",
						size) //
						));
