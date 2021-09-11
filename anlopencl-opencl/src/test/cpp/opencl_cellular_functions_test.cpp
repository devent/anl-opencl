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
 * opencl_cellular_functions_test.cpp
 *
 * Flag to run only this tests:
 * --gtest_filter="opencl_cellular_functions*D_test*"
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
const std::string out_dir = "out/cellular_functions/";

/**
 * opencl_cellular_function2D_fixture
 */
class opencl_cellular_function2D_fixture: public OpenCL_Context_Buffer_Fixture {
protected:
	std::shared_ptr<std::vector<REAL>> input;
	std::shared_ptr<cl::Buffer> inputBuffer;

	void setupInput() {
		auto t = GetParam();
		input = createVector<REAL>(t.imageSize * dim_real2);
		map2DNoZ(input->data(),
				calc_seamless_no_z_none,
				create_ranges_map2D(-10, 10, -10, 10),
				t.imageWidth, t.imageHeight);
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
 * opencl_cellular_function2D_test-opencl_cellular_function2D_fixture-show_image
 */
TEST_P(opencl_cellular_function2D_fixture, show_image) {
	auto t = GetParam();
	showImageScaleToRange(output, GREY_CV);
}

/**
 * opencl_cellular_function2D_test-opencl_cellular_function2D_fixture-save_image
 */
TEST_P(opencl_cellular_function2D_fixture, save_image) {
	auto t = GetParam();
	std::stringstream ss;
	mkpath(out_dir.c_str());
	ss << out_dir << t.kernel << ".png";
	saveImageScaleToRange(ss.str(), output, GREY_CV);
}

/**
 * opencl_cellular_function2D_test-opencl_cellular_function2D_fixture
 */
INSTANTIATE_TEST_SUITE_P(opencl_cellular_function2D_test, opencl_cellular_function2D_fixture,
		Values(
KernelContext("cellular_function2D_distEuclid", readFile("src/test/cpp/kernels/cellular_functions2D.cl"), size), //
KernelContext("cellular_function2D_distManhattan", readFile("src/test/cpp/kernels/cellular_functions2D.cl"), size), //
KernelContext("cellular_function2D_distGreatestAxis", readFile("src/test/cpp/kernels/cellular_functions2D.cl"), size), //
KernelContext("cellular_function2D_distLeastAxis", readFile("src/test/cpp/kernels/cellular_functions2D.cl"), size) //
));

/**
 * opencl_cellular_function3D_fixture
 */
class opencl_cellular_function3D_fixture: public OpenCL_Context_Buffer_Fixture {
protected:
	std::shared_ptr<std::vector<REAL>> input;
	std::shared_ptr<cl::Buffer> inputBuffer;
	void setupInput() {
		auto t = GetParam();
		input = createVector<REAL>(t.imageSize * dim_real3);
		map2D(input->data(),
				calc_seamless_none,
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
 * opencl_cellular_function3D_test-opencl_cellular_function3D_fixture-show_image
 */
TEST_P(opencl_cellular_function3D_fixture, show_image) {
	auto t = GetParam();
	showImageScaleToRange(output, GREY_CV);
}

/**
 * opencl_cellular_function3D_test-opencl_cellular_function3D_fixture-save_image
 */
TEST_P(opencl_cellular_function3D_fixture, save_image) {
	auto t = GetParam();
	std::stringstream ss;
	mkpath(out_dir.c_str());
	ss << out_dir << t.kernel << ".png";
	saveImageScaleToRange(ss.str(), output, GREY_CV);
}

/**
 * opencl_cellular_function3D_test-opencl_cellular_function3D_fixture
 */
INSTANTIATE_TEST_SUITE_P(opencl_cellular_function3D_test, opencl_cellular_function3D_fixture,
		Values(
KernelContext("cellular_function3D_distEuclid", readFile("src/test/cpp/kernels/cellular_functions3D.cl"), size), //
KernelContext("cellular_function3D_distManhattan", readFile("src/test/cpp/kernels/cellular_functions3D.cl"), size), //
KernelContext("cellular_function3D_distGreatestAxis", readFile("src/test/cpp/kernels/cellular_functions3D.cl"), size), //
KernelContext("cellular_function3D_distLeastAxis", readFile("src/test/cpp/kernels/cellular_functions3D.cl"), size) //
));

/**
 * opencl_cellular_function4D_fixture
 */
class opencl_cellular_function4D_fixture: public OpenCL_Context_Buffer_Fixture {
protected:
	std::shared_ptr<std::vector<REAL>> input;
	std::shared_ptr<cl::Buffer> inputBuffer;
	void setupInput() {
		auto t = GetParam();
		input = createVector<REAL>(t.imageSize * dim_real4);
		map2D(input->data(),
				calc_seamless_x,
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
 * opencl_cellular_function4D_test-opencl_cellular_function4D_fixture-show_image
 */
TEST_P(opencl_cellular_function4D_fixture, show_image) {
	auto t = GetParam();
	showImageScaleToRange(output, GREY_CV);
}

/**
 * opencl_cellular_function4D_test-opencl_cellular_function4D_fixture-save_image
 */
TEST_P(opencl_cellular_function4D_fixture, save_image) {
	auto t = GetParam();
	std::stringstream ss;
	mkpath(out_dir.c_str());
	ss << out_dir << t.kernel << ".png";
	saveImageScaleToRange(ss.str(), output, GREY_CV);
}

/**
 * opencl_cellular_function4D_test-opencl_cellular_function4D_fixture
 */
INSTANTIATE_TEST_SUITE_P(opencl_cellular_function4D_test, opencl_cellular_function4D_fixture,
		Values(
KernelContext("cellular_function4D_distEuclid", readFile("src/test/cpp/kernels/cellular_functions4D.cl"), size), //
KernelContext("cellular_function4D_distManhattan", readFile("src/test/cpp/kernels/cellular_functions4D.cl"), size), //
KernelContext("cellular_function4D_distGreatestAxis", readFile("src/test/cpp/kernels/cellular_functions4D.cl"), size), //
KernelContext("cellular_function4D_distLeastAxis", readFile("src/test/cpp/kernels/cellular_functions4D.cl"), size) //
));

/**
 * opencl_cellular_function6D_fixture
 */
class opencl_cellular_function6D_fixture: public OpenCL_Context_Buffer_Fixture {
protected:
	std::shared_ptr<std::vector<REAL>> input;
	std::shared_ptr<cl::Buffer> inputBuffer;

	void setupInput() {
		auto t = GetParam();
		input = createVector<REAL>(t.imageSize * dim_real8);
		map2D(input->data(),
				calc_seamless_xyz,
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
 * opencl_cellular_function6D_test-opencl_cellular_function6D_fixture-show_image
 */
TEST_P(opencl_cellular_function6D_fixture, show_image) {
	auto t = GetParam();
	showImageScaleToRange(output, GREY_CV);
}

/**
 * opencl_cellular_function6D_test-opencl_cellular_function6D_fixture-save_image
 */
TEST_P(opencl_cellular_function6D_fixture, save_image) {
	auto t = GetParam();
	std::stringstream ss;
	mkpath(out_dir.c_str());
	ss << out_dir << t.kernel << ".png";
	saveImageScaleToRange(ss.str(), output, GREY_CV);
}

/**
 * opencl_cellular_function6D_test-opencl_cellular_function6D_fixture
 */
INSTANTIATE_TEST_SUITE_P(opencl_cellular_function6D_test, opencl_cellular_function6D_fixture,
		Values(
KernelContext("cellular_function6D_distEuclid", readFile("src/test/cpp/kernels/cellular_functions6D.cl"), size), //
KernelContext("cellular_function6D_distManhattan", readFile("src/test/cpp/kernels/cellular_functions6D.cl"), size), //
KernelContext("cellular_function6D_distGreatestAxis", readFile("src/test/cpp/kernels/cellular_functions6D.cl"), size), //
KernelContext("cellular_function6D_distLeastAxis", readFile("src/test/cpp/kernels/cellular_functions6D.cl"), size) //
));
