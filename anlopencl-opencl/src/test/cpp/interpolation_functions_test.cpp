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
 * interpolation_functions_test.cpp
 *
 *  Created on: Jul 26, 2021
 *      Author: Erwin Müller
 */

#include <gtest/gtest.h>
#include "noise_gen.h"

using ::testing::TestWithParam;
using ::testing::Values;

struct Func {
	REAL x;
	REAL y;
};

class noInterp_params: public ::testing::TestWithParam<REAL> {
};

TEST_P(noInterp_params, noInterp) {
	auto t = GetParam();
	EXPECT_EQ(noInterp(t), 0);
}

INSTANTIATE_TEST_SUITE_P(noise, noInterp_params,
		Values(0.000, 0.500, 1.000, 1.500));

class linearInterp_params: public ::testing::TestWithParam<REAL> {
};

TEST_P(linearInterp_params, linearInterp) {
	auto t = GetParam();
	EXPECT_EQ(linearInterp(t), t);
}

INSTANTIATE_TEST_SUITE_P(noise, linearInterp_params,
		Values(0.000, 0.500, 1.000, 1.500));

class hermiteInterp_params: public ::testing::TestWithParam<Func> {
};

TEST_P(hermiteInterp_params, hermiteInterp) {
	auto t = GetParam();
	EXPECT_EQ(hermiteInterp(t.x), t.y);
}

INSTANTIATE_TEST_SUITE_P(noise, hermiteInterp_params,
		Values(
				Func{0.000, 0.000},
				Func{0.500, 0.500},
				Func{1.000, 1.000},
				Func{1.500, 0.000}));

class quinticInterp_params: public ::testing::TestWithParam<Func> {
};

TEST_P(quinticInterp_params, hermiteInterp) {
	auto t = GetParam();
	EXPECT_EQ(quinticInterp(t.x), t.y);
}

INSTANTIATE_TEST_SUITE_P(noise, quinticInterp_params,
		Values(
				Func{0.000, 0.000},
				Func{0.500, 0.500},
				Func{1.000, 1.000},
				Func{1.500, 3.375}));
