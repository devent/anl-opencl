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
 * noise_lut.c
 *
 *  Created on: Aug 4, 2021
 *      Author: Erwin Müller
 */

#ifndef USE_OPENCL
#include "noise_lut.h"
#endif // USE_OPENCL

// Lookup tables for 2D, 3D, 4D and 6D gradient and simplex noise.

REAL gradient2D_lut[8][2]=
{
	{-0.79056941504209483299972338610818,0.31622776601683793319988935444327},
	{0.79056941504209483299972338610818,0.31622776601683793319988935444327},
	{0.79056941504209483299972338610818,-0.31622776601683793319988935444327},
	{-0.79056941504209483299972338610818,-0.31622776601683793319988935444327},
	{0.31622776601683793319988935444327,0.79056941504209483299972338610818},
	{-0.31622776601683793319988935444327,0.79056941504209483299972338610818},
	{0.31622776601683793319988935444327,-0.79056941504209483299972338610818},
	{-0.31622776601683793319988935444327,-0.79056941504209483299972338610818}
};

REAL gradient3D_lut[24][3]=
{
	{-11,4,4},
	{-4,11,4},
	{-4,4,11},
	{11,4,4},
	{4,11,4},
	{4,4,11},
	{-11,-4,4},
	{4,-11,4},
	{-4,-4,11},
	{11,-4,4},
	{4,-11,4},
	{4,-4,11},
	{-11,4,-4},
	{-4,11,-4},
	{-4,4,-11},
	{11,4,-4},
	{4,11,-4},
	{4,4,-11},
	{-11,-4,-4},
	{-4,-11,-4},
	{-4,-4,-11},
	{11,-4,-4},
	{4,-11,-4},
	{4,-4,-11}
};

REAL gradient4D_lut[64][4]=
{
    {3,1,1,1},
	{1,3,1,1},
	{1,1,3,1},
	{1,1,1,3},
	{-3,1,1,1},
	{-1,3,1,1},
	{-1,1,3,1},
	{-1,1,1,3},
	{3,-1,1,1},
	{1,-3,1,1},
	{1,-1,3,1},
	{1,-1,1,3},
	{-3,-1,1,1},
	{-1,-3,1,1},
	{-1,-1,3,1},
	{-1,-1,1,3},
	{3,1,-1,1},
	{1,3,-1,1},
	{1,1,-3,1},
	{1,1,-1,3},
	{-3,1,-1,1},
	{-1,3,-1,1},
	{-1,1,-3,1},
	{-1,1,-1,3},
	{3,-1,-1,1},
	{1,-3,-1,1},
	{1,-1,-3,1},
	{1,-1,-1,3},
	{-3,-1,-1,1},
	{-1,-3,-1,1},
	{-1,-1,-3,1},
	{-1,-1,-1,3},
	{3,1,1,-1},
	{1,3,1,-1},
	{1,1,3,-1},
	{1,1,1,-3},
	{-3,1,1,-1},
	{-1,3,1,-1},
	{-1,1,3,-1},
	{-1,1,1,-3},
	{3,-1,1,-1},
	{1,-3,1,-1},
	{1,-1,3,-1},
	{1,-1,1,-3},
	{-3,-1,1,-1},
	{-1,-3,1,-1},
	{-1,-1,3,-1},
	{-1,-1,1,-3},
	{3,1,-1,-1},
	{1,3,-1,-1},
	{1,1,-3,-1},
	{1,1,-1,-3},
	{-3,1,-1,-1},
	{-1,3,-1,-1},
	{-1,1,-3,-1},
	{-1,1,-1,-3},
	{3,-1,-1,-1},
	{1,-3,-1,-1},
	{1,-1,-3,-1},
	{1,-1,-1,-3},
	{-3,-1,-1,-1},
	{-1,-3,-1,-1},
	{-1,-1,-3,-1},
	{-1,-1,-1,-3}
};

REAL gradient6D_lut[192][6]=
{
    {0,1,1,1,1,1},
    {0,1,1,1,1,-1},
    {0,1,1,1,-1,1},
    {0,1,1,1,-1,-1},
    {0,1,1,-1,1,1},
    {0,1,1,-1,1,-1},
    {0,1,1,-1,-1,1},
    {0,1,1,-1,-1,-1},
    {0,1,-1,1,1,1},
    {0,1,-1,1,1,-1},
    {0,1,-1,1,-1,1},
    {0,1,-1,1,-1,-1},
    {0,1,-1,-1,1,1},
    {0,1,-1,-1,1,-1},
    {0,1,-1,-1,-1,1},
    {0,1,-1,-1,-1,-1},
    {0,-1,1,1,1,1}, //
    {0,-1,1,1,1,-1},
    {0,-1,1,1,-1,1},
    {0,-1,1,1,-1,-1},
    {0,-1,1,-1,1,1},
    {0,-1,1,-1,1,-1},
    {0,-1,1,-1,-1,1},
    {0,-1,1,-1,-1,-1},
    {0,-1,-1,1,1,1},
    {0,-1,-1,1,1,-1},
    {0,-1,-1,1,-1,1},
    {0,-1,-1,1,-1,-1},
    {0,-1,-1,-1,1,1},
    {0,-1,-1,-1,1,-1},
    {0,-1,-1,-1,-1,1},
    {0,-1,-1,-1,-1,-1},

    {1,0,1,1,1,1},
    {1,0,1,1,1,-1},
    {1,0,1,1,-1,1},
    {1,0,1,1,-1,-1},
    {1,0,1,-1,1,1},
    {1,0,1,-1,1,-1},
    {1,0,1,-1,-1,1},
    {1,0,1,-1,-1,-1},
    {1,0,-1,1,1,1},
    {1,0,-1,1,1,-1},
    {1,0,-1,1,-1,1},
    {1,0,-1,1,-1,-1},
    {1,0,-1,-1,1,1},
    {1,0,-1,-1,1,-1},
    {1,0,-1,-1,-1,1},
    {1,0,-1,-1,-1,-1},
    {-1,0,1,1,1,1}, //
    {-1,0,1,1,1,-1},
    {-1,0,1,1,-1,1},
    {-1,0,1,1,-1,-1},
    {-1,0,1,-1,1,1},
    {-1,0,1,-1,1,-1},
    {-1,0,1,-1,-1,1},
    {-1,0,1,-1,-1,-1},
    {-1,0,-1,1,1,1},
    {-1,0,-1,1,1,-1},
    {-1,0,-1,1,-1,1},
    {-1,0,-1,1,-1,-1},
    {-1,0,-1,-1,1,1},
    {-1,0,-1,-1,1,-1},
    {-1,0,-1,-1,-1,1},
    {-1,0,-1,-1,-1,-1},

    {1,1,0,1,1,1},
    {1,1,0,1,1,-1},
    {1,1,0,1,-1,1},
    {1,1,0,1,-1,-1},
    {1,1,0,-1,1,1},
    {1,1,0,-1,1,-1},
    {1,1,0,-1,-1,1},
    {1,1,0,-1,-1,-1},
    {-1,1,0,1,1,1},
    {-1,1,0,1,1,-1},
    {-1,1,0,1,-1,1},
    {-1,1,0,1,-1,-1},
    {-1,1,0,-1,1,1},
    {-1,1,0,-1,1,-1},
    {-1,1,0,-1,-1,1},
    {-1,1,0,-1,-1,-1},
    {1,-1,0,1,1,1}, //
    {1,-1,0,1,1,-1},
    {1,-1,0,1,-1,1},
    {1,-1,0,1,-1,-1},
    {1,-1,0,-1,1,1},
    {1,-1,0,-1,1,-1},
    {1,-1,0,-1,-1,1},
    {1,-1,0,-1,-1,-1},
    {-1,-1,0,1,1,1},
    {-1,-1,0,1,1,-1},
    {-1,-1,0,1,-1,1},
    {-1,-1,0,1,-1,-1},
    {-1,-1,0,-1,1,1},
    {-1,-1,0,-1,1,-1},
    {-1,-1,0,-1,-1,1},
    {-1,-1,0,-1,-1,-1},

    {1,1,1,0,1,1},
    {1,1,1,0,1,-1},
    {1,1,1,0,-1,1},
    {1,1,1,0,-1,-1},
    {1,1,-1,0,1,1},
    {1,1,-1,0,1,-1},
    {1,1,-1,0,-1,1},
    {1,1,-1,0,-1,-1},
    {1,-1,1,0,1,1},
    {1,-1,1,0,1,-1},
    {1,-1,1,0,-1,1},
    {1,-1,1,0,-1,-1},
    {1,-1,-1,0,1,1},
    {1,-1,-1,0,1,-1},
    {1,-1,-1,0,-1,1},
    {1,-1,-1,0,-1,-1},
    {-1,1,1,0,1,1},
    {-1,1,1,0,1,-1},
    {-1,1,1,0,-1,1},
    {-1,1,1,0,-1,-1},
    {-1,1,-1,0,1,1},
    {-1,1,-1,0,1,-1},
    {-1,1,-1,0,-1,1},
    {-1,1,-1,0,-1,-1},
    {-1,-1,1,0,1,1},
    {-1,-1,1,0,1,-1},
    {-1,-1,1,0,-1,1},
    {-1,-1,1,0,-1,-1},
    {-1,-1,-1,0,1,1},
    {-1,-1,-1,0,1,-1},
    {-1,-1,-1,0,-1,1},
    {-1,-1,-1,0,-1,-1},

    {1,1,1,1,0,1},
    {1,1,1,1,0,-1},
    {1,1,1,-1,0,1},
    {1,1,1,-1,0,-1},
    {1,1,-1,1,0,1},
    {1,1,-1,1,0,-1},
    {1,1,-1,-1,0,1},
    {1,1,-1,-1,0,-1},
    {1,-1,1,1,0,1},
    {1,-1,1,1,0,-1},
    {1,-1,1,-1,0,1},
    {1,-1,1,-1,0,-1},
    {1,-1,-1,1,0,1},
    {1,-1,-1,1,0,-1},
    {1,-1,-1,-1,0,1},
    {1,-1,-1,-1,0,-1},
    {-1,1,1,1,0,1},
    {-1,1,1,1,0,-1},
    {-1,1,1,-1,0,1},
    {-1,1,1,-1,0,-1},
    {-1,1,-1,1,0,1},
    {-1,1,-1,1,0,-1},
    {-1,1,-1,-1,0,1},
    {-1,1,-1,-1,0,-1},
    {-1,-1,1,1,0,1},
    {-1,-1,1,1,0,-1},
    {-1,-1,1,-1,0,1},
    {-1,-1,1,-1,0,-1},
    {-1,-1,-1,1,0,1},
    {-1,-1,-1,1,0,-1},
    {-1,-1,-1,-1,0,1},
    {-1,-1,-1,-1,0,-1},

    {1,1,1,1,1,0},
    {1,1,1,1,-1,0},
    {1,1,1,-1,1,0},
    {1,1,1,-1,-1,0},
    {1,1,-1,1,1,0},
    {1,1,-1,1,-1,0},
    {1,1,-1,-1,1,0},
    {1,1,-1,-1,-1,0},
    {1,-1,1,1,1,0},
    {1,-1,1,1,-1,0},
    {1,-1,1,-1,1,0},
    {1,-1,1,-1,-1,0},
    {1,-1,-1,1,1,0},
    {1,-1,-1,1,-1,0},
    {1,-1,-1,-1,1,0},
    {1,-1,-1,-1,-1,0},
    {-1,1,1,1,1,0},
    {-1,1,1,1,-1,0},
    {-1,1,1,-1,1,0},
    {-1,1,1,-1,-1,0},
    {-1,1,-1,1,1,0},
    {-1,1,-1,1,-1,0},
    {-1,1,-1,-1,1,0},
    {-1,1,-1,-1,-1,0},
    {-1,-1,1,1,1,0},
    {-1,-1,1,1,-1,0},
    {-1,-1,1,-1,1,0},
    {-1,-1,1,-1,-1,0},
    {-1,-1,-1,1,1,0},
    {-1,-1,-1,1,-1,0},
    {-1,-1,-1,-1,1,0},
    {-1,-1,-1,-1,-1,0}
};

REAL whitenoise_lut[256]=
{
    -0.714286,
    0.301587,
    0.333333,
    -1,
    0.396825,
    -0.0793651,
    -0.968254,
    -0.047619,
    0.301587,
    -0.111111,
    0.015873,
    0.968254,
    -0.428571,
    0.428571,
    0.047619,
    0.84127,
    -0.015873,
    -0.746032,
    -0.809524,
    -0.619048,
    -0.301587,
    -0.68254,
    0.777778,
    0.365079,
    -0.460317,
    0.714286,
    0.142857,
    0.047619,
    -0.0793651,
    -0.492063,
    -0.873016,
    -0.269841,
    -0.84127,
    -0.809524,
    -0.396825,
    -0.777778,
    -0.396825,
    -0.746032,
    0.301587,
    -0.52381,
    0.650794,
    0.301587,
    -0.015873,
    0.269841,
    0.492063,
    -0.936508,
    -0.777778,
    0.555556,
    0.68254,
    -0.650794,
    -0.968254,
    0.619048,
    0.777778,
    0.68254,
    0.206349,
    -0.555556,
    0.904762,
    0.587302,
    -0.174603,
    -0.047619,
    -0.206349,
    -0.68254,
    0.111111,
    -0.52381,
    0.174603,
    -0.968254,
    -0.111111,
    -0.238095,
    0.396825,
    -0.777778,
    -0.206349,
    0.142857,
    0.904762,
    -0.111111,
    -0.269841,
    0.777778,
    -0.015873,
    -0.047619,
    -0.333333,
    0.68254,
    -0.238095,
    0.904762,
    0.0793651,
    0.68254,
    -0.301587,
    -0.333333,
    0.206349,
    0.52381,
    0.904762,
    -0.015873,
    -0.555556,
    0.396825,
    0.460317,
    -0.142857,
    0.587302,
    1,
    -0.650794,
    -0.333333,
    -0.365079,
    0.015873,
    -0.873016,
    -1,
    -0.777778,
    0.174603,
    -0.84127,
    -0.428571,
    0.365079,
    -0.587302,
    -0.587302,
    0.650794,
    0.714286,
    0.84127,
    0.936508,
    0.746032,
    0.047619,
    -0.52381,
    -0.714286,
    -0.746032,
    -0.206349,
    -0.301587,
    -0.174603,
    0.460317,
    0.238095,
    0.968254,
    0.555556,
    -0.269841,
    0.206349,
    -0.0793651,
    0.777778,
    0.174603,
    0.111111,
    -0.714286,
    -0.84127,
    -0.68254,
    0.587302,
    0.746032,
    -0.68254,
    0.587302,
    0.365079,
    0.492063,
    -0.809524,
    0.809524,
    -0.873016,
    -0.142857,
    -0.142857,
    -0.619048,
    -0.873016,
    -0.587302,
    0.0793651,
    -0.269841,
    -0.460317,
    -0.904762,
    -0.174603,
    0.619048,
    0.936508,
    0.650794,
    0.238095,
    0.111111,
    0.873016,
    0.0793651,
    0.460317,
    -0.746032,
    -0.460317,
    0.428571,
    -0.714286,
    -0.365079,
    -0.428571,
    0.206349,
    0.746032,
    -0.492063,
    0.269841,
    0.269841,
    -0.365079,
    0.492063,
    0.873016,
    0.142857,
    0.714286,
    -0.936508,
    1,
    -0.142857,
    -0.904762,
    -0.301587,
    -0.968254,
    0.619048,
    0.269841,
    -0.809524,
    0.936508,
    0.714286,
    0.333333,
    0.428571,
    0.0793651,
    -0.650794,
    0.968254,
    0.809524,
    0.492063,
    0.555556,
    -0.396825,
    -1,
    -0.492063,
    -0.936508,
    -0.492063,
    -0.111111,
    0.809524,
    0.333333,
    0.238095,
    0.174603,
    0.333333,
    0.873016,
    0.809524,
    -0.047619,
    -0.619048,
    -0.174603,
    0.84127,
    0.111111,
    0.619048,
    -0.0793651,
    0.52381,
    1,
    0.015873,
    0.52381,
    -0.619048,
    -0.52381,
    1,
    0.650794,
    -0.428571,
    0.84127,
    -0.555556,
    0.015873,
    0.428571,
    0.746032,
    -0.238095,
    -0.238095,
    0.936508,
    -0.206349,
    -0.936508,
    0.873016,
    -0.555556,
    -0.650794,
    -0.904762,
    0.52381,
    0.968254,
    -0.333333,
    -0.904762,
    0.396825,
    0.047619,
    -0.84127,
    -0.365079,
    -0.587302,
    -1,
    -0.396825,
    0.365079,
    0.555556,
    0.460317,
    0.142857,
    -0.460317,
    0.238095,
};

