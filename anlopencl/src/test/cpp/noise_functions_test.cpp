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
 * noise_functions_test.cpp
 *
 *  Created on: Jul 26, 2021
 *      Author: Erwin Müller
 */

#include <gtest/gtest.h>
#include <spdlog/spdlog.h>
#include <spdlog/sinks/stdout_color_sinks.h>
#include "noise_gen.h"

using ::testing::TestWithParam;
using ::testing::Values;

struct Func2D {
	vector2 v;
	uint seed;
	std::string noise_desc;
	noise_func2 noise_func;
	interp_func interp;
	REAL y;
};

class noise_func2_params: public ::testing::TestWithParam<Func2D> {
protected:
	static std::shared_ptr<spdlog::logger> logger;
};

std::shared_ptr<spdlog::logger> noise_func2_params::logger = []() -> std::shared_ptr<spdlog::logger> {
	logger = spdlog::stderr_color_mt("noise_func2_params", spdlog::color_mode::automatic);
	logger->set_level(spdlog::level::trace);
	return logger;
}();

TEST_P(noise_func2_params, value_noise2D) {
	auto t = GetParam();
	logger->info("Test noise_func:={}", t.noise_desc);
	EXPECT_NEAR(t.noise_func(t.v, t.seed, t.interp), t.y, 0.00001);
}

INSTANTIATE_TEST_SUITE_P(noise, noise_func2_params, Values( //
		Func2D { vector2 { 0.000, 0.000 }, 100, "value_noise2D-noInterp", value_noise2D, noInterp, -0.36470 }, //
		Func2D { vector2 { 0.100, 0.150 }, 100, "value_noise2D-noInterp", value_noise2D, noInterp, -0.36470 }, //
		Func2D { vector2 { 0.000, 0.000 }, 100, "value_noise2D-linearInterp", value_noise2D, linearInterp, -0.36470 }, //
		Func2D { vector2 { 0.100, 0.150 }, 100, "value_noise2D-linearInterp", value_noise2D, linearInterp, -0.38227 }, //
		Func2D { vector2 { 0.000, 0.000 }, 100, "value_noise2D-hermiteInterp", value_noise2D, hermiteInterp, -0.36470 }, //
		Func2D { vector2 { 0.100, 0.150 }, 100, "value_noise2D-hermiteInterp", value_noise2D, hermiteInterp, -0.37877 }, //
		Func2D { vector2 { 0.000, 0.000 }, 100, "value_noise2D-quinticInterp", value_noise2D, quinticInterp, -0.36470 }, //
		Func2D { vector2 { 0.100, 0.150 }, 100, "value_noise2D-quinticInterp", value_noise2D, quinticInterp, -0.37333 }, //
		//
		Func2D { vector2 { 0.100, 0.150 }, 100, "gradient_noise2D-noInterp", gradient_noise2D, noInterp, 0.126491 }, //
		//
		Func2D { vector2 { 0.100, 0.150 }, 100, "gradval_noise2D-noInterp", gradval_noise2D, noInterp, -0.238214 }, //
		//
		Func2D { vector2 { 0.100, 0.150 }, 100, "white_noise2D-noInterp", white_noise2D, noInterp, -0.396824 }, //
		//
		Func2D { vector2 { 0.100, 0.150 }, 100, "simplex_noise2D-noInterp", simplex_noise2D, noInterp, 0.242163 } //
));
