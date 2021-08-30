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
	std::string randomc;
	std::string randomh;
};

#endif /* OPENCLCONTEXT_H_ */
