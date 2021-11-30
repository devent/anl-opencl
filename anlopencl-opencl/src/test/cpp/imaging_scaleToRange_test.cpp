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
 * imaging_scaleToRange_test.cpp
 *
 * Flag to run only this tests:
 * --gtest_filter="imaging_scaleToRange_test*"
 *
 *  Created on: Jul 26, 2021
 *      Author: Erwin Müller
 */

#include <gtest/gtest.h>
#include "imaging.h"
#include "noise_gen.h"

using ::testing::TestWithParam;
using ::testing::Values;

struct imaging_scaleToRange_data {
	size_t count;
	REAL low;
	REAL high;
};

class imaging_scaleToRange_param: public ::testing::TestWithParam<imaging_scaleToRange_data> {
protected:
	std::vector<REAL> data;
	REAL min;
	REAL max;

	virtual void SetUp() {
		auto t = GetParam();
		data = std::vector<REAL>(t.count);
		std::vector<vector3> out(t.count);
		map2D(out.data(), calc_seamless_none, create_ranges_default(), t.count / 2, t.count / 2, 0);
		printf("Before data:\n");
		for (int i = 0; i < t.count; ++i) {
			REAL v = value_noise3D(out[i], 0, noInterp);
			data[i] = v;
			printf("%f\n", v);
			if (min > v) {
				min = v;
			}
			if (max < v) {
				max = v;
			}
		}
		printf("##\n");
	}

	virtual void TearDown() {
	}
};

TEST_P(imaging_scaleToRange_param, scaleToRange3) {
	auto t = GetParam();
	scaleToRange(data.data(), t.count, min, max, t.low, t.high);
	ASSERT_NEAR(data[0], 0.00000, 0.00001);
	ASSERT_NEAR(data[1], 0.11111, 0.00001);
	ASSERT_NEAR(data[2], 0.42995, 0.00001);
	ASSERT_NEAR(data[3], 1.00000, 0.00001);
	for (int i = 0; i < data.size(); ++i) {
		printf("%f\n", data[i]);
	}
}

INSTANTIATE_TEST_SUITE_P(imaging_scaleToRange_test, imaging_scaleToRange_param,
		Values(
				imaging_scaleToRange_data { 4, 0, 1 } //
				));

