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
	calc_seamless calc_coord;
	vector3 *a;
	int awidth, aheight;
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
