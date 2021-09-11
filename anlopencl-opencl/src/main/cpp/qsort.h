//
// Copyright (C) 2021 Erwin Müller <erwin@muellerpublic.de>
// Released as open-source under the Apache License, Version 2.0.
//
// ****************************************************************************
// ANL-OpenCL :: OpenCL
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

/**
 * @file qsort.h
 * @author Erwin Müller
 * @date Aug 26, 2021
 * @brief Wraps the GNU qsort function depedent on if we are in a OpenCL kernel or not.
 */

#ifndef QSORT_H_
#define QSORT_H_

#ifdef __cplusplus
extern "C" {
#endif

#ifndef ANLOPENCL_USE_OPENCL
#include <stdlib.h>
/* Use GNU qsort from stdlib if not in a OpenCL kernel. */
#define sort qsort
#else

/* Comparison function. Taken from https://github.com/lattera/glibc/blob/master/stdlib/stdlib.h */
typedef int (*__compar_d_fn_t) (const void *, const void *, void *);

/* Use the GNU qsort copy if in a OpenCL kernel. */
void
sort (void *const pbase, size_t total_elems, size_t size,
	    __compar_d_fn_t cmp);

/* qsort. Taken from https://github.com/lattera/glibc/blob/master/stdlib/qsort.c */
void
_quicksort (void *const pbase, size_t total_elems, size_t size,
	    __compar_d_fn_t cmp, void *arg);

#endif


#ifdef __cplusplus
}
#endif

#endif /* QSORT_H_ */
