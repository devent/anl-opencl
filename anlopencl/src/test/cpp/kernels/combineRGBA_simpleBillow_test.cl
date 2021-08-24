kernel void main(
global float3 *input,
global float4 *output
) {
	int id0 = get_global_id(0);
	kiss09_state srnd;
	kiss09_seed(&srnd, 200);
	float bm = simpleBillow3(input[id0], value_noise3D, 200, linearInterp, random_kiss09, &srnd, 1, 0.125, true);
	float r = bm * 0.5 + 0.5;

	bm = simpleBillow3(input[id0], value_noise3D, 2000, linearInterp, random_kiss09, &srnd, 1, 0.125, true);
	float g = bm * 0.5 + 0.5;

	bm = simpleBillow3(input[id0], value_noise3D, 4000, linearInterp, random_kiss09, &srnd, 1, 0.125, true);
	float b = bm * 0.5 + 0.5;

	output[id0] = combineRGBA(r, g, b, 1.0);
}
