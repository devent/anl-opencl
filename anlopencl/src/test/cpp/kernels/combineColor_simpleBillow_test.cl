kernel void combineRGBA_simpleBillow(
global float3 *input,
global float4 *output
) {
	int id0 = get_global_id(0);
	kiss09_state srnd;
	kiss09_seed(&srnd, 200);

	input[id0] = scaleDomain3(input[id0], 5.0);

	float bm = simpleBillow3(input[id0], value_noise3D, 200, linearInterp, random_kiss09, &srnd, 1, 0.125, true);
	float r = bm * 0.5 + 0.5;

	bm = simpleBillow3(input[id0], value_noise3D, 2000, linearInterp, random_kiss09, &srnd, 1, 0.125, true);
	float g = bm * 0.5 + 0.5;

	bm = simpleBillow3(input[id0], value_noise3D, 4000, linearInterp, random_kiss09, &srnd, 1, 0.125, true);
	float b = bm * 0.5 + 0.5;

	output[id0] = combineRGBA(r, g, b, 1.0);
}

kernel void combineHSVA_simpleBillow(
global float3 *input,
global float4 *output
) {
	int id0 = get_global_id(0);
	kiss09_state srnd;
	kiss09_seed(&srnd, 200);

	input[id0] = scaleDomain3(input[id0], 5.0);

	float bm = simpleBillow3(input[id0], value_noise3D, 200, linearInterp, random_kiss09, &srnd, 1, 0.125, true);
	float h = bm * 0.5 + 0.5;

	bm = simpleBillow3(input[id0], value_noise3D, 2000, linearInterp, random_kiss09, &srnd, 1, 0.125, true);
	float s = bm * 0.5 + 0.5;

	bm = simpleBillow3(input[id0], value_noise3D, 4000, linearInterp, random_kiss09, &srnd, 1, 0.125, true);
	float v = bm * 0.5 + 0.5;

	output[id0] = combineHSVA(h, s, v, 1.0);
}
