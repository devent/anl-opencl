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
 * OpenCLTestFixture.cpp
 *
 *  Created on: Aug 3, 2021
 *      Author: Erwin Müller
 */

#include "OpenCLTestFixture.h"
#include <spdlog/sinks/stdout_color_sinks.h>
#include <opencv2/highgui.hpp>
#include <opencv2/core/mat.hpp>
#include <opencv2/imgcodecs.hpp>
#include "imaging.h"
#include "OpenCLContext.h"

using ::testing::TestWithParam;
using ::testing::Values;
using ::spdlog::info;
using ::spdlog::error;

std::string mat_to_s(cv::Mat & m) {
    std::string ms;
    ms << m;
    return ms;
}

std::shared_ptr<spdlog::logger> Abstract_OpenCL_Context_Fixture::logger = []() -> std::shared_ptr<spdlog::logger> {
	logger = spdlog::stderr_color_mt("OpenCL_Context_Fixture", spdlog::color_mode::automatic);
	logger->set_level(spdlog::level::trace);
	logger->flush_on(spdlog::level::debug);
	logger->flush_on(spdlog::level::err);
	return logger;
}();

void Abstract_OpenCL_Context_Fixture::SetUpTestSuite() {
	cl::Device d = cl::Device::getDefault();
	logger->debug("Max compute units: {}", d.getInfo<CL_DEVICE_MAX_COMPUTE_UNITS>());
	logger->debug("Max dimensions: {}", d.getInfo<CL_DEVICE_MAX_WORK_ITEM_DIMENSIONS>());
	//std::cout << "Max work item sizes: " << d.getInfo<CL_DEVICE_MAX_WORK_ITEM_SIZES>() << "\n";
	logger->debug("Max work group sizes: {}", d.getInfo<CL_DEVICE_MAX_WORK_GROUP_SIZE>());
	logger->debug("Max pipe args: {}", d.getInfo<CL_DEVICE_MAX_PIPE_ARGS>());
	logger->debug("Max pipe active reservations: {}", d.getInfo<CL_DEVICE_PIPE_MAX_ACTIVE_RESERVATIONS>());
	logger->debug("Max pipe packet size: {}", d.getInfo<CL_DEVICE_PIPE_MAX_PACKET_SIZE>());
	logger->debug("Device SVM capabilities: {}", d.getInfo<CL_DEVICE_SVM_CAPABILITIES>());
}

void Abstract_OpenCL_Context_Fixture::SetUp() {
	OpenCL_Context context;
	EXPECT_TRUE(context.loadPlatform()) << "Unable to load platform";
	auto t = GetParam();
	auto kernel = context.createPrograms(t.source);
	size_t numElements;
	try {
		numElements = runKernel(kernel);
	} catch (const cl::Error &ex) {
		logger->error("Run kernel error {}: {}", ex.err(), ex.what());
		throw ex;
	}
	try {
		logger->debug("Copy output buffer size {}", t.imageSize);
		copyBuffers();
	} catch (const cl::Error &ex) {
		logger->error("Output buffer error {}: {}", ex.err(), ex.what());
		throw ex;
	}
}

void Abstract_OpenCL_Context_Fixture::showImageScaleToRange(
		std::shared_ptr<std::vector<float>> output, int type) {
    float min = *std::min_element(output->begin(), output->end());
    float max = *std::max_element(output->begin(), output->end());
    scaleToRange(output->data(), output->size(), min, max, 0, 1);
    logger->trace("Scale to range min={}, max={}", min, max);
	showImage(output, type);
}

void Abstract_OpenCL_Context_Fixture::showImage(
		std::shared_ptr<std::vector<float>> output, int type) {
	auto t = GetParam();
	logger->debug("Preparing showing image size {}x{}", t.imageWidth, t.imageHeight);
	cv::Mat m = cv::Mat(cv::Size(t.imageWidth, t.imageHeight), type);
    std::memcpy(m.data, output->data(), output->size() * sizeof(float));
    //logger->trace("Showing m={}", mat_to_s(m));
    std::string w = "Grey Image Vec Copy";
    cv::namedWindow(w, cv::WINDOW_NORMAL);
    cv::resizeWindow(w, 512, 512);
    cv::imshow(w, m);
    cv::waitKey(0);
    cv::destroyAllWindows();
}

void Abstract_OpenCL_Context_Fixture::saveImageScaleToRange(
		std::string fileName, std::shared_ptr<std::vector<float>> output, int type) {
    float min = *std::min_element(output->begin(), output->end());
    float max = *std::max_element(output->begin(), output->end());
    scaleToRange(output->data(), output->size(), min, max, 0, 1);
    logger->trace("Scale to range min={}, max={}", min, max);
	saveImage(fileName, output, type);
}

void Abstract_OpenCL_Context_Fixture::saveImage(
		std::string fileName, std::shared_ptr<std::vector<float>> output, int type) {
	auto t = GetParam();
	logger->debug("Preparing saving image size {}x{}", t.imageWidth, t.imageHeight);
	cv::Mat m = cv::Mat(cv::Size(t.imageWidth, t.imageHeight), type);
    std::memcpy(m.data, output->data(), output->size() * sizeof(float));
    m.convertTo(m, CV_8UC3, 255.0);
    cv::imwrite(fileName, m);
}

void OpenCL_Context_Buffer_Fixture::copyBuffers() {
	copy(*outputBuffer, *output);
}

void OpenCL_Context_SVM_Fixture::copyBuffers() {
	cl::mapSVM(*output);
}

