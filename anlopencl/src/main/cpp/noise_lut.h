/*
 * noise_lut.h
 *
 *  Created on: Aug 4, 2021
 *      Author: Erwin Müller
 */

#ifndef NOISE_LUT_H_
#define NOISE_LUT_H_

#ifdef __cplusplus
extern "C" {
#endif

#ifndef USE_OPENCL
#include <opencl_utils.h>
#endif // USE_OPENCL

extern REAL gradient2D_lut[8][2];
extern REAL gradient3D_lut[24][3];
extern REAL gradient4D_lut[64][4];
extern REAL gradient6D_lut[192][6];
extern REAL whitenoise_lut[256];

#ifdef __cplusplus
}
#endif

#endif /* NOISE_LUT_H_ */
