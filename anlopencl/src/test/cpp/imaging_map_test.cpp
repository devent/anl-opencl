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
 * imaging_map_test.cpp
 *
 * Flag to run only this tests:
 * --gtest_filter="imaging_map_test*"
 *
 *  Created on: Jul 26, 2021
 *      Author: Erwin Müller
 */

#include <gtest/gtest.h>
#include "imaging.h"

using ::testing::TestWithParam;
using ::testing::Values;

struct map2D_data {
	calc_seamless calc_seamless;
	struct SMappingRanges ranges;
	size_t width;
	size_t height;
	REAL z;
};

struct map2D_2_data {
	calc_seamless calc_seamless;
	struct SMappingRanges ranges;
	size_t width;
	size_t height;
	REAL z;
	std::vector<vector2> expected;
};

/*
 * ###
 * imaging_map_no_z_test-map2D_2_no_z_params
 * ###
 */

class map2D_2_no_z_params: public ::testing::TestWithParam<map2D_2_data> {
protected:
	std::vector<vector2> out;
	virtual void SetUp() {
		auto t = GetParam();
		out = std::vector<vector2>(t.width * t.height);
	}
};

TEST_P(map2D_2_no_z_params, no_z_seamless_none) {
	auto t = GetParam();
	map2DNoZ(out.data(), t.calc_seamless, t.ranges, t.width, t.height);
	for (int i = 0; i < out.size(); ++i) {
		printf("%f/%f\n", out[i].x, out[i].y);
		EXPECT_NEAR(out[i].x, t.expected[i].x, 0.00001);
		EXPECT_NEAR(out[i].y, t.expected[i].y, 0.00001);
	}
}

INSTANTIATE_TEST_SUITE_P(imaging_map_no_z_test, map2D_2_no_z_params,
		Values(
				map2D_2_data { calc_seamless_no_z_none, create_ranges_default(), 2, 2, 0, {
						(vector2){-1.000000,-1.000000},
						(vector2){0.000000,-1.000000},
						(vector2){-1.000000,0.000000},
						(vector2){0.000000,0.000000}
				} } //
				));

struct map2D_3_data {
	calc_seamless calc_seamless;
	struct SMappingRanges ranges;
	size_t width;
	size_t height;
	REAL z;
	std::vector<vector3> expected;
};

/*
 * ###
 * imaging_map_test-map2D_3_params
 * ###
 */

class map2D_3_params: public ::testing::TestWithParam<map2D_3_data> {
protected:
	std::vector<vector3> out;
	virtual void SetUp() {
		auto t = GetParam();
		out = std::vector<vector3>(t.width * t.height);
	}
};

TEST_P(map2D_3_params, seamless_none) {
	auto t = GetParam();
	map2D(out.data(), t.calc_seamless, t.ranges, t.width, t.height, t.z);
	for (int i = 0; i < out.size(); ++i) {
		EXPECT_NEAR(out[i].x, t.expected[i].x, 0.00001);
		EXPECT_NEAR(out[i].y, t.expected[i].y, 0.00001);
		EXPECT_NEAR(out[i].z, t.expected[i].z, 0.00001);
		printf("%f/%f/%f\n", out[i].x, out[i].y, out[i].z);
	}
}

INSTANTIATE_TEST_SUITE_P(imaging_map_test, map2D_3_params,
		Values(
				map2D_3_data { calc_seamless_none, create_ranges_default(), 2, 2, 99, {
						(vector3){-1.000000,-1.000000,99.00000},
						(vector3){0.000000,-1.000000,99.00000},
						(vector3){-1.000000,0.000000,99.00000},
						(vector3){0.000000,0.000000,99.00000}
				} } //
				));

/*
 * ###
 * imaging_map_no_z_test-map2D_no_z_3_params
 * ###
 */

class map2D_no_z_3_params: public ::testing::TestWithParam<map2D_3_data> {
protected:
	std::vector<vector3> out;
	virtual void SetUp() {
		auto t = GetParam();
		out = std::vector<vector3>(t.width * t.height);
	}
};

TEST_P(map2D_no_z_3_params, calc_seamless_no_z) {
	auto t = GetParam();
	map2DNoZ(out.data(), t.calc_seamless, t.ranges, t.width, t.height);
	for (int i = 0; i < out.size(); ++i) {
		printf("%f/%f/%f\n", out[i].x, out[i].y, out[i].z);
	}
	for (int i = 0; i < out.size(); ++i) {
		EXPECT_NEAR(out[i].x, t.expected[i].x, 0.00001);
		EXPECT_NEAR(out[i].y, t.expected[i].y, 0.00001);
		EXPECT_NEAR(out[i].z, t.expected[i].z, 0.00001);
	}
}

INSTANTIATE_TEST_SUITE_P(imaging_map_no_z_test, map2D_no_z_3_params,
		Values(
				map2D_3_data { calc_seamless_no_z_x, create_ranges_default(), 2, 2, 99, {
						(vector3){-0.681690,-1.000000,-1.000000},
						(vector3){-1.318310,-1.000000,-1.000000},
						(vector3){-0.681690,-1.000000,0.000000},
						(vector3){-1.318310,-1.000000,0.000000}
				} } //
));

class map2D_4_params: public ::testing::TestWithParam<map2D_data> {
protected:
	std::vector<vector4> out;
	virtual void SetUp() {
		auto t = GetParam();
		out = std::vector<vector4>(t.width * t.height);
	}
};

TEST_P(map2D_4_params, seamless_none) {
	auto t = GetParam();
	map2D(out.data(), t.calc_seamless, t.ranges, t.width, t.height, t.z);
	for (int i = 0; i < out.size(); ++i) {
		printf("%f/%f/%f/%f\n", out[i].x, out[i].y, out[i].z, out[i].w);
	}
}

INSTANTIATE_TEST_SUITE_P(imaging_map_test, map2D_4_params, Values(map2D_data {
		calc_seamless_x, create_ranges_default(), 2, 2, 99 }, //
		//
		map2D_data { calc_seamless_y, create_ranges_default(), 2, 2, 99 }, //
		//
		map2D_data { calc_seamless_z, create_ranges_default(), 2, 2, 99 } //
		));

class map2D_8_params: public ::testing::TestWithParam<map2D_data> {
protected:
	std::vector<vector8> out;
	virtual void SetUp() {
		auto t = GetParam();
		out = std::vector<vector8>(t.width * t.height);
	}
};

TEST_P(map2D_8_params, seamless_none) {
	auto t = GetParam();
	map2D(out.data(), t.calc_seamless, t.ranges, t.width, t.height, t.z);
	for (int i = 0; i < out.size(); ++i) {
		printf("%f/%f/%f/%f/%f/%f\n", out[i].x, out[i].y, out[i].z, out[i].w, out[i].s4, out[i].s5);
	}
}

INSTANTIATE_TEST_SUITE_P(imaging_map_test, map2D_8_params, Values(map2D_data {
		calc_seamless_xy, create_ranges_default(), 2, 2, 99 }, //
		//
		map2D_data { calc_seamless_xz, create_ranges_default(), 2, 2, 99 }, //
		//
		map2D_data { calc_seamless_yz, create_ranges_default(), 2, 2, 99 }, //
		//
		map2D_data { calc_seamless_xyz, create_ranges_default(), 2, 2, 99 } //
		));
