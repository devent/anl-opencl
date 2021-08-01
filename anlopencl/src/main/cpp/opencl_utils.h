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

typedef float float2 __attribute__((ext_vector_type(2)));
typedef float float3 __attribute__((ext_vector_type(3)));
typedef float float4 __attribute__((ext_vector_type(4)));

#endif // USE_OPENCL

#ifdef __cplusplus
}
#endif // __cplusplus

#endif /* OPENCL_UTILS_H_ */
