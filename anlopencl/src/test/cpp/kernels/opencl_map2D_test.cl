kernel void opencl_map2D_test(
global float *output,
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
