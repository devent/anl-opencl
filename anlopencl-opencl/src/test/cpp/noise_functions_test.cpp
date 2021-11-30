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
//
// ****************************************************************************
// ANL-OpenCL :: OpenCL bundles and uses the RandomCL library:
// https://github.com/bstatcomp/RandomCL
// ****************************************************************************
//
// BSD 3-Clause License
//
// Copyright (c) 2018, Tadej Ciglarič, Erik Štrumbelj, Rok Češnovar. All rights reserved.
//
// Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
//
// * Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
//
// * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
//
// * Neither the name of the copyright holder nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
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

/**
 * noise_gen_noise2D_test-noise_func2_params-noise2D
 * <ul>
 * <li>Func2D
 * <li>float2
 * <li>value_noise2D
 * <li>gradient_noise2D
 * <li>gradval_noise2D
 * <li>white_noise2D
 * <li>simplex_noise2D
 * </ul>
 */
TEST_P(noise_func2_params, noise2D) {
	auto t = GetParam();
	EXPECT_NEAR(t.noise_func(t.v, t.seed, t.interp), t.y, 0.00001);
}

/**
 * noise_gen_noise2D_test-noise_func2_params
 * <ul>
 * <li>Func2D
 * <li>float2
 * <li>value_noise2D
 * <li>gradient_noise2D
 * <li>gradval_noise2D
 * <li>white_noise2D
 * <li>simplex_noise2D
 * </ul>
 */
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

/**
 * noise_gen_noise3D_test-noise_func3_params-noise3D
 * <ul>
 * <li>Func3D
 * <li>float3
 * <li>value_noise3D
 * <li>gradient_noise3D
 * <li>gradval_noise3D
 * <li>white_noise3D
 * <li>simplex_noise3D
 * </ul>
 */
TEST_P(noise_func3_params, noise3D) {
	auto t = GetParam();
	EXPECT_NEAR(t.noise_func(t.v, t.seed, t.interp), t.y, 0.00001);
}

/**
 * noise_gen_noise3D_test-noise_func3_params
 * <ul>
 * <li>Func3D
 * <li>float3
 * <li>value_noise3D
 * <li>gradient_noise3D
 * <li>gradval_noise3D
 * <li>white_noise3D
 * <li>simplex_noise3D
 * </ul>
 */
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

/**
 * noise_gen_noise4D_test-noise_func4_params-noise4D
 * <ul>
 * <li>Func4D
 * <li>float4
 * <li>value_noise4D
 * <li>gradient_noise4D
 * <li>gradval_noise4D
 * <li>white_noise4D
 * <li>simplex_noise4D
 * </ul>
 */
TEST_P(noise_func4_params, value_noise4D) {
	auto t = GetParam();
	EXPECT_NEAR(t.noise_func(t.v, t.seed, t.interp), t.y, 0.00001);
}

/**
 * noise_gen_noise4D_test-noise_func4_params
 * <ul>
 * <li>Func4D
 * <li>float4
 * <li>value_noise4D
 * <li>gradient_noise4D
 * <li>gradval_noise4D
 * <li>white_noise4D
 * <li>simplex_noise4D
 * </ul>
 */
INSTANTIATE_TEST_SUITE_P(noise_gen_noise4D_test, noise_func4_params, Values( //
Func4D { (vector4){ -1.0, -1.0, -1.0, 0.0 }, 200, value_noise4D, noInterp, 0.76471 }, // 0
//
Func4D { (vector4){ -0.681690,-1.000000,-1.000000,0.000000 }, 200, simplex_noise4D, noInterp, 0.571490 }, // 1
Func4D { (vector4){ -0.681690,-1.000000,0.000000,0.000000 }, 200, simplex_noise4D, noInterp, -0.835860 }, // 2
Func4D { (vector4){ -1.318310,-1.000000,-1.000000,0.000000 }, 200, simplex_noise4D, noInterp, -0.361193 }, // 3
Func4D { (vector4){ -1.318310,-1.000000,0.000000,0.000000 }, 200, simplex_noise4D, noInterp, -0.572204 } // 4
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

/**
 * noise_gen_noise6D_test-noise_func6_params-noise6D
 * <ul>
 * <li>Func6D
 * <li>float6
 * <li>value_noise6D
 * <li>gradient_noise6D
 * <li>gradval_noise6D
 * <li>white_noise6D
 * <li>simplex_noise6D
 * </ul>
 */
TEST_P(noise_func6_params, value_noise6D) {
	auto t = GetParam();
	EXPECT_NEAR(t.noise_func(t.v, t.seed, t.interp), t.y, 0.00001);
}

/**
 * noise_gen_noise6D_test-noise_func6_params
 * <ul>
 * <li>Func6D
 * <li>float6
 * <li>value_noise6D
 * <li>gradient_noise6D
 * <li>gradval_noise6D
 * <li>white_noise6D
 * <li>simplex_noise6D
 * </ul>
 */
INSTANTIATE_TEST_SUITE_P(noise_gen_noise6D_test, noise_func6_params, Values( //
Func6D { (vector8){ -1.0, -1.0, -1.0, -1.0, -1.0, 0.0 }, 200, value_noise6D, noInterp, 0.81961 }, // 0
//
Func6D { (vector8){ -0.681690,-1.000000,-0.681690,-1.000000,-1.318310,-1.000000 }, 200, simplex_noise6D, noInterp, 0.007967 }, // 1
Func6D { (vector8){ -0.681690,-1.000000,-1.318310,-1.000000,-1.318310,-1.000000 }, 200, simplex_noise6D, noInterp, 0.043427 }, // 2
Func6D { (vector8){ -1.318310,-1.000000,-0.681690,-1.000000,-1.318310,-1.000000 }, 200, simplex_noise6D, noInterp, -0.144565 }, // 3
Func6D { (vector8){ -1.318310,-1.000000,-1.318310,-1.000000,-1.318310,-1.000000 }, 200, simplex_noise6D, noInterp, -0.125572 } // 4
));
