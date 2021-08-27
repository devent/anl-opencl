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

#ifndef USE_OPENCL
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
