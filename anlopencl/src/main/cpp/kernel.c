/**
 * Copyright (C) 2021 Erwin Müller <erwin@muellerpublic.de>
 * Released as open-source under the Apache License, Version 2.0.
 *
 * ****************************************************************************
 * ANL-OpenCL :: Core
 * ****************************************************************************
 *
 * Copyright (C) 2021 Erwin Müller <erwin@muellerpublic.de>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ****************************************************************************
 * ANL-OpenCL :: Core is a derivative work based on Josua Tippetts' C++ library:
 * http://accidentalnoise.sourceforge.net/index.html
 * ****************************************************************************
 *
 * Copyright (C) 2011 Joshua Tippetts
 *
 *   This software is provided 'as-is', without any express or implied
 *   warranty.  In no event will the authors be held liable for any damages
 *   arising from the use of this software.
 *
 *   Permission is granted to anyone to use this software for any purpose,
 *   including commercial applications, and to alter it and redistribute it
 *   freely, subject to the following restrictions:
 *
 *   1. The origin of this software must not be misrepresented; you must not
 *      claim that you wrote the original software. If you use this software
 *      in a product, an acknowledgment in the product documentation would be
 *      appreciated but is not required.
 *   2. Altered source versions must be plainly marked as such, and must not be
 *      misrepresented as being the original software.
 *   3. This notice may not be removed or altered from any source distribution.
 */
/*
 * imaging.c
 *
 *  Created on: Aug 7, 2021
 *      Author: Erwin Müller
 */

#ifndef USE_OPENCL
#include "kernel.h"
#endif // USE_OPENCL

vector3 *rotateDomain3(vector3 *src, size_t index, REAL angle, REAL ax, REAL ay,
		REAL az) {
	REAL len = sqrt(ax * ax + ay * ay + az * az);
	ax /= len;
	ay /= len;
	az /= len;

	REAL cosangle = cos(angle);
	REAL sinangle = sin(angle);

	REAL rotmatrix[3][3];

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
	vector3 s = src[index];
	nx = (rotmatrix[0][0] * s.x) + (rotmatrix[1][0] * s.y)
			+ (rotmatrix[2][0] * s.z);
	ny = (rotmatrix[0][1] * s.x) + (rotmatrix[1][1] * s.y)
			+ (rotmatrix[2][1] * s.z);
	nz = (rotmatrix[0][2] * s.x) + (rotmatrix[1][2] * s.y)
			+ (rotmatrix[2][2] * s.z);
	src[index].x = nx;
	src[index].y = ny;
	src[index].z = nz;
	return src;
}

vector3 *scaleDomain3(vector3 *src, size_t index, REAL scale) {
	src[index].x = src[index].x * scale;
	src[index].y = src[index].y * scale;
	src[index].z = src[index].z * scale;
	return src;
}

vector3 *simpleFractalLayer(vector3 *v, size_t index, noise_func3 basistype,
		uint seed, interp_func interp,
		REAL layerscale, REAL layerfreq, bool rot,
		REAL angle, REAL ax, REAL ay, REAL az) {
	REAL value = basistype(v[index], seed, interp);
	value *= layerscale;
	scaleDomain3(v, index, layerfreq);
	if (rot) {
		REAL len = sqrt(ax * ax + ay * ay + az * az);
		ax /= len;
		ay /= len;
		az /= len;
		rotateDomain3(v, index, angle, ax, ay, az);
	}
	return v;
}

void simplefBm(
		vector3 *v, size_t index,
		noise_func3 basistype, uint seed, interp_func interp,
		random_func rnd, void *srnd,
		uint numoctaves, REAL frequency, bool rot) {
    if(numoctaves<1) return;

    simpleFractalLayer(v, index,
    		basistype, seed + 10, interp,
			1.0, 1.0 * frequency, rot,
            rnd(srnd)*3.14159265, rnd(srnd), rnd(srnd), rnd(srnd));

    for(uint c=0; c<numoctaves-1; ++c)
    {
//        CInstructionIndex nextlayer=simpleFractalLayer(basistype, interpindex, 1.0/std::pow(2.0, (REAL)(c)), std::pow(2.0, (REAL)(c))*frequency, seed+10+c*1000,rot,
//                                    rnd.get01()*3.14159265, rnd.get01(), rnd.get01(), rnd.get01());
//        lastlayer=add(lastlayer,nextlayer);
    }
}


