kernel void value_noise2D_noInterp(
global float2 *input,
global float *output
) {
	int id0 = get_global_id(0);
	output[id0] = value_noise2D(input[id0], 200, noInterp);
}

kernel void value_noise2D_linearInterp(
global float2 *input,
global float *output
) {
	int id0 = get_global_id(0);
	output[id0] = value_noise2D(input[id0], 200, linearInterp);
}

kernel void value_noise2D_hermiteInterp(
global float2 *input,
global float *output
) {
	int id0 = get_global_id(0);
	output[id0] = value_noise2D(input[id0], 200, hermiteInterp);
}

kernel void value_noise2D_quinticInterp(
global float2 *input,
global float *output
) {
	int id0 = get_global_id(0);
	output[id0] = value_noise2D(input[id0], 200, quinticInterp);
}

kernel void gradient_noise2D_noInterp(
global float2 *input,
global float *output
) {
	int id0 = get_global_id(0);
	output[id0] = gradient_noise2D(input[id0], 200, noInterp);
}

kernel void gradient_noise2D_linearInterp(
global float2 *input,
global float *output
) {
	int id0 = get_global_id(0);
	output[id0] = gradient_noise2D(input[id0], 200, linearInterp);
}

kernel void gradient_noise2D_hermiteInterp(
global float2 *input,
global float *output
) {
	int id0 = get_global_id(0);
	output[id0] = gradient_noise2D(input[id0], 200, hermiteInterp);
}

kernel void gradient_noise2D_quinticInterp(
global float2 *input,
global float *output
) {
	int id0 = get_global_id(0);
	output[id0] = gradient_noise2D(input[id0], 200, quinticInterp);
}

kernel void gradval_noise2D_noInterp(
global float2 *input,
global float *output
) {
	int id0 = get_global_id(0);
	output[id0] = gradval_noise2D(input[id0], 200, noInterp);
}

kernel void gradval_noise2D_linearInterp(
global float2 *input,
global float *output
) {
	int id0 = get_global_id(0);
	output[id0] = gradval_noise2D(input[id0], 200, linearInterp);
}

kernel void gradval_noise2D_hermiteInterp(
global float2 *input,
global float *output
) {
	int id0 = get_global_id(0);
	output[id0] = gradval_noise2D(input[id0], 200, hermiteInterp);
}

kernel void gradval_noise2D_quinticInterp(
global float2 *input,
global float *output
) {
	int id0 = get_global_id(0);
	output[id0] = gradval_noise2D(input[id0], 200, quinticInterp);
}

kernel void white_noise2D_noInterp(
global float2 *input,
global float *output
) {
	int id0 = get_global_id(0);
	output[id0] = white_noise2D(input[id0], 200, noInterp);
}

kernel void simplex_noise2D_noInterp(
global float2 *input,
global float *output
) {
	int id0 = get_global_id(0);
	output[id0] = simplex_noise2D(input[id0], 200, noInterp);
}
