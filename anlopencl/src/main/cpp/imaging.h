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

#ifndef USE_OPENCL
#include <opencl_utils.h>
#endif // USE_OPENCL

enum EMappingModes {
	SEAMLESS_NONE,
	SEAMLESS_X,
	SEAMLESS_Y,
	SEAMLESS_Z,
	SEAMLESS_XY,
	SEAMLESS_XZ,
	SEAMLESS_YZ,
	SEAMLESS_XYZ
};

struct SMappingRanges {
	REAL mapx0, mapy0, mapz0, mapx1, mapy1, mapz1;
	REAL loopx0, loopy0, loopz0, loopx1, loopy1, loopz1;
};

struct SMappingRanges create_range_default();

struct SChunk;

typedef void (*calc_seamless)(void*, int, size_t x, size_t y, REAL p, REAL q,
		struct SChunk chunk, struct SMappingRanges ranges);

struct SChunk {
	calc_seamless calc_seamless;
	void *out;
	int width, height;
	int chunkheight, chunkyoffset;
	struct SMappingRanges ranges;
	REAL z;
};

void calc_seamless_none(void *out, int index, size_t x, size_t y, REAL p,
		REAL q, struct SChunk chunk, struct SMappingRanges ranges);
void calc_seamless_x(void *out, int index, size_t x, size_t y, REAL p,
		REAL q, struct SChunk chunk, struct SMappingRanges ranges);
void calc_seamless_y(void *out, int index, size_t x, size_t y, REAL p,
		REAL q, struct SChunk chunk, struct SMappingRanges ranges);
void calc_seamless_z(void *out, int index, size_t x, size_t y, REAL p,
		REAL q, struct SChunk chunk, struct SMappingRanges ranges);
void calc_seamless_xy(void *out, int index, size_t x, size_t y, REAL p,
		REAL q, struct SChunk chunk, struct SMappingRanges ranges);
void calc_seamless_xz(void *out, int index, size_t x, size_t y, REAL p,
		REAL q, struct SChunk chunk, struct SMappingRanges ranges);
void calc_seamless_yz(void *out, int index, size_t x, size_t y, REAL p,
		REAL q, struct SChunk chunk, struct SMappingRanges ranges);
void calc_seamless_xyz(void *out, int index, size_t x, size_t y, REAL p,
		REAL q, struct SChunk chunk, struct SMappingRanges ranges);

void* map2D(void *out, calc_seamless calc_seamless,
		struct SMappingRanges ranges, size_t width, size_t height, REAL z);

#ifdef __cplusplus
}
#endif

#endif /* IMAGING_H_ */
