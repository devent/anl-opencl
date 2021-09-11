//
// Copyright (C) 2021 Erwin Müller <erwin@muellerpublic.de>
// Released as open-source under the Apache License, Version 2.0.
//
// ****************************************************************************
// ANL-OpenCL :: OpenCL
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

/**
 * @file noise_gen.h
 * @author Erwin Müller
 * @date Jul 26, 2021
 * @brief Contains the definitions of the noise generator functions.
 */

#ifndef NOISE_GEN_H_
#define NOISE_GEN_H_

#ifdef __cplusplus
extern "C" {
#endif

#ifndef ANLOPENCL_USE_OPENCL
#include "opencl_utils.h"
#endif // ANLOPENCL_USE_OPENCL

/**
 * The interpolation function.
 * <ul>
 * <li>noInterp
 * <li>linearInterp
 * <li>hermiteInterp
 * <li>quinticInterp
 * </ul>
 */
typedef REAL (*interp_func)(REAL);

/**
 * The 2D noise function.
 * <ul>
 * <li>value_noise2D
 * <li>gradient_noise2D
 * <li>gradval_noise2D
 * <li>white_noise2D
 * <li>simplex_noise2D
 * </ul>
 */
typedef REAL (*noise_func2)(vector2, uint, interp_func);

/**
 * The 3D noise function.
 * <ul>
 * <li>value_noise3D
 * <li>gradient_noise3D
 * <li>gradval_noise3D
 * <li>white_noise3D
 * <li>simplex_noise3D
 * </ul>
 */
typedef REAL (*noise_func3)(vector3, uint, interp_func);

/**
 * The 4D noise function.
 * <ul>
 * <li>value_noise4D
 * <li>gradient_noise4D
 * <li>gradval_noise4D
 * <li>white_noise4D
 * <li>simplex_noise4D
 * </ul>
 */
typedef REAL (*noise_func4)(vector4, uint, interp_func);

/**
 * The 6D noise function.
 * <ul>
 * <li>value_noise6D
 * <li>gradient_noise6D
 * <li>gradval_noise6D
 * <li>white_noise6D
 * <li>simplex_noise6D
 * </ul>
 */
typedef REAL (*noise_func6)(vector8, uint, interp_func);

/**
 * The 2D distance function.
 * <ul>
 * <li>distEuclid2
 * <li>distManhattan2
 * <li>distGreatestAxis2
 * <li>distLeastAxis2
 * </ul>
 */
typedef REAL (*dist_func2)(vector2, vector2);

/**
 * The 3D distance function.
 * <ul>
 * <li>distEuclid3
 * <li>distManhattan3
 * <li>distGreatestAxis3
 * <li>distLeastAxis3
 * </ul>
 */
typedef REAL (*dist_func3)(vector3, vector3);

/**
 * The 4D distance function.
 * <ul>
 * <li>distEuclid4
 * <li>distManhattan4
 * <li>distGreatestAxis4
 * <li>distLeastAxis4
 * </ul>
 */
typedef REAL (*dist_func4)(vector4, vector4);

/**
 * The 6D distance function.
 * <ul>
 * <li>distEuclid6
 * <li>distManhattan6
 * <li>distGreatestAxis6
 * <li>distLeastAxis6
 * </ul>
 */
typedef REAL (*dist_func6)(vector8, vector8);

/**
 * No interpolation, i.e. returns 0.
 */
REAL noInterp(REAL t);

/**
 * Linear interpolation, i.e. returns t.
 */
REAL linearInterp(REAL t);

/**
 * Hermite interpolation.
 */
REAL hermiteInterp(REAL t);

/**
 * Quintic interpolation.
 */
REAL quinticInterp(REAL t);

/**
 * Returns the Euclidean distance between two points.
 * \f$d(a,b) = |a-b|\f$
 */
REAL distEuclid2(vector2 a, vector2 b);

/**
 * Returns the Euclidean distance between two points.
 * \f$d(a,b) = |a-b|\f$
 */
REAL distEuclid3(vector3 a, vector3 b);

/**
 * Returns the Euclidean distance between two points.
 * \f$d(a,b) = |a-b|\f$
 */
REAL distEuclid4(vector4 a, vector4 b);

/**
 * Returns the Euclidean distance between two points.
 * \f$d(a,b) = |a-b|\f$
 */
REAL distEuclid6(vector8 a, vector8 b);

/**
 * Returns the Manhattan distance between two points.
 *
 * <blockquote>
 * A taxicab geometry is a form of geometry in which the usual distance function or metric of Euclidean geometry is replaced by a new metric in which the distance between two points is the sum of the absolute differences of their Cartesian coordinates.
 * https://en.wikipedia.org/wiki/Taxicab_geometry
 * </blockquote>
 */
REAL distManhattan2(vector2 a, vector2 b);

/**
 * Returns the Manhattan distance between two points.
 *
 * <blockquote>
 * A taxicab geometry is a form of geometry in which the usual distance function or metric of Euclidean geometry is replaced by a new metric in which the distance between two points is the sum of the absolute differences of their Cartesian coordinates.
 * https://en.wikipedia.org/wiki/Taxicab_geometry
 * </blockquote>
 */
REAL distManhattan3(vector3 a, vector3 b);

/**
 * Returns the Manhattan distance between two points.
 *
 * <blockquote>
 * A taxicab geometry is a form of geometry in which the usual distance function or metric of Euclidean geometry is replaced by a new metric in which the distance between two points is the sum of the absolute differences of their Cartesian coordinates.
 * https://en.wikipedia.org/wiki/Taxicab_geometry
 * </blockquote>
 */
REAL distManhattan4(vector4 a, vector4 b);

/**
 * Returns the Manhattan distance between two points.
 *
 * <blockquote>
 * A taxicab geometry is a form of geometry in which the usual distance function or metric of Euclidean geometry is replaced by a new metric in which the distance between two points is the sum of the absolute differences of their Cartesian coordinates.
 * https://en.wikipedia.org/wiki/Taxicab_geometry
 * </blockquote>
 */
REAL distManhattan6(vector8 a, vector8 b);

/**
 * Returns the distance between two points on the axis that have the greatest distance.
 */
REAL distGreatestAxis2(vector2 a, vector2 b);

/**
 * Returns the distance between two points on the axis that have the greatest distance.
 */
REAL distGreatestAxis3(vector3 a, vector3 b);

/**
 * Returns the distance between two points on the axis that have the greatest distance.
 */
REAL distGreatestAxis4(vector4 a, vector4 b);

/**
 * Returns the distance between two points on the axis that have the greatest distance.
 */
REAL distGreatestAxis6(vector8 a, vector8 b);

/**
 * Returns the distance between two points on the axis that have the least distance.
 */
REAL distLeastAxis2(vector2 a, vector2 b);

/**
 * Returns the distance between two points on the axis that have the least distance.
 */
REAL distLeastAxis3(vector3 a, vector3 b);

/**
 * Returns the distance between two points on the axis that have the least distance.
 */
REAL distLeastAxis4(vector4 a, vector4 b);

/**
 * Returns the distance between two points on the axis that have the least distance.
 */
REAL distLeastAxis6(vector8 a, vector8 b);

/**
 * Value noise functions.
 *
 * <blockquote>
 * Value noise is a type of noise commonly used as a procedural texture primitive in computer graphics.
 * This method consists of the creation of a lattice of points which are assigned random values.
 * The noise function then returns the interpolated number based on the values of the surrounding lattice points.
 * https://en.wikipedia.org/wiki/Value_noise
 * </blockquote>
 *
 * @param v the vector2 (x,y) coordinates.
 * @param seed the uint random number seed.
 * @param interp the interpolation function:
 * <ul>
 * <li>noInterp
 * <li>linearInterp
 * <li>hermiteInterp
 * <li>quinticInterp
 * </ul>
 */
REAL value_noise2D(vector2 v, uint seed, interp_func interp);

/**
 * Value noise functions.
 *
 * <blockquote>
 * Value noise is a type of noise commonly used as a procedural texture primitive in computer graphics.
 * This method consists of the creation of a lattice of points which are assigned random values.
 * The noise function then returns the interpolated number based on the values of the surrounding lattice points.
 * https://en.wikipedia.org/wiki/Value_noise
 * </blockquote>
 *
 * @param v the vector3 (x,y,z) coordinates.
 * @param seed the uint random number seed.
 * @param interp the interpolation function:
 * <ul>
 * <li>noInterp
 * <li>linearInterp
 * <li>hermiteInterp
 * <li>quinticInterp
 * </ul>
 */
REAL value_noise3D(vector3 v, uint seed, interp_func interp);

/**
 * Value noise functions.
 *
 * <blockquote>
 * Value noise is a type of noise commonly used as a procedural texture primitive in computer graphics.
 * This method consists of the creation of a lattice of points which are assigned random values.
 * The noise function then returns the interpolated number based on the values of the surrounding lattice points.
 * https://en.wikipedia.org/wiki/Value_noise
 * </blockquote>
 *
 * @param v the vector4 (x,y,z,w) coordinates.
 * @param seed the uint random number seed.
 * @param interp the interpolation function:
 * <ul>
 * <li>noInterp
 * <li>linearInterp
 * <li>hermiteInterp
 * <li>quinticInterp
 * </ul>
 */
REAL value_noise4D(vector4 v, uint seed, interp_func interp);

/**
 * Value noise functions.
 *
 * <blockquote>
 * Value noise is a type of noise commonly used as a procedural texture primitive in computer graphics.
 * This method consists of the creation of a lattice of points which are assigned random values.
 * The noise function then returns the interpolated number based on the values of the surrounding lattice points.
 * https://en.wikipedia.org/wiki/Value_noise
 * </blockquote>
 *
 * @param v the vector8 (x,y,z,w,u,v) coordinates.
 * @param seed the uint random number seed.
 * @param interp the interpolation function:
 * <ul>
 * <li>noInterp
 * <li>linearInterp
 * <li>hermiteInterp
 * <li>quinticInterp
 * </ul>
 */
REAL value_noise6D(vector8 v, uint seed, interp_func interp);

/**
 * Gradient noise functions.
 *
 * <blockquote>
 * Gradient noise is a type of noise commonly used as a procedural texture primitive in computer graphics.
 * This method consists of a creation of a lattice of random (or typically pseudorandom) gradients,
 * dot products of which are then interpolated to obtain values in between the lattices.
 * An artifact of some implementations of this noise is that the returned value at the lattice points is 0.
 * Unlike the value noise, gradient noise has more energy in the high frequencies.
 * https://en.wikipedia.org/wiki/Gradient_noise
 * </blockquote>
 *
 * @param v the vector2 (x,y) coordinates.
 * @param seed the uint random number seed.
 * @param interp the interpolation function:
 * <ul>
 * <li>noInterp
 * <li>linearInterp
 * <li>hermiteInterp
 * <li>quinticInterp
 * </ul>
 */
REAL gradient_noise2D(vector2 v, uint seed, interp_func interp);

/**
 * Gradient noise functions.
 *
 * <blockquote>
 * Gradient noise is a type of noise commonly used as a procedural texture primitive in computer graphics.
 * This method consists of a creation of a lattice of random (or typically pseudorandom) gradients,
 * dot products of which are then interpolated to obtain values in between the lattices.
 * An artifact of some implementations of this noise is that the returned value at the lattice points is 0.
 * Unlike the value noise, gradient noise has more energy in the high frequencies.
 * https://en.wikipedia.org/wiki/Gradient_noise
 * </blockquote>
 *
 * @param v the vector3 (x,y,z) coordinates.
 * @param seed the uint random number seed.
 * @param interp the interpolation function:
 * <ul>
 * <li>noInterp
 * <li>linearInterp
 * <li>hermiteInterp
 * <li>quinticInterp
 * </ul>
 */
REAL gradient_noise3D(vector3 v, uint seed, interp_func interp);

/**
 * Gradient noise functions.
 *
 * <blockquote>
 * Gradient noise is a type of noise commonly used as a procedural texture primitive in computer graphics.
 * This method consists of a creation of a lattice of random (or typically pseudorandom) gradients,
 * dot products of which are then interpolated to obtain values in between the lattices.
 * An artifact of some implementations of this noise is that the returned value at the lattice points is 0.
 * Unlike the value noise, gradient noise has more energy in the high frequencies.
 * https://en.wikipedia.org/wiki/Gradient_noise
 * </blockquote>
 *
 * @param v the vector4 (x,y,z,w) coordinates.
 * @param seed the uint random number seed.
 * @param interp the interpolation function:
 * <ul>
 * <li>noInterp
 * <li>linearInterp
 * <li>hermiteInterp
 * <li>quinticInterp
 * </ul>
 */
REAL gradient_noise4D(vector4 v, uint seed, interp_func interp);

/**
 * Gradient noise functions.
 *
 * <blockquote>
 * Gradient noise is a type of noise commonly used as a procedural texture primitive in computer graphics.
 * This method consists of a creation of a lattice of random (or typically pseudorandom) gradients,
 * dot products of which are then interpolated to obtain values in between the lattices.
 * An artifact of some implementations of this noise is that the returned value at the lattice points is 0.
 * Unlike the value noise, gradient noise has more energy in the high frequencies.
 * https://en.wikipedia.org/wiki/Gradient_noise
 * </blockquote>
 *
 * @param v the vector8 (x,y,z,w,u,v) coordinates.
 * @param seed the uint random number seed.
 * @param interp the interpolation function:
 * <ul>
 * <li>noInterp
 * <li>linearInterp
 * <li>hermiteInterp
 * <li>quinticInterp
 * </ul>
 */
REAL gradient_noise6D(vector8 v, uint seed, interp_func interp);

/**
 * Combined value and gradient noise functions.
 *
 * @param v the vector2 (x,y) coordinates.
 * @param seed the uint random number seed.
 * @param interp the interpolation function:
 * <ul>
 * <li>noInterp
 * <li>linearInterp
 * <li>hermiteInterp
 * <li>quinticInterp
 * </ul>
 */
REAL gradval_noise2D(vector2 v, uint seed, interp_func interp);

/**
 * Combined value and gradient noise functions.
 *
 * @param v the vector3 (x,y,z) coordinates.
 * @param seed the uint random number seed.
 * @param interp the interpolation function:
 * <ul>
 * <li>noInterp
 * <li>linearInterp
 * <li>hermiteInterp
 * <li>quinticInterp
 * </ul>
 */
REAL gradval_noise3D(vector3 v, uint seed, interp_func interp);

/**
 * Combined value and gradient noise functions.
 *
 * @param v the vector4 (x,y,z,w) coordinates.
 * @param seed the uint random number seed.
 * @param interp the interpolation function:
 * <ul>
 * <li>noInterp
 * <li>linearInterp
 * <li>hermiteInterp
 * <li>quinticInterp
 * </ul>
 */
REAL gradval_noise4D(vector4 v, uint seed, interp_func interp);

/**
 * Combined value and gradient noise functions.
 *
 * @param v the vector8 (x,y,z,w,u,v) coordinates.
 * @param seed the uint random number seed.
 * @param interp the interpolation function:
 * <ul>
 * <li>noInterp
 * <li>linearInterp
 * <li>hermiteInterp
 * <li>quinticInterp
 * </ul>
 */
REAL gradval_noise6D(vector8 v, uint seed, interp_func interp);

/**
 * White noise functions.
 *
 * <blockquote>
 * In signal processing, white noise is a random signal having equal
 * intensity at different frequencies, giving it a constant power spectral density.
 * https://en.wikipedia.org/wiki/White_noise
 * </blockquote>
 *
 * The interpolation function parameter is not used.
 * The interpolation function parameter is only for compatibility with
 * the other noise functions.
 *
 * @param v the vector2 (x,y) coordinates.
 * @param seed the uint random number seed.
 * @param interp not used.
 */
REAL white_noise2D(vector2 v, uint seed, interp_func interp);

/**
 * White noise functions.
 *
 * <blockquote>
 * In signal processing, white noise is a random signal having equal
 * intensity at different frequencies, giving it a constant power spectral density.
 * https://en.wikipedia.org/wiki/White_noise
 * </blockquote>
 *
 * The interpolation function parameter is not used.
 * The interpolation function parameter is only for compatibility with
 * the other noise functions.
 *
 * @param v the vector3 (x,y,z) coordinates.
 * @param seed the uint random number seed.
 * @param interp not used.
 */
REAL white_noise3D(vector3 v, uint seed, interp_func interp);

/**
 * White noise functions.
 *
 * <blockquote>
 * In signal processing, white noise is a random signal having equal
 * intensity at different frequencies, giving it a constant power spectral density.
 * https://en.wikipedia.org/wiki/White_noise
 * </blockquote>
 *
 * The interpolation function parameter is not used.
 * The interpolation function parameter is only for compatibility with
 * the other noise functions.
 *
 * @param v the vector4 (x,y,z,w) coordinates.
 * @param seed the uint random number seed.
 * @param interp not used.
 */
REAL white_noise4D(vector4 v, uint seed, interp_func interp);

/**
 * White noise functions.
 *
 * <blockquote>
 * In signal processing, white noise is a random signal having equal
 * intensity at different frequencies, giving it a constant power spectral density.
 * https://en.wikipedia.org/wiki/White_noise
 * </blockquote>
 *
 * The interpolation function parameter is not used.
 * The interpolation function parameter is only for compatibility with
 * the other noise functions.
 *
 * @param v the vector8 (x,y,z,w,u,v) coordinates.
 * @param seed the uint random number seed.
 * @param interp not used.
 */
REAL white_noise6D(vector8 v, uint seed, interp_func interp);

/**
 * Simplex noise functions.
 * <blockquote>
 * Simplex noise is a method for constructing an n-dimensional noise function
 * comparable to Perlin noise ("classic" noise) but with fewer directional
 * artifacts and, in higher dimensions, a lower computational overhead.
 * https://en.wikipedia.org/wiki/Simplex_noise
 * </blockquote>
 *
 * The interpolation function parameter is not used.
 * The interpolation function parameter is only for compatibility with
 * the other noise functions.
 *
 * @param v the vector2 (x,y) coordinates.
 * @param seed the uint random number seed.
 * @param interp not used.
 */
REAL simplex_noise2D(vector2 v, uint seed, interp_func interp);

/**
 * Simplex noise functions.
 * <blockquote>
 * Simplex noise is a method for constructing an n-dimensional noise function
 * comparable to Perlin noise ("classic" noise) but with fewer directional
 * artifacts and, in higher dimensions, a lower computational overhead.
 * https://en.wikipedia.org/wiki/Simplex_noise
 * </blockquote>
 *
 * The interpolation function parameter is not used.
 * The interpolation function parameter is only for compatibility with
 * the other noise functions.
 *
 * @param v the vector3 (x,y,z) coordinates.
 * @param seed the uint random number seed.
 * @param interp not used.
 */
REAL simplex_noise3D(vector3 v, uint seed, interp_func interp);

/**
 * Simplex noise functions.
 * <blockquote>
 * Simplex noise is a method for constructing an n-dimensional noise function
 * comparable to Perlin noise ("classic" noise) but with fewer directional
 * artifacts and, in higher dimensions, a lower computational overhead.
 * https://en.wikipedia.org/wiki/Simplex_noise
 * </blockquote>
 *
 * The interpolation function parameter is not used.
 * The interpolation function parameter is only for compatibility with
 * the other noise functions.
 *
 * @param v the vector4 (x,y,z,w) coordinates.
 * @param seed the uint random number seed.
 * @param interp not used.
 */
REAL simplex_noise4D(vector4 v, uint seed, interp_func interp);

/**
 * Simplex noise functions. <b>In my tests the output have gray lines artifacts.</b>
 * <blockquote>
 * Simplex noise is a method for constructing an n-dimensional noise function
 * comparable to Perlin noise ("classic" noise) but with fewer directional
 * artifacts and, in higher dimensions, a lower computational overhead.
 * https://en.wikipedia.org/wiki/Simplex_noise
 * </blockquote>
 *
 * The interpolation function parameter is not used.
 * The interpolation function parameter is only for compatibility with
 * the other noise functions.
 *
 * @param v the vector4 (x,y,z,w) coordinates.
 * @param seed the uint random number seed.
 * @param interp not used.
 */
REAL new_simplex_noise4D(vector4 v, uint seed, interp_func interp);

/**
 * Simplex noise functions.
 * <blockquote>
 * Simplex noise is a method for constructing an n-dimensional noise function
 * comparable to Perlin noise ("classic" noise) but with fewer directional
 * artifacts and, in higher dimensions, a lower computational overhead.
 * https://en.wikipedia.org/wiki/Simplex_noise
 * </blockquote>
 *
 * The interpolation function parameter is not used.
 * The interpolation function parameter is only for compatibility with
 * the other noise functions.
 *
 * @param v the vector8 (x,y,z,w,u,v) coordinates.
 * @param seed the uint random number seed.
 * @param interp not used.
 */
REAL simplex_noise6D(vector8 v, uint seed, interp_func interp);

/**
 * Cellular noise functions. Compute distance (for cellular modules) and displacement (for voronoi modules).
 *
 * @param v the vector2 (x,y) coordinates.
 * @param seed the uint random number seed.
 * @param f a real number array containing exactly 4 frequencies.
 * @param disp a real number array containing exactly 4 points of displacement.
 * @param distance the dist_func2 function calculating the distance:
 * <ul>
 * <li>distEuclid2
 * <li>distManhattan2
 * <li>distGreatestAxis2
 * <li>distLeastAxis2
 * </ul>
 */
REAL cellular_function2D(vector2 v, uint seed, REAL *f, REAL *disp, dist_func2 distance);

/**
 * Cellular noise functions. Compute distance (for cellular modules) and displacement (for voronoi modules).
 *
 * @param v the vector3 (x,y,z) coordinates.
 * @param seed the uint random number seed.
 * @param f a real number array containing exactly 4 frequencies.
 * @param disp a real number array containing exactly 4 points of displacement.
 * @param distance the dist_func2 function calculating the distance:
 * <ul>
 * <li>distEuclid3
 * <li>distManhattan3
 * <li>distGreatestAxis3
 * <li>distLeastAxis3
 * </ul>
 */
REAL cellular_function3D(vector3 v, uint seed, REAL *f, REAL *disp, dist_func3 distance);

/**
 * Cellular noise functions. Compute distance (for cellular modules) and displacement (for voronoi modules).
 *
 * @param v the vector4 (x,y,z,w) coordinates.
 * @param seed the uint random number seed.
 * @param f a real number array containing exactly 4 frequencies.
 * @param disp a real number array containing exactly 4 points of displacement.
 * @param distance the dist_func2 function calculating the distance:
 * <ul>
 * <li>distEuclid4
 * <li>distManhattan4
 * <li>distGreatestAxis4
 * <li>distLeastAxis4
 * </ul>
 */
REAL cellular_function4D(vector4 v, uint seed, REAL *f, REAL *disp, dist_func4 distance);

/**
 * Cellular noise functions. Compute distance (for cellular modules) and displacement (for voronoi modules).
 *
 * @param v the vector8 (x,y,z,w,u,v) coordinates.
 * @param seed the uint random number seed.
 * @param f a real number array containing exactly 4 frequencies.
 * @param disp a real number array containing exactly 4 points of displacement.
 * @param distance the dist_func2 function calculating the distance:
 * <ul>
 * <li>distEuclid6
 * <li>distManhattan6
 * <li>distGreatestAxis6
 * <li>distLeastAxis6
 * </ul>
 */
REAL cellular_function6D(vector8 v, uint seed, REAL *f, REAL *disp, dist_func6 distance);

#ifdef __cplusplus
}
#endif

#endif /* NOISE_GEN_H_ */
