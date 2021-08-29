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
 * noise_functions_test.cpp
 *
 * Flag to run only this tests:
 * --gtest_filter="noise_gen_noise*D_test*"
 *
 *  Created on: Jul 26, 2021
 *      Author: Erwin Müller
 */

#include <gtest/gtest.h>
#include "noise_gen.h"

using ::testing::TestWithParam;
using ::testing::Values;

struct Func2D {
	vector2 v;
	uint seed;
	noise_func2 noise_func;
	interp_func interp;
	REAL y;
};

class noise_func2_params: public ::testing::TestWithParam<Func2D> {
};

TEST_P(noise_func2_params, value_noise2D) {
	auto t = GetParam();
	EXPECT_NEAR(t.noise_func(t.v, t.seed, t.interp), t.y, 0.00001);
}

INSTANTIATE_TEST_SUITE_P(noise_gen_noise2D_test, noise_func2_params, Values( //
		Func2D { (vector2){ -1.0, -1.0 }, 200, value_noise2D, noInterp, -0.27843 }, //
		Func2D { (vector2){ 0.0, 0.0 }, 200, value_noise2D, noInterp, 0.15294 }, //
		//
		Func2D { (vector2){ -1.0, -1.0 }, 200, value_noise2D, linearInterp, 0.15294 }, //
		Func2D { (vector2){ 0.0, 0.0 }, 200, value_noise2D, linearInterp, 0.33333 }, //
		//
		Func2D { (vector2){ -1.0, -1.0 }, 200, value_noise2D, hermiteInterp, 0.15294 }, //
		Func2D { (vector2){ 0.0, 0.0 }, 200, value_noise2D, hermiteInterp, 0.33333 }, //
		//
		Func2D { (vector2){ -1.0, -1.0 }, 200, value_noise2D, quinticInterp, 0.15294 }, //
		Func2D { (vector2){ 0.0, 0.0 }, 200, value_noise2D, quinticInterp, 0.33333 }, //
		//
		Func2D { (vector2){ -1.0, -1.0 }, 200, gradient_noise2D, noInterp, 1.10679 }, //
		//
		Func2D { (vector2){ -1.0, -1.0 }, 200, gradval_noise2D, noInterp, 0.82836 }, //
		//
		Func2D { (vector2){ -1.0, -1.0 }, 200, white_noise2D, noInterp, 1.00000 }, //
		//
		Func2D { (vector2){ -1.0, -1.0 }, 200, simplex_noise2D, noInterp, 0.29398 }, //
		Func2D { (vector2){ 0.0, 0.0 }, 200, simplex_noise2D, noInterp, -0.00000 } //
));

struct Func3D {
	vector3 v;
	uint seed;
	noise_func3 noise_func;
	interp_func interp;
	REAL y;
};

class noise_func3_params: public ::testing::TestWithParam<Func3D> {
};

TEST_P(noise_func3_params, value_noise3D) {
	auto t = GetParam();
	EXPECT_NEAR(t.noise_func(t.v, t.seed, t.interp), t.y, 0.00001);
}

INSTANTIATE_TEST_SUITE_P(noise_gen_noise3D_test, noise_func3_params, Values( //
		Func3D { (vector3){ -1.0, -1.0, 0.0 }, 200, value_noise3D, noInterp, -0.82745 }, // 0
		Func3D { (vector3){ -1.0, -0.5, 0.0 }, 200, value_noise3D, noInterp, 0.56078 }, //
		Func3D { (vector3){ -1.0, 0.0, 0.0 }, 200, value_noise3D, noInterp, 0.56078 }, //
		Func3D { (vector3){ -1.0, 0.5, 0.0 }, 200, value_noise3D, noInterp, 0.92157 }, //
		Func3D { (vector3){ -0.5, -1.0, 0.0 }, 200, value_noise3D, noInterp, 0.25490 }, //
		Func3D { (vector3){ -0.5, -0.5, 0.0 }, 200, value_noise3D, noInterp, 0.88235 }, //
		Func3D { (vector3){ -0.5, 0.0, 0.0 }, 200, value_noise3D, noInterp, 0.88235 }, //
		Func3D { (vector3){ -0.5, 0.5, 0.0 }, 200, value_noise3D, noInterp, 0.98431 }, //
		Func3D { (vector3){ 0.0, -1.0, 0.0 }, 200, value_noise3D, noInterp, 0.25490 }, //
		Func3D { (vector3){ 0.0, -0.5, 0.0 }, 200, value_noise3D, noInterp, 0.88235 }, //
		Func3D { (vector3){ 0.0, 0.0, 0.0 }, 200, value_noise3D, noInterp, 0.88235 }, // 10
		Func3D { (vector3){ 0.0, 0.5, 0.0 }, 200, value_noise3D, noInterp, 0.98431 }, //
		Func3D { (vector3){ 0.5, -1.0, 0.0 }, 200, value_noise3D, noInterp, 0.80392 }, //
		Func3D { (vector3){ 0.5, -0.5, 0.0 }, 200, value_noise3D, noInterp, -0.40392 }, //
		Func3D { (vector3){ 0.5, 0.0, 0.0 }, 200, value_noise3D, noInterp, -0.40392 }, //
		Func3D { (vector3){ 0.5, 0.5, 0.0 }, 200, value_noise3D, noInterp, 0.82745 }, // 15
		//
		Func3D { (vector3){ -1.0, -1.0, 0.0 }, 200, value_noise3D, linearInterp, -0.47451 }, //
		Func3D { (vector3){ 0.0, 0.0, 0.0 }, 200, value_noise3D, linearInterp, 0.28627 }, //
		Func3D { (vector3){ -1.0, -1.0, 0.0 }, 200, value_noise3D, hermiteInterp, -0.47451 }, //
		Func3D { (vector3){ 0.0, 0.0, 0.0 }, 200, value_noise3D, hermiteInterp, 0.28627 }, //
		Func3D { (vector3){ -1.0, -1.0, 0.0 }, 200, value_noise3D, quinticInterp, -0.47451 }, // 20
		Func3D { (vector3){ 0.0, 0.0, 0.0 }, 200, value_noise3D, quinticInterp, 0.28627 }, //
		//
		Func3D { (vector3){ -1.0, -1.0, 0.0 }, 200, gradient_noise3D, noInterp, -3.00000 }, //
		//
		Func3D { (vector3){ -1.0, -1.0, 0.0 }, 200, gradval_noise3D, noInterp, 0.82836 }, //
		//
		Func3D { (vector3){ -1.0, -1.0, 0.0 }, 200, white_noise3D, noInterp, 1.00000 }, //
		//
		Func3D { (vector3){ -1.0, -1.0, 0.0 }, 200, simplex_noise3D, noInterp, -3.94705 }, //
		Func3D { (vector3){ 0.0, 0.0, 0.0 }, 200, simplex_noise3D, noInterp, 0.00032 } //
));

struct Func4D {
	vector4 v;
	uint seed;
	noise_func4 noise_func;
	interp_func interp;
	REAL y;
};

class noise_func4_params: public ::testing::TestWithParam<Func4D> {
};

TEST_P(noise_func4_params, value_noise4D) {
	auto t = GetParam();
	EXPECT_NEAR(t.noise_func(t.v, t.seed, t.interp), t.y, 0.00001);
}

INSTANTIATE_TEST_SUITE_P(noise_gen_noise4D_test, noise_func4_params, Values( //
		Func4D { (vector4){ -1.0, -1.0, -1.0, 0.0 }, 200, value_noise4D, noInterp, 0.76471 } // 0
));

struct Func6D {
	vector8 v;
	uint seed;
	noise_func6 noise_func;
	interp_func interp;
	REAL y;
};

class noise_func6_params: public ::testing::TestWithParam<Func6D> {
};

TEST_P(noise_func6_params, value_noise6D) {
	auto t = GetParam();
	EXPECT_NEAR(t.noise_func(t.v, t.seed, t.interp), t.y, 0.00001);
}

INSTANTIATE_TEST_SUITE_P(noise_gen_noise6D_test, noise_func6_params, Values( //
		Func6D { (vector8){ -1.0, -1.0, -1.0, -1.0, -1.0, 0.0 }, 200, value_noise6D, noInterp, 0.81961 } // 0
));
