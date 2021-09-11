################################################################################
# Automatically-generated file. Do not edit!
################################################################################

# Add inputs and outputs from these tool invocations to the build variables 
C_SRCS += \
../src/main/cpp/hashing.c \
../src/main/cpp/imaging.c \
../src/main/cpp/kernel.c \
../src/main/cpp/noise_gen.c \
../src/main/cpp/noise_lut.c \
../src/main/cpp/opencl_utils.c \
../src/main/cpp/qsort.c \
../src/main/cpp/random.c \
../src/main/cpp/utility.c 

BCS += \
./src/main/cpp/hashing.bc \
./src/main/cpp/imaging.bc \
./src/main/cpp/kernel.bc \
./src/main/cpp/noise_gen.bc \
./src/main/cpp/noise_lut.bc \
./src/main/cpp/opencl_utils.bc \
./src/main/cpp/qsort.bc \
./src/main/cpp/random.bc \
./src/main/cpp/utility.bc 

C_DEPS += \
./src/main/cpp/hashing.d \
./src/main/cpp/imaging.d \
./src/main/cpp/kernel.d \
./src/main/cpp/noise_gen.d \
./src/main/cpp/noise_lut.d \
./src/main/cpp/opencl_utils.d \
./src/main/cpp/qsort.d \
./src/main/cpp/random.d \
./src/main/cpp/utility.d 


# Each subdirectory must supply rules for building sources it contributes
src/main/cpp/%.bc: ../src/main/cpp/%.c src/main/cpp/subdir.mk
	@echo 'Building file: $<'
	@echo 'Invoking: LLVM Clang'
	clang -D_GNU_SOURCE -DANLOPENCL_USE_THREAD -I/home/devent/Projects/dwarf-hustle/llvm-project -I"/home/devent/Projects/dwarf-hustle/anl-opencl/anlopencl/src/main/cpp" -O0 -emit-llvm -g3 -Wall -c -fmessage-length=0 -MMD -MP -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '


