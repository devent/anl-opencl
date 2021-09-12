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
 * kernel_rotateDomain_test.cpp
 *
 * Flag to run only this tests:
 * --gtest_filter="kernel_rotateDomain*_test*"
 *
 *  Created on: Auf 13, 2021
 *      Author: Erwin Müller
 */

#include <gtest/gtest.h>
#include "utility.h"
#include "imaging.h"
#include "kernel.h"

using ::testing::TestWithParam;
using ::testing::Values;

// ####################################
// kernel_rotateDomain3
// ####################################

struct kernel_rotateDomain3_data {
	size_t count;
	REAL angle;
	REAL ax;
	REAL ay;
	REAL az;
	std::vector<vector3> expected;
};

class kernel_rotateDomain3_param: public ::testing::TestWithParam<kernel_rotateDomain3_data> {
protected:
	std::vector<vector3> data;
	virtual void SetUp() {
		auto t = GetParam();
		data = std::vector<vector3>(t.count);
		// -1.000000/-1.000000/0.000000
		// 0.000000/-1.000000/0.000000
		// -1.000000/0.000000/0.000000
		// 0.000000/0.000000/0.000000
		map2D(data.data(), calc_seamless_none, create_ranges_default(), t.count / 2, t.count / 2, 0);
		printf("Before data:\n");
		for (int i = 0; i < t.count; ++i) {
			printf("%f/%f/%f\n", data[i].x, data[i].y, data[i].z);
		}
		printf("##\n");
	}
};

TEST_P(kernel_rotateDomain3_param, rotateDomain3) {
	auto t = GetParam();
	auto after = std::vector<vector3>(t.count);
	for (int i = 0; i < t.count; ++i) {
		after[i] = rotateDomain3(data.data()[i], t.angle, t.ax, t.ay, t.az);
	}
	printf("After data:\n");
	for (int i = 0; i < after.size(); ++i) {
		printf("%f/%f/%f\n", after[i].x, after[i].y, after[i].z);
	}
	printf("##\n");
	for (int i = 0; i < after.size(); ++i) {
		ASSERT_NEAR(after[i].x, t.expected[i].x, 0.00001);
		ASSERT_NEAR(after[i].y, t.expected[i].y, 0.00001);
		ASSERT_NEAR(after[i].z, t.expected[i].z, 0.00001);
	}
}

INSTANTIATE_TEST_SUITE_P(kernel_rotateDomain3_test, kernel_rotateDomain3_param,
		Values(
				kernel_rotateDomain3_data { 4, 0, 1, 0, 0, {
						(vector3){ -1, -1, 0 },
						(vector3){ 0, -1, 0 },
						(vector3){ -1, 0, 0 },
						(vector3){ 0, 0, 0 }
					} //
				}, //
				kernel_rotateDomain3_data { 4, 1.57, 1, 0, 0, {
						(vector3){ -1, -0.000796, -1 },
						(vector3){ 0.000000, -0.000796, -1.000000 },
						(vector3){ -1.000000, 0.000000, 0.000000 },
						(vector3){ 0, 0, 0 }
					}
				} //
));

// ####################################
// kernel_rotateDomain4
// ####################################

struct kernel_rotateDomain4_data {
	size_t count;
	REAL angle;
	REAL ax;
	REAL ay;
	REAL az;
	std::vector<vector4> expected;
};

class kernel_rotateDomain4_param: public ::testing::TestWithParam<kernel_rotateDomain4_data> {
protected:
	std::vector<vector4> data;
	virtual void SetUp() {
		auto t = GetParam();
		data = std::vector<vector4>(t.count);
		// -0.681690/-1.000000/-1.000000/99.000000
		// -1.318310/-1.000000/-1.000000/99.000000
		// -0.681690/-1.000000/0.000000/99.000000
		// -1.318310/-1.000000/0.000000/99.000000
		map2D(data.data(), calc_seamless_x, create_ranges_default(), t.count / 2, t.count / 2, 99);
		printf("Before data:\n");
		for (int i = 0; i < t.count; ++i) {
			printf("%f/%f/%f/%f\n", data[i].x, data[i].y, data[i].z, data[i].w);
		}
		printf("##\n");
	}
};

TEST_P(kernel_rotateDomain4_param, rotateDomain4) {
	auto t = GetParam();
	auto after = std::vector<vector4>(t.count);
	for (int i = 0; i < t.count; ++i) {
		after[i] = rotateDomain4(data.data()[i], t.angle, t.ax, t.ay, t.az);
	}
	printf("After data:\n");
	for (int i = 0; i < after.size(); ++i) {
		printf("%f/%f/%f\n", after[i].x, after[i].y, after[i].z);
	}
	printf("##\n");
	for (int i = 0; i < after.size(); ++i) {
		ASSERT_NEAR(after[i].x, t.expected[i].x, 0.00001);
		ASSERT_NEAR(after[i].y, t.expected[i].y, 0.00001);
		ASSERT_NEAR(after[i].z, t.expected[i].z, 0.00001);
		ASSERT_NEAR(after[i].w, t.expected[i].w, 0.00001);
	}
}

INSTANTIATE_TEST_SUITE_P(kernel_rotateDomain4_test, kernel_rotateDomain4_param,
		Values(
				kernel_rotateDomain4_data { 4, 0, 1, 0, 0, {
						(vector4){ -1, -1, 0, 99 },
						(vector4){ 0, -1, 0, 99 },
						(vector4){ -1, 0, 0, 99 },
						(vector4){ 0, 0, 0, 99 }
					} //
				}, //
				kernel_rotateDomain4_data { 4, 1.57, 1, 0, 0, {
						(vector4){ -1, -0.000796, -1, 99 },
						(vector4){ 0.000000, -0.000796, -1.000000, 99 },
						(vector4){ -1.000000, 0.000000, 0.000000, 99 },
						(vector4){ 0, 0, 0, 99 }
					}
				} //
));

