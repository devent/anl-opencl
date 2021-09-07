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
 * imaging.c
 *
 *  Created on: Aug 7, 2021
 *      Author: Erwin Müller
 */

#ifndef ANLOPENCL_USE_OPENCL
#include "kernel.h"
#include <limits.h>
#endif // ANLOPENCL_USE_OPENCL

void rotateDomain(REAL dest[], REAL src[],
		REAL angle, REAL ax, REAL ay, REAL az) {
	REAL len = sqrt(ax * ax + ay * ay + az * az);
	ax /= len;
	ay /= len;
	az /= len;

	REAL cosangle = cos(angle);
	REAL sinangle = sin(angle);

	REAL rotmatrix[3][3];

	REAL x = src[0];
	REAL y = src[1];
	REAL z = src[2];

	rotmatrix[0][0] = 1.0 + (1.0 - cosangle) * (ax * ax - 1.0);
	rotmatrix[1][0] = -az * sinangle + (1.0 - cosangle) * ax * ay;
	rotmatrix[2][0] = ay * sinangle + (1.0 - cosangle) * ax * az;

	rotmatrix[0][1] = az * sinangle + (1.0 - cosangle) * ax * ay;
	rotmatrix[1][1] = 1.0 + (1.0 - cosangle) * (ay * ay - 1.0);
	rotmatrix[2][1] = -ax * sinangle + (1.0 - cosangle) * ay * az;

	rotmatrix[0][2] = -ay * sinangle + (1.0 - cosangle) * ax * az;
	rotmatrix[1][2] = ax * sinangle + (1.0 - cosangle) * ay * az;
	rotmatrix[2][2] = 1.0 + (1.0 - cosangle) * (az * az - 1.0);

	REAL nx, ny, nz;
	nx = (rotmatrix[0][0] * x) + (rotmatrix[1][0] * y)
			+ (rotmatrix[2][0] * z);
	ny = (rotmatrix[0][1] * x) + (rotmatrix[1][1] * y)
			+ (rotmatrix[2][1] * z);
	nz = (rotmatrix[0][2] * x) + (rotmatrix[1][2] * y)
			+ (rotmatrix[2][2] * z);

	dest[0] = nx;
	dest[1] = ny;
	dest[2] = nz;
}

vector3 rotateDomain3(vector3 src, REAL angle, REAL ax, REAL ay,
		REAL az) {
	REAL dest[3];
	REAL s[] = { src.x, src.y, src.z };
	rotateDomain(dest, s, angle, ax, ay, az);
	return (vector3){dest[0], dest[1], dest[2]};
}

vector4 rotateDomain4(vector4 src, REAL angle, REAL ax, REAL ay,
		REAL az) {
	REAL dest[3];
	REAL s[] = { src.x, src.y, src.z };
	rotateDomain(dest, s, angle, ax, ay, az);
	return (vector4){dest[0], dest[1], dest[2], src.w};
}

vector8 rotateDomain6(vector8 src, REAL angle, REAL ax, REAL ay,
		REAL az) {
	REAL dest[3];
	REAL s[] = {src.x, src.y, src.z};
	rotateDomain(dest, s, angle, ax, ay, az);
	return (vector8){dest[0], dest[1], dest[2], src.w, src.s4, src.s5, 0, 0};
}

vector4 combineRGBA(REAL r, REAL g, REAL b, REAL a) {
	return (vector4){r, g, b, a};
}

vector4 combineHSVA(REAL h, REAL s, REAL v, REAL a) {
	double P, Q, T, fract;
	if (h >= 360.0)
		h = 0.0;
	else
		h = h / 60.0;
	fract = h - floor(h);

	P = v * (1.0 - s);
	Q = v * (1.0 - s * fract);
	T = v * (1.0 - s * (1.0 - fract));

	if (h >= 0 && h < 1)
		return (vector4) {v, T, P, 1};
	else if (h >= 1 && h < 2)
		return (vector4) {Q, v, P, a};
	else if (h >= 2 && h < 3)
		return (vector4) {P, v, T, a};
	else if (h >= 3 && h < 4)
		return (vector4) {P, Q, v, a};
	else if (h >= 4 && h < 5)
		return (vector4) {T, P, v, a};
	else if (h >= 5 && h < 6)
		return (vector4) {v, P, Q, a};
	else
		return (vector4) {0, 0, 0, a};
}

REAL simpleFractalLayer3(vector3 v, noise_func3 basistype,
		uint seed, interp_func interp,
		REAL layerscale, REAL layerfreq, bool rot,
		REAL angle, REAL ax, REAL ay, REAL az) {
	if (rot) {
		REAL len = sqrt(ax * ax + ay * ay + az * az);
		ax /= len;
		ay /= len;
		az /= len;
		v = rotateDomain3(v, angle, ax, ay, az);
	}
	v = scaleDomain(v, layerfreq);
	REAL value = basistype(v, seed, interp);
	value *= layerscale;
	return value;
}

REAL simpleFractalLayer4(vector4 v, noise_func4 basistype,
		uint seed, interp_func interp,
		REAL layerscale, REAL layerfreq, bool rot,
		REAL angle, REAL ax, REAL ay, REAL az) {
	if (rot) {
		REAL len = sqrt(ax * ax + ay * ay + az * az);
		ax /= len;
		ay /= len;
		az /= len;
		v = rotateDomain4(v, angle, ax, ay, az);
	}
	v = scaleDomain(v, layerfreq);
	REAL value = basistype(v, seed, interp);
	value *= layerscale;
	return value;
}

REAL simpleFractalLayer6(vector8 v, noise_func6 basistype,
		uint seed, interp_func interp,
		REAL layerscale, REAL layerfreq, bool rot,
		REAL angle, REAL ax, REAL ay, REAL az) {
	if (rot) {
		REAL len = sqrt(ax * ax + ay * ay + az * az);
		ax /= len;
		ay /= len;
		az /= len;
		v = rotateDomain6(v, angle, ax, ay, az);
	}
	v = scaleDomain(v, layerfreq);
	REAL value = basistype(v, seed, interp);
	value *= layerscale;
	return value;
}

REAL simpleRidgedLayer3(vector3 v, noise_func3 basistype,
		uint seed, interp_func interp,
		REAL layerscale, REAL layerfreq, bool rot,
		REAL angle, REAL ax, REAL ay, REAL az) {
	if (rot) {
		REAL len = sqrt(ax * ax + ay * ay + az * az);
		ax /= len;
		ay /= len;
		az /= len;
		v = rotateDomain3(v, angle, ax, ay, az);
	}
	v = scaleDomain(v, layerfreq);
	REAL value = basistype(v, seed, interp);
	value = fabs(value);
	value -= 1;
	value *= layerscale;
	return value;
}

REAL simpleRidgedLayer4(vector4 v, noise_func4 basistype,
		uint seed, interp_func interp,
		REAL layerscale, REAL layerfreq, bool rot,
		REAL angle, REAL ax, REAL ay, REAL az) {
	if (rot) {
		REAL len = sqrt(ax * ax + ay * ay + az * az);
		ax /= len;
		ay /= len;
		az /= len;
		v = rotateDomain4(v, angle, ax, ay, az);
	}
	v = scaleDomain(v, layerfreq);
	REAL value = basistype(v, seed, interp);
	value = fabs(value);
	value -= 1;
	value *= layerscale;
	return value;
}

REAL simpleRidgedLayer6(vector8 v, noise_func6 basistype,
		uint seed, interp_func interp,
		REAL layerscale, REAL layerfreq, bool rot,
		REAL angle, REAL ax, REAL ay, REAL az) {
	if (rot) {
		REAL len = sqrt(ax * ax + ay * ay + az * az);
		ax /= len;
		ay /= len;
		az /= len;
		v = rotateDomain6(v, angle, ax, ay, az);
	}
	v = scaleDomain(v, layerfreq);
	REAL value = basistype(v, seed, interp);
	value = fabs(value);
	value -= 1;
	value *= layerscale;
	return value;
}

REAL simpleBillowLayer3(vector3 v, noise_func3 basistype,
		uint seed, interp_func interp,
		REAL layerscale, REAL layerfreq, bool rot,
		REAL angle, REAL ax, REAL ay, REAL az) {
	if (rot) {
		REAL len = sqrt(ax * ax + ay * ay + az * az);
		ax /= len;
		ay /= len;
		az /= len;
		v = rotateDomain3(v, angle, ax, ay, az);
	}
	v = scaleDomain(v, layerfreq);
	REAL value = basistype(v, seed, interp);
	value = fabs(value);
	value *= 2;
	value -= 1;
	value *= layerscale;
	return value;
}

REAL simpleBillowLayer4(vector4 v, noise_func4 basistype,
		uint seed, interp_func interp,
		REAL layerscale, REAL layerfreq, bool rot,
		REAL angle, REAL ax, REAL ay, REAL az) {
	if (rot) {
		REAL len = sqrt(ax * ax + ay * ay + az * az);
		ax /= len;
		ay /= len;
		az /= len;
		v = rotateDomain4(v, angle, ax, ay, az);
	}
	v = scaleDomain(v, layerfreq);
	REAL value = basistype(v, seed, interp);
	value = fabs(value);
	value *= 2;
	value -= 1;
	value *= layerscale;
	return value;
}

REAL simpleBillowLayer6(vector8 v, noise_func6 basistype,
		uint seed, interp_func interp,
		REAL layerscale, REAL layerfreq, bool rot,
		REAL angle, REAL ax, REAL ay, REAL az) {
	if (rot) {
		REAL len = sqrt(ax * ax + ay * ay + az * az);
		ax /= len;
		ay /= len;
		az /= len;
		v = rotateDomain6(v, angle, ax, ay, az);
	}
	v = scaleDomain(v, layerfreq);
	REAL value = basistype(v, seed, interp);
	value = fabs(value);
	value *= 2;
	value -= 1;
	value *= layerscale;
	return value;
}

REAL simplefBm3(
		vector3 v,
		noise_func3 basistype, uint seed, interp_func interp,
		random_func rnd, void *srnd,
		uint numoctaves, REAL frequency, bool rot) {
	REAL value = simpleFractalLayer3(
			v,
			basistype, seed + 10, interp,
			1.0, 1.0 * frequency, rot,
			rnd(srnd) * M_PI * 2.0, rnd(srnd), rnd(srnd), rnd(srnd));
	for (uint c = 0; c < numoctaves - 1; ++c) {
		REAL octave = simpleFractalLayer3(v, basistype,
				seed + 10 + c * 1000, interp,
				1.0 / pow(2.0, c), pow(2.0, c) * frequency,
				rot, rnd(srnd) * M_PI * 2.0, rnd(srnd), rnd(srnd), rnd(srnd));
		value = value + octave;
	}
	return value;
}

REAL simplefBm4(
		vector4 v,
		noise_func4 basistype, uint seed, interp_func interp,
		random_func rnd, void *srnd,
		uint numoctaves, REAL frequency, bool rot) {
	REAL value = simpleFractalLayer4(
			v,
			basistype, seed + 10, interp,
			1.0, 1.0 * frequency, rot,
			rnd(srnd) * M_PI * 2.0, rnd(srnd), rnd(srnd), rnd(srnd));
	for (uint c = 0; c < numoctaves - 1; ++c) {
		REAL octave = simpleFractalLayer4(v, basistype,
				seed + 10 + c * 1000, interp,
				1.0 / pow(2.0, c), pow(2.0, c) * frequency,
				rot, rnd(srnd) * M_PI * 2.0, rnd(srnd), rnd(srnd), rnd(srnd));
		value = value + octave;
	}
	return value;
}

REAL simplefBm6(
		vector8 v,
		noise_func6 basistype, uint seed, interp_func interp,
		random_func rnd, void *srnd,
		uint numoctaves, REAL frequency, bool rot) {
	REAL value = simpleFractalLayer6(
			v,
			basistype, seed + 10, interp,
			1.0, 1.0 * frequency, rot,
			rnd(srnd) * M_PI * 2.0, rnd(srnd), rnd(srnd), rnd(srnd));
	for (uint c = 0; c < numoctaves - 1; ++c) {
		REAL octave = simpleFractalLayer6(v, basistype,
				seed + 10 + c * 1000, interp,
				1.0 / pow(2.0, c), pow(2.0, c) * frequency,
				rot, rnd(srnd) * M_PI * 2.0, rnd(srnd), rnd(srnd), rnd(srnd));
		value = value + octave;
	}
	return value;
}

REAL simpleRidgedMultifractal3(
		vector3 v,
		noise_func3 basistype, uint seed, interp_func interp,
		random_func rnd, void *srnd,
		uint numoctaves, REAL frequency, bool rot) {
	REAL value = simpleRidgedLayer3(
			v,
			basistype, seed + 10, interp,
			1.0, 1.0 * frequency, rot,
			rnd(srnd) * M_PI * 2.0, rnd(srnd), rnd(srnd), rnd(srnd));
	for (uint c = 0; c < numoctaves - 1; ++c) {
		REAL octave = simpleRidgedLayer3(v, basistype,
				seed + 10 + c * 1000, interp,
				1.0 / pow(2.0, c), pow(2.0, c) * frequency,
				rot, rnd(srnd) * M_PI * 2.0, rnd(srnd), rnd(srnd), rnd(srnd));
		value = value + octave;
	}
	return value;
}

REAL simpleRidgedMultifractal4(
		vector4 v,
		noise_func4 basistype, uint seed, interp_func interp,
		random_func rnd, void *srnd,
		uint numoctaves, REAL frequency, bool rot) {
	REAL value = simpleRidgedLayer4(
			v,
			basistype, seed + 10, interp,
			1.0, 1.0 * frequency, rot,
			rnd(srnd) * M_PI * 2.0, rnd(srnd), rnd(srnd), rnd(srnd));
	for (uint c = 0; c < numoctaves - 1; ++c) {
		REAL octave = simpleRidgedLayer4(v, basistype,
				seed + 10 + c * 1000, interp,
				1.0 / pow(2.0, c), pow(2.0, c) * frequency,
				rot, rnd(srnd) * M_PI * 2.0, rnd(srnd), rnd(srnd), rnd(srnd));
		value = value + octave;
	}
	return value;
}

REAL simpleRidgedMultifractal6(
		vector8 v,
		noise_func6 basistype, uint seed, interp_func interp,
		random_func rnd, void *srnd,
		uint numoctaves, REAL frequency, bool rot) {
	REAL value = simpleRidgedLayer6(
			v,
			basistype, seed + 10, interp,
			1.0, 1.0 * frequency, rot,
			rnd(srnd) * M_PI * 2.0, rnd(srnd), rnd(srnd), rnd(srnd));
	for (uint c = 0; c < numoctaves - 1; ++c) {
		REAL octave = simpleRidgedLayer6(v, basistype,
				seed + 10 + c * 1000, interp,
				1.0 / pow(2.0, c), pow(2.0, c) * frequency,
				rot, rnd(srnd) * M_PI * 2.0, rnd(srnd), rnd(srnd), rnd(srnd));
		value = value + octave;
	}
	return value;
}

REAL simpleBillow3(
		vector3 v,
		noise_func3 basistype, uint seed, interp_func interp,
		random_func rnd, void *srnd,
		uint numoctaves, REAL frequency, bool rot) {
	REAL value = simpleBillowLayer3(
			v,
			basistype, seed + 10, interp,
			1.0, 1.0 * frequency, rot,
			rnd(srnd) * M_PI * 2.0, rnd(srnd), rnd(srnd), rnd(srnd));
	for (uint c = 0; c < numoctaves - 1; ++c) {
		REAL octave = simpleBillowLayer3(v, basistype,
				seed + 10 + c * 1000, interp,
				1.0 / pow(2.0, c), pow(2.0, c) * frequency,
				rot, rnd(srnd) * M_PI * 2.0, rnd(srnd), rnd(srnd), rnd(srnd));
		value = value + octave;
	}
	return value;
}

REAL simpleBillow4(
		vector4 v,
		noise_func4 basistype, uint seed, interp_func interp,
		random_func rnd, void *srnd,
		uint numoctaves, REAL frequency, bool rot) {
	REAL value = simpleBillowLayer4(
			v,
			basistype, seed + 10, interp,
			1.0, 1.0 * frequency, rot,
			rnd(srnd) * M_PI * 2.0, rnd(srnd), rnd(srnd), rnd(srnd));
	for (uint c = 0; c < numoctaves - 1; ++c) {
		REAL octave = simpleBillowLayer4(v, basistype,
				seed + 10 + c * 1000, interp,
				1.0 / pow(2.0, c), pow(2.0, c) * frequency,
				rot, rnd(srnd) * M_PI * 2.0, rnd(srnd), rnd(srnd), rnd(srnd));
		value = value + octave;
	}
	return value;
}
REAL simpleBillow6(
		vector8 v,
		noise_func6 basistype, uint seed, interp_func interp,
		random_func rnd, void *srnd,
		uint numoctaves, REAL frequency, bool rot) {
	REAL value = simpleBillowLayer6(
			v,
			basistype, seed + 10, interp,
			1.0, 1.0 * frequency, rot,
			rnd(srnd) * M_PI * 2.0, rnd(srnd), rnd(srnd), rnd(srnd));
	for (uint c = 0; c < numoctaves - 1; ++c) {
		REAL octave = simpleBillowLayer6(v, basistype,
				seed + 10 + c * 1000, interp,
				1.0 / pow(2.0, c), pow(2.0, c) * frequency,
				rot, rnd(srnd) * M_PI * 2.0, rnd(srnd), rnd(srnd), rnd(srnd));
		value = value + octave;
	}
	return value;
}

