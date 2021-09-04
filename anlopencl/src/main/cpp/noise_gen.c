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
#include "qsort.h"
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

REAL distEuclid4(vector4 a, vector4 b) {
	REAL dx = b.x - a.x;
	REAL dy = b.y - a.y;
	REAL dz = b.z - a.z;
	REAL dw = b.z - a.z;
	return sqrt(dx * dx + dy * dy + dz * dz + dw * dw);
}

REAL distEuclid6(vector8 a, vector8 b) {
	REAL dx = b.x - a.x;
	REAL dy = b.y - a.y;
	REAL dz = b.z - a.z;
	REAL dw = b.z - a.z;
	REAL du = b.s4 - a.s4;
	REAL dv = b.s5 - a.s5;
	return sqrt(dx * dx + dy * dy + dz * dz + dw * dw + du * du + dv * dv);
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

REAL distManhattan4(vector4 a, vector4 b) {
	REAL dx = fabs(b.x - a.x);
	REAL dy = fabs(b.y - a.y);
	REAL dz = fabs(b.z - a.z);
	REAL dw = fabs(b.w - a.w);
	return dx + dy + dz + dw;
}

REAL distManhattan6(vector8 a, vector8 b) {
	REAL dx = fabs(b.x - a.x);
	REAL dy = fabs(b.y - a.y);
	REAL dz = fabs(b.z - a.z);
	REAL dw = fabs(b.w - a.w);
	REAL du = fabs(b.s4 - a.s4);
	REAL dv = fabs(b.s5 - a.s5);
	return dx + dy + dz + dw + du + dv;
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
	return fmax(dz, fmax(dx, dy));
}

REAL distGreatestAxis4(vector4 a, vector4 b) {
	REAL dx = fabs(b.x - a.x);
	REAL dy = fabs(b.y - a.y);
	REAL dz = fabs(b.z - a.z);
	REAL dw = fabs(b.w - a.w);
	return fmax(dw, fmax(dz, fmax(dx, dy)));
}

REAL distGreatestAxis6(vector8 a, vector8 b) {
	REAL dx = fabs(b.x - a.x);
	REAL dy = fabs(b.y - a.y);
	REAL dz = fabs(b.z - a.z);
	REAL dw = fabs(b.w - a.w);
	REAL du = fabs(b.s4 - a.s4);
	REAL dv = fabs(b.s5 - a.s5);
	return fmax(du, fmax(dv, fmax(dw, fmax(dz, fmax(dx, dy)))));
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
	return fmin(dz, fmin(dx, dy));
}

REAL distLeastAxis4(vector4 a, vector4 b) {
	REAL dx = fabs(b.x - a.x);
	REAL dy = fabs(b.y - a.y);
	REAL dz = fabs(b.z - a.z);
	REAL dw = fabs(b.w - a.w);
	return fmin(dw, fmin(dz, fmin(dx, dy)));
}

REAL distLeastAxis6(vector8 a, vector8 b) {
	REAL dx = fabs(b.x - a.x);
	REAL dy = fabs(b.y - a.y);
	REAL dz = fabs(b.z - a.z);
	REAL dw = fabs(b.w - a.w);
	REAL du = fabs(b.s4 - a.s4);
	REAL dv = fabs(b.s5 - a.s5);
	return fmin(du, fmin(dv, fmin(dw, fmin(dz, fmin(dx, dy)))));
}

uint compute_hash2(vector2 v, uint seed) {
	//REAL d[3]={x,y,(REAL)seed};
	//uint s=sizeof(d) / sizeof(uint);
	//return xor_fold_hash(fnv_32_a_buf(d, s));
	uint hash = FNV_32_INIT;
	hash = fnv_32_a_combine(hash, (uint) (v.x * (REAL) 1000000));
	hash = fnv_32_a_combine(hash, (uint) (v.y * (REAL) 1000000));
	hash = fnv_32_a_combine(hash, seed);
	return xor_fold_hash(hash);
}

uint compute_hash3(vector3 v, uint seed) {
	//REAL d[4]={x,y,z,(REAL)seed};
	//uint s=sizeof(d) / sizeof(uint);
	//return xor_fold_hash(fnv_32_a_buf(d, s));
	uint hash = FNV_32_INIT;
	hash = fnv_32_a_combine(hash, (uint) (v.x * (REAL) 1000000));
	hash = fnv_32_a_combine(hash, (uint) (v.y * (REAL) 1000000));
	hash = fnv_32_a_combine(hash, (uint) (v.z * (REAL) 1000000));
	hash = fnv_32_a_combine(hash, seed);
	return xor_fold_hash(hash);
}

uint compute_hash4(vector4 v, uint seed) {
	//REAL d[5]={x,y,z,w,(REAL)seed};
	//uint s=sizeof(d) / sizeof(uint);
	//return xor_fold_hash(fnv_32_a_buf(d, s));
	uint hash = FNV_32_INIT;
	hash = fnv_32_a_combine(hash, (uint) (v.x * (REAL) 1000000));
	hash = fnv_32_a_combine(hash, (uint) (v.y * (REAL) 1000000));
	hash = fnv_32_a_combine(hash, (uint) (v.z * (REAL) 1000000));
	hash = fnv_32_a_combine(hash, (uint) (v.w * (REAL) 1000000));
	hash = fnv_32_a_combine(hash, seed);
	return xor_fold_hash(hash);
}

uint compute_hash6(vector8 v, uint seed) {
	//REAL d[7]={x,y,z,w,u,v,(REAL)seed};
	//uint s=sizeof(d) / sizeof(uint);
	//return xor_fold_hash(fnv_32_a_buf(d, s));
	uint hash = FNV_32_INIT;
	hash = fnv_32_a_combine(hash, (uint) (v.x * (REAL) 1000000));
	hash = fnv_32_a_combine(hash, (uint) (v.y * (REAL) 1000000));
	hash = fnv_32_a_combine(hash, (uint) (v.z * (REAL) 1000000));
	hash = fnv_32_a_combine(hash, (uint) (v.w * (REAL) 1000000));
	hash = fnv_32_a_combine(hash, (uint) (v.s4 * (REAL) 1000000));
	hash = fnv_32_a_combine(hash, (uint) (v.s5 * (REAL) 1000000));
	hash = fnv_32_a_combine(hash, seed);
	return xor_fold_hash(hash);
}

// Worker noise functions

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

REAL value_noise_4(vector4 v, int ix, int iy, int iz, int iw, uint seed) {
	uint n = (hash_coords_4(ix, iy, iz, iw, seed)) % 256;
	REAL noise = (REAL) n / (255.0);
	return noise * 2.0 - 1.0;
}

REAL value_noise_6(vector8 v, int ix, int iy, int iz, int iw, int iu, int iv, uint seed) {
	uint n = (hash_coords_6(ix, iy, iz, iw, iu, iv, seed)) % 256;
	REAL noise = (REAL) n / (255.0);
	return noise * 2.0 - 1.0;
}

REAL grad_noise_2(vector2 v, int ix, int iy, uint seed) {
	uint hash = hash_coords_2(ix, iy, seed) % 8;
	REAL *vec = &gradient2D_lut[hash][0];

	REAL dx = v.x - (REAL) ix;
	REAL dy = v.y - (REAL) iy;

	return (dx * vec[0] + dy * vec[1]);
}

REAL grad_noise_3(vector3 v, int ix, int iy, int iz, uint seed) {
	uint hash = hash_coords_3(ix, iy, iz, seed) % 12;
	REAL *vec = &gradient3D_lut[hash][0];

	REAL dx = v.x - (REAL) ix;
	REAL dy = v.y - (REAL) iy;
	REAL dz = v.z - (REAL) iz;
	return (dx * vec[0] + dy * vec[1] + dz * vec[2]);
}

REAL grad_noise_4(vector4 v, int ix, int iy, int iz, int iw, uint seed) {
	uint hash = hash_coords_4(ix, iy, iz, iw, seed) % 64;
	REAL *vec = &gradient4D_lut[hash][0];

	REAL dx = v.x - (REAL) ix;
	REAL dy = v.y - (REAL) iy;
	REAL dz = v.z - (REAL) iz;
	REAL dw = v.w - (REAL) iw;

	return (dx * vec[0] + dy * vec[1] + dz * vec[2] + dw * vec[3]);
}

REAL grad_noise_6(vector8 v, int ix, int iy, int iz, int iw, int iu, int iv, uint seed) {
	uint hash = hash_coords_6(ix, iy, iz, iw, iu, iv, seed) % 192;
	REAL *vec = &gradient6D_lut[hash][0];

	REAL dx = v.x - (REAL) ix;
	REAL dy = v.y - (REAL) iy;
	REAL dz = v.z - (REAL) iz;
	REAL dw = v.w - (REAL) iw;
	REAL du = v.s4 - (REAL) iu;
	REAL dv = v.s5 - (REAL) iv;

	return (dx * vec[0] + dy * vec[1] + dz * vec[2] + dw * vec[3] + du * vec[4]
			+ dv * vec[5]);
}

// Worker noise functions

typedef REAL (*worker_noise_2)(vector2, int, int, uint);
typedef REAL (*worker_noise_3)(vector3, int, int, int, uint);
typedef REAL (*worker_noise_4)(vector4, int, int, int, int, uint);
typedef REAL (*worker_noise_6)(vector8, int, int, int, int, int, int, uint);

// Edge/Face/Cube/Hypercube interpolation

REAL interp_X_2(vector2 v, REAL xs, int x0, int x1, int iy, uint seed,
		worker_noise_2 noisefunc) {
	REAL v1 = noisefunc(v, x0, iy, seed);
	REAL v2 = noisefunc(v, x1, iy, seed);
	return lerp(xs, v1, v2);
}

REAL interp_XY_2(vector2 v, REAL xs, REAL ys, int x0, int x1, int y0, int y1,
		uint seed, worker_noise_2 noisefunc) {
	REAL v1 = interp_X_2(v, xs, x0, x1, y0, seed, noisefunc);
	REAL v2 = interp_X_2(v, xs, x0, x1, y1, seed, noisefunc);
	return lerp(ys, v1, v2);
}

REAL interp_X_3(vector3 v, REAL xs, int x0, int x1, int iy, int iz, uint seed,
		worker_noise_3 noisefunc) {
	REAL v1 = noisefunc(v, x0, iy, iz, seed);
	REAL v2 = noisefunc(v, x1, iy, iz, seed);
	return lerp(xs, v1, v2);
}

REAL interp_XY_3(vector3 v, REAL xs, REAL ys, int x0, int x1, int y0, int y1,
		int iz, uint seed, worker_noise_3 noisefunc) {
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

REAL interp_X_4(vector4 v, REAL xs, int x0, int x1, int iy, int iz, int iw,
		uint seed, worker_noise_4 noisefunc) {
	REAL v1 = noisefunc(v, x0, iy, iz, iw, seed);
	REAL v2 = noisefunc(v, x1, iy, iz, iw, seed);
	return lerp(xs, v1, v2);
}

REAL interp_XY_4(vector4 v, REAL xs, REAL ys, int x0, int x1, int y0, int y1,
		int iz, int iw, uint seed, worker_noise_4 noisefunc) {
	REAL v1 = interp_X_4(v, xs, x0, x1, y0, iz, iw, seed, noisefunc);
	REAL v2 = interp_X_4(v, xs, x0, x1, y1, iz, iw, seed, noisefunc);
	return lerp(ys, v1, v2);
}

REAL interp_XYZ_4(vector4 v, REAL xs, REAL ys, REAL zs, int x0, int x1, int y0,
		int y1, int z0, int z1, int iw, uint seed, worker_noise_4 noisefunc) {
	REAL v1 = interp_XY_4(v, xs, ys, x0, x1, y0, y1, z0, iw, seed, noisefunc);
	REAL v2 = interp_XY_4(v, xs, ys, x0, x1, y0, y1, z1, iw, seed, noisefunc);
	return lerp(zs, v1, v2);
}

REAL interp_XYZW_4(vector4 v, REAL xs, REAL ys, REAL zs, REAL ws, int x0,
		int x1, int y0, int y1, int z0, int z1, int w0, int w1, uint seed,
		worker_noise_4 noisefunc) {
	REAL v1 = interp_XYZ_4(v, xs, ys, zs, x0, x1, y0, y1, z0, z1, w0, seed,
			noisefunc);
	REAL v2 = interp_XYZ_4(v, xs, ys, zs, x0, x1, y0, y1, z0, z1, w1, seed,
			noisefunc);
	return lerp(ws, v1, v2);
}

REAL interp_X_6(vector8 v, REAL xs, int x0, int x1, int iy, int iz, int iw,
		int iu, int iv, uint seed, worker_noise_6 noisefunc) {
	REAL v1 = noisefunc(v, x0, iy, iz, iw, iu, iv, seed);
	REAL v2 = noisefunc(v, x1, iy, iz, iw, iu, iv, seed);
	return lerp(xs, v1, v2);
}

REAL interp_XY_6(vector8 v, REAL xs, REAL ys, int x0, int x1, int y0, int y1,
		int iz, int iw, int iu, int iv, uint seed, worker_noise_6 noisefunc) {
	REAL v1 = interp_X_6(v, xs, x0, x1, y0, iz, iw, iu, iv, seed, noisefunc);
	REAL v2 = interp_X_6(v, xs, x0, x1, y1, iz, iw, iu, iv, seed, noisefunc);
	return lerp(ys, v1, v2);
}

REAL interp_XYZ_6(vector8 v, REAL xs, REAL ys, REAL zs, int x0, int x1, int y0,
		int y1, int z0, int z1, int iw, int iu, int iv, uint seed,
		worker_noise_6 noisefunc) {
	REAL v1 = interp_XY_6(v, xs, ys, x0, x1, y0, y1, z0, iw, iu, iv, seed,
			noisefunc);
	REAL v2 = interp_XY_6(v, xs, ys, x0, x1, y0, y1, z1, iw, iu, iv, seed,
			noisefunc);
	return lerp(zs, v1, v2);
}

REAL interp_XYZW_6(vector8 v, REAL xs, REAL ys, REAL zs, REAL ws, int x0,
		int x1, int y0, int y1, int z0, int z1, int w0, int w1, int iu, int iv,
		uint seed, worker_noise_6 noisefunc) {
	REAL v1 = interp_XYZ_6(v, xs, ys, zs, x0, x1, y0, y1, z0, z1, w0, iu, iv,
			seed, noisefunc);
	REAL v2 = interp_XYZ_6(v, xs, ys, zs, x0, x1, y0, y1, z0, z1, w1, iu, iv,
			seed, noisefunc);
	return lerp(ws, v1, v2);
}

REAL interp_XYZWU_6(vector8 v, REAL xs, REAL ys, REAL zs, REAL ws, REAL us,
		int x0, int x1, int y0, int y1, int z0, int z1, int w0, int w1, int u0,
		int u1, int iv, uint seed, worker_noise_6 noisefunc) {
	REAL v1 = interp_XYZW_6(v, xs, ys, zs, ws, x0, x1, y0, y1, z0, z1, w0, w1,
			u0, iv, seed, noisefunc);
	REAL v2 = interp_XYZW_6(v, xs, ys, zs, ws, x0, x1, y0, y1, z0, z1, w0, w1,
			u1, iv, seed, noisefunc);
	return lerp(us, v1, v2);
}

REAL interp_XYZWUV_6(vector8 v, REAL xs, REAL ys, REAL zs, REAL ws, REAL us,
		REAL vs, int x0, int x1, int y0, int y1, int z0, int z1, int w0, int w1,
		int u0, int u1, int v0, int v1, uint seed, worker_noise_6 noisefunc) {
	REAL val1 = interp_XYZWU_6(v, xs, ys, zs, ws, us, x0, x1, y0, y1, z0, z1,
			w0, w1, u0, u1, v0, seed, noisefunc);
	REAL val2 = interp_XYZWU_6(v, xs, ys, zs, ws, us, x0, x1, y0, y1, z0, z1,
			w0, w1, u0, u1, v1, seed, noisefunc);
	return lerp(vs, val1, val2);
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

REAL value_noise4D(vector4 v, uint seed, interp_func interp) {
	int4 v0 = fast_floor4(v);
	int4 v1 = v0 + 1;

	REAL xs = interp((v.x - (REAL) v0.x));
	REAL ys = interp((v.y - (REAL) v0.y));
	REAL zs = interp((v.z - (REAL) v0.z));
	REAL ws = interp((v.w - (REAL) v0.w));

	return interp_XYZW_4(v, xs, ys, zs, ws, v0.x, v1.x, v0.y, v1.y, v0.z, v1.z,
			v0.w, v1.w, seed, value_noise_4);
}

REAL value_noise6D(vector8 v, uint seed, interp_func interp) {
	int8 v0 = fast_floor8(v);
	int8 v1 = v0 + 1;

	REAL xs = interp((v.x - (REAL) v0.x));
	REAL ys = interp((v.y - (REAL) v0.y));
	REAL zs = interp((v.z - (REAL) v0.z));
	REAL ws = interp((v.w - (REAL) v0.w));
	REAL us = interp((v.s4 - (REAL) v0.s4));
	REAL vs = interp((v.s5 - (REAL) v0.s5));

	return interp_XYZWUV_6(v, xs, ys, zs, ws, us, vs, v0.x, v1.x, v0.y, v1.y,
			v0.z, v1.z, v0.w, v1.w, v0.s4, v1.s4, v0.s5, v1.s5, seed,
			value_noise_6);
}

REAL gradient_noise2D(vector2 v, uint seed, interp_func interp) {
	int2 v0 = fast_floor2(v);
	int2 v1 = v0 + 1;

	REAL xs = interp((v.x - (REAL) v0.x));
	REAL ys = interp((v.y - (REAL) v0.y));

	return interp_XY_2(v, xs, ys, v0.x, v1.x, v0.y, v1.y, seed, grad_noise_2);
}

REAL gradient_noise3D(vector3 v, uint seed, interp_func interp) {
	int3 v0 = fast_floor3(v);
	int3 v1 = v0 + 1;

	REAL xs = interp((v.x - (REAL) v0.x));
	REAL ys = interp((v.y - (REAL) v0.y));
	REAL zs = interp((v.z - (REAL) v0.z));

	return interp_XYZ_3(v, xs, ys, zs, v0.x, v1.x, v0.y, v1.y, v0.z, v1.z, seed,
			grad_noise_3);
}

REAL gradient_noise4D(vector4 v, uint seed, interp_func interp) {
	int4 v0 = fast_floor4(v);
	int4 v1 = v0 + 1;

	REAL xs = interp((v.x - (REAL) v0.x));
	REAL ys = interp((v.y - (REAL) v0.y));
	REAL zs = interp((v.z - (REAL) v0.z));
	REAL ws = interp((v.w - (REAL) v0.w));

	return interp_XYZW_4(v, xs, ys, zs, ws, v0.x, v1.x, v0.y, v1.y, v0.z, v1.z,
			v0.w, v1.w, seed, grad_noise_4);
}

REAL gradient_noise6D(vector8 v, uint seed, interp_func interp) {
	int8 v0 = fast_floor8(v);
	int8 v1 = v0 + 1;

	REAL xs = interp((v.x - (REAL) v0.x));
	REAL ys = interp((v.y - (REAL) v0.y));
	REAL zs = interp((v.z - (REAL) v0.z));
	REAL ws = interp((v.w - (REAL) v0.w));
	REAL us = interp((v.s4 - (REAL) v0.s4));
	REAL vs = interp((v.s5 - (REAL) v0.s5));

	return interp_XYZWUV_6(v, xs, ys, zs, ws, us, vs, v0.x, v1.x, v0.y, v1.y,
			v0.z, v1.z, v0.w, v1.w, v0.s4, v1.s4, v0.s5, v1.s5, seed,
			grad_noise_6);
}

REAL gradval_noise2D(vector2 v, uint seed, interp_func interp) {
	return value_noise2D(v, seed, interp)
			+ gradient_noise2D(v, seed, interp);
}

REAL gradval_noise3D(vector3 v, uint seed, interp_func interp) {
	return value_noise3D(v, seed, interp)
			+ gradient_noise3D(v, seed, interp);
}

REAL gradval_noise4D(vector4 v, uint seed, interp_func interp) {
	return value_noise4D(v, seed, interp)
			+ gradient_noise4D(v, seed, interp);
}

REAL gradval_noise6D(vector8 v, uint seed, interp_func interp) {
	return value_noise6D(v, seed, interp)
			+ gradient_noise6D(v, seed, interp);
}

REAL white_noise2D(vector2 v, uint seed, interp_func interp) {
	unsigned char hash = compute_hash2(v, seed);
	return whitenoise_lut[hash];
}

REAL white_noise3D(vector3 v, uint seed, interp_func interp) {
	unsigned char hash = compute_hash3(v, seed);
	return whitenoise_lut[hash];
}

REAL white_noise4D(vector4 v, uint seed, interp_func interp) {
	unsigned char hash = compute_hash4(v, seed);
	return whitenoise_lut[hash];
}

REAL white_noise6D(vector8 v, uint seed, interp_func interp) {
	unsigned char hash = compute_hash6(v, seed);
	return whitenoise_lut[hash];
}

// Cellular functions. Compute distance (for cellular modules) and displacement (for voronoi modules)

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

void cellular_function2D(vector2 v, uint seed, REAL *f, REAL *disp,
		dist_func2 distance) {
//	int xint = fast_floor(x);
//	int yint = fast_floor(y);
	int2 vint = fast_floor2(v);

	for (int c = 0; c < 4; ++c) {
		f[c] = 99999.0;
		disp[c] = 0.0;
	}

	vector2 vpos;
	for (int ycur = vint.y - 3; ycur <= vint.y + 3; ++ycur) {
		for (int xcur = vint.x - 3; xcur <= vint.x + 3; ++xcur) {
			vpos = (vector2) ((REAL) xcur + value_noise_2(v, xcur, ycur, seed),
					(REAL) ycur	+ value_noise_2(v, xcur, ycur, seed + 1));
			REAL dist = distance(vpos, v);
			int2 vval = fast_floor2(vpos);
			REAL dsp = value_noise_2(v, vval.x, vval.y, seed + 3);
			add_dist(f, disp, dist, dsp);
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
	uint h0 = hash_coords_2(i, j, seed) % 8;
	uint h1 = hash_coords_2(i + i1, j + j1, seed) % 8;
	uint h2 = hash_coords_2(i + 1, j + 1, seed) % 8;

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

REAL simplex_noise3D(vector3 v, uint seed, interp_func interp) {
	//static REAL F3 = 1.0/3.0;
	//static REAL G3 = 1.0/6.0;
	REAL n0, n1, n2, n3;

	REAL s = (v.x + v.y + v.z) * F3;
	int i = fast_floor(v.x + s);
	int j = fast_floor(v.y + s);
	int k = fast_floor(v.z + s);

	REAL t = (i + j + k) * G3;
	REAL X0 = i - t;
	REAL Y0 = j - t;
	REAL Z0 = k - t;

	REAL x0 = v.x - X0;
	REAL y0 = v.y - Y0;
	REAL z0 = v.z - Z0;

	int i1, j1, k1;
	int i2, j2, k2;

	if (x0 >= y0) {
		if (y0 >= z0) {
			i1 = 1;
			j1 = 0;
			k1 = 0;
			i2 = 1;
			j2 = 1;
			k2 = 0;
		} else if (x0 >= z0) {
			i1 = 1;
			j1 = 0;
			k1 = 0;
			i2 = 1;
			j2 = 0;
			k2 = 1;
		} else {
			i1 = 0;
			j1 = 0;
			k1 = 1;
			i2 = 1;
			j2 = 0;
			k2 = 1;
		}
	} else {
		if (y0 < z0) {
			i1 = 0;
			j1 = 0;
			k1 = 1;
			i2 = 0;
			j2 = 1;
			k2 = 1;
		} else if (x0 < z0) {
			i1 = 0;
			j1 = 1;
			k1 = 0;
			i2 = 0;
			j2 = 1;
			k2 = 1;
		} else {
			i1 = 0;
			j1 = 1;
			k1 = 0;
			i2 = 1;
			j2 = 1;
			k2 = 0;
		}
	}

	REAL x1 = x0 - i1 + G3;
	REAL y1 = y0 - j1 + G3;
	REAL z1 = z0 - k1 + G3;
	REAL x2 = x0 - i2 + 2.0 * G3;
	REAL y2 = y0 - j2 + 2.0 * G3;
	REAL z2 = z0 - k2 + 2.0 * G3;
	REAL x3 = x0 - 1.0 + 3.0 * G3;
	REAL y3 = y0 - 1.0 + 3.0 * G3;
	REAL z3 = z0 - 1.0 + 3.0 * G3;

	uint h0, h1, h2, h3;

	h0 = hash_coords_3(i, j, k, seed) % 24;
	h1 = hash_coords_3(i + i1, j + j1, k + k1, seed) % 24;
	h2 = hash_coords_3(i + i2, j + j2, k + k2, seed) % 24;
	h3 = hash_coords_3(i + 1, j + 1, k + 1, seed) % 24;

	REAL *g0 = &gradient3D_lut[h0][0];
	REAL *g1 = &gradient3D_lut[h1][0];
	REAL *g2 = &gradient3D_lut[h2][0];
	REAL *g3 = &gradient3D_lut[h3][0];

	REAL t0 = 0.6 - x0 * x0 - y0 * y0 - z0 * z0;
	if (t0 < 0.0)
		n0 = 0.0;
	else {
		t0 *= t0;
		n0 = t0 * t0 * array_dot3(g0, x0, y0, z0);
	}

	REAL t1 = 0.6 - x1 * x1 - y1 * y1 - z1 * z1;
	if (t1 < 0.0)
		n1 = 0.0;
	else {
		t1 *= t1;
		n1 = t1 * t1 * array_dot3(g1, x1, y1, z1);
	}

	REAL t2 = 0.6 - x2 * x2 - y2 * y2 - z2 * z2;
	if (t2 < 0)
		n2 = 0.0;
	else {
		t2 *= t2;
		n2 = t2 * t2 * array_dot3(g2, x2, y2, z2);
	}

	REAL t3 = 0.6 - x3 * x3 - y3 * y3 - z3 * z3;
	if (t3 < 0)
		n3 = 0.0;
	else {
		t3 *= t3;
		n3 = t3 * t3 * array_dot3(g3, x3, y3, z3);
	}

	return (32.0 * (n0 + n1 + n2 + n3)) * 1.25086885 + 0.0003194984;
}

typedef struct SVectorOrdering {
	REAL val;
	int axis;
} SVectorOrdering;

int vectorOrderingCompare(const void *a, const void *b) {
	SVectorOrdering v1 = *((SVectorOrdering*) a);
	SVectorOrdering v2 = *((SVectorOrdering*) b);
	if (v1.val == v2.val)
		return 0;
	if (v1.val > v2.val)
		return -1;
	return 1;
}

void sortBy_4(REAL *l1, int *l2) {
	SVectorOrdering a[4];
	for (int c = 0; c < 4; ++c) {
		a[c].val = l1[c];
		a[c].axis = l2[c];
	}
	sort(&a[0], 4, sizeof(SVectorOrdering), vectorOrderingCompare);
	for (int c = 0; c < 4; ++c) {
		a[c].val = l1[c];
		a[c].axis = l2[c];
	}
	for (int c = 0; c < 4; ++c)
		l2[c] = a[c].axis;
}

void sortBy_6(REAL *l1, int *l2) {
	SVectorOrdering a[6];
	for (int c = 0; c < 6; ++c) {
		a[c].val = l1[c];
		a[c].axis = l2[c];
	}
	sort(&a[0], 6, sizeof(SVectorOrdering), vectorOrderingCompare);
	for (int c = 0; c < 6; ++c)
		l2[c] = a[c].axis;
}

// f = ((self.d + 1) ** .5 - 1) / self.d
// double F4 = (sqrt(5.0) - 1.0) / 4.0;
#define simplex_noise4D_F4 (0.30901699437494745)

// g=self.f/(1+self.d*self.f)
// double G4 = F4 / (1.0 + 4.0 * F4);
#define simplex_noise4D_G4 (0.13819660112501053)

// double sideLength = 2.0 / (4.0 * F4 + 1.0);
#define simplex_noise4D_sideLength (0.89442719099991586)

// double a = sqrt((sideLength * sideLength) - ((sideLength / 2.0) * (sideLength / 2.0)));
#define simplex_noise4D_a (0.7745966692414834)

// double cornerToFace = sqrt((a * a + (a / 2.0) * (a / 2.0)));
#define simplex_noise4D_cornerToFace (0.86602540378443871)

// double cornerToFaceSquared = cornerToFace * cornerToFace;
#define simplex_noise4D_cornerToFaceSquared (0.75000000000000011)

// double valueScaler = pow(3.0, -0.5);
static REAL simplex_noise4D_valueScaler = 0.57735026918962573;

REAL simplex_noise4D(vector4 v, uint seed, interp_func interp) {
	// Rough estimated/expirmentally determined function
	// for scaling output to be -1 to 1
	simplex_noise4D_valueScaler *= pow(3.0, -3.5) * 100.0 + 13.0;

	REAL loc[4] = { v.x, v.y, v.z, v.w };
	REAL s = 0;
	for (int c = 0; c < 4; ++c)
		s += loc[c];
	s *= simplex_noise4D_F4;

	int skewLoc[4] = { fast_floor(v.x + s), fast_floor(v.y + s), fast_floor(v.z + s),
			fast_floor(v.w + s) };
	int intLoc[4] = { fast_floor(v.x + s), fast_floor(v.y + s), fast_floor(v.z + s),
			fast_floor(v.w + s) };
	REAL unskew = 0.0;
	for (int c = 0; c < 4; ++c)
		unskew += skewLoc[c];
	unskew *= simplex_noise4D_G4;
	REAL cellDist[4] = { loc[0] - (REAL) skewLoc[0] + unskew, loc[1]
			- (REAL) skewLoc[1] + unskew, loc[2] - (REAL) skewLoc[2] + unskew,
			loc[3] - (REAL) skewLoc[3] + unskew };
	int distOrder[4] = { 0, 1, 2, 3 };
	sortBy_4(cellDist, distOrder);

	int newDistOrder[5] = { -1, distOrder[0], distOrder[1], distOrder[2],
			distOrder[3] };

	REAL n = 0.0;
	REAL skewOffset = 0.0;

	for (int c = 0; c < 5; ++c) {
		int i = newDistOrder[c];
		if (i != -1)
			intLoc[i] += 1;

		REAL u[4];
		for (int d = 0; d < 4; ++d) {
			u[d] = cellDist[d] - (intLoc[d] - skewLoc[d]) + skewOffset;
		}

		REAL t = simplex_noise4D_cornerToFaceSquared;

		for (int d = 0; d < 4; ++d) {
			t -= u[d] * u[d];
		}

		if (t > 0.0) {
			uint h = hash_coords_4(intLoc[0], intLoc[1], intLoc[2], intLoc[3],
					seed) % 64;
			REAL *vec = &gradient4D_lut[h][0];
			REAL gr = 0.0;
			for (int d = 0; d < 4; ++d) {
				gr += vec[d] * u[d];
			}

			n += gr * t * t * t * t;
		}
		skewOffset += simplex_noise4D_G4;
	}
	n *= simplex_noise4D_valueScaler;
	return n;
}

REAL simplex_noise6D(vector8 v, uint seed, interp_func interp) {
	// Skew
	//self.f = ((self.d + 1) ** .5 - 1) / self.d
	REAL F4 = (sqrt(7.0) - 1.0) / 6.0; //(sqrt(5.0)-1.0)/4.0;

	// Unskew
	// self.g=self.f/(1+self.d*self.f)
	REAL G4 = F4 / (1.0 + 6.0 * F4);

	REAL sideLength = sqrt(6.0) / (6.0 * F4 + 1.0);
	REAL a = sqrt(
			(sideLength * sideLength)
					- ((sideLength / 2.0) * (sideLength / 2.0)));
	REAL cornerFace = sqrt(a * a + (a / 2.0) * (a / 2.0));

	REAL cornerFaceSqrd = cornerFace * cornerFace;

	//self.valueScaler=(self.d-1)**-.5
	REAL valueScaler = pow(5.0, -0.5);
	valueScaler *= pow(5.0, -3.5) * 100 + 13;

	REAL loc[6] = { v.x, v.y, v.z, v.w, v.s4, v.s5 };
	REAL s = 0;
	for (int c = 0; c < 6; ++c)
		s += loc[c];
	s *= F4;

	int skewLoc[6] = { fast_floor(v.x + s), fast_floor(v.y + s), fast_floor(
			v.z + s), fast_floor(v.w + s), fast_floor(v.s4 + s), fast_floor(
			v.s5 + s) };
	int intLoc[6] = { fast_floor(v.x + s), fast_floor(v.y + s), fast_floor(
			v.z + s), fast_floor(v.w + s), fast_floor(v.s4 + s), fast_floor(
			v.s5 + s) };
	REAL unskew = 0.0;
	for (int c = 0; c < 6; ++c)
		unskew += skewLoc[c];
	unskew *= G4;
	REAL cellDist[6] = { loc[0] - (REAL) skewLoc[0] + unskew, loc[1]
			- (REAL) skewLoc[1] + unskew, loc[2] - (REAL) skewLoc[2] + unskew,
			loc[3] - (REAL) skewLoc[3] + unskew, loc[4] - (REAL) skewLoc[4]
					+ unskew, loc[5] - (REAL) skewLoc[5] + unskew };
	int distOrder[6] = { 0, 1, 2, 3, 4, 5 };
	sortBy_6(cellDist, distOrder);

	int newDistOrder[7] = { -1, distOrder[0], distOrder[1], distOrder[2],
			distOrder[3], distOrder[4], distOrder[5] };

	REAL n = 0.0;
	REAL skewOffset = 0.0;

	for (int c = 0; c < 7; ++c) {
		int i = newDistOrder[c];
		if (i != -1)
			intLoc[i] += 1;

		REAL u[6];
		for (int d = 0; d < 6; ++d) {
			u[d] = cellDist[d] - (intLoc[d] - skewLoc[d]) + skewOffset;
		}

		REAL t = cornerFaceSqrd;

		for (int d = 0; d < 6; ++d) {
			t -= u[d] * u[d];
		}

		if (t > 0.0) {
			uint h = hash_coords_6(intLoc[0], intLoc[1], intLoc[2], intLoc[3],
					intLoc[4], intLoc[5], seed) % 192;
			REAL *vec = &gradient6D_lut[h][0];
			REAL gr = 0.0;
			for (int d = 0; d < 6; ++d) {
				gr += vec[d] * u[d];
			}

			n += gr * t * t * t * t * t;
		}
		skewOffset += G4;
	}
	n *= valueScaler;
	return n;
}
