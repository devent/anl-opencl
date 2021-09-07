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
 * OpenCLContext.h
 *
 *  Created on: Aug 29, 2021
 *      Author: Erwin Müller
 */

#ifndef OPENCLCONTEXT_H_
#define OPENCLCONTEXT_H_

#include <spdlog/spdlog.h>
#define CL_HPP_ENABLE_EXCEPTIONS
#include <CL/opencl.hpp>

#ifdef ANLOPENCL_USE_DOUBLE
/**
 * double2 have 2 doubles.
 */
const size_t dim_real2 = sizeof(cl_double2) / sizeof(cl_double);

/**
 * double3 have 4 doubles.
 */
const size_t dim_real3 = sizeof(cl_double3) / sizeof(cl_double);

/**
 * double4 have 4 doubles.
 */
const size_t dim_real4 = sizeof(cl_double4) / sizeof(cl_double);

/**
 * double8 have 8 doubles.
 */
const size_t dim_real8 = sizeof(cl_double8) / sizeof(cl_double);
#else
/**
 * float2 have 2 floats.
 */
const size_t dim_real2 = sizeof(cl_float2) / sizeof(cl_float);

/**
 * float3 have 4 floats.
 */
const size_t dim_real3 = sizeof(cl_float3) / sizeof(cl_float);

/**
 * float4 have 4 floats.
 */
const size_t dim_real4 = sizeof(cl_float4) / sizeof(cl_float);

/**
 * float8 have 8 floats.
 */
const size_t dim_real8 = sizeof(cl_float8) / sizeof(cl_float);
#endif // ANLOPENCL_USE_DOUBLE

/**
 * Reads the file as string.
 */
std::string readFile(std::string fileName);

/**
 * Shortcut function to create a shared_ptr vector.
 */
template <typename T>
std::shared_ptr<std::vector<T>> createVector(size_t count) {
	return std::make_shared<std::vector<T>>(count, 0);
}

/**
 * Shortcut function to create a shared_ptr Buffer from the vector.
 */
template <typename T>
std::shared_ptr<cl::Buffer> createBufferPtr(
		std::shared_ptr<std::vector<T>> vector,
		cl_mem_flags flags = CL_MEM_READ_WRITE) {
	typedef typename std::vector<T>::value_type DataType;
	return std::make_shared<cl::Buffer>(
			flags | CL_MEM_USE_HOST_PTR,
			sizeof(DataType) * vector->size(),
			vector->data());
}

/**
 * Shortcut function to create a shared_ptr Buffer from the vector.
 */
template <typename T>
cl::Buffer createBuffer(
		std::vector<T> vector,
		cl_mem_flags flags = CL_MEM_READ_WRITE) {
	typedef typename std::vector<T>::value_type DataType;
	return cl::Buffer(
			flags | CL_MEM_USE_HOST_PTR,
			sizeof(DataType) * vector.size(),
			vector.data());
}

/**
 * Helper class to create an OpenCL context and to compile and run OpenCL kernel.
 */
class OpenCL_Context {
public:
	/**
	 * Creates the OpenCL context.
	 */
	OpenCL_Context();

	/**
	 * Loads the OpenCL 3 platform.
	 */
	bool loadPlatform();

	/**
	 * Loads the noise input headers.
	 */
	void loadInputHeaders();

	/**
	 * Compiles all sources and the kernel to one program.
	 */
	cl::Program createLibrary();

	/**
	 * Compiles all sources and the kernel to one program.
	 * Using the pre-compiled library program.
	 */
	cl::Program createKernel(cl::Program lib, const std::string kernel);

	/**
	 * Compiles all sources and the kernel to one program.
	 */
	cl::Program createPrograms(const std::string kernel);
private:
	static std::shared_ptr<spdlog::logger> logger;
	std::vector<cl::Program> inputHeaders;
	std::vector<std::string> inputHeaderNames;
	std::string sources;
	std::string kiss09cl;
	std::string randomcl;
};

#endif /* OPENCLCONTEXT_H_ */
