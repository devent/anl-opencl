
// #########################################
// value_noise3D
// #########################################

kernel void simpleBillowLayer3_value_noise3D_noInterp_norot(
global float3 *input,
global float *output
) {
	int id0 = get_global_id(0);
	output[id0] = simpleBillowLayer3(input[id0], value_noise3D, 200, noInterp,
		1, 0.125, false, 0.0, 0.0, 0.0, 0.0);
}

kernel void simpleBillowLayer3_value_noise3D_noInterp_rot(
global float3 *input,
global float *output
) {
	int id0 = get_global_id(0);
	output[id0] = simpleBillowLayer3(input[id0], value_noise3D, 200, noInterp,
		1, 0.125, true, 1.57, 1.0, 0.0, 0.0);
}

kernel void simpleBillowLayer3_value_noise3D_linearInterp_norot(
global float3 *input,
global float *output
) {
	int id0 = get_global_id(0);
	output[id0] = simpleBillowLayer3(input[id0], value_noise3D, 200, linearInterp,
		1, 0.125, false, 0.0, 0.0, 0.0, 0.0);
}

kernel void simpleBillowLayer3_value_noise3D_linearInterp_rot(
global float3 *input,
global float *output
) {
	int id0 = get_global_id(0);
	output[id0] = simpleBillowLayer3(input[id0], value_noise3D, 200, linearInterp,
		1, 0.125, true, 1.57, 1.0, 0.0, 0.0);
}

kernel void simpleBillowLayer3_value_noise3D_hermiteInterp_norot(
global float3 *input,
global float *output
) {
	int id0 = get_global_id(0);
	output[id0] = simpleBillowLayer3(input[id0], value_noise3D, 200, hermiteInterp,
		1, 0.125, false, 0.0, 0.0, 0.0, 0.0);
}

kernel void simpleBillowLayer3_value_noise3D_hermiteInterp_rot(
global float3 *input,
global float *output
) {
	int id0 = get_global_id(0);
	output[id0] = simpleBillowLayer3(input[id0], value_noise3D, 200, hermiteInterp,
		1, 0.125, true, 1.57, 1.0, 0.0, 0.0);
}

kernel void simpleBillowLayer3_value_noise3D_quinticInterp_norot(
global float3 *input,
global float *output
) {
	int id0 = get_global_id(0);
	output[id0] = simpleBillowLayer3(input[id0], value_noise3D, 200, quinticInterp,
		1, 0.125, false, 0.0, 0.0, 0.0, 0.0);
}

kernel void simpleBillowLayer3_value_noise3D_quinticInterp_rot(
global float3 *input,
global float *output
) {
	int id0 = get_global_id(0);
	output[id0] = simpleBillowLayer3(input[id0], value_noise3D, 200, quinticInterp,
		1, 0.125, true, 1.57, 1.0, 0.0, 0.0);
}

// #########################################
// gradient_noise3D
// #########################################

kernel void simpleBillowLayer3_gradient_noise3D_noInterp_norot(
global float3 *input,
global float *output
) {
	int id0 = get_global_id(0);
	output[id0] = simpleBillowLayer3(input[id0], gradient_noise3D, 200, noInterp,
		1, 0.125, false, 0.0, 0.0, 0.0, 0.0);
}

kernel void simpleBillowLayer3_gradient_noise3D_noInterp_rot(
global float3 *input,
global float *output
) {
	int id0 = get_global_id(0);
	output[id0] = simpleBillowLayer3(input[id0], gradient_noise3D, 200, noInterp,
		1, 0.125, true, 1.57, 1.0, 0.0, 0.0);
}

kernel void simpleBillowLayer3_gradient_noise3D_linearInterp_norot(
global float3 *input,
global float *output
) {
	int id0 = get_global_id(0);
	output[id0] = simpleBillowLayer3(input[id0], gradient_noise3D, 200, linearInterp,
		1, 0.125, false, 0.0, 0.0, 0.0, 0.0);
}

kernel void simpleBillowLayer3_gradient_noise3D_linearInterp_rot(
global float3 *input,
global float *output
) {
	int id0 = get_global_id(0);
	output[id0] = simpleBillowLayer3(input[id0], gradient_noise3D, 200, linearInterp,
		1, 0.125, true, 1.57, 1.0, 0.0, 0.0);
}

kernel void simpleBillowLayer3_gradient_noise3D_hermiteInterp_norot(
global float3 *input,
global float *output
) {
	int id0 = get_global_id(0);
	output[id0] = simpleBillowLayer3(input[id0], gradient_noise3D, 200, hermiteInterp,
		1, 0.125, false, 0.0, 0.0, 0.0, 0.0);
}

kernel void simpleBillowLayer3_gradient_noise3D_hermiteInterp_rot(
global float3 *input,
global float *output
) {
	int id0 = get_global_id(0);
	output[id0] = simpleBillowLayer3(input[id0], gradient_noise3D, 200, hermiteInterp,
		1, 0.125, true, 1.57, 1.0, 0.0, 0.0);
}

kernel void simpleBillowLayer3_gradient_noise3D_quinticInterp_norot(
global float3 *input,
global float *output
) {
	int id0 = get_global_id(0);
	output[id0] = simpleBillowLayer3(input[id0], gradient_noise3D, 200, quinticInterp,
		1, 0.125, false, 0.0, 0.0, 0.0, 0.0);
}

kernel void simpleBillowLayer3_gradient_noise3D_quinticInterp_rot(
global float3 *input,
global float *output
) {
	int id0 = get_global_id(0);
	output[id0] = simpleBillowLayer3(input[id0], gradient_noise3D, 200, quinticInterp,
		1, 0.125, true, 1.57, 1.0, 0.0, 0.0);
}

// #########################################
// gradval_noise3D
// #########################################

kernel void simpleBillowLayer3_gradval_noise3D_noInterp_norot(
global float3 *input,
global float *output
) {
	int id0 = get_global_id(0);
	output[id0] = simpleBillowLayer3(input[id0], gradval_noise3D, 200, noInterp,
		1, 0.125, false, 0.0, 0.0, 0.0, 0.0);
}

kernel void simpleBillowLayer3_gradval_noise3D_noInterp_rot(
global float3 *input,
global float *output
) {
	int id0 = get_global_id(0);
	output[id0] = simpleBillowLayer3(input[id0], gradval_noise3D, 200, noInterp,
		1, 0.125, true, 1.57, 1.0, 0.0, 0.0);
}

kernel void simpleBillowLayer3_gradval_noise3D_linearInterp_norot(
global float3 *input,
global float *output
) {
	int id0 = get_global_id(0);
	output[id0] = simpleBillowLayer3(input[id0], gradval_noise3D, 200, linearInterp,
		1, 0.125, false, 0.0, 0.0, 0.0, 0.0);
}

kernel void simpleBillowLayer3_gradval_noise3D_linearInterp_rot(
global float3 *input,
global float *output
) {
	int id0 = get_global_id(0);
	output[id0] = simpleBillowLayer3(input[id0], gradval_noise3D, 200, linearInterp,
		1, 0.125, true, 1.57, 1.0, 0.0, 0.0);
}

kernel void simpleBillowLayer3_gradval_noise3D_hermiteInterp_norot(
global float3 *input,
global float *output
) {
	int id0 = get_global_id(0);
	output[id0] = simpleBillowLayer3(input[id0], gradval_noise3D, 200, hermiteInterp,
		1, 0.125, false, 0.0, 0.0, 0.0, 0.0);
}

kernel void simpleBillowLayer3_gradval_noise3D_hermiteInterp_rot(
global float3 *input,
global float *output
) {
	int id0 = get_global_id(0);
	output[id0] = simpleBillowLayer3(input[id0], gradval_noise3D, 200, hermiteInterp,
		1, 0.125, true, 1.57, 1.0, 0.0, 0.0);
}

kernel void simpleBillowLayer3_gradval_noise3D_quinticInterp_norot(
global float3 *input,
global float *output
) {
	int id0 = get_global_id(0);
	output[id0] = simpleBillowLayer3(input[id0], gradval_noise3D, 200, quinticInterp,
		1, 0.125, false, 0.0, 0.0, 0.0, 0.0);
}

kernel void simpleBillowLayer3_gradval_noise3D_quinticInterp_rot(
global float3 *input,
global float *output
) {
	int id0 = get_global_id(0);
	output[id0] = simpleBillowLayer3(input[id0], gradval_noise3D, 200, quinticInterp,
		1, 0.125, true, 1.57, 1.0, 0.0, 0.0);
}

// #########################################
// white_noise3D
// #########################################

kernel void simpleBillowLayer3_white_noise3D_noInterp_norot(
global float3 *input,
global float *output
) {
	int id0 = get_global_id(0);
	output[id0] = simpleBillowLayer3(input[id0], white_noise3D, 200, noInterp,
		1, 0.125, false, 0.0, 0.0, 0.0, 0.0);
}

kernel void simpleBillowLayer3_white_noise3D_noInterp_rot(
global float3 *input,
global float *output
) {
	int id0 = get_global_id(0);
	output[id0] = simpleBillowLayer3(input[id0], white_noise3D, 200, noInterp,
		1, 0.125, true, 1.57, 1.0, 0.0, 0.0);
}

// #########################################
// simplex_noise3D
// #########################################

kernel void simpleBillowLayer3_simplex_noise3D_noInterp_norot(
global float3 *input,
global float *output
) {
	int id0 = get_global_id(0);
	output[id0] = simpleBillowLayer3(input[id0], simplex_noise3D, 200, noInterp,
		1, 0.125, false, 0.0, 0.0, 0.0, 0.0);
}

kernel void simpleBillowLayer3_simplex_noise3D_noInterp_rot(
global float3 *input,
global float *output
) {
	int id0 = get_global_id(0);
	output[id0] = simpleBillowLayer3(input[id0], simplex_noise3D, 200, noInterp,
		1, 0.125, true, 1.57, 1.0, 0.0, 0.0);
}
