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
 * imaging.c
 *
 *  Created on: Aug 7, 2021
 *      Author: Erwin Müller
 */

#ifndef USE_OPENCL
#ifdef USE_THREAD
#include <pthread.h>
#ifdef _GNU_SOURCE
#include <sys/sysinfo.h>
#endif // _GNU_SOURCE
#endif // USE_THREAD
#include "imaging.h"
#endif // USE_OPENCL

#define PI2 (2.0*M_PI)

struct SMappingRanges create_ranges_default() {
	struct SMappingRanges r;
	r.mapx0 = r.mapy0 = r.mapz0 = r.loopx0 = r.loopy0 = r.loopz0 = -1;
	r.mapx1 = r.mapy1 = r.mapz1 = r.loopx1 = r.loopy1 = r.loopz1 = 1;
	return r;
}

struct SMappingRanges create_ranges_map2D(REAL x0, REAL x1, REAL y0, REAL y1) {
	struct SMappingRanges r;
	r.mapx0 = x0;
	r.mapx1 = x1;
	r.mapy0 = y0;
	r.mapy1 = y1;
	r.mapz0 = 0;
	r.mapz1 = 0;

	r.loopx0 = x0;
	r.loopx1 = x1;
	r.loopy0 = y0;
	r.loopy1 = y1;
	r.loopz0 = 1;
	r.loopz1 = 1;
	return r;
}

struct SMappingRanges create_ranges_map3D(REAL x0, REAL x1, REAL y0, REAL y1,
		REAL z0, REAL z1) {
	struct SMappingRanges r;
	r.mapx0 = x0;
	r.mapx1 = x1;
	r.mapy0 = y0;
	r.mapy1 = y1;
	r.mapz0 = z0;
	r.mapz1 = z1;

	r.loopx0 = x0;
	r.loopx1 = x1;
	r.loopy0 = y0;
	r.loopy1 = y1;
	r.loopz0 = z0;
	r.loopz1 = z1;
	return r;
}

void calc_seamless_none(void *out, int index, size_t x, size_t y, REAL p,
		REAL q, struct SChunk chunk, struct SMappingRanges ranges) {
	vector3* v = ((vector3*) (out));
	v[index].x = ranges.mapx0 + p * (ranges.mapx1 - ranges.mapx0);
	v[index].y = ranges.mapy0 + q * (ranges.mapy1 - ranges.mapy0);
	v[index].z = chunk.z;
}

void calc_seamless_x(void *out, int index, size_t x, size_t y, REAL p,
		REAL q, struct SChunk chunk, struct SMappingRanges ranges) {
	REAL dx, dy;
	vector4* v = ((vector4*) (out));
	dx = ranges.loopx1 - ranges.loopx0;
	dy = ranges.mapy1 - ranges.mapy0;
	p = p * (ranges.mapx1 - ranges.mapx0) / (ranges.loopx1 - ranges.loopx0);
	v[index].x = ranges.loopx0 + cos(p * PI2) * dx / PI2;
	v[index].y = ranges.loopx0 + sin(p * PI2) * dx / PI2;
	v[index].z = ranges.mapy0 + q * dy;
	v[index].w = chunk.z;
}

void calc_seamless_y(void *out, int index, size_t x, size_t y, REAL p,
		REAL q, struct SChunk chunk, struct SMappingRanges ranges) {
	REAL dx, dy;
	vector4* v = ((vector4*) (out));
	dx = ranges.mapx1 - ranges.mapx0;
	dy = ranges.loopy1 - ranges.loopy0;
	q = q * (ranges.mapy1 - ranges.mapy0) / (ranges.loopy1 - ranges.loopy0);
	v[index].x = ranges.mapx0 + p * dx;
	v[index].y = ranges.loopy0 + cos(q * PI2) * dy / PI2;
	v[index].z = ranges.loopy0 + sin(q * PI2) * dy / PI2;
	v[index].w = chunk.z;
}

void calc_seamless_z(void *out, int index, size_t x, size_t y, REAL p,
		REAL q, struct SChunk chunk, struct SMappingRanges ranges) {
	REAL dx, dy, dz, r, zval;
	vector4* v = ((vector4*) (out));
	dx = ranges.mapx1 - ranges.mapx0;
	dy = ranges.mapy1 - ranges.mapy0;
	dz = ranges.loopz1 - ranges.loopz0;
	v[index].x = ranges.mapx0 + p * dx;
	v[index].y = ranges.mapy0 + p * dx;
	r = (chunk.z - ranges.mapz0) / (ranges.mapz1 - ranges.mapz0);
	zval = r * (ranges.mapz1 - ranges.mapz0) / (ranges.loopz1 - ranges.loopz0);
	v[index].z = ranges.loopz0 + cos(zval * PI2) * dz / PI2;
	v[index].w = ranges.loopz0 + sin(zval * PI2) * dz / PI2;
}

void calc_seamless_xy(void *out, int index, size_t x, size_t y, REAL p,
		REAL q, struct SChunk chunk, struct SMappingRanges ranges) {
	REAL dx, dy;
	vector8* v = ((vector8*) (out));
	dx = ranges.loopx1 - ranges.loopx0;
	dy = ranges.loopy1 - ranges.loopy0;
	p = p * (ranges.mapx1 - ranges.mapx0) / (ranges.loopx1 - ranges.loopx0);
	q = q * (ranges.mapy1 - ranges.mapy0) / (ranges.loopy1 - ranges.loopy0);
	v[index].x = ranges.loopx0 + cos(p * PI2) * dx / PI2;
	v[index].y = ranges.loopx0 + sin(p * PI2) * dx / PI2;
	v[index].z = ranges.loopy0 + cos(q * PI2) * dy / PI2;
	v[index].w = ranges.loopy0 + sin(q * PI2) * dy / PI2;
	v[index].s4 = chunk.z;
	v[index].s5 = 0;
}

void calc_seamless_xz(void *out, int index, size_t x, size_t y, REAL p,
		REAL q, struct SChunk chunk, struct SMappingRanges ranges) {
	REAL dx, dy, dz, r, zval;
	vector8* v = ((vector8*) (out));
	dx = ranges.loopx1 - ranges.loopx0;
	dy = ranges.mapy1 - ranges.mapy0;
	dz = ranges.loopz1 - ranges.loopz0;
	r = (chunk.z - ranges.mapz0) / (ranges.mapz1 - ranges.mapz0);
	zval = r * (ranges.mapx1 - ranges.mapz0) / (ranges.loopz1 - ranges.loopz0);
	p = p * (ranges.mapx1 - ranges.mapx0) / (ranges.loopx1 - ranges.loopx0);
	v[index].x = ranges.loopx0 + cos(p * PI2) * dx / PI2;
	v[index].y = ranges.loopx0 + sin(p * PI2) * dx / PI2;
	v[index].z = ranges.mapy0 + q * dy;
	v[index].w = ranges.loopz0 + cos(zval * PI2) * dz / PI2;
	v[index].s4 = ranges.loopz0 + sin(zval * PI2) * dz / PI2;
	v[index].s5 = 0;
}

void calc_seamless_yz(void *out, int index, size_t x, size_t y, REAL p,
		REAL q, struct SChunk chunk, struct SMappingRanges ranges) {
	REAL dx, dy, dz, r, zval;
	vector8* v = ((vector8*) (out));
	dx = ranges.mapx1 - ranges.mapx0;
	dy = ranges.loopy1 - ranges.loopy0;
	dz = ranges.loopz1 - ranges.loopz0;
	r = (chunk.z - ranges.mapz0) / (ranges.mapz1 - ranges.mapz0);
	zval = r * (ranges.mapz1 - ranges.mapz0) / (ranges.loopz1 - ranges.loopz0);
	q = q * (ranges.mapy1 - ranges.mapy0) / (ranges.loopy1 - ranges.loopy0);
	v[index].x = ranges.mapx0 + p * dx;
	v[index].y = ranges.loopy0 + cos(q * PI2) * dy / PI2;
	v[index].z = ranges.loopy0 + sin(q * PI2) * dy / PI2;
	v[index].w = ranges.loopz0 + cos(zval * PI2) * dz / PI2;
	v[index].s4 = ranges.loopz0 + sin(zval * PI2) * dz / PI2;
	v[index].s5 = 0;
}

void calc_seamless_xyz(void *out, int index, size_t x, size_t y, REAL p,
		REAL q, struct SChunk chunk, struct SMappingRanges ranges) {
	REAL dx, dy, dz, r, zval;
	vector8* v = ((vector8*) (out));
	dx = ranges.loopx1 - ranges.loopx0;
	dy = ranges.loopy1 - ranges.loopy0;
	dz = ranges.loopz1 - ranges.loopz0;
	p = p * (ranges.mapx1 - ranges.mapx0) / (ranges.loopx1 - ranges.loopx0);
	q = q * (ranges.mapy1 - ranges.mapy0) / (ranges.loopy1 - ranges.loopy0);
	r = (chunk.z - ranges.mapz0) / (ranges.mapz1 - ranges.mapz0);
	zval = r * (ranges.mapz1 - ranges.mapz0) / (ranges.loopz1 - ranges.loopz0);
	v[index].x = ranges.loopx0 + cos(p * PI2) * dx / PI2;
	v[index].y = ranges.loopx0 + sin(p * PI2) * dx / PI2;
	v[index].z = ranges.loopy0 + cos(q * PI2) * dy / PI2;
	v[index].w = ranges.loopy0 + sin(q * PI2) * dy / PI2;
	v[index].s4 = ranges.loopz0 + cos(zval * PI2) * dz / PI2;
	v[index].s5 = ranges.loopz0 + sin(zval * PI2) * dz / PI2;
}

void* map2DChunk(void *vargp) {
	struct SChunk chunk = *(struct SChunk*)(vargp);
	struct SMappingRanges ranges = chunk.ranges;
	for (int x = 0; x < chunk.width; ++x) {
		for (int y = 0; y < chunk.chunkheight; ++y) {
			int realy = y + chunk.chunkyoffset;
			int index = chunk.chunkyoffset * chunk.height + y * chunk.width + x;
			REAL p = (REAL) x / (REAL) (chunk.width);
			REAL q = (REAL) realy / (REAL) (chunk.height);
			chunk.calc_seamless(chunk.out, index, 3, y, p, q, chunk, ranges);
		}
	}
	return NULL;
}

#ifndef USE_OPENCL
#ifdef USE_THREAD
/**
 * From https://stackoverflow.com/questions/7341046/posix-equivalent-of-boostthreadhardware-concurrency
 */
int hardware_concurrency() {
#if defined(PTW32_VERSION) || defined(__hpux)
    return pthread_num_processors_np();
#elif defined(__APPLE__) || defined(__FreeBSD__)
    int count;
    size_t size=sizeof(count);
    return sysctlbyname("hw.ncpu",&count,&size,NULL,0)?0:count;
#elif defined(BOOST_HAS_UNISTD_H) && defined(_SC_NPROCESSORS_ONLN)
    int const count=sysconf(_SC_NPROCESSORS_ONLN);
    return (count>0)?count:0;
#elif defined(_GNU_SOURCE)
    return get_nprocs();
#else
	return 0;
#endif
}
#endif // USE_THREAD
#endif // USE_OPENCL

void* map2D(void *out, calc_seamless calc_seamless,
		struct SMappingRanges ranges, size_t width, size_t height, REAL z) {
#if !defined(USE_THREAD) && !defined(USE_THREAD)
	struct SChunk chunk;
	chunk.calc_seamless = calc_seamless;
	chunk.out = out;
	chunk.width = width;
	chunk.height = height;
	chunk.chunkheight = height;
	chunk.chunkyoffset = 0;
	chunk.ranges = ranges;
	chunk.z = z;
    map2DChunk(&chunk);
#else
	int threadcount = hardware_concurrency();
	int chunksize = floor(height / threadcount);
	if (chunksize == 0) {
		chunksize = height;
		threadcount = 1;
	}
	pthread_t threads[threadcount];
	struct SChunk* chunks[threadcount];
	for (int thread = 0; thread < threadcount; ++thread) {
		chunks[thread] = malloc(sizeof(struct SChunk));
		chunks[thread]->calc_seamless = calc_seamless;
		chunks[thread]->out = out;
		chunks[thread]->width = width;
		chunks[thread]->height = height;
		int offsety = thread * chunksize;
		if (thread == threadcount - 1) {
			chunks[thread]->chunkheight = height - (chunksize * (threadcount - 1));
		} else {
			chunks[thread]->chunkheight = chunksize;
		}
		chunks[thread]->chunkyoffset = offsety;
		chunks[thread]->ranges = ranges;
		chunks[thread]->z = z;
		pthread_create(&threads[thread], NULL, map2DChunk, chunks[thread]);
	}
	for (int c = 0; c < threadcount; ++c) {
		pthread_join(threads[c], NULL);
		free(chunks[c]);
	}
#endif // USE_THREAD && USE_THREAD
	return out;
}

void calc_seamless_no_z_none(void *out, int index, size_t x, size_t y, REAL p,
		REAL q, struct SChunk chunk, struct SMappingRanges ranges) {
	vector2* v = ((vector2*) (out));
	v[index].x = ranges.mapx0 + p * (ranges.mapx1 - ranges.mapx0);
	v[index].y = ranges.mapy0 + q * (ranges.mapy1 - ranges.mapy0);
}

void calc_seamless_no_z_x(void *out, int index, size_t x, size_t y, REAL p,
		REAL q, struct SChunk chunk, struct SMappingRanges ranges) {
	REAL dx, dy;
	vector3* v = ((vector3*) (out));
	dx = ranges.loopx1 - ranges.loopx0;
	dy = ranges.mapy1 - ranges.mapy0;
	p = p * (ranges.mapx1 - ranges.mapx0) / (ranges.loopx1 - ranges.loopx0);
	v[index].x = ranges.loopx0 + cos(p * PI2) * dx / PI2;
	v[index].y = ranges.loopx0 + sin(p * PI2) * dx / PI2;
	v[index].z = ranges.mapy0 + q * dy;
}

void calc_seamless_no_z_y(void *out, int index, size_t x, size_t y, REAL p,
		REAL q, struct SChunk chunk, struct SMappingRanges ranges) {
	REAL dx, dy;
	vector3* v = ((vector3*) (out));
	dx = ranges.mapx1 - ranges.mapx0;
	dy = ranges.loopy1 - ranges.loopy0;
	q = q * (ranges.mapy1 - ranges.mapy0) / (ranges.loopy1 - ranges.loopy0);
	v[index].x = ranges.mapx0 + p * dx;
	v[index].y = ranges.loopy0 + cos(q * PI2) * dy / PI2;
	v[index].z = ranges.loopy0 + sin(q * PI2) * dy / PI2;
}

void calc_seamless_no_z_z(void *out, int index, size_t x, size_t y, REAL p,
		REAL q, struct SChunk chunk, struct SMappingRanges ranges) {
	REAL dx, dy, dz, r, zval;
	vector4* v = ((vector4*) (out));
	dx = ranges.mapx1 - ranges.mapx0;
	dy = ranges.mapy1 - ranges.mapy0;
	dz = ranges.loopz1 - ranges.loopz0;
	v[index].x = ranges.mapx0 + p * dx;
	v[index].y = ranges.mapy0 + p * dx;
	r = (chunk.z - ranges.mapz0) / (ranges.mapz1 - ranges.mapz0);
	zval = r * (ranges.mapz1 - ranges.mapz0) / (ranges.loopz1 - ranges.loopz0);
	v[index].z = ranges.loopz0 + cos(zval * PI2) * dz / PI2;
	v[index].w = ranges.loopz0 + sin(zval * PI2) * dz / PI2;
}

void calc_seamless_no_z_xy(void *out, int index, size_t x, size_t y, REAL p,
		REAL q, struct SChunk chunk, struct SMappingRanges ranges) {
	REAL dx, dy;
	vector4* v = ((vector4*) (out));
	dx = ranges.loopx1 - ranges.loopx0;
	dy = ranges.loopy1 - ranges.loopy0;
	p = p * (ranges.mapx1 - ranges.mapx0) / (ranges.loopx1 - ranges.loopx0);
	q = q * (ranges.mapy1 - ranges.mapy0) / (ranges.loopy1 - ranges.loopy0);
	v[index].x = ranges.loopx0 + cos(p * PI2) * dx / PI2;
	v[index].y = ranges.loopx0 + sin(p * PI2) * dx / PI2;
	v[index].z = ranges.loopy0 + cos(q * PI2) * dy / PI2;
	v[index].w = ranges.loopy0 + sin(q * PI2) * dy / PI2;
}

void calc_seamless_no_z_xz(void *out, int index, size_t x, size_t y, REAL p,
		REAL q, struct SChunk chunk, struct SMappingRanges ranges) {
	REAL dx, dy, dz, r, zval;
	vector8* v = ((vector8*) (out));
	dx = ranges.loopx1 - ranges.loopx0;
	dy = ranges.mapy1 - ranges.mapy0;
	dz = ranges.loopz1 - ranges.loopz0;
	r = (chunk.z - ranges.mapz0) / (ranges.mapz1 - ranges.mapz0);
	zval = r * (ranges.mapx1 - ranges.mapz0) / (ranges.loopz1 - ranges.loopz0);
	p = p * (ranges.mapx1 - ranges.mapx0) / (ranges.loopx1 - ranges.loopx0);
	v[index].x = ranges.loopx0 + cos(p * PI2) * dx / PI2;
	v[index].y = ranges.loopx0 + sin(p * PI2) * dx / PI2;
	v[index].z = ranges.mapy0 + q * dy;
	v[index].w = ranges.loopz0 + cos(zval * PI2) * dz / PI2;
	v[index].s4 = ranges.loopz0 + sin(zval * PI2) * dz / PI2;
	v[index].s5 = 0;
}

void calc_seamless_no_z_yz(void *out, int index, size_t x, size_t y, REAL p,
		REAL q, struct SChunk chunk, struct SMappingRanges ranges) {
	REAL dx, dy, dz, r, zval;
	vector8* v = ((vector8*) (out));
	dx = ranges.mapx1 - ranges.mapx0;
	dy = ranges.loopy1 - ranges.loopy0;
	dz = ranges.loopz1 - ranges.loopz0;
	r = (chunk.z - ranges.mapz0) / (ranges.mapz1 - ranges.mapz0);
	zval = r * (ranges.mapz1 - ranges.mapz0) / (ranges.loopz1 - ranges.loopz0);
	q = q * (ranges.mapy1 - ranges.mapy0) / (ranges.loopy1 - ranges.loopy0);
	v[index].x = ranges.mapx0 + p * dx;
	v[index].y = ranges.loopy0 + cos(q * PI2) * dy / PI2;
	v[index].z = ranges.loopy0 + sin(q * PI2) * dy / PI2;
	v[index].w = ranges.loopz0 + cos(zval * PI2) * dz / PI2;
	v[index].s4 = ranges.loopz0 + sin(zval * PI2) * dz / PI2;
	v[index].s5 = 0;
}

void calc_seamless_no_z_xyz(void *out, int index, size_t x, size_t y, REAL p,
		REAL q, struct SChunk chunk, struct SMappingRanges ranges) {
	REAL dx, dy, dz, r, zval;
	vector8* v = ((vector8*) (out));
	dx = ranges.loopx1 - ranges.loopx0;
	dy = ranges.loopy1 - ranges.loopy0;
	dz = ranges.loopz1 - ranges.loopz0;
	p = p * (ranges.mapx1 - ranges.mapx0) / (ranges.loopx1 - ranges.loopx0);
	q = q * (ranges.mapy1 - ranges.mapy0) / (ranges.loopy1 - ranges.loopy0);
	r = (chunk.z - ranges.mapz0) / (ranges.mapz1 - ranges.mapz0);
	zval = r * (ranges.mapz1 - ranges.mapz0) / (ranges.loopz1 - ranges.loopz0);
	v[index].x = ranges.loopx0 + cos(p * PI2) * dx / PI2;
	v[index].y = ranges.loopx0 + sin(p * PI2) * dx / PI2;
	v[index].z = ranges.loopy0 + cos(q * PI2) * dy / PI2;
	v[index].w = ranges.loopy0 + sin(q * PI2) * dy / PI2;
	v[index].s4 = ranges.loopz0 + cos(zval * PI2) * dz / PI2;
	v[index].s5 = ranges.loopz0 + sin(zval * PI2) * dz / PI2;
}

void* map2DChunkNoZ(void *vargp) {
	struct SChunk chunk = *(struct SChunk*)(vargp);
	struct SMappingRanges ranges = chunk.ranges;
	for (int x = 0; x < chunk.width; ++x) {
		for (int y = 0; y < chunk.chunkheight; ++y) {
			int realy = y + chunk.chunkyoffset;
			int index = chunk.chunkyoffset * chunk.height + y * chunk.width + x;
			REAL p = (REAL) x / (REAL) (chunk.width);
			REAL q = (REAL) realy / (REAL) (chunk.height);
			chunk.calc_seamless(chunk.out, index, 3, y, p, q, chunk, ranges);
		}
	}
	return NULL;
}

void* map2DNoZ(void *out, calc_seamless calc_seamless,
		struct SMappingRanges ranges, size_t width, size_t height) {
#if !defined(USE_THREAD) && !defined(USE_THREAD)
	struct SChunk chunk;
	chunk.calc_seamless = calc_seamless;
	chunk.out = out;
	chunk.width = width;
	chunk.height = height;
	chunk.chunkheight = height;
	chunk.chunkyoffset = 0;
	chunk.ranges = ranges;
	chunk.z = 0;
    map2DChunkNoZ(&chunk);
#else
	int threadcount = hardware_concurrency();
	int chunksize = floor(height / threadcount);
	if (chunksize == 0) {
		chunksize = height;
		threadcount = 1;
	}
	pthread_t threads[threadcount];
	struct SChunk* chunks[threadcount];
	for (int thread = 0; thread < threadcount; ++thread) {
		chunks[thread] = malloc(sizeof(struct SChunk));
		chunks[thread]->calc_seamless = calc_seamless;
		chunks[thread]->out = out;
		chunks[thread]->width = width;
		chunks[thread]->height = height;
		int offsety = thread * chunksize;
		if (thread == threadcount - 1) {
			chunks[thread]->chunkheight = height - (chunksize * (threadcount - 1));
		} else {
			chunks[thread]->chunkheight = chunksize;
		}
		chunks[thread]->chunkyoffset = offsety;
		chunks[thread]->ranges = ranges;
		chunks[thread]->z = 0;
		pthread_create(&threads[thread], NULL, map2DChunkNoZ, chunks[thread]);
	}
	for (int c = 0; c < threadcount; ++c) {
		pthread_join(threads[c], NULL);
		free(chunks[c]);
	}
#endif // USE_THREAD && USE_THREAD
	return out;
}

REAL* scaleToRange(REAL *data, size_t count, REAL min, REAL max, REAL low, REAL high) {
	for (int i = 0; i < count; ++i) {
		REAL temp = data[i];
		temp = temp - min;
		REAL ftemp = temp / (max - min);
		REAL val = ftemp * (high - low);
		val = val + low;
		data[i] = val;
	}
	return data;
}

