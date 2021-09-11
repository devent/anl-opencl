//
// Copyright (C) 2021 Erwin Müller <erwin@muellerpublic.de>
// Released as open-source under the Apache License, Version 2.0.
//
// ****************************************************************************
// ANL-OpenCL :: OpenCL
// ****************************************************************************
//
// Copyright (C) 2021 Erwin Müller <erwin@muellerpublic.de>
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//
// ****************************************************************************
// ANL-OpenCL :: OpenCL is a derivative work based on Josua Tippetts' C++ library:
// http://accidentalnoise.sourceforge.net/index.html
// ****************************************************************************
//
// Copyright (C) 2011 Joshua Tippetts
//
//   This software is provided 'as-is', without any express or implied
//   warranty.  In no event will the authors be held liable for any damages
//   arising from the use of this software.
//
//   Permission is granted to anyone to use this software for any purpose,
//   including commercial applications, and to alter it and redistribute it
//   freely, subject to the following restrictions:
//
//   1. The origin of this software must not be misrepresented; you must not
//      claim that you wrote the original software. If you use this software
//      in a product, an acknowledgment in the product documentation would be
//      appreciated but is not required.
//   2. Altered source versions must be plainly marked as such, and must not be
//      misrepresented as being the original software.
//   3. This notice may not be removed or altered from any source distribution.
//

/*
 * opencl_noise4D_functions_test.cpp
 *
 * Flag to run only this tests:
 * --gtest_filter="opencl_noise4D_functions_test*"
 *
 *  Created on: Auf 27, 2021
 *      Author: Erwin Müller
 */

#define CL_HPP_ENABLE_EXCEPTIONS
#include <CL/opencl.hpp>

#include "OpenCLTestFixture.h"
#include "imaging.h"

using ::testing::TestWithParam;
using ::testing::Values;
using ::spdlog::info;
using ::spdlog::error;

/**
 * Width x Height of the generated image.
 */
const size_t size = pow(2, 10);

/**
 * Output directory.
 */
const std::string out_dir = "out/noise_functions/";

/**
 * opencl_noise4D_functions_fixture
 */
class opencl_noise4D_functions_fixture: public OpenCL_Context_Buffer_Fixture {
protected:
	std::shared_ptr<std::vector<REAL>> input;
	std::shared_ptr<cl::Buffer> inputBuffer;

	void setupInput() {
		auto t = GetParam();
		input = createVector<REAL>(t.imageSize * dim_real4);
		map2D(input->data(), calc_seamless_x,
				create_ranges_map2D(-10, 10, -10, 10),
				t.imageWidth, t.imageHeight, 0);
		inputBuffer = createBufferPtr(input);
	}

	virtual std::shared_ptr<cl::Buffer> getInputBuffer() {
		return inputBuffer;
	}

	virtual size_t runKernel(cl::Program & kernel) {
		return commonRunKernel(kernel);
	}

};

/**
 * opencl_noise4D_functions_test-opencl_noise4D_functions_fixture-show_image
 */
TEST_P(opencl_noise4D_functions_fixture, show_image) {
	auto t = GetParam();
	showImageScaleToRange(output, GREY_CV);
}

/**
 * opencl_noise4D_functions_test-opencl_noise4D_functions_fixture-save_image
 */
TEST_P(opencl_noise4D_functions_fixture, save_image) {
	auto t = GetParam();
	std::stringstream ss;
	mkpath(out_dir.c_str());
	ss << out_dir << t.kernel << ".png";
	saveImageScaleToRange(ss.str(), output, GREY_CV);
}

/**
 * opencl_noise4D_functions_test-opencl_noise4D_functions_fixture
 */
INSTANTIATE_TEST_SUITE_P(opencl_noise4D_functions_test, opencl_noise4D_functions_fixture,
		Values(
KernelContext("value_noise4D_noInterp", readFile("src/test/cpp/kernels/noise4D_functions.cl"), size), //
KernelContext("value_noise4D_linearInterp", readFile("src/test/cpp/kernels/noise4D_functions.cl"), size), //
KernelContext("value_noise4D_hermiteInterp", readFile("src/test/cpp/kernels/noise4D_functions.cl"), size), //
KernelContext("value_noise4D_quinticInterp", readFile("src/test/cpp/kernels/noise4D_functions.cl"), size), //
//
KernelContext("gradient_noise4D_noInterp", readFile("src/test/cpp/kernels/noise4D_functions.cl"), size), //
KernelContext("gradient_noise4D_linearInterp", readFile("src/test/cpp/kernels/noise4D_functions.cl"), size), //
KernelContext("gradient_noise4D_hermiteInterp", readFile("src/test/cpp/kernels/noise4D_functions.cl"), size), //
KernelContext("gradient_noise4D_quinticInterp", readFile("src/test/cpp/kernels/noise4D_functions.cl"), size), //
//
KernelContext("gradval_noise4D_noInterp", readFile("src/test/cpp/kernels/noise4D_functions.cl"), size), //
KernelContext("gradval_noise4D_linearInterp", readFile("src/test/cpp/kernels/noise4D_functions.cl"), size), //
KernelContext("gradval_noise4D_hermiteInterp", readFile("src/test/cpp/kernels/noise4D_functions.cl"), size), //
KernelContext("gradval_noise4D_quinticInterp", readFile("src/test/cpp/kernels/noise4D_functions.cl"), size), //
//
KernelContext("white_noise4D_noInterp", readFile("src/test/cpp/kernels/noise4D_functions.cl"), size), //
//
KernelContext("simplex_noise4D_noInterp", readFile("src/test/cpp/kernels/noise4D_functions.cl"), size) //
));
