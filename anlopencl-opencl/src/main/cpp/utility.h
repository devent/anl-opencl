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

#ifndef ANLOPENCL_USE_OPENCL
#include <opencl_utils.h>
#endif // ANLOPENCL_USE_OPENCL

#ifndef ANLOPENCL_USE_OPENCL
REAL degrees(REAL radians);
#endif // ANLOPENCL_USE_OPENCL

#ifndef ANLOPENCL_USE_OPENCL
REAL radians(REAL degrees);
#endif // ANLOPENCL_USE_OPENCL

#ifndef ANLOPENCL_USE_OPENCL
REAL clamp(REAL v, REAL l, REAL h);
#endif // ANLOPENCL_USE_OPENCL

/**
 * Converts an array to a vector2.
 */
#define a2vector2(a, i) ((vector2)(a[i], a[i+1]))

/**
 * Converts an array to a vector3.
 */
#define a2vector3(a, i) ((vector3)(a[i], a[i+1], a[i+2]))

/**
 * Converts an array to a vector4.
 */
#define a2vector4(a, i) ((vector4)(a[i], a[i+1], a[i+2], a[i+3]))

/**
 * Converts an array to a vector8.
 */
#define a2vector8(a, i) ((vector8)(a[i], a[i+1], a[i+2], a[i+3], a[i+4], a[i+5], a[i+6], a[i+7]))

REAL lerp(REAL t, REAL a, REAL b);

bool isPowerOf2(unsigned int n);

REAL hermite_blend(REAL t);

REAL quintic_blend(REAL t);

int fast_floor(REAL t);
int2 fast_floor2(vector2 v);
int3 fast_floor3(vector3 v);
int4 fast_floor4(vector4 v);
int8 fast_floor8(vector8 v);

REAL array_dot(REAL *arr, REAL a, REAL b);
REAL array_dot2(REAL *arr, REAL a, REAL b);
REAL array_dot3(REAL *arr, REAL a, REAL b, REAL c);
REAL array_dot4(REAL *arr, REAL a, REAL b, REAL c, REAL d);
REAL array_dot6(REAL *arr, REAL a, REAL b, REAL c, REAL d, REAL e, REAL f);

REAL bias(REAL b, REAL t);

REAL gain(REAL g, REAL t);

#ifdef __cplusplus
}
#endif // __cplusplus

#endif /* UTILITY_H_ */
