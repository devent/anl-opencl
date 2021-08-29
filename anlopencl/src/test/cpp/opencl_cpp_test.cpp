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
 * opencl_color_noise3D_test.cpp
 *
 * Flag to run only this tests:
 * --gtest_filter="opencl_color_noise3D_test*"
 *
 *  Created on: Jul 27, 2021
 *      Author: Erwin Müller
 */

#include <memory>
#include <strings.h>
#include <bits/stdc++.h>

#define CL_HPP_ENABLE_EXCEPTIONS
#include <CL/opencl.hpp>

#include <spdlog/spdlog.h>
#include <spdlog/sinks/stdout_color_sinks.h>

#include <opencv2/core.hpp>
#include <opencv2/highgui.hpp>
#include <opencv2/core/mat.hpp>

#include "OpenCLTestFixture.h"
#include "imaging.h"

using ::testing::TestWithParam;
using ::testing::Values;
using ::spdlog::info;
using ::spdlog::error;

const int numElements = 32;

TEST(opencl_cpp_test, basic_example) {
    // Filter for a 2.0 platform and set it as the default
    std::vector<cl::Platform> platforms;
    cl::Platform::get(&platforms);
    cl::Platform plat;
    for (auto &p : platforms) {
        std::string platver = p.getInfo<CL_PLATFORM_VERSION>();
        if (platver.find("OpenCL 3.") != std::string::npos) {
            plat = p;
        }
    }
    if (plat() == 0)  {
        std::cout << "No OpenCL 3.0 platform found.";
        return;
    }
    cl::Platform newP = cl::Platform::setDefault(plat);
    if (newP != plat) {
        std::cout << "Error setting default platform.";
        return;
    }
    // Use C++11 raw string literals for kernel source code
    std::string kernel1{R"CLC(
        global int globalA;
        kernel void updateGlobal()
        {
          globalA = 75;
        }
    )CLC"};
    std::string kernel2{R"CLC(
        typedef struct { global int *bar; } Foo;
        kernel void vectorAdd(global const Foo* aNum, global const int *inputA, global const int *inputB,
                              global int *output, int val, queue_t childQueue)
        {
          output[get_global_id(0)] = inputA[get_global_id(0)] + inputB[get_global_id(0)] + val + *(aNum->bar);
          queue_t default_queue = get_default_queue();
          ndrange_t ndrange = ndrange_1D(get_global_size(0)/2, get_global_size(0)/2);
          // Have a child kernel write into third quarter of output
          enqueue_kernel(default_queue, CLK_ENQUEUE_FLAGS_WAIT_KERNEL, ndrange,
            ^{
                output[get_global_size(0)*2 + get_global_id(0)] =
                  inputA[get_global_size(0)*2 + get_global_id(0)] + inputB[get_global_size(0)*2 + get_global_id(0)] + globalA;
            });
          // Have a child kernel write into last quarter of output
          enqueue_kernel(childQueue, CLK_ENQUEUE_FLAGS_WAIT_KERNEL, ndrange,
            ^{
                output[get_global_size(0)*3 + get_global_id(0)] =
                  inputA[get_global_size(0)*3 + get_global_id(0)] + inputB[get_global_size(0)*3 + get_global_id(0)] + globalA + 2;
            });
        }
    )CLC"};
    // New simpler string interface style
    std::vector<std::string> programStrings {kernel1, kernel2};
    cl::Program vectorAddProgram(programStrings);
    try {
        vectorAddProgram.build("-cl-std=CL3.0");
    }
    catch (...) {
        // Print build info for all devices
        cl_int buildErr = CL_SUCCESS;
        auto buildInfo = vectorAddProgram.getBuildInfo<CL_PROGRAM_BUILD_LOG>(&buildErr);
        for (auto &pair : buildInfo) {
            std::cerr << pair.second << std::endl << std::endl;
        }
        return;
    }
    typedef struct { int *bar; } Foo;
    // Get and run kernel that initializes the program-scope global
    // A test for kernels that take no arguments
    auto program2Kernel =
        cl::KernelFunctor<>(vectorAddProgram, "updateGlobal");
    program2Kernel(
        cl::EnqueueArgs(
        cl::NDRange(1)));
    // SVM allocations
    auto anSVMInt = cl::allocate_svm<int, cl::SVMTraitCoarse<>>();
    *anSVMInt = 5;
    cl::SVMAllocator<Foo, cl::SVMTraitCoarse<cl::SVMTraitReadOnly<>>> svmAllocReadOnly;
    auto fooPointer = cl::allocate_pointer<Foo>(svmAllocReadOnly);
    fooPointer->bar = anSVMInt.get();
    cl::SVMAllocator<int, cl::SVMTraitCoarse<>> svmAlloc;
    std::vector<int, cl::SVMAllocator<int, cl::SVMTraitCoarse<>>> inputA(numElements, 1, svmAlloc);
    cl::coarse_svm_vector<int> inputB(numElements, 2, svmAlloc);
    //
    auto output = std::vector<int, cl::SVMAllocator<int, cl::SVMTraitCoarse<>>>(numElements, 1, svmAlloc);
    // Traditional cl_mem allocations
    //std::vector<int> output(numElements, 0xdeadbeef);
    //cl::Buffer outputBuffer(begin(output), end(output), false);
    // Default command queue, also passed in as a parameter
    cl::DeviceCommandQueue defaultDeviceQueue = cl::DeviceCommandQueue::makeDefault(
        cl::Context::getDefault(), cl::Device::getDefault());
    auto vectorAddKernel =
        cl::KernelFunctor<
            decltype(fooPointer)&,
            int*,
            cl::coarse_svm_vector<int>&,
			cl::coarse_svm_vector<int>&,
            int,
            cl::DeviceCommandQueue
            >(vectorAddProgram, "vectorAdd");
    // Ensure that the additional SVM pointer is available to the kernel
    // This one was not passed as a parameter
    vectorAddKernel.setSVMPointers(anSVMInt);
    // Hand control of coarse allocations to runtime
    cl::enqueueUnmapSVM(anSVMInt);
    cl::enqueueUnmapSVM(fooPointer);
    cl::unmapSVM(inputB);
    cl::unmapSVM(output);
    cl_int error;
    vectorAddKernel(
        cl::EnqueueArgs(
            cl::NDRange(numElements/2),
            cl::NDRange(numElements/2)),
        fooPointer,
        inputA.data(),
        inputB,
        output,
        3,
        defaultDeviceQueue,
       error
        );
    //cl::copy(outputBuffer, begin(output), end(output));
    // Grab the SVM output vector using a map
    cl::mapSVM(output);
    cl::Device d = cl::Device::getDefault();
    std::cout << "Output:\n";
    for (int i = 1; i < numElements; ++i) {
        std::cout << "\t" << output[i] << "\n";
    }
    std::cout << "\n\n";
}
