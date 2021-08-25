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
 * opencl_map2D_test.cpp
 *
 * Flag to run only this tests:
 * --gtest_filter="opencl_map2D_test*"
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

#include "OpenCLTestFixture.h"
#include "opencl_utils.h"
#include "imaging.h"

using ::testing::TestWithParam;
using ::testing::Values;
using ::spdlog::info;
using ::spdlog::error;

class value_map2D_default_fixture: public OpenCL_Context_Buffer_Fixture {
protected:

	const size_t channels = 3;

	std::shared_ptr<std::vector<float>> coord;

	std::shared_ptr<cl::Buffer> coordBuffer;

	virtual size_t runKernel(cl::Program & kernel) {
		auto t = GetParam();
		auto kernelf = cl::KernelFunctor<
				cl::Buffer,
				int, // sizeWith
				int, // sizeHeight
				float, // z
				float, // sx0
				float, // sx1
				float, // sy0
				float, // sy1
				cl::LocalSpaceArg
				>(kernel, t.kernel);
		output = createVector<float>(t.imageSize * channels);
	    outputBuffer = createBufferPtr(output);
	    coord = createVector<float>(t.imageSize * dim_float3);
	    coordBuffer = createBufferPtr(coord);
	    cl::DeviceCommandQueue defaultDeviceQueue;
	    defaultDeviceQueue = cl::DeviceCommandQueue::makeDefault();
		logger->trace("Start kernel with size {}", t.imageSize);
		cl_int error;
		kernelf(
				cl::EnqueueArgs(cl::NDRange(t.imageSize), cl::NDRange(4)),
				*outputBuffer,
				t.imageWidth,
				t.imageHeight,
				0,
				-10,
				10,
				-10,
				10,
				cl::Local(sizeof(float) * 3 * 4),
				error);
		logger->info("Created kernel error={}", error);
		return t.imageSize;
	}

};

TEST_P(value_map2D_default_fixture, show_image) {
	auto t = GetParam();
	showImage(output, CV_32FC3);
}

const size_t size = pow(2, 3);

INSTANTIATE_TEST_SUITE_P(opencl_map2D_test, value_map2D_default_fixture,
		Values(
				KernelContext("opencl_map2D_default_test", readFile("src/test/cpp/kernels/opencl_map2D_test.cl"), size) //
						));
