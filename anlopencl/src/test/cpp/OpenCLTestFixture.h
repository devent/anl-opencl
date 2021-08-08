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
 * OpenCLTestFixture.h
 *
 *  Created on: Aug 3, 2021
 *      Author: Erwin Müller
 */

#ifndef OPENCLTESTFIXTURE_H_
#define OPENCLTESTFIXTURE_H_

#include <spdlog/spdlog.h>
#include <gtest/gtest.h>
#include <memory>
#include <string>

#define CL_HPP_ENABLE_EXCEPTIONS
#include <CL/opencl.hpp>

struct KernelContext {
	std::string kernel;
	std::string source;
	size_t imageWidth = 1024;
	size_t imageHeight;
	size_t imageSize;
	KernelContext(std::string kernel, std::string source, size_t imageWidth) :
			kernel(kernel), source(source), imageWidth(imageWidth) {
		imageHeight = imageWidth;
		imageSize = imageWidth * imageHeight;
	}
};

class OpenCL_Context_Fixture: public ::testing::TestWithParam<KernelContext> {
public:
	static std::shared_ptr<spdlog::logger> logger;
	std::shared_ptr<std::vector<float>> output;
	std::shared_ptr<cl::Buffer> outputBuffer;
protected:
	static void SetUpTestSuite();
	virtual void SetUp();
	virtual void TearDown();
	virtual size_t runKernel(cl::Program & kernel) = 0;
};

#endif /* OPENCLTESTFIXTURE_H_ */
