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

struct SMappingRanges create_range_default() {
	struct SMappingRanges r;
	r.mapx0 = r.mapy0 = r.mapz0 = r.loopx0 = r.loopy0 = r.loopz0 = -1;
	r.mapx1 = r.mapy1 = r.mapz1 = r.loopx1 = r.loopy1 = r.loopz1 = 1;
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
	v[index].x = ranges.loopx0 + cos(p * M_PI_2) * dx / M_PI_2;
	v[index].y = ranges.loopx0 + sin(p * M_PI_2) * dx / M_PI_2;
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
	v[index].y = ranges.loopy0 + cos(q * M_PI_2) * dy / M_PI_2;
	v[index].z = ranges.loopy0 + sin(q * M_PI_2) * dy / M_PI_2;
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
	v[index].z = ranges.loopz0 + cos(zval * M_PI_2) * dz / M_PI_2;
	v[index].w = ranges.loopz0 + sin(zval * M_PI_2) * dz / M_PI_2;
}

void calc_seamless_xy(void *out, int index, size_t x, size_t y, REAL p,
		REAL q, struct SChunk chunk, struct SMappingRanges ranges) {
	REAL dx, dy;
	vector8* v = ((vector8*) (out));
	dx = ranges.loopx1 - ranges.loopx0;
	dy = ranges.loopy1 - ranges.loopy0;
	p = p * (ranges.mapx1 - ranges.mapx0) / (ranges.loopx1 - ranges.loopx0);
	q = q * (ranges.mapy1 - ranges.mapy0) / (ranges.loopy1 - ranges.loopy0);
	v[index].x = ranges.loopx0 + cos(p * M_PI_2) * dx / M_PI_2;
	v[index].y = ranges.loopx0 + sin(p * M_PI_2) * dx / M_PI_2;
	v[index].z = ranges.loopy0 + cos(q * M_PI_2) * dy / M_PI_2;
	v[index].w = ranges.loopy0 + sin(q * M_PI_2) * dy / M_PI_2;
	v[index].s4 = chunk.z;
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
	v[index].x = ranges.loopx0 + cos(p * M_PI_2) * dx / M_PI_2;
	v[index].y = ranges.loopx0 + sin(p * M_PI_2) * dx / M_PI_2;
	v[index].z = ranges.mapy0 + q * dy;
	v[index].w = ranges.loopz0 + cos(zval * M_PI_2) * dz / M_PI_2;
	v[index].s4 = ranges.loopz0 + sin(zval * M_PI_2) * dz / M_PI_2;
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
	v[index].y = ranges.loopy0 + cos(q * M_PI_2) * dy / M_PI_2;
	v[index].z = ranges.loopy0 + sin(q * M_PI_2) * dy / M_PI_2;
	v[index].w = ranges.loopz0 + cos(zval * M_PI_2) * dz / M_PI_2;
	v[index].s4 = ranges.loopz0 + sin(zval * M_PI_2) * dz / M_PI_2;
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
	v[index].x = ranges.loopx0 + cos(p * M_PI_2) * dx / M_PI_2;
	v[index].y = ranges.loopx0 + sin(p * M_PI_2) * dx / M_PI_2;
	v[index].z = ranges.loopy0 + cos(q * M_PI_2) * dy / M_PI_2;
	v[index].w = ranges.loopy0 + sin(q * M_PI_2) * dy / M_PI_2;
	v[index].s4 = ranges.loopz0 + cos(zval * M_PI_2) * dz / M_PI_2;
	v[index].s5 = ranges.loopz0 + sin(zval * M_PI_2) * dz / M_PI_2;
}

void* map2DChunk(void *vargp) {
	struct SChunk chunk = *(struct SChunk*)(vargp);
	struct SMappingRanges ranges = chunk.ranges;
	for (int x = 0; x < chunk.awidth; ++x) {
		for (int y = 0; y < chunk.chunkheight; ++y) {
			int realy = y + chunk.chunkyoffset;
			int index = chunk.chunkyoffset * chunk.aheight + y * chunk.awidth + x;
			//printf("[%d] off=%d %d/%d %d\n", pthread_self(), chunk.chunkyoffset, x, y, index);
			REAL p = (REAL) x / (REAL) (chunk.awidth);
			REAL q = (REAL) realy / (REAL) (chunk.aheight);
			chunk.calc_seamless(chunk.a, index, x, y, p, q, chunk, ranges);
		}
	}
	return NULL;
}

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

void* map2D(void *out, calc_seamless calc_seamless,
		struct SMappingRanges ranges, size_t width, size_t height, REAL z) {
#ifndef USE_THREAD
	struct SChunk chunk;
	chunk.calc_seamless = calc_seamless;
	chunk.a = out;
	chunk.awidth = width;
	chunk.aheight = height;
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
	for (int thread = 0; thread < threadcount; ++thread) {
		struct SChunk chunk;
		chunk.calc_seamless = calc_seamless;
		chunk.a = out;
		chunk.awidth = width;
		chunk.aheight = height;
		int offsety = thread * chunksize;
		if (thread == threadcount - 1) {
			chunk.chunkheight = height - (chunksize * (threadcount - 1));
		} else {
			chunk.chunkheight = chunksize;
		}
		chunk.chunkyoffset = offsety;
		chunk.ranges = ranges;
		chunk.z = z;
		pthread_create(&threads[thread], NULL, map2DChunk, &chunk);
		pthread_join(threads[thread], NULL);
	}
//	for (int c = 0; c < threadcount; ++c) {
//		pthread_join(threads[c], NULL);
//	}
#endif // USE_THREAD
	return out;
}
