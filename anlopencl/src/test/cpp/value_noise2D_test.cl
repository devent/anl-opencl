#define USE_OPENCL
#include <utility.h>
__kernel void value_noise2D_test(__global float2 *in, __global float2 *out,
                          __local float *sMemx, __local float *sMemy) {
	int tid = get_local_id(0);
}
