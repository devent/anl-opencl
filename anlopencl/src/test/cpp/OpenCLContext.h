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
	 * Compiles all sources and the kernel to one program.
	 */
	cl::Program createLibrary();

	/**
	 * Compiles all sources and the kernel to one program.
	 */
	cl::Program createPrograms(const std::string kernel);
private:
	std::shared_ptr<spdlog::logger> logger;
	std::string sources;
};

#endif /* OPENCLCONTEXT_H_ */
