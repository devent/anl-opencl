/*
 * hashing.h
 *
 *  Created on: Jul 27, 2021
 *      Author: Erwin Müller
 */

#ifndef HASHING_H_
#define HASHING_H_

#ifdef __cplusplus
extern "C" {
#endif

#ifndef USE_OPENCL

#include <opencl_utils.h>

#endif // USE_OPENCL

#define FNV_32_PRIME ((uint)0x01000193)
#define FNV_32_INIT ((uint )2166136261)
#define FNV_MASK_8 (((uint)1<<8)-1)

uint hash_coords_2(uint x, uint y, uint seed);
uint hash_coords_3(uint x, uint y, uint z, uint seed);
uint hash_coords_4(uint x, uint y, uint z, uint w, uint seed);
uint hash_coords_6(uint x, uint y, uint z, uint w, uint u, uint v, uint seed);

uint fnv_32_a_combine(uint hash, uint val);
unsigned char xor_fold_hash(uint hash);

#ifdef __cplusplus
}
#endif

#endif /* HASHING_H_ */
