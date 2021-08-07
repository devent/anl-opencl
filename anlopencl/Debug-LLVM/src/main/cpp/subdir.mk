################################################################################
# Automatically-generated file. Do not edit!
################################################################################

# Add inputs and outputs from these tool invocations to the build variables 
C_SRCS += \
../src/main/cpp/hashing.c \
../src/main/cpp/noise_gen.c \
../src/main/cpp/noise_lut.c 

BCS += \
./src/main/cpp/hashing.bc \
./src/main/cpp/noise_gen.bc \
./src/main/cpp/noise_lut.bc 

C_DEPS += \
./src/main/cpp/hashing.d \
./src/main/cpp/noise_gen.d \
./src/main/cpp/noise_lut.d 


# Each subdirectory must supply rules for building sources it contributes
src/main/cpp/%.bc: ../src/main/cpp/%.c src/main/cpp/subdir.mk
	@echo 'Building file: $<'
	@echo 'Invoking: LLVM Clang'
	clang -I/home/devent/Projects/dwarf-hustle/llvm-project -I"/home/devent/Projects/dwarf-hustle/anl-opencl/anlopencl/src/main/cpp" -O0 -emit-llvm -g3 -Wall -c -fmessage-length=0 -MMD -MP -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '


