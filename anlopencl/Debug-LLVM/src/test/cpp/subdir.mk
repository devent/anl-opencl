################################################################################
# Automatically-generated file. Do not edit!
################################################################################

# Add inputs and outputs from these tool invocations to the build variables 
CPP_SRCS += \
../src/test/cpp/OpenCLContext.cpp \
../src/test/cpp/OpenCLTestFixture.cpp \
../src/test/cpp/anlopencl_core_test.cpp \
../src/test/cpp/noise_cellular_functions_test.cpp \
../src/test/cpp/noise_functions_test.cpp \
../src/test/cpp/opencl_cellular_functions_test.cpp \
../src/test/cpp/opencl_heightmap_example_test.cpp \
../src/test/cpp/opencl_noise2D_functions_test.cpp \
../src/test/cpp/opencl_noise3D_functions_test.cpp \
../src/test/cpp/opencl_noise4D_functions_test.cpp \
../src/test/cpp/opencl_noise6D_functions_test.cpp \
../src/test/cpp/opencl_simpleBillowLayer_test.cpp \
../src/test/cpp/opencl_simpleBillow_test.cpp \
../src/test/cpp/opencl_simpleFractalLayer_test.cpp \
../src/test/cpp/opencl_simpleRidgedLayer_test.cpp \
../src/test/cpp/opencl_simpleRidgedMultifractal_test.cpp \
../src/test/cpp/opencl_simplefBm_test.cpp \
../src/test/cpp/random_test.cpp 

BCS += \
./src/test/cpp/OpenCLContext.bc \
./src/test/cpp/OpenCLTestFixture.bc \
./src/test/cpp/anlopencl_core_test.bc \
./src/test/cpp/noise_cellular_functions_test.bc \
./src/test/cpp/noise_functions_test.bc \
./src/test/cpp/opencl_cellular_functions_test.bc \
./src/test/cpp/opencl_heightmap_example_test.bc \
./src/test/cpp/opencl_noise2D_functions_test.bc \
./src/test/cpp/opencl_noise3D_functions_test.bc \
./src/test/cpp/opencl_noise4D_functions_test.bc \
./src/test/cpp/opencl_noise6D_functions_test.bc \
./src/test/cpp/opencl_simpleBillowLayer_test.bc \
./src/test/cpp/opencl_simpleBillow_test.bc \
./src/test/cpp/opencl_simpleFractalLayer_test.bc \
./src/test/cpp/opencl_simpleRidgedLayer_test.bc \
./src/test/cpp/opencl_simpleRidgedMultifractal_test.bc \
./src/test/cpp/opencl_simplefBm_test.bc \
./src/test/cpp/random_test.bc 

CPP_DEPS += \
./src/test/cpp/OpenCLContext.d \
./src/test/cpp/OpenCLTestFixture.d \
./src/test/cpp/anlopencl_core_test.d \
./src/test/cpp/noise_cellular_functions_test.d \
./src/test/cpp/noise_functions_test.d \
./src/test/cpp/opencl_cellular_functions_test.d \
./src/test/cpp/opencl_heightmap_example_test.d \
./src/test/cpp/opencl_noise2D_functions_test.d \
./src/test/cpp/opencl_noise3D_functions_test.d \
./src/test/cpp/opencl_noise4D_functions_test.d \
./src/test/cpp/opencl_noise6D_functions_test.d \
./src/test/cpp/opencl_simpleBillowLayer_test.d \
./src/test/cpp/opencl_simpleBillow_test.d \
./src/test/cpp/opencl_simpleFractalLayer_test.d \
./src/test/cpp/opencl_simpleRidgedLayer_test.d \
./src/test/cpp/opencl_simpleRidgedMultifractal_test.d \
./src/test/cpp/opencl_simplefBm_test.d \
./src/test/cpp/random_test.d 


# Each subdirectory must supply rules for building sources it contributes
src/test/cpp/%.bc: ../src/test/cpp/%.cpp src/test/cpp/subdir.mk
	@echo 'Building file: $<'
	@echo 'Invoking: LLVM Clang++'
	clang++ -D_GNU_SOURCE -DANLOPENCL_USE_DOUBLE -DANLOPENCL_USE_THREAD -I"/home/devent/Projects/dwarf-hustle/anl-opencl/anlopencl/src/main/cpp" -I/usr/include/opencv4 -I/home/devent/Projects/dwarf-hustle/spdlog/include -I/home/devent/Projects/dwarf-hustle/OpenCL-CLHPP/include -I/home/devent/Projects/dwarf-hustle/OpenCL-Headers -I/home/devent/Projects/dwarf-hustle/googletest/googletest/include -I/home/devent/Projects/dwarf-hustle/benchmark/include -O0 -emit-llvm -g3 -Wall -c -fmessage-length=0 -std=c++0x -MMD -MP -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '


