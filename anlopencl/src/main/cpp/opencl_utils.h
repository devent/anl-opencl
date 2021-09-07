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
 * opencl_utils.h
 *
 *  Created on: Jul 26, 2021
 *      Author: Erwin Müller
 */

#ifndef OPENCL_UTILS_H_
#define OPENCL_UTILS_H_

#ifdef __cplusplus
extern "C" {
#endif

#ifdef ANLOPENCL_USE_DOUBLE
#define REAL double
#else
#define REAL float
#endif // ANLOPENCL_USE_DOUBLE

#ifndef ANLOPENCL_USE_OPENCL
#include <math.h>
#include <stdlib.h>
#include <stdbool.h>

typedef REAL vector2 __attribute__((ext_vector_type(2)));
typedef REAL vector3 __attribute__((ext_vector_type(3)));
typedef REAL vector4 __attribute__((ext_vector_type(4)));
typedef REAL vector8 __attribute__((ext_vector_type(8)));
typedef REAL vector16 __attribute__((ext_vector_type(16)));

typedef int int2 __attribute__((ext_vector_type(2)));
typedef int int3 __attribute__((ext_vector_type(3)));
typedef int int4 __attribute__((ext_vector_type(4)));
typedef int int8 __attribute__((ext_vector_type(8)));
typedef int int16 __attribute__((ext_vector_type(16)));

/**
 * Replacement for the OpenCL function convert_float4.
 */
vector4 convert_real4(int4 v);

#else
#ifdef ANLOPENCL_USE_DOUBLE
#define vector2 double2
#define vector3 double3
#define vector4 double4
#define vector8 double8
#define vector16 double16
#else
#define vector2 float2
#define vector3 float3
#define vector4 float4
#define vector8 float8
#define vector16 float16
#endif // ANLOPENCL_USE_DOUBLE
#endif // ANLOPENCL_USE_OPENCL

#ifdef __cplusplus
}
#endif // __cplusplus

#endif /* OPENCL_UTILS_H_ */
