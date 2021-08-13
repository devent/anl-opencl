/*
 * utility.c
 *
 *  Created on: Aug 13, 2021
 *      Author: Erwin Müller
 */

#ifndef USE_OPENCL
#include "utility.h"
#endif // USE_OPENCL

#ifndef USE_OPENCL
REAL degrees(REAL radians) {
	return (180 / M_PI) * radians;
}
#endif // USE_OPENCL

#ifndef USE_OPENCL
REAL radians(REAL degrees) {
	return (M_PI / 180) * degrees;
}
#endif // USE_OPENCL

#ifndef USE_OPENCL
REAL clamp(REAL v, REAL l, REAL h) {
	if (v < l)
		v = l;
	if (v > h)
		v = h;

	return v;
}
#endif // USE_OPENCL

REAL lerp(REAL t, REAL a, REAL b) {
	return a + t * (b - a);
}

bool isPowerOf2(unsigned int n) {
	// from https://dzone.com/articles/ispowerof2-c
	return n == 1 || (n & (n - 1)) == 0;
}

REAL hermite_blend(REAL t) {
	return (t * t * (3 - 2 * t));
}

REAL quintic_blend(REAL t) {
	return t * t * t * (t * (t * 6 - 15) + 10);
}

int fast_floor(REAL t) {
	return (t > 0 ? (int) t : (int) t - 1);
}

int2 fast_floor2(vector2 v) {
	return (int2){ fast_floor(v.x), fast_floor(v.y) };
}

int3 fast_floor3(vector3 v) {
	return (int3){ fast_floor(v.x), fast_floor(v.y), fast_floor(v.z) };
}

REAL array_dot(REAL *arr, REAL a, REAL b) {
	return a * arr[0] + b * arr[1];
}

REAL array_dot2(REAL *arr, REAL a, REAL b) {
	return a * arr[0] + b * arr[1];
}

REAL array_dot3(REAL *arr, REAL a, REAL b, REAL c) {
	return a * arr[0] + b * arr[1] + c * arr[2];
}

REAL bias(REAL b, REAL t) {
	return pow(t, log(b) / log((REAL)0.5));
}

REAL gain(REAL g, REAL t) {
	if (t < 0.5) {
		return bias(1.0 - g, 2.0 * t) / 2.0;
	} else {
		return 1.0 - bias(1.0 - g, 2.0 - 2.0 * t) / 2.0;
	}
}

