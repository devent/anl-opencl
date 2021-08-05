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

#define REAL float

#ifndef USE_OPENCL
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

#else
#define vector2 float2
#define vector3 float3
#define vector4 float4
#define vector8 float8
#define vector16 float16
#endif // USE_OPENCL

#ifdef __cplusplus
}
#endif // __cplusplus

#endif /* OPENCL_UTILS_H_ */
