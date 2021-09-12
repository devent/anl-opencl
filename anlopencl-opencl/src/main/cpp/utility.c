//
// Copyright (C) 2021 Erwin Müller <erwin@muellerpublic.de>
// Released as open-source under the Apache License, Version 2.0.
//
// ****************************************************************************
// ANL-OpenCL :: OpenCL
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

/*
 * utility.c
 *
 *  Created on: Aug 13, 2021
 *      Author: Erwin Müller
 */

#ifndef ANLOPENCL_USE_OPENCL
#include "utility.h"
#endif // ANLOPENCL_USE_OPENCL

#ifndef ANLOPENCL_USE_OPENCL
REAL degrees(REAL radians) {
	return (180 / M_PI) * radians;
}
#endif // ANLOPENCL_USE_OPENCL

#ifndef ANLOPENCL_USE_OPENCL
REAL radians(REAL degrees) {
	return (M_PI / 180) * degrees;
}
#endif // ANLOPENCL_USE_OPENCL

#ifndef ANLOPENCL_USE_OPENCL
REAL clamp(REAL v, REAL l, REAL h) {
	if (v < l)
		v = l;
	if (v > h)
		v = h;

	return v;
}
#endif // ANLOPENCL_USE_OPENCL

REAL lerp(REAL t, REAL a, REAL b) {
	return a + t * (b - a);
}

bool isPowerOf2(unsigned int n) {
	// from https://dzone.com/articles/ispowerof2-c
	return n == 1 || (n & (n - 1)) == 0;
}

REAL hermite_blend(REAL t) {
	return (t * t * (3 - 2 * t));
}

REAL quintic_blend(REAL t) {
	return t * t * t * (t * (t * 6 - 15) + 10);
}

int fast_floor(REAL t) {
	return (t > 0 ? (int) t : (int) t - 1);
}

int2 fast_floor2(vector2 v) {
	return (int2){ fast_floor(v.x), fast_floor(v.y) };
}

int3 fast_floor3(vector3 v) {
	return (int3){ fast_floor(v.x), fast_floor(v.y), fast_floor(v.z) };
}

int4 fast_floor4(vector4 v) {
	return (int4){ fast_floor(v.x), fast_floor(v.y), fast_floor(v.z), fast_floor(v.w) };
}

int8 fast_floor8(vector8 v) {
	return (int8){ fast_floor(v.x), fast_floor(v.y), fast_floor(v.z)
		, fast_floor(v.w), fast_floor(v.s4), fast_floor(v.s5)
		, fast_floor(v.s6), fast_floor(v.s7)};
}

REAL array_dot(REAL *arr, REAL a, REAL b) {
	return a * arr[0] + b * arr[1];
}

REAL array_dot2(REAL *arr, REAL a, REAL b) {
	return a * arr[0] + b * arr[1];
}

REAL array_dot3(REAL *arr, REAL a, REAL b, REAL c) {
	return a * arr[0] + b * arr[1] + c * arr[2];
}

REAL array_dot4(REAL *arr, REAL a, REAL b, REAL c, REAL d) {
	return a * arr[0] + b * arr[1] + c * arr[2] + d * arr[3];
}

REAL array_dot6(REAL *arr, REAL a, REAL b, REAL c, REAL d, REAL e, REAL f) {
	return a * arr[0] + b * arr[1] + c * arr[2] + d * arr[3] + e * arr[4] + f * arr[5];
}

REAL bias(REAL b, REAL t) {
	return pow(t, log(b) / log((REAL)0.5));
}

REAL gain(REAL g, REAL t) {
	if (t < 0.5) {
		return bias(1.0 - g, 2.0 * t) / 2.0;
	} else {
		return 1.0 - bias(1.0 - g, 2.0 - 2.0 * t) / 2.0;
	}
}

