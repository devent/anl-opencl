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
 * hashing.c
 *
 *  Created on: Jul 27, 2021
 *      Author: Erwin Müller
 */

#ifndef ANLOPENCL_USE_OPENCL
#include <hashing.h>
#endif // ANLOPENCL_USE_OPENCL

#ifdef ANL_LONG_PERIOD_HASHING
// Use long-period hashing

uint p241[241]= {220, 23, 228, 115, 24, 40, 34, 83, 129, 162, 169, 49, 16, 203, 89, 103, 136, 222, 48, 214, 174, 98, 235, 62, 145, 120, 178, 87, 125, 131, 56, 180, 60, 61, 37, 165,
                         216, 211, 63, 117, 224, 107, 77, 142, 119, 39, 130, 108, 192, 241, 102, 52, 45, 42, 195, 13, 182, 239, 141, 64, 207, 25, 215, 233, 19, 101, 204, 105, 219, 240, 8, 157, 5, 126, 14, 139,
                         156, 137, 110, 79, 99, 20, 76, 159, 187, 177, 53, 7, 149, 65, 197, 44, 128, 190, 57, 9, 229, 186, 234, 114, 210, 232, 212, 132, 227, 10, 217, 185, 147, 38, 183, 189, 58, 193, 50, 172, 171,
                         81, 166, 112, 32, 12, 91, 155, 202, 173, 237, 35, 18, 144, 161, 54, 84, 74, 121, 17, 90, 1, 200, 236, 46, 67, 51, 96, 22, 179, 226, 146, 124, 123, 188, 104, 113, 88, 184, 66, 116, 230, 191,
                         208, 31, 86, 69, 92, 163, 29, 127, 93, 4, 26, 27, 221, 150, 225, 196, 30, 41, 43, 3, 106, 6, 71, 181, 95, 194, 164, 238, 133, 82, 72, 151, 111, 176, 168, 75, 28, 140, 59, 15, 143, 218, 175,
                         70, 109, 152, 118, 223, 138, 213, 85, 198, 68, 170, 154, 11, 206, 80, 36, 55, 148, 33, 201, 231, 199, 94, 205, 134, 97, 122, 167, 73, 21, 135, 2, 160, 209, 100, 153, 158, 47, 78
                        };


uint p251[251]= {50, 104, 31, 242, 160, 101, 203, 124, 141, 127, 248, 180, 45, 114, 198, 103, 116, 119, 138, 123, 139, 183, 189, 224, 223, 172, 229, 149, 113, 35, 207, 171, 129, 158,
                         176, 16, 9, 225, 2, 133, 65, 162, 25, 150, 215, 179, 142, 100, 107, 118, 93, 153, 249, 108, 236, 217, 169, 245, 49, 185, 46, 24, 247, 201, 216, 241, 166, 170, 4, 155, 82, 55, 177, 15, 60, 134,
                         36, 148, 11, 208, 197, 220, 69, 72, 68, 230, 168, 213, 164, 187, 6, 67, 193, 28, 18, 234, 219, 117, 52, 206, 85, 251, 89, 94, 37, 228, 126, 40, 111, 79, 109, 105, 30, 86, 92, 70, 194, 246, 61,
                         66, 106, 112, 110, 233, 211, 87, 122, 83, 44, 48, 42, 75, 3, 167, 120, 90, 102, 156, 43, 243, 84, 205, 137, 41, 178, 239, 237, 202, 29, 238, 165, 5, 21, 59, 64, 190, 121, 199, 80, 34, 182, 173,
                         226, 222, 196, 152, 62, 78, 159, 195, 191, 143, 39, 97, 218, 135, 10, 132, 53, 244, 14, 96, 131, 146, 144, 125, 26, 184, 12, 32, 214, 209, 38, 63, 76, 57, 77, 221, 51, 13, 161, 71, 115, 175,
                         240, 186, 8, 33, 20, 231, 140, 91, 23, 81, 95, 1, 235, 227, 19, 22, 181, 147, 157, 232, 210, 174, 7, 192, 136, 56, 17, 204, 128, 212, 130, 27, 54, 74, 47, 88, 250, 200, 99, 151, 188, 58, 145,
                         163, 154, 98, 73
                        };


uint p257[257]= {65, 136, 54, 28, 22, 91, 135, 151, 234, 27, 223, 127, 174, 83, 66, 188, 114, 7, 21, 40, 226, 133, 181, 197, 161, 129, 159, 102, 163, 254, 255, 24, 118, 143, 238, 53,
                         179, 198, 12, 257, 80, 37, 95, 191, 88, 202, 85, 201, 217, 220, 70, 175, 107, 93, 148, 15, 98, 248, 177, 150, 237, 124, 239, 57, 251, 142, 11, 71, 121, 92, 10, 184, 69, 240, 253, 103, 137, 13,
                         256, 211, 229, 119, 84, 106, 5, 185, 108, 230, 152, 132, 193, 182, 172, 125, 116, 3, 110, 245, 100, 222, 189, 89, 96, 78, 34, 221, 214, 14, 168, 208, 231, 113, 77, 33, 52, 35, 63, 90, 42, 233,
                         153, 192, 41, 75, 173, 213, 31, 131, 61, 126, 134, 204, 68, 236, 97, 50, 123, 109, 20, 205, 30, 227, 250, 170, 218, 190, 44, 72, 186, 166, 23, 18, 86, 130, 157, 73, 58, 67, 249, 48, 149, 64,
                         176, 29, 225, 203, 16, 165, 25, 79, 215, 252, 187, 112, 141, 246, 17, 169, 247, 139, 194, 199, 154, 49, 235, 178, 145, 76, 183, 210, 128, 62, 144, 167, 209, 38, 74, 19, 162, 138, 81, 140, 196,
                         36, 244, 26, 39, 122, 243, 206, 104, 228, 147, 216, 8, 6, 164, 2, 101, 55, 146, 111, 82, 46, 158, 32, 43, 4, 200, 207, 120, 195, 45, 160, 9, 47, 156, 59, 60, 117, 99, 87, 224, 180, 232, 51, 115,
                         1, 155, 56, 219, 171, 105, 212, 242, 241, 94
                        };


uint p263[263]= {128, 96, 113, 105, 173, 171, 62, 89, 183, 122, 243, 135, 180, 139, 93, 235, 213, 100, 14, 124, 224, 119, 98, 234, 256, 148, 114, 258, 111, 167, 4, 190, 179, 22, 205, 241,
                         210, 162, 9, 249, 126, 58, 138, 99, 240, 153, 226, 92, 1, 182, 123, 131, 5, 212, 223, 63, 169, 204, 142, 13, 32, 103, 130, 109, 38, 21, 61, 108, 8, 168, 88, 147, 184, 232, 77, 116, 196, 159, 25,
                         254, 69, 84, 176, 225, 260, 19, 78, 64, 43, 248, 186, 117, 95, 211, 101, 26, 40, 118, 115, 150, 45, 250, 194, 33, 97, 57, 17, 71, 207, 3, 146, 83, 157, 164, 107, 155, 48, 244, 158, 24, 242, 217,
                         80, 246, 56, 75, 192, 16, 86, 233, 11, 121, 259, 263, 239, 50, 137, 218, 161, 152, 203, 160, 198, 247, 31, 253, 229, 222, 91, 127, 134, 165, 53, 188, 76, 214, 230, 163, 185, 30, 125, 90, 209, 29,
                         132, 36, 255, 197, 7, 15, 237, 70, 82, 133, 12, 55, 156, 252, 18, 49, 208, 65, 136, 23, 67, 51, 54, 102, 120, 143, 262, 106, 202, 181, 178, 149, 177, 68, 231, 193, 41, 227, 94, 28, 145, 199, 37,
                         66, 10, 170, 261, 219, 59, 187, 47, 154, 2, 195, 215, 201, 46, 52, 104, 129, 35, 87, 34, 228, 81, 79, 39, 27, 200, 20, 174, 245, 112, 141, 73, 251, 189, 166, 206, 42, 172, 110, 216, 6, 236, 140,
                         220, 221, 44, 257, 144, 191, 60, 85, 72, 175, 151, 74, 238
                        };


// Long period hash
uint hash_coords_2(uint x, uint y, uint seed)
{
    return (
               p241[(p241[(p241[x%241]+y)%241]+seed)%241]+
               p251[(p251[(p251[x%251]+y)%251]+seed)%251]+
               p257[(p257[(p257[x%257]+y)%257]+seed)%257]+
               p263[(p263[(p263[x%263]+y)%263]+seed)%263]);
}

uint hash_coords_3(uint x, uint y, uint z, uint seed)
{
    return (
               p241[(p241[(p241[(p241[x%241]+y)%241]+z)%241]+seed)%241]+
               p251[(p251[(p251[(p251[x%251]+y)%251]+z)%251]+seed)%251]+
               p257[(p257[(p257[(p257[x%257]+y)%257]+z)%257]+seed)%257]+
               p263[(p263[(p263[(p263[x%263]+y)%263]+z)%263]+seed)%263]
           );
}

uint hash_coords_4(uint x, uint y, uint z, uint w, uint seed)
{
    return (
               p241[(p241[(p241[(p241[(p241[x%241]+y)%241]+z)%241]+w)%241]+seed)%241]+
               p251[(p251[(p251[(p241[(p251[x%251]+y)%251]+z)%251]+w)%251]+seed)%251]+
               p257[(p257[(p257[(p257[(p257[x%257]+y)%257]+z)%257]+w)%257]+seed)%257]+
               p263[(p263[(p263[(p263[(p263[x%263]+y)%263]+z)%263]+w)%263]+seed)%263]
           );
}

uint hash_coords_6(uint x, uint y, uint z, uint w, uint u, uint v, uint seed)
{
    return (
               p241[(p241[(p241[(p241[(p241[(p241[(p241[x%241]+y)%241]+z)%241]+w)%241]+u)%241]+v)%241]+seed)%241]+
               p251[(p251[(p251[(p251[(p251[(p251[(p251[x%251]+y)%251]+z)%251]+w)%251]+u)%251]+v)%251]+seed)%251]+
               p257[(p257[(p257[(p257[(p257[(p257[(p257[x%257]+y)%257]+z)%257]+w)%257]+u)%257]+v)%257]+seed)%257]+
               p263[(p263[(p263[(p263[(p263[(p263[(p263[x%263]+y)%263]+z)%263]+w)%263]+u)%263]+v)%263]+seed)%263]
           );
}

#else
uint permute[512] = { 218, 193, 236, 205, 110, 181, 34, 9, 187, 28, 41, 60, 219,
		24, 241, 222, 86, 152, 248, 76, 78, 210, 85, 208, 254, 59, 198, 2, 87,
		208, 148, 194, 117, 82, 10, 38, 155, 74, 242, 238, 93, 65, 206, 24, 162,
		158, 146, 94, 51, 77, 12, 159, 123, 235, 234, 55, 47, 226, 54, 240, 187,
		53, 40, 255, 213, 148, 192, 209, 68, 153, 85, 83, 57, 75, 137, 147, 11,
		81, 138, 140, 71, 254, 91, 237, 253, 165, 200, 105, 43, 122, 95, 207,
		36, 7, 168, 30, 20, 96, 235, 181, 5, 163, 162, 111, 157, 135, 136, 191,
		39, 42, 249, 103, 174, 251, 248, 229, 96, 111, 252, 70, 63, 92, 244,
		233, 120, 210, 21, 205, 23, 80, 168, 166, 53, 36, 15, 29, 71, 37, 66,
		100, 89, 49, 176, 216, 45, 45, 39, 76, 97, 167, 83, 1, 204, 86, 125,
		200, 114, 124, 224, 167, 173, 175, 227, 109, 156, 189, 219, 14, 133,
		186, 164, 196, 163, 156, 144, 102, 244, 19, 84, 161, 239, 81, 175, 153,
		1, 157, 251, 107, 180, 149, 212, 137, 193, 211, 17, 230, 126, 122, 54,
		182, 115, 101, 211, 151, 91, 198, 238, 116, 149, 72, 150, 233, 80, 120,
		58, 220, 154, 199, 202, 56, 212, 189, 158, 79, 20, 84, 3, 69, 151, 170,
		33, 197, 29, 176, 182, 143, 217, 155, 222, 178, 242, 145, 119, 47, 25,
		245, 67, 123, 15, 215, 226, 172, 201, 110, 220, 14, 104, 44, 236, 68,
		64, 160, 46, 89, 104, 48, 201, 188, 133, 132, 4, 128, 135, 121, 225, 63,
		225, 27, 130, 26, 142, 249, 62, 140, 23, 237, 190, 202, 28, 108, 192, 8,
		2, 196, 215, 142, 77, 32, 8, 214, 180, 221, 107, 209, 17, 186, 195, 147,
		78, 250, 35, 62, 234, 185, 130, 33, 65, 114, 145, 108, 30, 207, 6, 232,
		57, 0, 250, 221, 174, 239, 252, 246, 203, 52, 3, 97, 183, 247, 255, 99,
		66, 88, 5, 129, 61, 73, 43, 12, 152, 16, 112, 177, 93, 232, 138, 90,
		129, 214, 166, 102, 98, 13, 35, 79, 118, 27, 50, 154, 161, 246, 52, 136,
		48, 4, 243, 247, 18, 245, 253, 67, 10, 100, 188, 228, 146, 34, 41, 37,
		31, 90, 165, 56, 118, 94, 13, 171, 26, 169, 131, 159, 170, 199, 240,
		191, 25, 139, 121, 9, 113, 50, 116, 141, 21, 177, 127, 231, 64, 51, 40,
		132, 169, 134, 18, 164, 82, 61, 88, 105, 184, 217, 172, 119, 72, 131,
		227, 46, 223, 73, 87, 38, 206, 6, 173, 99, 117, 60, 228, 126, 74, 22,
		160, 42, 134, 109, 55, 243, 128, 141, 139, 144, 179, 218, 184, 22, 195,
		150, 115, 179, 95, 223, 241, 178, 185, 197, 70, 31, 11, 143, 75, 69,
		125, 103, 183, 112, 124, 19, 16, 32, 213, 171, 7, 44, 98, 59, 106, 230,
		58, 203, 113, 204, 92, 0, 106, 231, 229, 101, 224, 190, 127, 216, 49,
		194 };

uint hash_coords_2(uint x, uint y, uint seed) {
	return permute[(x & 0xff) + permute[(y & 0xff) + permute[seed & 0xff]]];
}

uint hash_coords_3(uint x, uint y, uint z, uint seed) {
	return permute[(x & 0xff)
			+ permute[(y & 0xff) + permute[(z & 0xff) + permute[seed & 0xff]]]];
}

uint hash_coords_4(uint x, uint y, uint z, uint w, uint seed) {
	return permute[(x & 0xff)
			+ permute[(y & 0xff)
					+ permute[(z & 0xff)
							+ permute[(w & 0xff) + permute[seed & 0xff]]]]];
}

uint hash_coords_6(uint x, uint y, uint z, uint w, uint u, uint v, uint seed) {
	return permute[(x & 0xff)
			+ permute[(y & 0xff)
					+ permute[(z & 0xff)
							+ permute[(w & 0xff)
									+ permute[(u & 0xff)
											+ permute[(v & 0xff)
													+ permute[seed & 0xff]]]]]]];
}

#endif

uint fnv_32_a_buf(void *buf, uint len) {
	uint hval = (uint) FNV_32_INIT;
	uint *bp = (uint*) buf;
	uint *be = bp + len;
	while (bp < be) {
		hval ^= *bp++;
		hval *= FNV_32_PRIME;
	}
	return hval;
}

uint fnv_32_a_combine(uint hash, uint val) {
	hash ^= val;
	hash *= FNV_32_PRIME;
	return hash;
}

unsigned char xor_fold_hash(uint hash) {
	// Implement XOR-folding to reduce from 32 to 8-bit hash
	return (unsigned char) ((hash >> 8) ^ (hash & FNV_MASK_8));
}
