//
// Copyright (C) 2021-2022 Erwin Müller <erwin@muellerpublic.de>
// Released as open-source under the Apache License, Version 2.0.
//
// ****************************************************************************
// ANL-OpenCL :: OpenCL
// ****************************************************************************
//
// Copyright (C) 2021-2022 Erwin Müller <erwin@muellerpublic.de>
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
// ANL-OpenCL :: OpenCL is a derivative work based on Josua Tippetts' C++ library:
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
//
// ****************************************************************************
// ANL-OpenCL :: OpenCL bundles and uses the RandomCL library:
// https://github.com/bstatcomp/RandomCL
// ****************************************************************************
//
// BSD 3-Clause License
//
// Copyright (c) 2018, Tadej Ciglarič, Erik Štrumbelj, Rok Češnovar. All rights reserved.
//
// Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
//
// * Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
//
// * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
//
// * Neither the name of the copyright holder nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
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

#ifndef ANLOPENCL_USE_OPENCL
#include "opencl_utils.h"
#include "noise_gen.h"
#endif // ANLOPENCL_USE_OPENCL

/**
 * Function that returns a random number.
 */
typedef REAL (*random_func)(void*);

/**
 * Rotates the source coordinates by the angle over the rotation axis.
 * @param src the source vector3 (x,y,z).
 * @return the rotated vector3 (x',y',z').
 */
vector3 rotateDomain3(vector3 src, REAL angle, REAL ax, REAL ay, REAL az);

/**
 * Rotates the source coordinates by the angle over the rotation axis.
 * @param src the source vector4 (x,y,z,w).
 * @return the rotated vector4 (x',y',z',w).
 */
vector4 rotateDomain4(vector4 src, REAL angle, REAL ax, REAL ay, REAL az);

/**
 * Rotates the source coordinates by the angle over the rotation axis.
 * @param src the source vector8 (x,y,z,w,u,v).
 * @return the rotated vector8 (x',y',z',w,u,v).
 */
vector8 rotateDomain6(vector8 src, REAL angle, REAL ax, REAL ay, REAL az);

/**
 * Multiplies the source coordinates by the scale.
 */
#define scaleDomain(v, scale) (v * scale)

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
 * Returns simple fractal layer value for the coordinate.
 * @param v the vector4 (x,y,z,w) coordinate.
 * @param basistype the noise_func4 basis noise generation function.
 * <ul>
 * <li>value_noise4D
 * <li>gradient_noise4D
 * <li>gradval_noise4D
 * <li>white_noise4D
 * <li>simplex_noise4D
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
REAL simpleFractalLayer4(vector4 v, noise_func4 basistype,
		uint seed, interp_func interp,
		REAL layerscale, REAL layerfreq, bool rot,
		REAL angle, REAL ax, REAL ay, REAL az);
/**
 * Returns simple fractal layer value for the coordinate.
 * @param v the vector8 (x,y,z,w,v,u) coordinate.
 * @param basistype the noise_func6 basis noise generation function.
 * <ul>
 * <li>value_noise6D
 * <li>gradient_noise6D
 * <li>gradval_noise6D
 * <li>white_noise6D
 * <li>simplex_noise6D
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
REAL simpleFractalLayer6(vector8 v, noise_func6 basistype,
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
 * Returns simple ridged layer value for the coordinate.
 * @param v the vector4 (x,y,z) coordinate.
 * @param basistype the noise_func4 basis noise generation function.
 * <ul>
 * <li>value_noise4D
 * <li>gradient_noise4D
 * <li>gradval_noise4D
 * <li>white_noise4D
 * <li>simplex_noise4D
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
REAL simpleRidgedLayer4(vector4 v, noise_func4 basistype,
		uint seed, interp_func interp,
		REAL layerscale, REAL layerfreq, bool rot,
		REAL angle, REAL ax, REAL ay, REAL az);
/**
 * Returns simple ridged layer value for the coordinate.
 * @param v the vector8 (x,y,z,w,u,v) coordinate.
 * @param basistype the noise_func6 basis noise generation function.
 * <ul>
 * <li>value_noise6D
 * <li>gradient_noise6D
 * <li>gradval_noise6D
 * <li>white_noise6D
 * <li>simplex_noise6D
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
REAL simpleRidgedLayer6(vector8 v, noise_func6 basistype,
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
 * Returns simple billow layer value for the coordinate.
 * @param v the vector4 (x,y,z,w) coordinate.
 * @param basistype the noise_func4 basis noise generation function.
 * <ul>
 * <li>value_noise4D
 * <li>gradient_noise4D
 * <li>gradval_noise4D
 * <li>white_noise4D
 * <li>simplex_noise4D
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
REAL simpleBillowLayer4(vector4 v, noise_func4 basistype,
		uint seed, interp_func interp,
		REAL layerscale, REAL layerfreq, bool rot,
		REAL angle, REAL ax, REAL ay, REAL az);

/**
 * Returns simple billow layer value for the coordinate.
 * @param v the vector8 (x,y,z,w,u,v) coordinate.
 * @param basistype the noise_func6 basis noise generation function.
 * <ul>
 * <li>value_noise6D
 * <li>gradient_noise6D
 * <li>gradval_noise6D
 * <li>white_noise6D
 * <li>simplex_noise6D
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
REAL simpleBillowLayer6(vector8 v, noise_func6 basistype,
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
 * Returns fractional brownian motion value for the coordinate.
 * @param v the vector4 (x,y,z,w) coordinate.
 * @param basistype the noise_func4 basis noise generation function.
 * <ul>
 * <li>value_noise4D
 * <li>gradient_noise4D
 * <li>gradval_noise4D
 * <li>white_noise4D
 * <li>simplex_noise4D
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
REAL simplefBm4(
		vector4 v,
		noise_func4 basistype, uint seed, interp_func interp,
		random_func rnd, void *srnd,
		uint numoctaves, REAL frequency, bool rot);

/**
 * Returns fractional brownian motion value for the coordinate.
 * @param v the vector8 (x,y,z,w,u,v) coordinate.
 * @param basistype the noise_func6 basis noise generation function.
 * <ul>
 * <li>value_noise6D
 * <li>gradient_noise6D
 * <li>gradval_noise6D
 * <li>white_noise6D
 * <li>simplex_noise6D
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
REAL simplefBm6(
		vector8 v,
		noise_func6 basistype, uint seed, interp_func interp,
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
 * Returns ridged-multifractal noise value for the coordinate.
 * @param v the vector4 (x,y,z,w) coordinate.
 * @param basistype the noise_func4 basis noise generation function.
 * <ul>
 * <li>value_noise4D
 * <li>gradient_noise4D
 * <li>gradval_noise4D
 * <li>white_noise4D
 * <li>simplex_noise4D
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
REAL simpleRidgedMultifractal4(
		vector4 v,
		noise_func4 basistype, uint seed, interp_func interp,
		random_func rnd, void *srnd,
		uint numoctaves, REAL frequency, bool rot);

/**
 * Returns ridged-multifractal noise value for the coordinate.
 * @param v the vector8 (x,y,z,w,u,v) coordinate.
 * @param basistype the noise_func6 basis noise generation function.
 * <ul>
 * <li>value_noise6D
 * <li>gradient_noise6D
 * <li>gradval_noise6D
 * <li>white_noise6D
 * <li>simplex_noise6D
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
REAL simpleRidgedMultifractal6(
		vector8 v,
		noise_func6 basistype, uint seed, interp_func interp,
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

/**
 * Returns billow (cloud-like, lumpy) fractal value for the coordinate.
 * @param v the vector4 (x,y,z,w) coordinate.
 * @param basistype the noise_func4 basis noise generation function.
 * <ul>
 * <li>value_noise4D
 * <li>gradient_noise4D
 * <li>gradval_noise4D
 * <li>white_noise4D
 * <li>simplex_noise4D
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
REAL simpleBillow4(
		vector4 v,
		noise_func4 basistype, uint seed, interp_func interp,
		random_func rnd, void *srnd,
		uint numoctaves, REAL frequency, bool rot);

/**
 * Returns billow (cloud-like, lumpy) fractal value for the coordinate.
 * @param v the vector8 (x,y,z,w,u,v) coordinate.
 * @param basistype the noise_func6 basis noise generation function.
 * <ul>
 * <li>value_noise6D
 * <li>gradient_noise6D
 * <li>gradval_noise6D
 * <li>white_noise6D
 * <li>simplex_noise6D
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
REAL simpleBillow6(
		vector8 v,
		noise_func6 basistype, uint seed, interp_func interp,
		random_func rnd, void *srnd,
		uint numoctaves, REAL frequency, bool rot);

/** Returns the x component of the vector **/
#define x(v) (v.x)
/** Returns the y component of the vector **/
#define y(v) (v.y)
/** Returns the z component of the vector **/
#define z(v) (v.z)
/** Returns the w component of the vector **/
#define w(v) (v.w)
/** Returns the u component of the vector **/
#define u(v) (v.s4)
/** Returns the v component of the vector **/
#define v(v) (v.s5)

#ifdef __cplusplus
}
#endif

#endif /* KERNEL_H_ */
