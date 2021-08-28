
// #########################################
// value_noise3D
// #########################################

kernel void simpleBillow3_value_noise3D_noInterp_norot(
global float3 *input,
global float *output
) {
	int id0 = get_global_id(0);
	kiss09_state srnd;
	kiss09_seed(&srnd, 200);
	output[id0] = simpleBillow3(input[id0], value_noise3D, 200, noInterp,
		random_kiss09, &srnd, 3, 0.125, false);
}

kernel void simpleBillow3_value_noise3D_noInterp_rot(
global float3 *input,
global float *output
) {
	int id0 = get_global_id(0);
	kiss09_state srnd;
	kiss09_seed(&srnd, 200);
	output[id0] = simpleBillow3(input[id0], value_noise3D, 200, noInterp,
		random_kiss09, &srnd, 3, 0.125, true);
}

kernel void simpleBillow3_value_noise3D_linearInterp_norot(
global float3 *input,
global float *output
) {
	int id0 = get_global_id(0);
	kiss09_state srnd;
	kiss09_seed(&srnd, 200);
	output[id0] = simpleBillow3(input[id0], value_noise3D, 200, linearInterp,
		random_kiss09, &srnd, 3, 0.125, false);
}

kernel void simpleBillow3_value_noise3D_linearInterp_rot(
global float3 *input,
global float *output
) {
	int id0 = get_global_id(0);
	kiss09_state srnd;
	kiss09_seed(&srnd, 200);
	output[id0] = simpleBillow3(input[id0], value_noise3D, 200, linearInterp,
		random_kiss09, &srnd, 3, 0.125, true);
}

kernel void simpleBillow3_value_noise3D_hermiteInterp_norot(
global float3 *input,
global float *output
) {
	int id0 = get_global_id(0);
	kiss09_state srnd;
	kiss09_seed(&srnd, 200);
	output[id0] = simpleBillow3(input[id0], value_noise3D, 200, hermiteInterp,
		random_kiss09, &srnd, 3, 0.125, false);
}

kernel void simpleBillow3_value_noise3D_hermiteInterp_rot(
global float3 *input,
global float *output
) {
	int id0 = get_global_id(0);
	kiss09_state srnd;
	kiss09_seed(&srnd, 200);
	output[id0] = simpleBillow3(input[id0], value_noise3D, 200, hermiteInterp,
		random_kiss09, &srnd, 3, 0.125, true);
}

kernel void simpleBillow3_value_noise3D_quinticInterp_norot(
global float3 *input,
global float *output
) {
	int id0 = get_global_id(0);
	kiss09_state srnd;
	kiss09_seed(&srnd, 200);
	output[id0] = simpleBillow3(input[id0], value_noise3D, 200, quinticInterp,
		random_kiss09, &srnd, 3, 0.125, false);
}

kernel void simpleBillow3_value_noise3D_quinticInterp_rot(
global float3 *input,
global float *output
) {
	int id0 = get_global_id(0);
	kiss09_state srnd;
	kiss09_seed(&srnd, 200);
	output[id0] = simpleBillow3(input[id0], value_noise3D, 200, quinticInterp,
		random_kiss09, &srnd, 3, 0.125, true);
}

// #########################################
// gradient_noise3D
// #########################################

kernel void simpleBillow3_gradient_noise3D_noInterp_norot(
global float3 *input,
global float *output
) {
	int id0 = get_global_id(0);
	kiss09_state srnd;
	kiss09_seed(&srnd, 200);
	output[id0] = simpleBillow3(input[id0], gradient_noise3D, 200, noInterp,
		random_kiss09, &srnd, 3, 0.125, false);
}

kernel void simpleBillow3_gradient_noise3D_noInterp_rot(
global float3 *input,
global float *output
) {
	int id0 = get_global_id(0);
	kiss09_state srnd;
	kiss09_seed(&srnd, 200);
	output[id0] = simpleBillow3(input[id0], gradient_noise3D, 200, noInterp,
		random_kiss09, &srnd, 3, 0.125, true);
}

kernel void simpleBillow3_gradient_noise3D_linearInterp_norot(
global float3 *input,
global float *output
) {
	int id0 = get_global_id(0);
	kiss09_state srnd;
	kiss09_seed(&srnd, 200);
	output[id0] = simpleBillow3(input[id0], gradient_noise3D, 200, linearInterp,
		random_kiss09, &srnd, 3, 0.125, false);
}

kernel void simpleBillow3_gradient_noise3D_linearInterp_rot(
global float3 *input,
global float *output
) {
	int id0 = get_global_id(0);
	kiss09_state srnd;
	kiss09_seed(&srnd, 200);
	output[id0] = simpleBillow3(input[id0], gradient_noise3D, 200, linearInterp,
		random_kiss09, &srnd, 3, 0.125, true);
}

kernel void simpleBillow3_gradient_noise3D_hermiteInterp_norot(
global float3 *input,
global float *output
) {
	int id0 = get_global_id(0);
	kiss09_state srnd;
	kiss09_seed(&srnd, 200);
	output[id0] = simpleBillow3(input[id0], gradient_noise3D, 200, hermiteInterp,
		random_kiss09, &srnd, 3, 0.125, false);
}

kernel void simpleBillow3_gradient_noise3D_hermiteInterp_rot(
global float3 *input,
global float *output
) {
	int id0 = get_global_id(0);
	kiss09_state srnd;
	kiss09_seed(&srnd, 200);
	output[id0] = simpleBillow3(input[id0], gradient_noise3D, 200, hermiteInterp,
		random_kiss09, &srnd, 3, 0.125, true);
}

kernel void simpleBillow3_gradient_noise3D_quinticInterp_norot(
global float3 *input,
global float *output
) {
	int id0 = get_global_id(0);
	kiss09_state srnd;
	kiss09_seed(&srnd, 200);
	output[id0] = simpleBillow3(input[id0], gradient_noise3D, 200, quinticInterp,
		random_kiss09, &srnd, 3, 0.125, false);
}

kernel void simpleBillow3_gradient_noise3D_quinticInterp_rot(
global float3 *input,
global float *output
) {
	int id0 = get_global_id(0);
	kiss09_state srnd;
	kiss09_seed(&srnd, 200);
	output[id0] = simpleBillow3(input[id0], gradient_noise3D, 200, quinticInterp,
		random_kiss09, &srnd, 3, 0.125, true);
}

// #########################################
// gradval_noise3D
// #########################################

kernel void simpleBillow3_gradval_noise3D_noInterp_norot(
global float3 *input,
global float *output
) {
	int id0 = get_global_id(0);
	kiss09_state srnd;
	kiss09_seed(&srnd, 200);
	output[id0] = simpleBillow3(input[id0], gradval_noise3D, 200, noInterp,
		random_kiss09, &srnd, 3, 0.125, false);
}

kernel void simpleBillow3_gradval_noise3D_noInterp_rot(
global float3 *input,
global float *output
) {
	int id0 = get_global_id(0);
	kiss09_state srnd;
	kiss09_seed(&srnd, 200);
	output[id0] = simpleBillow3(input[id0], gradval_noise3D, 200, noInterp,
		random_kiss09, &srnd, 3, 0.125, true);
}

kernel void simpleBillow3_gradval_noise3D_linearInterp_norot(
global float3 *input,
global float *output
) {
	int id0 = get_global_id(0);
	kiss09_state srnd;
	kiss09_seed(&srnd, 200);
	output[id0] = simpleBillow3(input[id0], gradval_noise3D, 200, linearInterp,
		random_kiss09, &srnd, 3, 0.125, false);
}

kernel void simpleBillow3_gradval_noise3D_linearInterp_rot(
global float3 *input,
global float *output
) {
	int id0 = get_global_id(0);
	kiss09_state srnd;
	kiss09_seed(&srnd, 200);
	output[id0] = simpleBillow3(input[id0], gradval_noise3D, 200, linearInterp,
		random_kiss09, &srnd, 3, 0.125, true);
}

kernel void simpleBillow3_gradval_noise3D_hermiteInterp_norot(
global float3 *input,
global float *output
) {
	int id0 = get_global_id(0);
	kiss09_state srnd;
	kiss09_seed(&srnd, 200);
	output[id0] = simpleBillow3(input[id0], gradval_noise3D, 200, hermiteInterp,
		random_kiss09, &srnd, 3, 0.125, false);
}

kernel void simpleBillow3_gradval_noise3D_hermiteInterp_rot(
global float3 *input,
global float *output
) {
	int id0 = get_global_id(0);
	kiss09_state srnd;
	kiss09_seed(&srnd, 200);
	output[id0] = simpleBillow3(input[id0], gradval_noise3D, 200, hermiteInterp,
		random_kiss09, &srnd, 3, 0.125, true);
}

kernel void simpleBillow3_gradval_noise3D_quinticInterp_norot(
global float3 *input,
global float *output
) {
	int id0 = get_global_id(0);
	kiss09_state srnd;
	kiss09_seed(&srnd, 200);
	output[id0] = simpleBillow3(input[id0], gradval_noise3D, 200, quinticInterp,
		random_kiss09, &srnd, 3, 0.125, false);
}

kernel void simpleBillow3_gradval_noise3D_quinticInterp_rot(
global float3 *input,
global float *output
) {
	int id0 = get_global_id(0);
	kiss09_state srnd;
	kiss09_seed(&srnd, 200);
	output[id0] = simpleBillow3(input[id0], gradval_noise3D, 200, quinticInterp,
		random_kiss09, &srnd, 3, 0.125, true);
}

// #########################################
// white_noise3D
// #########################################

kernel void simpleBillow3_white_noise3D_noInterp_norot(
global float3 *input,
global float *output
) {
	int id0 = get_global_id(0);
	kiss09_state srnd;
	kiss09_seed(&srnd, 200);
	output[id0] = simpleBillow3(input[id0], white_noise3D, 200, noInterp,
		random_kiss09, &srnd, 3, 0.125, false);
}

kernel void simpleBillow3_white_noise3D_noInterp_rot(
global float3 *input,
global float *output
) {
	int id0 = get_global_id(0);
	kiss09_state srnd;
	kiss09_seed(&srnd, 200);
	output[id0] = simpleBillow3(input[id0], white_noise3D, 200, noInterp,
		random_kiss09, &srnd, 3, 0.125, true);
}

// #########################################
// simplex_noise3D
// #########################################

kernel void simpleBillow3_simplex_noise3D_noInterp_norot(
global float3 *input,
global float *output
) {
	int id0 = get_global_id(0);
	kiss09_state srnd;
	kiss09_seed(&srnd, 200);
	output[id0] = simpleBillow3(input[id0], simplex_noise3D, 200, noInterp,
		random_kiss09, &srnd, 3, 0.125, false);
}

kernel void simpleBillow3_simplex_noise3D_noInterp_rot(
global float3 *input,
global float *output
) {
	int id0 = get_global_id(0);
	kiss09_state srnd;
	kiss09_seed(&srnd, 200);
	output[id0] = simpleBillow3(input[id0], simplex_noise3D, 200, noInterp,
		random_kiss09, &srnd, 3, 0.125, true);
}
