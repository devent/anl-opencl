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
 * compile_library_test.cpp
 *
 * Flag to run only this tests:
 * --gtest_filter="compile_library_test*"
 *
 *  Created on: Aug 29, 2021
 *      Author: Erwin Müller
 */

#include <gtest/gtest.h>
#include <spdlog/spdlog.h>
#include <spdlog/sinks/stdout_color_sinks.h>
#include <fstream>
#include <iterator>
#include "OpenCLContext.h"

std::shared_ptr<spdlog::logger> logger = []() -> std::shared_ptr<spdlog::logger> {
	logger = spdlog::stderr_color_mt("test", spdlog::color_mode::automatic);
	logger->set_level(spdlog::level::trace);
	logger->flush_on(spdlog::level::debug);
	logger->flush_on(spdlog::level::err);
	return logger;
}();

void write_to_file(std::string name, std::vector<unsigned char> data) {
	std::ofstream f(name, std::ios::out | std::ios::binary);
	f.write((char*)data.data(), data.size());
}

std::vector<char> read_from_file(std::string name) {
	std::vector<char> buffer;
	std::ifstream f(name, std::ios::in | std::ios::binary);
	f.seekg(0, f.end);
	size_t length = f.tellg();
	f.seekg(0, f.beg);
	if (length > 0) {
	    buffer.resize(length);
	    f.read(&buffer[0], length);
	}
	return buffer;
}

TEST(compile_library_test, load_library_from_file) {
	OpenCL_Context context;
	EXPECT_TRUE(context.loadPlatform()) << "Unable to load platform";
	{
		auto library = context.createLibrary();
		int err = 0;
		auto bins = library.getInfo<CL_PROGRAM_BINARIES>(&err);
		logger->debug("Libraries {} size {}", bins.size(), bins[0].size());
		write_to_file("anlopencl.bin", bins[0]);
	}
	{
		auto bin = read_from_file("anlopencl.bin");
		logger->debug("Load library size {}", bin.size());
		cl::Program p;
		try {
			p = cl::Program(bin);
		} catch (const cl::Error &ex) {
			logger->error("Error compile {} {}", ex.err(), ex.what());
			throw ex;
		}
	}
}
