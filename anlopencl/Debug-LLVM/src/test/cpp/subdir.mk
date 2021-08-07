################################################################################
# Automatically-generated file. Do not edit!
################################################################################

# Add inputs and outputs from these tool invocations to the build variables 
CPP_SRCS += \
../src/test/cpp/OpenCLTestFixture.cpp \
../src/test/cpp/anlopencl_core_test.cpp \
../src/test/cpp/distance_functions_test.cpp \
../src/test/cpp/interpolation_functions_test.cpp \
../src/test/cpp/noise_functions_test.cpp \
../src/test/cpp/opencl_test.cpp 

BCS += \
./src/test/cpp/OpenCLTestFixture.bc \
./src/test/cpp/anlopencl_core_test.bc \
./src/test/cpp/distance_functions_test.bc \
./src/test/cpp/interpolation_functions_test.bc \
./src/test/cpp/noise_functions_test.bc \
./src/test/cpp/opencl_test.bc 

CPP_DEPS += \
./src/test/cpp/OpenCLTestFixture.d \
./src/test/cpp/anlopencl_core_test.d \
./src/test/cpp/distance_functions_test.d \
./src/test/cpp/interpolation_functions_test.d \
./src/test/cpp/noise_functions_test.d \
./src/test/cpp/opencl_test.d 


# Each subdirectory must supply rules for building sources it contributes
src/test/cpp/%.bc: ../src/test/cpp/%.cpp src/test/cpp/subdir.mk
	@echo 'Building file: $<'
	@echo 'Invoking: LLVM Clang++'
	clang++ -I"/home/devent/Projects/dwarf-hustle/anl-opencl/anlopencl/src/main/cpp" -I/usr/include/opencv4 -I/home/devent/Projects/dwarf-hustle/spdlog/include -I/home/devent/Projects/dwarf-hustle/OpenCL-CLHPP/include -I/home/devent/Projects/dwarf-hustle/OpenCL-Headers -I/home/devent/Projects/dwarf-hustle/googletest/googletest/include -O0 -emit-llvm -g3 -Wall -c -fmessage-length=0 -std=c++0x -MMD -MP -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '


