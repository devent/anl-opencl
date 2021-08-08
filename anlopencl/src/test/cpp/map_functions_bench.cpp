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
#include <benchmark/benchmark.h>
#include "imaging.h"

static void map2D_seamless_none_bench(benchmark::State &state) {
	for (auto _ : state) {
		state.PauseTiming();
		auto width = state.range(0);
		auto out = std::vector<vector3>(width * width);
		auto ranges = create_range_default();
		double z = 99;
		state.ResumeTiming();
		map2D(out.data(), calc_seamless_none, ranges, width, width, z);
		benchmark::DoNotOptimize(out.data());
	}
}

// Register the function as a benchmark
BENCHMARK(map2D_seamless_none_bench)->
#ifdef USE_THREAD
		Name("map2D_seamless_none-THREAD")
#else
		Name("map2D_seamless_none-NO-THREAD")
#endif // USE_THREAD
		->Repetitions(4)->Unit(benchmark::kMillisecond)->RangeMultiplier(8)->Range(8, 8<<10);

TEST(map2D_bench, seamless_none) {
	int argc = 0;
	char** argv = NULL;
    ::benchmark::Initialize(&argc, argv);
    ASSERT_FALSE(::benchmark::ReportUnrecognizedArguments(argc, argv));
    ::benchmark::RunSpecifiedBenchmarks();
    ::benchmark::Shutdown();
}

//BENCHMARK_MAIN();
