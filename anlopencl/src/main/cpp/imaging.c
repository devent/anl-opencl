/*
 * imaging.c
 *
 *  Created on: Aug 7, 2021
 *      Author: Erwin Müller
 */

#ifndef USE_OPENCL
#ifdef USE_THREAD
#include <thread>
#endif
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

void map2DChunk(struct SChunk chunk) {
	struct SMappingRanges ranges = chunk.ranges;
	for (int x = 0; x < chunk.awidth; ++x) {
		for (int y = 0; y < chunk.chunkheight; ++y) {
			int realy = y + chunk.chunkyoffset;
			int index = y * chunk.awidth + x;
			REAL p = (REAL) x / (REAL) (chunk.awidth);
			REAL q = (REAL) realy / (REAL) (chunk.aheight);
			chunk.calc_coord(chunk.a, index, x, y, p, q, chunk, ranges);
		}
	}
}

void* map2D(void *out, calc_seamless calc_seamless,
		struct SMappingRanges ranges, size_t width, size_t height, REAL z) {
#ifndef USE_THREAD
	struct SChunk chunk;
	chunk.calc_coord = calc_seamless;
	chunk.a = out;
	chunk.awidth = width;
	chunk.aheight = height;
	chunk.chunkheight = height;
	chunk.chunkyoffset = 0;
	chunk.ranges = ranges;
	chunk.z = z;
    map2DChunk(chunk);
#else
    unsigned threadcount=std::thread::hardware_concurrency();
    int chunksize=std::floor(a.height() / threadcount);
    std::vector<std::thread> threads;

    for(unsigned int thread=0; thread<threadcount; ++thread)
    {
        SChunk chunk(at);
        chunk.seamlessmode=seamlessmode;
        REAL *arr=a.getData();
        int offsety=thread*chunksize;
        chunk.a=&arr[offsety*a.width()];
        chunk.awidth=a.width();
        chunk.aheight=a.height();
        if(thread==threadcount-1) chunk.chunkheight=a.height()-(chunksize*(threadcount-1));
        else chunk.chunkheight=chunksize;
        chunk.chunkyoffset=offsety;
        chunk.kernel=k;
        chunk.ranges=ranges;
        chunk.z=z;
        threads.push_back(std::thread(map2DChunk, chunk));
    }

    for(unsigned int c=0; c<threads.size(); ++c)
    {
        threads[c].join();
    }
#endif // USE_THREAD
    return out;
}
