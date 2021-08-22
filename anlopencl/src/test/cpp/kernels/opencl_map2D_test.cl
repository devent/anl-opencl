kernel void opencl_map2D_default_test(
global float *output,
const int sizeWidth,
const int sizeHeight,
const float z,
const float sx0,
const float sx1,
const float sy0,
const float sy1,
local float3 *coord
) {
	int id0 = get_global_id(0);
	float fid0 = id0;
	int s0 = get_global_size(0);
	float x0, x1, y0, y1, s;
	s = (sx1 - sx0) / (float)(sizeWidth);
	x0 = sx0 + (id0 % sizeWidth) * s;
	x1 = sx0 + ((id0 % sizeWidth) + 1) * s;
	y0 = sy0 + floor(fid0 / (s0 / sizeWidth)) * s;
	y1 = sy0 + floor(fid0 / (s0 / sizeWidth) + 1) * s;
	printf("g=%d s=%f %f/%f/%f/%f\n", id0, s,x0,x1,y0,y1);
	barrier(CLK_LOCAL_MEM_FENCE);
}

kernel void opencl_map2D_range_test(
global float *output,
const float x0,
const float x1,
const float y0,
const float y1,
const float sizeWidth,
const float sizeHeight,
const float z,
local float3 *coord
) {
	int id = get_global_id(0);
	if (id == 0) {
		map2D(coord, calc_seamless_none, create_ranges_default(), sizeWidth, sizeHeight, z);
	}
	barrier(CLK_LOCAL_MEM_FENCE);
	output[id] = value_noise3D(coord[id], 200, noInterp);
}
