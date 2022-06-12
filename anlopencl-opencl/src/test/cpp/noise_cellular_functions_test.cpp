//
// Copyright (C) 2021-2022 Erwin Müller <erwin@muellerpublic.de>
// Released as open-source under the Apache License, Version 2.0.
//
// ****************************************************************************
// ANL-OpenCL :: OpenCL
// ****************************************************************************
//
// Copyright (C) 2021-2022 Erwin Müller <erwin@muellerpublic.de>
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
 * --gtest_filter="noise_gen_cellular_noise*D_test*"
 *
 *  Created on: Jul 26, 2021
 *      Author: Erwin Müller
 */

#include <gtest/gtest.h>
#include "noise_gen.h"

using ::testing::TestWithParam;
using ::testing::Values;

/**
 * Cell2D
 */
struct Cell2D {
	vector2 v;
	uint seed;
	dist_func2 dist_func;
	REAL y;
};

/**
 * noise_cell_func2_params
 */
class noise_cell_func2_params: public ::testing::TestWithParam<Cell2D> {
protected:
	REAL f[4] = { 10, 5, 2.5, 1.25 };
	REAL disp[4] = { 100, 50, 25, 10 };
};

/**
 * noise_gen_cellular_noise2D_test-noise_cell_func2_params-cell2D
 * <ul>
 * <li>Cell2D
 * <li>cellular_function2D
 * </ul>
 */
TEST_P(noise_cell_func2_params, cell2D) {
	auto t = GetParam();
	EXPECT_NEAR(cellular_function2D(t.v, t.seed, f, disp, t.dist_func), t.y, 0.00001);
}

/**
 * noise_gen_cellular_noise2D_test-noise_cell_func2_params
 * <ul>
 * <li>Cell2D
 * <li>float2
 * <li>distEuclid2
 * <li>distManhattan2
 * <li>distGreatestAxis2
 * </ul>
 */
INSTANTIATE_TEST_SUITE_P(noise_gen_cellular_noise2D_test, noise_cell_func2_params, Values( //
Cell2D { (vector2){ -10.0, -10.0 }, 200, distEuclid2, 18.011899 }, // 0
Cell2D { (vector2){ -5.0, -10.0 }, 200, distEuclid2, 72.943490 }, //
Cell2D { (vector2){ 0.0, -10.0 }, 200, distEuclid2, -57.643821 }, //
Cell2D { (vector2){ 5.0, -10.0 }, 200, distEuclid2, 49.627965 }, //
Cell2D { (vector2){ -10.0, 5.0 }, 200, distEuclid2, 0.133623 }, //
Cell2D { (vector2){ -5.0, 5.0 }, 200, distEuclid2, -87.675907 }, // 5
Cell2D { (vector2){ 0.0, 5.0 }, 200, distEuclid2, 95.684490 }, //
Cell2D { (vector2){ 5.0, 5.0 }, 200, distEuclid2, -10.900518 }, //
Cell2D { (vector2){ -10.0, 0.0 }, 200, distEuclid2, -23.899339 }, //
Cell2D { (vector2){ -5.0, 0.0 }, 200, distEuclid2, 47.488621 }, //
Cell2D { (vector2){ 0.0, 0.0 }, 200, distEuclid2, 46.469304 }, // 10
Cell2D { (vector2){ 5.0, 0.0 }, 200, distEuclid2, -93.750297 }, //
Cell2D { (vector2){ -10.0, -5.0 }, 200, distEuclid2, -47.540723 }, //
Cell2D { (vector2){ -5, -5 }, 200, distEuclid2, 17.030809 }, //
Cell2D { (vector2){ 0, -5 }, 200, distEuclid2, 138.801578 }, //
Cell2D { (vector2){ 5, -5 }, 200, distEuclid2, 69.693651 }, // 15
//
Cell2D { (vector2){ -1.0, -1.0 }, 200, distManhattan2, -15.063725 }, //
Cell2D { (vector2){ 0.0, 0.0 }, 200, distManhattan2, -4.259804 }, //
//
Cell2D { (vector2){ -1.0, -1.0 }, 200, distGreatestAxis2, -18.294118 }, //
Cell2D { (vector2){ 0.0, 0.0 }, 200, distGreatestAxis2, 31.107847 } //
));

/**
 * Cell3D
 */
struct Cell3D {
	vector3 v;
	uint seed;
	dist_func3 dist_func;
	REAL y;
};

/**
 * noise_cell_func3_params
 */
class noise_cell_func3_params: public ::testing::TestWithParam<Cell3D> {
protected:
	REAL f[4] = { 10, 5, 2.5, 1.25 };
	REAL disp[4] = { 100, 50, 25, 10 };
};

/**
 * noise_gen_cellular_noise3D_test-noise_cell_func3_params-cell3D
 * <ul>
 * <li>Cell3D
 * <li>cellular_function3D
 * </ul>
 */
TEST_P(noise_cell_func3_params, cell3D) {
	auto t = GetParam();
	EXPECT_NEAR(cellular_function3D(t.v, t.seed, f, disp, t.dist_func), t.y, 0.00001);
}

/**
 * noise_gen_cellular_noise3D_test-noise_cell_func3_params
 * <ul>
 * <li>Cell3D
 * <li>float3
 * <li>distEuclid3
 * <li>distManhattan3
 * <li>distGreatestAxis3
 * <li>distLeastAxis3
 * </ul>
 */
INSTANTIATE_TEST_SUITE_P(noise_gen_cellular_noise3D_test, noise_cell_func3_params, Values( //
Cell3D { (vector3){ -1.0, -1.0, 0.0 }, 200, distEuclid3, 24.79700 }, // 0
//
Cell3D { (vector3){ -1.0, -1.0, 0.0 }, 200, distManhattan3, 32.22059 }, //
//
Cell3D { (vector3){ -1.0, -1.0, 0.0 }, 200, distGreatestAxis3, 17.058834 }, //
//
Cell3D { (vector3){ -1.0, -1.0, 0.0 }, 200, distLeastAxis3, 24.79700 } //
));

/**
 * Cell4D
 */
struct Cell4D {
	vector4 v;
	uint seed;
	dist_func4 dist_func;
	REAL y;
};

/**
 * noise_cell_func4_params
 */
class noise_cell_func4_params: public ::testing::TestWithParam<Cell4D> {
protected:
	REAL f[4] = { 10, 5, 2.5, 1.25 };
	REAL disp[4] = { 100, 50, 25, 10 };
};

/**
 * noise_gen_cellular_noise4D_test-noise_cell_func4_params-cell4D
 * <ul>
 * <li>Cell4D
 * <li>cellular_function4D
 * </ul>
 */
TEST_P(noise_cell_func4_params, cell4D) {
	auto t = GetParam();
	EXPECT_NEAR(cellular_function4D(t.v, t.seed, f, disp, t.dist_func), t.y, 0.00001);
}

/**
 * noise_gen_cellular_noise4D_test-noise_cell_func4_params
 * <ul>
 * <li>Cell4D
 * <li>float4
 * <li>distEuclid4
 * <li>distManhattan4
 * <li>distGreatestAxis4
 * <li>distLeastAxis4
 * </ul>
 */
INSTANTIATE_TEST_SUITE_P(noise_gen_cellular_noise4D_test, noise_cell_func4_params, Values( //
Cell4D { (vector4){ -1.0, -1.0, -1.0, 0.0 }, 200, distEuclid4, -81.92229 }, // 0
//
Cell4D { (vector4){ -1.0, -1.0, -1.0, 0.0 }, 200, distManhattan4, -75.90196 }, //
//
Cell4D { (vector4){ -1.0, -1.0, -1.0, 0.0 }, 200, distGreatestAxis4, -121.44118 }, //
//
Cell4D { (vector4){ -1.0, -1.0, -1.0, 0.0 }, 200, distLeastAxis4, -81.92229 } //
));

/**
 * Cell6D
 */
struct Cell6D {
	vector8 v;
	uint seed;
	dist_func6 dist_func;
	REAL y;
};

/**
 * noise_cell_func6_params
 */
class noise_cell_func6_params: public ::testing::TestWithParam<Cell6D> {
protected:
	REAL f[4] = { 10, 5, 2.5, 1.25 };
	REAL disp[4] = { 100, 50, 25, 10 };
};

/**
 * noise_gen_cellular_noise6D_test-noise_cell_func6_params-cell6D
 * <ul>
 * <li>Cell6D
 * <li>cellular_function6D
 * </ul>
 */
TEST_P(noise_cell_func6_params, cell6D) {
	auto t = GetParam();
	EXPECT_NEAR(cellular_function6D(t.v, t.seed, f, disp, t.dist_func), t.y, 0.00001);
}

/**
 * noise_gen_cellular_noise6D_test-noise_cell_func6_params
 * <ul>
 * <li>Cell6D
 * <li>float8
 * <li>distEuclid6
 * <li>distManhattan6
 * <li>distGreatestAxis6
 * <li>distLeastAxis6
 * </ul>
 */
INSTANTIATE_TEST_SUITE_P(noise_gen_cellular_noise6D_test, noise_cell_func6_params, Values( //
Cell6D { (vector8){ -1.0, -1.0, -1.0, -1.0, -1.0, 0.0 }, 200, distEuclid6, -101.23390 }, // 0
//
Cell6D { (vector8){ -1.0, -1.0, -1.0, -1.0, -1.0, 0.0 }, 200, distManhattan6, -75.90686 }, //
//
Cell6D { (vector8){ -1.0, -1.0, -1.0, -1.0, -1.0, 0.0 }, 200, distGreatestAxis6, -82.80882 }, //
//
Cell6D { (vector8){ -1.0, -1.0, -1.0, -1.0, -1.0, 0.0 }, 200, distLeastAxis6, -101.23390 } //
));

