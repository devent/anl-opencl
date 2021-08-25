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
#include <opencv2/core.hpp>

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

std::string readFile(std::string fileName);

template <typename T>
std::shared_ptr<std::vector<T>> createVector(size_t count) {
	return std::make_shared<std::vector<T>>(count, 0);
}

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

std::string mat_to_s(cv::Mat & m);

/**
 * Blocking copy operation between iterators and a buffer.
 * Device to Host.
 * Uses specified queue.
 */
template<typename T>
cl_int copy(const cl::CommandQueue &queue, const cl::Buffer &buffer,
		std::vector<T> &target) {
	typedef typename std::vector<T>::value_type DataType;
	const size_t byteLength = target.size() * sizeof(DataType);

	cl_int error;
	DataType *pointer = static_cast<DataType*>(queue.enqueueMapBuffer(buffer,
			CL_TRUE, CL_MAP_READ, 0, byteLength, 0, 0, &error));
	// if exceptions enabled, enqueueMapBuffer will throw
	if (error != CL_SUCCESS) {
		return error;
	}
	std::memcpy(target.data(), pointer, byteLength);
	cl::Event endEvent;
	error = queue.enqueueUnmapMemObject(buffer, pointer, 0, &endEvent);
	// if exceptions enabled, enqueueUnmapMemObject will throw
	if (error != CL_SUCCESS) {
		return error;
	}
	endEvent.wait();
	return CL_SUCCESS;
}

/**
 * Blocking copy operation between iterators and a buffer.
 * Device to Host.
 * Uses default command queue.
 */
template<typename T>
inline cl_int copy(const cl::Buffer &buffer, std::vector<T> &target) {
	cl_int error;
	cl::CommandQueue queue = cl::CommandQueue::getDefault(&error);
	if (error != CL_SUCCESS)
		return error;

	return copy(queue, buffer, target);
}

/**
 * float2 have 2 floats.
 */
const size_t dim_float2 = sizeof(cl_float2) / sizeof(cl_float);

/**
 * float3 have 4 floats.
 */
const size_t dim_float3 = sizeof(cl_float3) / sizeof(cl_float);

/**
 * float4 have 4 floats.
 */
const size_t dim_float4 = sizeof(cl_float4) / sizeof(cl_float);

class Abstract_OpenCL_Context_Fixture: public ::testing::TestWithParam<KernelContext> {
public:
	static std::shared_ptr<spdlog::logger> logger;
protected:
	static void SetUpTestSuite();
	virtual void SetUp();
	virtual size_t runKernel(cl::Program & kernel) = 0;
	virtual void copyBuffers() = 0;
	void showImageScaleToRange(std::shared_ptr<std::vector<float>> output, int type);
	void showImage(std::shared_ptr<std::vector<float>> output, int type);
};

class OpenCL_Context_Buffer_Fixture: public Abstract_OpenCL_Context_Fixture {
public:
	std::shared_ptr<std::vector<float>> output;
	std::shared_ptr<cl::Buffer> outputBuffer;
protected:
	virtual void copyBuffers();
};

template < class T >
using coarse_float_svm_vector = std::vector<T, cl::SVMAllocator<float, cl::SVMTraitCoarse<>>>;

class OpenCL_Context_SVM_Fixture: public Abstract_OpenCL_Context_Fixture {
public:
	cl::SVMAllocator<float, cl::SVMTraitCoarse<>> outputAlloc;
	std::shared_ptr<coarse_float_svm_vector<float>> output;
protected:
	virtual void copyBuffers();
};

#endif /* OPENCLTESTFIXTURE_H_ */
