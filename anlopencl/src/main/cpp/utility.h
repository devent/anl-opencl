/*
 * utility.h
 *
 *  Created on: Jul 27, 2021
 *      Author: Erwin Müller
 */

#ifndef UTILITY_H_
#define UTILITY_H_

#ifdef __cplusplus
extern "C" {
#endif

#include <opencl_utils.h>

#ifndef USE_OPENCL
REAL clamp(REAL v, REAL l, REAL h) {
	if (v < l)
		v = l;
	if (v > h)
		v = h;

	return v;
}
#endif

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

#ifdef __cplusplus
}
#endif

#endif /* UTILITY_H_ */
