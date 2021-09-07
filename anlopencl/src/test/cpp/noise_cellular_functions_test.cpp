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
 * --gtest_filter="noise_gen_cellular_noise*D_test*"
 *
 *  Created on: Jul 26, 2021
 *      Author: Erwin Müller
 */

#include <gtest/gtest.h>
#include "noise_gen.h"

using ::testing::TestWithParam;
using ::testing::Values;

struct Cell2D {
	vector2 v;
	uint seed;
	dist_func2 dist_func;
	REAL y;
};

class noise_cell_func2_params: public ::testing::TestWithParam<Cell2D> {
protected:
	REAL f[4] = { 10, 5, 2.5, 1.25 };
	REAL disp[4] = { 100, 50, 25, 10 };
};

/**
 * noise_gen_cellular_noise2D_test-noise_cell_func2_params-cell2D
 * <ul>
 * <li>Cell2D
 * <li>float2
 * <li>distEuclid2
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

