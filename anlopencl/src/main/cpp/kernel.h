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

/*
 * kernel.h
 *
 *  Created on: Aug 13, 2021
 *      Author: Erwin Müller
 */

#ifndef KERNEL_H_
#define KERNEL_H_

#ifdef __cplusplus
extern "C" {
#endif

#ifndef USE_OPENCL
#include "opencl_utils.h"
#include "noise_gen.h"
#endif // USE_OPENCL

typedef REAL (*random_func)(void*);

/**
 * Rotates the source coordinates by the angle over the rotation axis.
 */
vector3 rotateDomain3(vector3 src, REAL angle, REAL ax, REAL ay, REAL az);

/**
 * Multiplies the source coordinates by the scale.
 */
vector3 scaleDomain3(vector3 src, REAL scale);

/**
 * Combines the RGBA values.
 */
vector4 combineRGBA(REAL r, REAL g, REAL b, REAL a);

/**
 * Combines the HSVA values.
 */
vector4 combineHSVA(REAL h, REAL s, REAL v, REAL a);

/**
 * Returns simple fractal layer value for the coordinate.
 * @param v the vector3 (x,y,z) coordinate.
 * @param basistype the noise_func3 basis noise generation function.
 * <ul>
 * <li>value_noise3D
 * <li>gradient_noise3D
 * <li>gradval_noise3D
 * <li>white_noise3D
 * <li>simplex_noise3D
 * </ul>
 * @param seed the uint seed for the noise generation function.
 * @param interp the interpolation function for the noise generation function.
 * <ul>
 * <li>noInterp
 * <li>linearInterp
 * <li>hermiteInterp
 * <li>quinticInterp
 * </ul>
 * @param layerscale the scaling of the layer.
 * @param layerfreq the frequency of the layer.
 * @param rot set to true to rotate the layer by the angle and rotation axis.
 * @param angle the rotation angle in radians.
 * @param ax the rotation vector x component.
 * @param ay the rotation vector y component.
 * @param az the rotation vector z component.
 */
REAL simpleFractalLayer3(vector3 v, noise_func3 basistype,
		uint seed, interp_func interp,
		REAL layerscale, REAL layerfreq, bool rot,
		REAL angle, REAL ax, REAL ay, REAL az);

/**
 * Returns simple ridged layer value for the coordinate.
 * @param v the vector3 (x,y,z) coordinate.
 * @param basistype the noise_func3 basis noise generation function.
 * <ul>
 * <li>value_noise3D
 * <li>gradient_noise3D
 * <li>gradval_noise3D
 * <li>white_noise3D
 * <li>simplex_noise3D
 * </ul>
 * @param seed the uint seed for the noise generation function.
 * @param interp the interpolation function for the noise generation function.
 * <ul>
 * <li>noInterp
 * <li>linearInterp
 * <li>hermiteInterp
 * <li>quinticInterp
 * </ul>
 * @param layerscale the scaling of the layer.
 * @param layerfreq the frequency of the layer.
 * @param rot set to true to rotate the layer by the angle and rotation axis.
 * @param angle the rotation angle in radians.
 * @param ax the rotation vector x component.
 * @param ay the rotation vector y component.
 * @param az the rotation vector z component.
 */
REAL simpleRidgedLayer3(vector3 v, noise_func3 basistype,
		uint seed, interp_func interp,
		REAL layerscale, REAL layerfreq, bool rot,
		REAL angle, REAL ax, REAL ay, REAL az);

/**
 * Returns simple billow layer value for the coordinate.
 * @param v the vector3 (x,y,z) coordinate.
 * @param basistype the noise_func3 basis noise generation function.
 * <ul>
 * <li>value_noise3D
 * <li>gradient_noise3D
 * <li>gradval_noise3D
 * <li>white_noise3D
 * <li>simplex_noise3D
 * </ul>
 * @param seed the uint seed for the noise generation function.
 * @param interp the interpolation function for the noise generation function.
 * <ul>
 * <li>noInterp
 * <li>linearInterp
 * <li>hermiteInterp
 * <li>quinticInterp
 * </ul>
 * @param layerscale the scaling of the layer.
 * @param layerfreq the frequency of the layer.
 * @param rot set to true to rotate the layer by the angle and rotation axis.
 * @param angle the rotation angle in radians.
 * @param ax the rotation vector x component.
 * @param ay the rotation vector y component.
 * @param az the rotation vector z component.
 */
REAL simpleBillowLayer3(vector3 v, noise_func3 basistype,
		uint seed, interp_func interp,
		REAL layerscale, REAL layerfreq, bool rot,
		REAL angle, REAL ax, REAL ay, REAL az);

/**
 * Returns fractional brownian motion value for the coordinate.
 * @param v the vector3 (x,y,z) coordinate.
 * @param basistype the noise_func3 basis noise generation function.
 * <ul>
 * <li>value_noise3D
 * <li>gradient_noise3D
 * <li>gradval_noise3D
 * <li>white_noise3D
 * <li>simplex_noise3D
 * </ul>
 * @param seed the uint seed for the noise generation function.
 * @param interp the interpolation function for the noise generation function.
 * <ul>
 * <li>noInterp
 * <li>linearInterp
 * <li>hermiteInterp
 * <li>quinticInterp
 * </ul>
 * @param numoctaves the number of octaves.
 * @param frequency the frequency.
 * @param rnd the random_func to return random values.
 * @param srnd the random generator state.
 * @param rot set to true to rotate the layers by a random value.
 */
REAL simplefBm3(
		vector3 v,
		noise_func3 basistype, uint seed, interp_func interp,
		random_func rnd, void *srnd,
		uint numoctaves, REAL frequency, bool rot);

/**
 * Returns ridged-multifractal noise value for the coordinate.
 * @param v the vector3 (x,y,z) coordinate.
 * @param basistype the noise_func3 basis noise generation function.
 * <ul>
 * <li>value_noise3D
 * <li>gradient_noise3D
 * <li>gradval_noise3D
 * <li>white_noise3D
 * <li>simplex_noise3D
 * </ul>
 * @param seed the uint seed for the noise generation function.
 * @param interp the interpolation function for the noise generation function.
 * <ul>
 * <li>noInterp
 * <li>linearInterp
 * <li>hermiteInterp
 * <li>quinticInterp
 * </ul>
 * @param numoctaves the number of octaves.
 * @param frequency the frequency.
 * @param rnd the random_func to return random values.
 * @param srnd the random generator state.
 * @param rot set to true to rotate the layers by a random value.
 */
REAL simpleRidgedMultifractal3(
		vector3 v,
		noise_func3 basistype, uint seed, interp_func interp,
		random_func rnd, void *srnd,
		uint numoctaves, REAL frequency, bool rot);

/**
 * Returns billow (cloud-like, lumpy) fractal value for the coordinate.
 * @param v the vector3 (x,y,z) coordinate.
 * @param basistype the noise_func3 basis noise generation function.
 * <ul>
 * <li>value_noise3D
 * <li>gradient_noise3D
 * <li>gradval_noise3D
 * <li>white_noise3D
 * <li>simplex_noise3D
 * </ul>
 * @param seed the uint seed for the noise generation function.
 * @param interp the interpolation function for the noise generation function.
 * <ul>
 * <li>noInterp
 * <li>linearInterp
 * <li>hermiteInterp
 * <li>quinticInterp
 * </ul>
 * @param numoctaves the number of octaves.
 * @param frequency the frequency.
 * @param rnd the random_func to return random values.
 * @param srnd the random generator state.
 * @param rot set to true to rotate the layers by a random value.
 */
REAL simpleBillow3(
		vector3 v,
		noise_func3 basistype, uint seed, interp_func interp,
		random_func rnd, void *srnd,
		uint numoctaves, REAL frequency, bool rot);

REAL x2(vector2 *coord);
REAL y2(vector2 *coord);

REAL x3(vector3 *coord);
REAL y3(vector3 *coord);
REAL z3(vector3 *coord);

REAL x4(vector4 *coord);
REAL y4(vector4 *coord);
REAL z4(vector4 *coord);
REAL w4(vector4 *coord);

REAL x8(vector8 *coord);
REAL y8(vector8 *coord);
REAL z8(vector8 *coord);
REAL w8(vector8 *coord);
REAL u8(vector8 *coord);

REAL x8(vector8 *coord);
REAL y8(vector8 *coord);
REAL z8(vector8 *coord);
REAL w8(vector8 *coord);
REAL u8(vector8 *coord);
REAL v8(vector8 *coord);

#ifdef __cplusplus
}
#endif

#endif /* KERNEL_H_ */
