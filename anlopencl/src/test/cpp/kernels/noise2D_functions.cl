//
// Copyright (C) 2021 Erwin Müller <erwin@muellerpublic.de>
// Released as open-source under the Apache License, Version 2.0.
//
// ****************************************************************************
// ANL-OpenCL :: Core
// ****************************************************************************
//
// Copyright (C) 2021 Erwin Müller <erwin@muellerpublic.de>
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//
// ****************************************************************************
// ANL-OpenCL :: Core is a derivative work based on Josua Tippetts' C++ library:
// http://accidentalnoise.sourceforge.net/index.html
// ****************************************************************************
//
// Copyright (C) 2011 Joshua Tippetts
//
//   This software is provided 'as-is', without any express or implied
//   warranty.  In no event will the authors be held liable for any damages
//   arising from the use of this software.
//
//   Permission is granted to anyone to use this software for any purpose,
//   including commercial applications, and to alter it and redistribute it
//   freely, subject to the following restrictions:
//
//   1. The origin of this software must not be misrepresented; you must not
//      claim that you wrote the original software. If you use this software
//      in a product, an acknowledgment in the product documentation would be
//      appreciated but is not required.
//   2. Altered source versions must be plainly marked as such, and must not be
//      misrepresented as being the original software.
//   3. This notice may not be removed or altered from any source distribution.
//

#include <opencl_utils.h>
#include <noise_gen.h>

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
