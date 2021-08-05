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
