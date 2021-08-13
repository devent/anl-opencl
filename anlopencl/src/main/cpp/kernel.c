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
#include "noise_gen.h"
#endif // USE_OPENCL

vector3* rotateDomain3(vector3 *src, size_t index, REAL angle, REAL ax, REAL ay,
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

//void simpleFractalLayer(uint basistype, CInstructionIndex interpindex,
//		REAL layerscale, REAL layerfreq, uint s, bool rot, REAL angle,
//		REAL ax, REAL ay, REAL az) {
//	CInstructionIndex myseed=seed(s);
//    CInstructionIndex base=nextIndex();
//    switch(basistype)
//    {
//    case anl::OP_ValueBasis:
//        valueBasis(interpindex, myseed);
//        break;
//    case anl::OP_GradientBasis:
//        gradientBasis(interpindex, myseed);
//        break;
//    case anl::OP_SimplexBasis:
//        simplexBasis(myseed);
//        break;
//    default:
//        gradientBasis(interpindex, myseed);
//        break;
//    }
//    constant(layerscale);
//    multiply(base,base+1);
//    constant(layerfreq);
//    CInstructionIndex sd=scaleDomain(base+2, lastIndex());
//    if(rot)
//    {
//        REAL len=std::sqrt(ax*ax+ay*ay+az*az);
//        constant(angle);
//        constant(ax/len);
//        constant(ay/len);
//        constant(az/len);
//        rotateDomain(sd, sd+1, sd+2, sd+3, sd+4);
//    }
//    return lastIndex();
//}
//
//void simplefBm(uint basistype, uint interptype, uint numoctaves,
//		REAL frequency, uint seed, bool rot) {
//    if(numoctaves<1) return 0;
//
//    CInstructionIndex interpindex=constant(interptype);
//    KISS rnd;
//    rnd.setSeed(seed);
//    simpleFractalLayer(basistype, interpindex, 1.0, 1.0*frequency, seed+10,rot,
//                       rnd.get01()*3.14159265, rnd.get01(), rnd.get01(), rnd.get01());
//    CInstructionIndex lastlayer=lastIndex();
//
//    for(uint c=0; c<numoctaves-1; ++c)
//    {
//        CInstructionIndex nextlayer=simpleFractalLayer(basistype, interpindex, 1.0/std::pow(2.0, (REAL)(c)), std::pow(2.0, (REAL)(c))*frequency, seed+10+c*1000,rot,
//                                    rnd.get01()*3.14159265, rnd.get01(), rnd.get01(), rnd.get01());
//        lastlayer=add(lastlayer,nextlayer);
//    }
//    return lastIndex();
//}
//

