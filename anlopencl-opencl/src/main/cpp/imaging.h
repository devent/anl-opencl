//
// Copyright (C) 2021-2022 Erwin Müller <erwin@muellerpublic.de>
// Released as open-source under the Apache License, Version 2.0.
//
// ****************************************************************************
// ANL-OpenCL :: OpenCL
// ****************************************************************************
//
// Copyright (C) 2021-2022 Erwin Müller <erwin@muellerpublic.de>
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
// ANL-OpenCL :: OpenCL is a derivative work based on Josua Tippetts' C++ library:
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
//
// ****************************************************************************
// ANL-OpenCL :: OpenCL bundles and uses the RandomCL library:
// https://github.com/bstatcomp/RandomCL
// ****************************************************************************
//
// BSD 3-Clause License
//
// Copyright (c) 2018, Tadej Ciglarič, Erik Štrumbelj, Rok Češnovar. All rights reserved.
//
// Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
//
// * Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
//
// * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
//
// * Neither the name of the copyright holder nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
//

/*
 * imaging.h
 *
 *  Created on: Aug 7, 2021
 *      Author: Erwin Müller
 */

#ifndef IMAGING_H_
#define IMAGING_H_

#ifdef __cplusplus
extern "C" {
#endif

#ifndef ANLOPENCL_USE_OPENCL
#include <opencl_utils.h>
#endif // ANLOPENCL_USE_OPENCL

struct __attribute__ ((packed)) SMappingRanges {
	REAL mapx0, mapy0, mapz0, mapx1, mapy1, mapz1;
	REAL loopx0, loopy0, loopz0, loopx1, loopy1, loopz1;
};

/**
 * Creates ranges from [-1..1] in all 3 dimensions.
 */
struct SMappingRanges create_ranges_default();

/**
 * Set the ranges from [-1..1] in all 3 dimensions and returns the set ranges.
 */
struct SMappingRanges set_ranges_default(struct SMappingRanges *const ranges);

/**
 * Creates ranges for the x-y dimensions and z=[0..1].
 */
struct SMappingRanges create_ranges_map2D(REAL x0, REAL x1, REAL y0, REAL y1);

/**
 * Set ranges for the x-y dimensions and z=[0..1] and returns the set ranges.
 */
struct SMappingRanges set_ranges_map2D(struct SMappingRanges *const ranges, REAL x0, REAL x1, REAL y0, REAL y1);

/**
 * Creates ranges for the 3 dimensions.
 */
struct SMappingRanges create_ranges_map3D(REAL x0, REAL x1, REAL y0, REAL y1,
		REAL z0, REAL z1);

/**
 * Set ranges for the 3 dimensions and returns the set ranges.
 */
struct SMappingRanges set_ranges_map3D(struct SMappingRanges *const ranges, REAL x0, REAL x1, REAL y0, REAL y1, REAL z0, REAL z1);

struct SChunk;

typedef void (*calc_seamless)(void*, int, size_t x, size_t y, REAL p, REAL q,
		struct SChunk chunk, struct SMappingRanges ranges);

typedef void (*calc_seamless_no_z)(void*, int, size_t x, size_t y, REAL p,
		REAL q, struct SChunk chunk, struct SMappingRanges ranges);

struct SChunk {
	calc_seamless calc_seamless;
	void *out;
	int width, height;
	int chunkheight, chunkyoffset;
	struct SMappingRanges ranges;
	REAL z;
};

/**
 * Use with map2D to have no seamless mapping ranges.
 *
 * @param out a pointer to an array of vector3.
 */
void calc_seamless_none(void *out, int index, size_t x, size_t y, REAL p,
		REAL q, struct SChunk chunk, struct SMappingRanges ranges);

/**
 * Use with map2D to have seamless along the X-axis mapping ranges.
 *
 * @param out a pointer to an array of vector4.
 */
void calc_seamless_x(void *out, int index, size_t x, size_t y, REAL p,
		REAL q, struct SChunk chunk, struct SMappingRanges ranges);

/**
 * Use with map2D to have seamless along the Y-axis mapping ranges.
 *
 * @param out a pointer to an array of vector4.
 */
void calc_seamless_y(void *out, int index, size_t x, size_t y, REAL p,
		REAL q, struct SChunk chunk, struct SMappingRanges ranges);

/**
 * Use with map2D to have seamless along the Z-axis mapping ranges.
 *
 * @param out a pointer to an array of vector4.
 */
void calc_seamless_z(void *out, int index, size_t x, size_t y, REAL p,
		REAL q, struct SChunk chunk, struct SMappingRanges ranges);

/**
 * Use with map2D to have seamless along the X-Y-axis mapping ranges.
 *
 * @param out a pointer to an array of vector8.
 */
void calc_seamless_xy(void *out, int index, size_t x, size_t y, REAL p,
		REAL q, struct SChunk chunk, struct SMappingRanges ranges);

/**
 * Use with map2D to have seamless along the X-Z-axis mapping ranges.
 *
 * @param out a pointer to an array of vector8.
 */
void calc_seamless_xz(void *out, int index, size_t x, size_t y, REAL p,
		REAL q, struct SChunk chunk, struct SMappingRanges ranges);

/**
 * Use with map2D to have seamless along the Y-Z-axis mapping ranges.
 *
 * @param out a pointer to an array of vector8.
 */
void calc_seamless_yz(void *out, int index, size_t x, size_t y, REAL p,
		REAL q, struct SChunk chunk, struct SMappingRanges ranges);

/**
 * Use with map2D to have seamless along the X-Y-Z-axis mapping ranges.
 *
 * @param out a pointer to an array of vector8.
 */
void calc_seamless_xyz(void *out, int index, size_t x, size_t y, REAL p,
		REAL q, struct SChunk chunk, struct SMappingRanges ranges);

/**
 * Creates the mapping ranges for the width and height. Different seamless functions are supported.
 * @param out a pointer to an array of vectors with the correct dimension.
 * <ul>
 * <li>calc_seamless_none: vector3
 * <li>calc_seamless_x: vector4
 * <li>calc_seamless_y: vector4
 * <li>calc_seamless_z: vector4
 * <li>calc_seamless_xy: vector8
 * <li>calc_seamless_xz: vector8
 * <li>calc_seamless_yz: vector8
 * <li>calc_seamless_xyz: vector8
 * <ul>
 */
void* map2D(void *out, calc_seamless calc_seamless,
		struct SMappingRanges ranges, size_t width, size_t height, REAL z);

/**
 * Use with map2D to have no seamless mapping ranges.
 * Works without the Z value.
 *
 * @param out a pointer to an array of vector2.
 */
void calc_seamless_no_z_none(void *out, int index, size_t x, size_t y, REAL p,
		REAL q, struct SChunk chunk, struct SMappingRanges ranges);

/**
 * Use with map2D to have seamless along the X-axis mapping ranges.
 * Works without the Z value.
 *
 * @param out a pointer to an array of vector3.
 */
void calc_seamless_no_z_x(void *out, int index, size_t x, size_t y, REAL p,
		REAL q, struct SChunk chunk, struct SMappingRanges ranges);

/**
 * Use with map2D to have seamless along the Y-axis mapping ranges.
 * Works without the Z value.
 *
 * @param out a pointer to an array of vector3.
 */
void calc_seamless_no_z_y(void *out, int index, size_t x, size_t y, REAL p,
		REAL q, struct SChunk chunk, struct SMappingRanges ranges);

/**
 * Use with map2D to have seamless along the Z-axis mapping ranges.
 * Works without the Z value.
 *
 * @param out a pointer to an array of vector4.
 */
void calc_seamless_no_z_z(void *out, int index, size_t x, size_t y, REAL p,
		REAL q, struct SChunk chunk, struct SMappingRanges ranges);

/**
 * Use with map2D to have seamless along the X-Y-axis mapping ranges.
 * Works without the Z value.
 *
 * @param out a pointer to an array of vector4.
 */
void calc_seamless_no_z_xy(void *out, int index, size_t x, size_t y, REAL p,
		REAL q, struct SChunk chunk, struct SMappingRanges ranges);

/**
 * Use with map2D to have seamless along the X-Z-axis mapping ranges.
 * Works without the Z value.
 *
 * @param out a pointer to an array of vector8.
 */
void calc_seamless_no_z_xz(void *out, int index, size_t x, size_t y, REAL p,
		REAL q, struct SChunk chunk, struct SMappingRanges ranges);

/**
 * Use with map2D to have seamless along the Y-Z-axis mapping ranges.
 * Works without the Z value.
 *
 * @param out a pointer to an array of vector8.
 */
void calc_seamless_no_z_yz(void *out, int index, size_t x, size_t y, REAL p,
		REAL q, struct SChunk chunk, struct SMappingRanges ranges);

/**
 * Use with map2D to have seamless along the X-Y-Z-axis mapping ranges.
 * Works without the Z value.
 *
 * @param out a pointer to an array of vector8.
 */
void calc_seamless_no_z_xyz(void *out, int index, size_t x, size_t y, REAL p,
		REAL q, struct SChunk chunk, struct SMappingRanges ranges);

/**
 * Creates the mapping ranges for the width and height, without a Z dimension.
 * Different seamless functions are supported.
 * @param out a pointer to an array of vectors with the correct dimension.
 * <ul>
 * <li>calc_seamless_no_z_none: vector2
 * <li>calc_seamless_no_z_x: vector3
 * <li>calc_seamless_no_z_y: vector3
 * <li>calc_seamless_no_z_z: vector4
 * <li>calc_seamless_no_z_xy: vector4
 * <li>calc_seamless_no_z_xz: vector8
 * <li>calc_seamless_no_z_yz: vector8
 * <li>calc_seamless_no_z_xyz: vector8
 * <ul>
 */
void* map2DNoZ(void *out, calc_seamless_no_z calc_seamless,
		struct SMappingRanges ranges, size_t width, size_t height);

/**
 * Scales the values in data to the range between low and high.
 * The data is modified by this function.
 *
 * @param data the REAL type data that contains `count` elements.
 * @param count the count of the elements in the data.
 * @param min the minimum value of the data in all dimensions.
 * @param max the minimum value of the data in all dimensions.
 * @param low the low value of the range.
 * @param high the high value of the range.
 * @return the scaled data.
 */
REAL* scaleToRange(REAL *data, size_t count, REAL min, REAL max, REAL low, REAL high);

#ifdef __cplusplus
}
#endif

#endif /* IMAGING_H_ */
