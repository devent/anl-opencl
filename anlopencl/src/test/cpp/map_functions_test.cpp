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
 * map_functions_test.cpp
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

class map2D_3_params: public ::testing::TestWithParam<map2D_data> {
protected:

	std::vector<vector3> out;

	virtual void SetUp() {
		auto t = GetParam();
		out = std::vector<vector3>(t.width * t.height);
	}

	virtual void TearDown() {
	}
};

TEST_P(map2D_3_params, seamless_none) {
	auto t = GetParam();
	map2D(out.data(), t.calc_seamless, t.ranges, t.width, t.height, t.z);
	for (int i = 0; i < out.size(); ++i) {
		printf("%f/%f/%f\n", out[i].x, out[i].y, out[i].z);
	}
}

INSTANTIATE_TEST_SUITE_P(map2D, map2D_3_params,
		Values(
				map2D_data { calc_seamless_none, create_range_default(), 2, 2, 99 } //
				));

class map2D_4_params: public ::testing::TestWithParam<map2D_data> {
protected:

	std::vector<vector4> out;

	virtual void SetUp() {
		auto t = GetParam();
		out = std::vector<vector4>(t.width * t.height);
	}

	virtual void TearDown() {
	}
};

TEST_P(map2D_4_params, seamless_none) {
	auto t = GetParam();
	map2D(out.data(), t.calc_seamless, t.ranges, t.width, t.height, t.z);
	for (int i = 0; i < out.size(); ++i) {
		printf("%f/%f/%f/%f\n", out[i].x, out[i].y, out[i].z, out[i].w);
	}
}

INSTANTIATE_TEST_SUITE_P(map2D, map2D_4_params, Values(map2D_data {
		calc_seamless_x, create_range_default(), 2, 2, 99 }, //
		//
		map2D_data { calc_seamless_y, create_range_default(), 2, 2, 99 }, //
		//
		map2D_data { calc_seamless_z, create_range_default(), 2, 2, 99 } //
		));

class map2D_8_params: public ::testing::TestWithParam<map2D_data> {
protected:

	std::vector<vector8> out;

	virtual void SetUp() {
		auto t = GetParam();
		out = std::vector<vector8>(t.width * t.height);
	}

	virtual void TearDown() {
	}
};

TEST_P(map2D_8_params, seamless_none) {
	auto t = GetParam();
	map2D(out.data(), t.calc_seamless, t.ranges, t.width, t.height, t.z);
	for (int i = 0; i < out.size(); ++i) {
		printf("%f/%f/%f/%f/%f/%f\n", out[i].x, out[i].y, out[i].z, out[i].w, out[i].s4, out[i].s5);
	}
}

INSTANTIATE_TEST_SUITE_P(map2D, map2D_8_params, Values(map2D_data {
		calc_seamless_xy, create_range_default(), 2, 2, 99 }, //
		//
		map2D_data { calc_seamless_xz, create_range_default(), 2, 2, 99 }, //
		//
		map2D_data { calc_seamless_yz, create_range_default(), 2, 2, 99 }, //
		//
		map2D_data { calc_seamless_xyz, create_range_default(), 2, 2, 99 } //
		));
