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
 * noise_gen.c
 *
 *  Created on: Jul 26, 2021
 *      Author: Erwin Müller
 */

#ifndef USE_OPENCL
#include <noise_gen.h>
#include <hashing.h>
#include <utility.h>
#include "noise_lut.h"
#endif // USE_OPENCL

REAL noInterp(REAL t) {
	return 0;
}

REAL linearInterp(REAL t) {
	return t;
}

REAL hermiteInterp(REAL t) {
	return (t * t * (3 - 2 * t));
}

REAL quinticInterp(REAL t) {
	return t * t * t * (t * (t * 6 - 15) + 10);
}

// Distance

REAL distEuclid2(vector2 a, vector2 b) {
	REAL dx = b.x - a.x;
	REAL dy = b.y - a.y;
	return sqrt(dx * dx + dy * dy);
}

REAL distEuclid3(vector3 a, vector3 b) {
	REAL dx = b.x - a.x;
	REAL dy = b.y - a.y;
	REAL dz = b.z - a.z;
	return sqrt(dx * dx + dy * dy + dz * dz);
}

REAL distManhattan2(vector2 a, vector2 b) {
	REAL dx = fabs(b.x - a.x);
	REAL dy = fabs(b.y - a.y);
	return dx + dy;
}

REAL distManhattan3(vector3 a, vector3 b) {
	REAL dx = fabs(b.x - a.x);
	REAL dy = fabs(b.y - a.y);
	REAL dz = fabs(b.z - a.z);
	return dx + dy + dz;
}

REAL distGreatestAxis2(vector2 a, vector2 b) {
	REAL dx = fabs(b.x - a.x);
	REAL dy = fabs(b.y - a.y);
	return fmax(dx, dy);
}

REAL distGreatestAxis3(vector3 a, vector3 b) {
	REAL dx = fabs(b.x - a.x);
	REAL dy = fabs(b.y - a.y);
	REAL dz = fabs(b.z - a.z);
	return fmax(dz,fmax(dx,dy));
}

REAL distLeastAxis2(vector2 a, vector2 b) {
	REAL dx = fabs(b.x - a.x);
	REAL dy = fabs(b.y - a.y);
	return fmin(dx,dy);
}

REAL distLeastAxis3(vector3 a, vector3 b) {
	REAL dx = fabs(b.x - a.x);
	REAL dy = fabs(b.y - a.y);
	REAL dz = fabs(b.z - a.z);
	return fmin(dz,fmin(dx,dy));
}

uint compute_hash_double_2(vector2 v, unsigned int seed) {
	//double d[3]={x,y,(double)seed};
	//unsigned int s=sizeof(d) / sizeof(unsigned int);
	//return xor_fold_hash(fnv_32_a_buf(d, s));

	uint hash = FNV_32_INIT;
	hash = fnv_32_a_combine(hash, (uint) (v.x * (REAL) 1000000));
	hash = fnv_32_a_combine(hash, (uint) (v.y * (REAL) 1000000));
	hash = fnv_32_a_combine(hash, seed);
	return xor_fold_hash(hash);
}

REAL value_noise_2(vector2 v, int ix, int iy, uint seed) {
	uint n = (hash_coords_2(ix, iy, seed)) % 256;
	REAL noise = (REAL) n / 255.0;
	return noise * 2.0 - 1.0;
}

REAL value_noise_3(vector3 v, int ix, int iy, int iz, uint seed) {
	uint n = (hash_coords_3(ix, iy, iz, seed)) % 256;
	REAL noise = (REAL) n / (255.0);
	return noise * 2.0 - 1.0;
}

REAL grad_noise_2(vector2 v, int ix, int iy, unsigned int seed) {
	uint hash = hash_coords_2(ix, iy, seed) % 8;
	REAL *vec = &gradient2D_lut[hash][0];

	REAL dx = v.x - (REAL) ix;
	REAL dy = v.y - (REAL) iy;

	return (dx * vec[0] + dy * vec[1]);
}

// Worker noise functions

typedef REAL (*worker_noise_2)(vector2, int, int, uint);
typedef REAL (*worker_noise_3)(vector3, int, int, int, uint);
typedef REAL (*worker_noise_4)(vector4, int, int, int, int, uint);
typedef REAL (*worker_noise_8)(vector8, int, int, int, int, int, int, int, int, uint);
typedef REAL (*worker_noise_16)(vector16, int, int, int, int, int, int, int, int, uint);

// Edge/Face/Cube/Hypercube interpolation

REAL interp_X_2(vector2 v, REAL xs, int x0, int x1, int iy,
		uint seed, worker_noise_2 noisefunc) {
	REAL v1 = noisefunc(v, x0, iy, seed);
	REAL v2 = noisefunc(v, x1, iy, seed);
	return lerp(xs, v1, v2);
}

REAL interp_XY_2(vector2 v, REAL xs, REAL ys, int x0, int x1, int y0,
		int y1, uint seed, worker_noise_2 noisefunc) {
	REAL v1 = interp_X_2(v, xs, x0, x1, y0, seed, noisefunc);
	REAL v2 = interp_X_2(v, xs, x0, x1, y1, seed, noisefunc);
	return lerp(ys, v1, v2);
}

REAL interp_X_3(vector3 v, REAL xs, int x0, int x1, int iy, int iz,
		uint seed, worker_noise_3 noisefunc) {
	REAL v1 = noisefunc(v, x0, iy, iz, seed);
	REAL v2 = noisefunc(v, x1, iy, iz, seed);
	return lerp(xs, v1, v2);
}

REAL interp_XY_3(vector3 v, REAL xs, REAL ys, int x0, int x1,
		int y0, int y1, int iz, uint seed, worker_noise_3 noisefunc) {
	REAL v1 = interp_X_3(v, xs, x0, x1, y0, iz, seed, noisefunc);
	REAL v2 = interp_X_3(v, xs, x0, x1, y1, iz, seed, noisefunc);
	return lerp(ys, v1, v2);
}

REAL interp_XYZ_3(vector3 v, REAL xs, REAL ys, REAL zs, int x0, int x1, int y0,
		int y1, int z0, int z1, uint seed, worker_noise_3 noisefunc) {
	REAL v1 = interp_XY_3(v, xs, ys, x0, x1, y0, y1, z0, seed, noisefunc);
	REAL v2 = interp_XY_3(v, xs, ys, x0, x1, y0, y1, z1, seed, noisefunc);
	return lerp(zs, v1, v2);
}

// The usable noise functions

REAL value_noise2D(vector2 v, uint seed, interp_func interp) {
	int2 v0 = fast_floor2(v);
	int2 v1 = v0 + 1;

	REAL xs = interp((v.x - (REAL) v0.x));
	REAL ys = interp((v.y - (REAL) v0.y));

	return interp_XY_2(v, xs, ys, v0.x, v1.x, v0.y, v1.y, seed, value_noise_2);
}

REAL value_noise3D(vector3 v, uint seed, interp_func interp) {
	int3 v0 = fast_floor3(v);
	int3 v1 = v0 + 1;

	REAL xs = interp((v.x - (REAL) v0.x));
	REAL ys = interp((v.y - (REAL) v0.y));
	REAL zs = interp((v.z - (REAL) v0.z));

	return interp_XYZ_3(v, xs, ys, zs, v0.x, v1.x, v0.y, v1.y, v0.z, v1.z, seed,
			value_noise_3);
}

REAL gradient_noise2D(vector2 v, uint seed, interp_func interp) {
	int2 v0 = fast_floor2(v);
	int2 v1 = v0 + 1;

	REAL xs = interp((v.x - (REAL) v0.x));
	REAL ys = interp((v.y - (REAL) v0.y));

	return interp_XY_2(v, xs, ys, v0.x, v1.x, v0.y, v1.y, seed, grad_noise_2);
}

REAL gradval_noise2D(vector2 v, uint seed, interp_func interp) {
	return value_noise2D(v, seed, interp)
			+ gradient_noise2D(v, seed, interp);
}

REAL white_noise2D(vector2 v, uint seed, interp_func interp) {
	unsigned char hash = compute_hash_double_2(v, seed);
	return whitenoise_lut[hash];
}

void add_dist(REAL *f, REAL *disp, REAL testdist, REAL testdisp) {
	int index;
	// Compare the given distance to the ones already in f
	if (testdist < f[3]) {
		index = 3;
		while (index > 0 && testdist < f[index - 1])
			index--;
		for (int i = 3; i-- > index;) {
			f[i + 1] = f[i];
			disp[i + 1] = disp[i];
		}
		f[index] = testdist;
		disp[index] = testdisp;
	}
}

// Cellular functions. Compute distance (for cellular modules) and displacement (for voronoi modules)

void cellular_function2D(vector2 v, uint seed, REAL *f, REAL *disp,
		dist_func2 distance) {
//	int xint = fast_floor(x);
//	int yint = fast_floor(y);
	int2 vint = fast_floor2(v);

	for (int c = 0; c < 4; ++c) {
		f[c] = 99999.0;
		disp[c] = 0.0;
	}

	{
		vector2 vpos;
		for (int ycur = vint.y - 3; ycur <= vint.y + 3; ++ycur) {
			for (int xcur = vint.x - 3; xcur <= vint.x + 3; ++xcur) {
				vpos = (vector2)((REAL)xcur + value_noise_2(v, xcur, ycur, seed),
						(REAL)ycur + value_noise_2(v, xcur, ycur, seed + 1));
				REAL dist = distance(vpos, v);
				int2 vval = fast_floor2(vpos);
				REAL dsp = value_noise_2(v, vval.x, vval.y, seed + 3);
				add_dist(f, disp, dist, dsp);
			}
		}
	}
}

const REAL F2 = 0.36602540378443864676372317075294;
const REAL G2 = 0.21132486540518711774542560974902;
const REAL F3 = 1.0 / 3.0;
const REAL G3 = 1.0 / 6.0;


REAL simplex_noise2D(vector2 v, uint seed, interp_func interp) {
	REAL s = (v.x + v.y) * F2;
	int i = fast_floor(v.x + s);
	int j = fast_floor(v.y + s);

	REAL t = (i + j) * G2;
	REAL X0 = i - t;
	REAL Y0 = j - t;
	REAL x0 = v.x - X0;
	REAL y0 = v.y - Y0;

	int i1, j1;
	if (x0 > y0) {
		i1 = 1;
		j1 = 0;
	} else {
		i1 = 0;
		j1 = 1;
	}

	REAL x1 = x0 - (REAL) i1 + G2;
	REAL y1 = y0 - (REAL) j1 + G2;
	REAL x2 = x0 - 1.0 + 2.0 * G2;
	REAL y2 = y0 - 1.0 + 2.0 * G2;

	// Hash the triangle coordinates to index the gradient table
	unsigned int h0 = hash_coords_2(i, j, seed) % 8;
	unsigned int h1 = hash_coords_2(i + i1, j + j1, seed) % 8;
	unsigned int h2 = hash_coords_2(i + 1, j + 1, seed) % 8;

	// Now, index the tables
	REAL *g0 = &gradient2D_lut[h0][0];
	REAL *g1 = &gradient2D_lut[h1][0];
	REAL *g2 = &gradient2D_lut[h2][0];

	REAL n0, n1, n2;
	// Calculate the contributions from the 3 corners
	REAL t0 = 0.5 - x0 * x0 - y0 * y0;
	if (t0 < 0)
		n0 = 0;
	else {
		t0 *= t0;
		n0 = t0 * t0 * array_dot2(g0, x0, y0);
	}

	REAL t1 = 0.5 - x1 * x1 - y1 * y1;
	if (t1 < 0)
		n1 = 0;
	else {
		t1 *= t1;
		n1 = t1 * t1 * array_dot2(g1, x1, y1);
	}

	REAL t2 = 0.5 - x2 * x2 - y2 * y2;
	if (t2 < 0)
		n2 = 0;
	else {
		t2 *= t2;
		n2 = t2 * t2 * array_dot2(g2, x2, y2);
	}

	// Add contributions together
	return (40.0 * (n0 + n1 + n2));
}
