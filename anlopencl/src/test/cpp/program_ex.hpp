/*
 * program_ex.hpp
 *
 *  Created on: Jul 29, 2021
 *      Author: Erwin Müller
 */

#ifndef PROGRAM_EX_HPP_
#define PROGRAM_EX_HPP_

#include <CL/cl2.hpp>

using namespace cl;

#define COMPILE_PROGRAM_ERR "Error compile program"

class ProgramEx : public Program {
public:
	ProgramEx(const string &source, bool build = false, cl_int *err = NULL) :
			Program { source, build, err } {
	}

	ProgramEx(const Context &context, const string &source, bool build = false,
			cl_int *err = NULL) :
			Program { source, build, err } {
	}

	ProgramEx(const Sources &sources, cl_int *err = NULL) : Program { sources, err } {
	}

	ProgramEx(const Context &context, const Sources &sources,
			cl_int *err = NULL) :
			Program { context, sources, err } {
	}

#if CL_HPP_TARGET_OPENCL_VERSION >= 210 || (CL_HPP_TARGET_OPENCL_VERSION==200 && defined(CL_HPP_USE_IL_KHR))
	/**
	 * Program constructor to allow construction of program from SPIR-V or another IL.
	 * Valid for either OpenCL >= 2.1 or when CL_HPP_USE_IL_KHR is defined.
	 */
	ProgramEx(const vector<char> &IL, bool build = false, cl_int *err = NULL) :
			Program { IL, build, err } {
	}

	/**
	 * Program constructor to allow construction of program from SPIR-V or another IL
	 * for a specific context.
	 * Valid for either OpenCL >= 2.1 or when CL_HPP_USE_IL_KHR is defined.
	 */
	ProgramEx(const Context &context, const vector<char> &IL,
			bool build = false, cl_int *err = NULL) :
			Program { context, IL, build, err } {
	}
#endif // #if CL_HPP_TARGET_OPENCL_VERSION >= 210

#if CL_HPP_TARGET_OPENCL_VERSION >= 120
    cl_int compile(
    		vector<Program> input_headers,
			vector<string> input_header_names,
			const char* options = NULL,
			void (CL_CALLBACK * notifyFptr)(cl_program, void *) = NULL,
			void* data = NULL) const {
    	vector<cl_program> programs(input_headers.size());
        for (unsigned int i = 0; i < input_headers.size(); i++) {
            programs[i] = input_headers[i]();
        }
        size_type n = input_header_names.size();
        vector<const char*> input_header_names_s(n);
        for (size_type i = 0; i < n; ++i) {
        	input_header_names_s[i] = input_header_names[i].data();
        }
        cl_int error = ::clCompileProgram(
            object_,
            0,
            NULL,
            options,
			programs.size(),
			programs.data(),
			input_header_names_s.data(),
            notifyFptr,
            data);
        return detail::buildErrHandler(error, COMPILE_PROGRAM_ERR,
				getBuildInfo<CL_PROGRAM_BUILD_LOG>());
	}
#endif // CL_HPP_TARGET_OPENCL_VERSION >= 120

};

#if CL_HPP_TARGET_OPENCL_VERSION >= 120

#define LINK_PROGRAM_ERR "Link program error"

inline Program linkProgram(
    Program input,
    const char* options = NULL,
    void (CL_CALLBACK * notifyFptr)(cl_program, void *) = NULL,
    void* data = NULL,
    cl_int* err = NULL)
{
    cl_int error_local = CL_SUCCESS;

    cl_program programs[1] = { input() };

    Context ctx = input.getInfo<CL_PROGRAM_CONTEXT>(&error_local);
    if(error_local!=CL_SUCCESS) {
        detail::errHandler(error_local, LINK_PROGRAM_ERR);
    }

    cl_program prog = ::clLinkProgram(
        ctx(),
        0,
        NULL,
        options,
        1,
        programs,
        notifyFptr,
        data,
        &error_local);

    detail::errHandler(error_local,COMPILE_PROGRAM_ERR);
    if (err != NULL) {
        *err = error_local;
    }

    return Program(prog);
}
#endif // CL_HPP_TARGET_OPENCL_VERSION >= 120

#endif /* PROGRAM_EX_HPP_ */
