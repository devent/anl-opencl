kernel void value_noise2D_noInterp(
global float2 *input,
global float2 *output
) {
	int id0 = get_global_id(0);
	output[id0] = value_noise2D(input[id0], 200, noInterp);
}

kernel void value_noise2D_linearInterp(
global float2 *input,
global float2 *output
) {
	int id0 = get_global_id(0);
	output[id0] = value_noise2D(input[id0], 200, linearInterp);
}

kernel void value_noise2D_hermiteInterp(
global float2 *input,
global float2 *output
) {
	int id0 = get_global_id(0);
	output[id0] = value_noise2D(input[id0], 200, hermiteInterp);
}

kernel void value_noise2D_quinticInterp(
global float2 *input,
global float2 *output
) {
	int id0 = get_global_id(0);
	output[id0] = value_noise2D(input[id0], 200, quinticInterp);
}
