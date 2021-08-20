kernel void opencl_map2D_default_test(
global float *output,
const float sizeWidth,
const float sizeHeight,
const float z,
global float3 *coord
) {
	int id0 = get_global_id(0);
	if (id0 == 0) {
		map2D(coord, calc_seamless_none, create_ranges_map2D(-10, 10, -10, 10), sizeWidth, sizeHeight, z);
	}
	barrier(CLK_LOCAL_MEM_FENCE);
	int i, j, k;
	i = id0 * 3 + 0;
	j = id0 * 3 + 1;
	k = id0 * 3 + 2;
	output[i] = value_noise3D(coord[id0], 200, linearInterp);
	output[j] = value_noise3D(coord[id0], 1000, linearInterp);
	output[k] = value_noise3D(coord[id0], 2000, linearInterp);
	printf("%d %f/%f/%f %f/%f/%f\n", id0,coord[id0].x, coord[id0].y, coord[id0].z, output[i],output[j],output[k]);
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
