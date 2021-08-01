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

REAL distEuclid2(REAL x1, REAL y1, REAL x2, REAL y2) {
	REAL dx = x2 - x1;
	REAL dy = y2 - y1;
	return sqrt(dx * dx + dy * dy);
}

REAL distEuclid3(REAL x1, REAL y1, REAL z1, REAL x2, REAL y2, REAL z2) {
	REAL dx = x2 - x1;
	REAL dy = y2 - y1;
	REAL dz = z2 - z1;
	return sqrt(dx * dx + dy * dy + dz * dz);
}

REAL distManhattan2(REAL x1, REAL y1, REAL x2, REAL y2) {
	REAL dx = fabs(x2 - x1);
	REAL dy = fabs(y2 - y1);
	return dx + dy;
}

REAL distManhattan3(REAL x1, REAL y1, REAL z1, REAL x2, REAL y2, REAL z2) {
	REAL dx = fabs(x2 - x1);
	REAL dy = fabs(y2 - y1);
	REAL dz = fabs(z2 - z1);
	//return fmax(dz,fmax(dx,dy));
	return dx + dy + dz;
}

REAL distManhattan4(REAL x1, REAL y1, REAL z1, REAL w1, REAL x2, REAL y2, REAL z2, REAL w2) {
	REAL dx = fabs(x2 - x1);
	REAL dy = fabs(y2 - y1);
	REAL dz = fabs(z2 - z1);
	REAL dw = fabs(w2 - w1);
	return dx + dy + dz + dw;
}

REAL distManhattan6(REAL x1, REAL y1, REAL z1, REAL w1, REAL u1, REAL v1, REAL x2, REAL y2, REAL z2, REAL w2, REAL u2, REAL v2) {
	REAL dx = fabs(x2 - x1);
	REAL dy = fabs(y2 - y1);
	REAL dz = fabs(z2 - z1);
	REAL dw = fabs(w2 - w1);
	REAL du = fabs(u2 - u1);
	REAL dv = fabs(v2 - v1);
	return dx + dy + dz + dw + du + dv;
}

REAL distGreatestAxis2(REAL x1, REAL y1, REAL x2, REAL y2) {
	REAL dx = fabs(x2 - x1);
	REAL dy = fabs(y2 - y1);
	return fmax(dx, dy);
}

REAL distGreatestAxis3(REAL x1, REAL y1, REAL z1, REAL x2, REAL y2, REAL z2) {
	REAL dx = fabs(x2 - x1);
	REAL dy = fabs(y2 - y1);
	REAL dz = fabs(z2 - z1);
	return fmax(dz,fmax(dx,dy));
}

REAL distLeastAxis2(REAL x1, REAL y1, REAL x2, REAL y2) {
	REAL dx = fabs(x2 - x1);
	REAL dy = fabs(y2 - y1);
	return fmin(dx,dy);
}

REAL distLeastAxis3(REAL x1, REAL y1, REAL z1, REAL x2, REAL y2, REAL z2) {
	REAL dx = fabs(x2 - x1);
	REAL dy = fabs(y2 - y1);
	REAL dz = fabs(z2 - z1);
	return fmin(dz,fmin(dx,dy));
}

REAL value_noise_2( REAL x, REAL y, int ix, int iy, uint seed) {
	uint n = (hash_coords_2(ix, iy, seed)) % 256;
	REAL noise = (REAL) n / 255.0;
	return noise * 2.0 - 1.0;
}

// Worker noise functions

typedef REAL (*worker_noise_2)(REAL, REAL, int, int, uint);
typedef REAL (*worker_noise_3)(REAL, REAL, REAL, int, int, int, uint);
typedef REAL (*worker_noise_4)(REAL, REAL, REAL, REAL, int, int, int, int, uint);
typedef REAL (*worker_noise_6)(REAL, REAL, REAL, REAL, REAL, REAL, int, int, int, int, int, int, uint);

// Edge/Face/Cube/Hypercube interpolation

REAL interp_X_2(REAL x, REAL y, REAL xs, int x0, int x1, int iy,
		uint seed, worker_noise_2 noisefunc) {
	REAL v1 = noisefunc(x, y, x0, iy, seed);
	REAL v2 = noisefunc(x, y, x1, iy, seed);
	return lerp(xs, v1, v2);
}

REAL interp_XY_2(REAL x, REAL y, REAL xs, REAL ys, int x0, int x1, int y0,
		int y1, uint seed, worker_noise_2 noisefunc) {
	REAL v1 = interp_X_2(x, y, xs, x0, x1, y0, seed, noisefunc);
	REAL v2 = interp_X_2(x, y, xs, x0, x1, y1, seed, noisefunc);
	return lerp(ys, v1, v2);
}

// The usable noise functions

REAL value_noise2D(REAL x, REAL y, uint seed, interp_func interp) {
	int x0 = fast_floor(x);
	int y0 = fast_floor(y);

	int x1 = x0 + 1;
	int y1 = y0 + 1;

	REAL xs = interp((x - (REAL) x0));
	REAL ys = interp((y - (REAL) y0));

	return interp_XY_2(x, y, xs, ys, x0, x1, y0, y1, seed, value_noise_2);
}
