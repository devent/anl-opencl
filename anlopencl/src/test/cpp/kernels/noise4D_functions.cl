kernel void value_noise4D_noInterp(
global float4 *input,
global float *output
) {
	int id0 = get_global_id(0);
	output[id0] = value_noise4D(input[id0], 200, noInterp);
}

kernel void value_noise4D_linearInterp(
global float4 *input,
global float *output
) {
	int id0 = get_global_id(0);
	output[id0] = value_noise4D(input[id0], 200, linearInterp);
}

kernel void value_noise4D_hermiteInterp(
global float4 *input,
global float *output
) {
	int id0 = get_global_id(0);
	output[id0] = value_noise4D(input[id0], 200, hermiteInterp);
}

kernel void value_noise4D_quinticInterp(
global float4 *input,
global float *output
) {
	int id0 = get_global_id(0);
	output[id0] = value_noise4D(input[id0], 200, quinticInterp);
}

kernel void gradient_noise4D_noInterp(
global float4 *input,
global float *output
) {
	int id0 = get_global_id(0);
	output[id0] = gradient_noise4D(input[id0], 200, noInterp);
}

kernel void gradient_noise4D_linearInterp(
global float4 *input,
global float *output
) {
	int id0 = get_global_id(0);
	output[id0] = gradient_noise4D(input[id0], 200, linearInterp);
}

kernel void gradient_noise4D_hermiteInterp(
global float4 *input,
global float *output
) {
	int id0 = get_global_id(0);
	output[id0] = gradient_noise4D(input[id0], 200, hermiteInterp);
}

kernel void gradient_noise4D_quinticInterp(
global float4 *input,
global float *output
) {
	int id0 = get_global_id(0);
	output[id0] = gradient_noise4D(input[id0], 200, quinticInterp);
}

kernel void gradval_noise4D_noInterp(
global float4 *input,
global float *output
) {
	int id0 = get_global_id(0);
	output[id0] = gradval_noise4D(input[id0], 200, noInterp);
}

kernel void gradval_noise4D_linearInterp(
global float4 *input,
global float *output
) {
	int id0 = get_global_id(0);
	output[id0] = gradval_noise4D(input[id0], 200, linearInterp);
}

kernel void gradval_noise4D_hermiteInterp(
global float4 *input,
global float *output
) {
	int id0 = get_global_id(0);
	output[id0] = gradval_noise4D(input[id0], 200, hermiteInterp);
}

kernel void gradval_noise4D_quinticInterp(
global float4 *input,
global float *output
) {
	int id0 = get_global_id(0);
	output[id0] = gradval_noise4D(input[id0], 200, quinticInterp);
}

kernel void white_noise4D_noInterp(
global float4 *input,
global float *output
) {
	int id0 = get_global_id(0);
	output[id0] = white_noise4D(input[id0], 200, noInterp);
}

kernel void simplex_noise4D_noInterp(
global float4 *input,
global float *output
) {
	int id0 = get_global_id(0);
	output[id0] = simplex_noise4D(input[id0], 200, noInterp);
}
