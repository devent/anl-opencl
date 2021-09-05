################################################################################
# Automatically-generated file. Do not edit!
################################################################################

# Add inputs and outputs from these tool invocations to the build variables 
CPP_SRCS += \
../src/test/cpp/OpenCLContext.cpp \
../src/test/cpp/OpenCLTestFixture.cpp \
../src/test/cpp/anlopencl_core_test.cpp \
../src/test/cpp/compile_library_test.cpp \
../src/test/cpp/distance_functions_test.cpp \
../src/test/cpp/imaging_map_test.cpp \
../src/test/cpp/imaging_scaleToRange_test.cpp \
../src/test/cpp/interpolation_functions_test.cpp \
../src/test/cpp/kernel_rotateDomain_test.cpp \
../src/test/cpp/kernel_simplefBm_test.cpp \
../src/test/cpp/map_functions_bench.cpp \
../src/test/cpp/noise_functions_test.cpp \
../src/test/cpp/random_test.cpp 

BCS += \
./src/test/cpp/OpenCLContext.bc \
./src/test/cpp/OpenCLTestFixture.bc \
./src/test/cpp/anlopencl_core_test.bc \
./src/test/cpp/compile_library_test.bc \
./src/test/cpp/distance_functions_test.bc \
./src/test/cpp/imaging_map_test.bc \
./src/test/cpp/imaging_scaleToRange_test.bc \
./src/test/cpp/interpolation_functions_test.bc \
./src/test/cpp/kernel_rotateDomain_test.bc \
./src/test/cpp/kernel_simplefBm_test.bc \
./src/test/cpp/map_functions_bench.bc \
./src/test/cpp/noise_functions_test.bc \
./src/test/cpp/random_test.bc 

CPP_DEPS += \
./src/test/cpp/OpenCLContext.d \
./src/test/cpp/OpenCLTestFixture.d \
./src/test/cpp/anlopencl_core_test.d \
./src/test/cpp/compile_library_test.d \
./src/test/cpp/distance_functions_test.d \
./src/test/cpp/imaging_map_test.d \
./src/test/cpp/imaging_scaleToRange_test.d \
./src/test/cpp/interpolation_functions_test.d \
./src/test/cpp/kernel_rotateDomain_test.d \
./src/test/cpp/kernel_simplefBm_test.d \
./src/test/cpp/map_functions_bench.d \
./src/test/cpp/noise_functions_test.d \
./src/test/cpp/random_test.d 


# Each subdirectory must supply rules for building sources it contributes
src/test/cpp/%.bc: ../src/test/cpp/%.cpp src/test/cpp/subdir.mk
	@echo 'Building file: $<'
	@echo 'Invoking: LLVM Clang++'
	clang++ -D_GNU_SOURCE -DUSE_THREAD -I"/home/devent/Projects/dwarf-hustle/anl-opencl/anlopencl/src/main/cpp" -I/usr/include/opencv4 -I/home/devent/Projects/dwarf-hustle/spdlog/include -I/home/devent/Projects/dwarf-hustle/OpenCL-CLHPP/include -I/home/devent/Projects/dwarf-hustle/OpenCL-Headers -I/home/devent/Projects/dwarf-hustle/googletest/googletest/include -I/home/devent/Projects/dwarf-hustle/benchmark/include -O0 -emit-llvm -g3 -Wall -c -fmessage-length=0 -std=c++0x -MMD -MP -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '


