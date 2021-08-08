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
 * utility.h
 *
 *  Created on: Jul 27, 2021
 *      Author: Erwin Müller
 */

#ifndef UTILITY_H_
#define UTILITY_H_

#ifdef __cplusplus
extern "C" {
#endif // __cplusplus

#ifndef USE_OPENCL

#include <opencl_utils.h>

#endif // USE_OPENCL

#ifndef USE_OPENCL
REAL clamp(REAL v, REAL l, REAL h) {
	if (v < l)
		v = l;
	if (v > h)
		v = h;

	return v;
}
#endif // USE_OPENCL

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
#if USE_OPENCL
	return convert_int2(floor(v));
#else
	return (int2){ floor(v.x), floor(v.y) };
#endif
}

int3 fast_floor3(vector3 v) {
#if USE_OPENCL
	return convert_int3(floor(v));
#else
	return (int3){ floor(v.x), floor(v.y), floor(v.z) };
#endif
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

#ifdef __cplusplus
}
#endif // __cplusplus

#endif /* UTILITY_H_ */
