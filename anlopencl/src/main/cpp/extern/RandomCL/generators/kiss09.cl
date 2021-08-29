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

/**
@file

Implements KISS (Keep It Simple, Stupid) generator, proposed in 2009.

G. Marsaglia, 64-bit kiss rngs, https://www.thecodingforums.com/threads/64-bit-kiss-rngs.673657.
*/
#pragma once

#define KISS09_FLOAT_MULTI 5.4210108624275221700372640e-20f
#define KISS09_DOUBLE_MULTI 5.4210108624275221700372640e-20

//https://www.thecodingforums.com/threads/64-bit-kiss-rngs.673657/

/**
State of kiss09 RNG.
*/
typedef struct {
	ulong x,c,y,z;
} kiss09_state;

/**
Generates a random 64-bit unsigned integer using kiss09 RNG.

@param state State of the RNG to use.
*/
#define kiss09_ulong(state) (\
	/*multiply with carry*/ \
	state.c = state.x >> 6, \
	state.x += (state.x << 58) + state.c, \
	state.c += state.x < (state.x << 58) + state.c, \
	/*xorshift*/ \
	state.y ^= state.y << 13, \
	state.y ^= state.y >> 17, \
	state.y ^= state.y << 43, \
	/*linear congruential*/ \
	state.z = 6906969069UL * state.z + 1234567UL, \
	state.x + state.y + state.z \
	)

/**
Generates a random 64-bit unsigned integer using kiss09 RNG.

This is alternative implementation of kiss09 RNG as a function.

@param state State of the RNG to use.
*/
#define kiss09_func_ulong(state) _kiss09_func_ulong(&state)
ulong _kiss09_func_ulong(kiss09_state* state){
	//multiply with carry
	ulong t = (state->x << 58) + state->c;
	state->c = state-> x >>6;
	state->x += t;
	state->c += state->x < t;
	//xorshift
	state->y ^= state->y << 13;
	state->y ^= state->y >> 17;
	state->y ^= state->y << 43;
	//linear congruential
	state->z = 6906969069UL * state->z + 1234567UL;
	return state->x + state->y + state->z;
}

/**
Seeds kiss09 RNG.

@param state Variable, that holds state of the generator to be seeded.
@param seed Value used for seeding. Should be randomly generated for each instance of generator (thread).
*/
void kiss09_seed(kiss09_state* state, ulong j){
	state->x = 1234567890987654321UL ^ j;
	state->c = 123456123456123456UL ^ j;
	state->y = 362436362436362436UL ^ j;
	if(state->y==0){
		state->y=1;
	}
	state->z = 1066149217761810UL ^ j;
}

/**
Generates a random 32-bit unsigned integer using kiss09 RNG.

@param state State of the RNG to use.
*/
#define kiss09_uint(state) ((uint)kiss09_ulong(state))

/**
Generates a random float using kiss09 RNG.

@param state State of the RNG to use.
*/
#define kiss09_float(state) (kiss09_ulong(state)*KISS09_FLOAT_MULTI)

/**
Generates a random double using kiss09 RNG.

@param state State of the RNG to use.
*/
#define kiss09_double(state) (kiss09_ulong(state)*KISS09_DOUBLE_MULTI)

/**
Generates a random double using kiss09 RNG. Since kiss09 returns 64-bit numbers this is equivalent to kiss09_double.

@param state State of the RNG to use.
*/
#define kiss09_double2(state) kiss09_double(state)