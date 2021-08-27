/**
 * @file opencl_utils.c
 * @author Erwin Müller
 * @date Aug 26, 2021
 * @brief Implements functions from opencl_utils.h.
 */
#ifndef USE_OPENCL
#include "opencl_utils.h"
#endif // USE_OPENCL

#ifndef USE_OPENCL
vector4 convert_float4(int4 v) {
	return (vector4)(v.x, v.y, v.z, v.w);
}
#endif // USE_OPENCL
