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
 * interpolation_functions_test.cpp
 *
 *  Created on: Jul 26, 2021
 *      Author: Erwin Müller
 */

#include <gtest/gtest.h>
#include "noise_gen.h"

using ::testing::TestWithParam;
using ::testing::Values;

struct Func2 {
	vector2 a;
	vector2 b;
	REAL y;
};

class distEuclid2_params: public ::testing::TestWithParam<Func2> {
};

TEST_P(distEuclid2_params, distEuclid2) {
	auto t = GetParam();
	EXPECT_NEAR(distEuclid2(t.a, t.b), t.y, 0.0001);
}

INSTANTIATE_TEST_SUITE_P(noise, distEuclid2_params,
		Values(
				Func2{vector2{0.000, 0.000}, vector2{0.000, 0.000}, 0.000},
				Func2{vector2{0.000, 0.000}, vector2{5.000, 5.000}, 7.071}
		));

class distManhattan2_params: public ::testing::TestWithParam<Func2> {
};

TEST_P(distManhattan2_params, distManhattan2) {
	auto t = GetParam();
	EXPECT_NEAR(distManhattan2(t.a, t.b), t.y, 0.0001);
}

INSTANTIATE_TEST_SUITE_P(noise, distManhattan2_params,
		Values(
				Func2{vector2{0.000, 0.000}, vector2{0.000, 0.000}, 0.000},
				Func2{vector2{0.000, 0.000}, vector2{5.000, 5.000}, 10.000}
		));

class distGreatestAxis2_params: public ::testing::TestWithParam<Func2> {
};

TEST_P(distGreatestAxis2_params, distGreatestAxis2) {
	auto t = GetParam();
	EXPECT_NEAR(distGreatestAxis2(t.a, t.b), t.y, 0.0001);
}

INSTANTIATE_TEST_SUITE_P(noise, distGreatestAxis2_params,
		Values(
				Func2{vector2{0.000, 0.000}, vector2{0.000, 0.000}, 0.000},
				Func2{vector2{0.000, 0.000}, vector2{5.000, 0.000}, 5.000},
				Func2{vector2{0.000, 0.000}, vector2{5.000, 5.000}, 5.000}
		));

class distLeastAxis2_params: public ::testing::TestWithParam<Func2> {
};

TEST_P(distLeastAxis2_params, distLeastAxis2) {
	auto t = GetParam();
	EXPECT_NEAR(distLeastAxis2(t.a, t.b), t.y, 0.0001);
}

INSTANTIATE_TEST_SUITE_P(noise, distLeastAxis2_params,
		Values(
				Func2{vector2{0.000, 0.000}, vector2{0.000, 0.000}, 0.000},
				Func2{vector2{0.000, 0.000}, vector2{0.000, 5.000}, 0.000},
				Func2{vector2{0.000, 0.000}, vector2{5.000, 5.000}, 5.000}
		));
