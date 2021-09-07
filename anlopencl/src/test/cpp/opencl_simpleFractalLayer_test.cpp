//
// Copyright (C) 2021 Erwin Müller <erwin@muellerpublic.de>
// Released as open-source under the Apache License, Version 2.0.
//
// ****************************************************************************
// ANL-OpenCL :: Core
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
// ANL-OpenCL :: Core is a derivative work based on Josua Tippetts' C++ library:
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
 * opencl_simpleFractalLayer_test.cpp
 *
 * Flag to run only this tests:
 * --gtest_filter="opencl_simpleFractalLayer_test*"
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

class opencl_simpleFractalLayer_fixture: public OpenCL_Context_Buffer_Fixture {
protected:

	std::shared_ptr<std::vector<REAL>> input;

	std::shared_ptr<cl::Buffer> inputBuffer;

	virtual size_t runKernel(cl::Program & kernel) {
		auto t = GetParam();
		auto kernelf = cl::KernelFunctor<cl::Buffer, cl::Buffer>(kernel, t.kernel);
		input = createVector<REAL>(t.imageSize * dim_real3);
		map2D(input->data(), calc_seamless_none,
				create_ranges_map2D(-10, 10, -10, 10),
				t.imageWidth, t.imageHeight, 10);
		inputBuffer = createBufferPtr(input);
		output = createVector<REAL>(t.imageSize);
		outputBuffer = createBufferPtr(output);
	    cl::DeviceCommandQueue defaultDeviceQueue;
	    defaultDeviceQueue = cl::DeviceCommandQueue::makeDefault();
		logger->trace("Start kernel with size {}", t.imageSize);
		cl_int error;
		kernelf(
				cl::EnqueueArgs(cl::NDRange(t.imageSize)),
				*inputBuffer,
				*outputBuffer,
				error);
		logger->info("Created kernel error={}", error);
		return t.imageSize;
	}

};

TEST_P(opencl_simpleFractalLayer_fixture, show_image) {
	auto t = GetParam();
	showImageScaleToRange(output, GREY_CV);
}

TEST_P(opencl_simpleFractalLayer_fixture, save_image) {
	auto t = GetParam();
	std::stringstream ss;
	ss << "out/simpleFractalLayer3/" << t.kernel << ".png";
	saveImageScaleToRange(ss.str(), output, GREY_CV);
}

const size_t size = pow(2, 10);

INSTANTIATE_TEST_SUITE_P(opencl_simpleFractalLayer_test, opencl_simpleFractalLayer_fixture,
		Values(
			KernelContext("simpleFractalLayer3_value_noise3D_noInterp_norot", readFile("src/test/cpp/kernels/simpleFractalLayer3_noise_functions.cl"), size), //
			KernelContext("simpleFractalLayer3_value_noise3D_noInterp_rot", readFile("src/test/cpp/kernels/simpleFractalLayer3_noise_functions.cl"), size), //
			KernelContext("simpleFractalLayer3_value_noise3D_linearInterp_norot", readFile("src/test/cpp/kernels/simpleFractalLayer3_noise_functions.cl"), size), //
			KernelContext("simpleFractalLayer3_value_noise3D_linearInterp_rot", readFile("src/test/cpp/kernels/simpleFractalLayer3_noise_functions.cl"), size), //
			KernelContext("simpleFractalLayer3_value_noise3D_hermiteInterp_norot", readFile("src/test/cpp/kernels/simpleFractalLayer3_noise_functions.cl"), size), //
			KernelContext("simpleFractalLayer3_value_noise3D_hermiteInterp_rot", readFile("src/test/cpp/kernels/simpleFractalLayer3_noise_functions.cl"), size), //
			KernelContext("simpleFractalLayer3_value_noise3D_quinticInterp_norot", readFile("src/test/cpp/kernels/simpleFractalLayer3_noise_functions.cl"), size), //
			KernelContext("simpleFractalLayer3_value_noise3D_quinticInterp_rot", readFile("src/test/cpp/kernels/simpleFractalLayer3_noise_functions.cl"), size), //
			//
			KernelContext("simpleFractalLayer3_gradient_noise3D_noInterp_norot", readFile("src/test/cpp/kernels/simpleFractalLayer3_noise_functions.cl"), size), //
			KernelContext("simpleFractalLayer3_gradient_noise3D_noInterp_rot", readFile("src/test/cpp/kernels/simpleFractalLayer3_noise_functions.cl"), size), //
			KernelContext("simpleFractalLayer3_gradient_noise3D_linearInterp_norot", readFile("src/test/cpp/kernels/simpleFractalLayer3_noise_functions.cl"), size), //
			KernelContext("simpleFractalLayer3_gradient_noise3D_linearInterp_rot", readFile("src/test/cpp/kernels/simpleFractalLayer3_noise_functions.cl"), size), //
			KernelContext("simpleFractalLayer3_gradient_noise3D_hermiteInterp_norot", readFile("src/test/cpp/kernels/simpleFractalLayer3_noise_functions.cl"), size), //
			KernelContext("simpleFractalLayer3_gradient_noise3D_hermiteInterp_rot", readFile("src/test/cpp/kernels/simpleFractalLayer3_noise_functions.cl"), size), //
			KernelContext("simpleFractalLayer3_gradient_noise3D_quinticInterp_norot", readFile("src/test/cpp/kernels/simpleFractalLayer3_noise_functions.cl"), size), //
			KernelContext("simpleFractalLayer3_gradient_noise3D_quinticInterp_rot", readFile("src/test/cpp/kernels/simpleFractalLayer3_noise_functions.cl"), size), //
			//
			KernelContext("simpleFractalLayer3_gradval_noise3D_noInterp_norot", readFile("src/test/cpp/kernels/simpleFractalLayer3_noise_functions.cl"), size), //
			KernelContext("simpleFractalLayer3_gradval_noise3D_noInterp_rot", readFile("src/test/cpp/kernels/simpleFractalLayer3_noise_functions.cl"), size), //
			KernelContext("simpleFractalLayer3_gradval_noise3D_linearInterp_norot", readFile("src/test/cpp/kernels/simpleFractalLayer3_noise_functions.cl"), size), //
			KernelContext("simpleFractalLayer3_gradval_noise3D_linearInterp_rot", readFile("src/test/cpp/kernels/simpleFractalLayer3_noise_functions.cl"), size), //
			KernelContext("simpleFractalLayer3_gradval_noise3D_hermiteInterp_norot", readFile("src/test/cpp/kernels/simpleFractalLayer3_noise_functions.cl"), size), //
			KernelContext("simpleFractalLayer3_gradval_noise3D_hermiteInterp_rot", readFile("src/test/cpp/kernels/simpleFractalLayer3_noise_functions.cl"), size), //
			KernelContext("simpleFractalLayer3_gradval_noise3D_quinticInterp_norot", readFile("src/test/cpp/kernels/simpleFractalLayer3_noise_functions.cl"), size), //
			KernelContext("simpleFractalLayer3_gradval_noise3D_quinticInterp_rot", readFile("src/test/cpp/kernels/simpleFractalLayer3_noise_functions.cl"), size), //
			//
			KernelContext("simpleFractalLayer3_white_noise3D_noInterp_norot", readFile("src/test/cpp/kernels/simpleFractalLayer3_noise_functions.cl"), size), //
			KernelContext("simpleFractalLayer3_white_noise3D_noInterp_rot", readFile("src/test/cpp/kernels/simpleFractalLayer3_noise_functions.cl"), size), //
			//
			KernelContext("simpleFractalLayer3_simplex_noise3D_noInterp_norot", readFile("src/test/cpp/kernels/simpleFractalLayer3_noise_functions.cl"), size), //
			KernelContext("simpleFractalLayer3_simplex_noise3D_noInterp_rot", readFile("src/test/cpp/kernels/simpleFractalLayer3_noise_functions.cl"), size) //
));
