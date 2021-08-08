################################################################################
# Automatically-generated file. Do not edit!
################################################################################

-include ../makefile.init

RM := rm -rf

# All of the sources participating in the build are defined here
-include sources.mk
-include src/test/cpp/subdir.mk
-include src/main/cpp/subdir.mk
-include subdir.mk
-include objects.mk

ifneq ($(MAKECMDGOALS),clean)
ifneq ($(strip $(CC_DEPS)),)
-include $(CC_DEPS)
endif
ifneq ($(strip $(C++_DEPS)),)
-include $(C++_DEPS)
endif
ifneq ($(strip $(C_UPPER_DEPS)),)
-include $(C_UPPER_DEPS)
endif
ifneq ($(strip $(CXX_DEPS)),)
-include $(CXX_DEPS)
endif
ifneq ($(strip $(BC_DEPS)),)
-include $(BC_DEPS)
endif
ifneq ($(strip $(C_DEPS)),)
-include $(C_DEPS)
endif
ifneq ($(strip $(CPP_DEPS)),)
-include $(CPP_DEPS)
endif
endif

-include ../makefile.defs

OPTIONAL_TOOL_DEPS := \
$(wildcard ../makefile.defs) \
$(wildcard ../makefile.init) \
$(wildcard ../makefile.targets) \


BUILD_ARTIFACT_NAME := anlopencl-core
BUILD_ARTIFACT_EXTENSION :=
BUILD_ARTIFACT_PREFIX :=
BUILD_ARTIFACT := $(BUILD_ARTIFACT_PREFIX)$(BUILD_ARTIFACT_NAME)$(if $(BUILD_ARTIFACT_EXTENSION),.$(BUILD_ARTIFACT_EXTENSION),)

# Add inputs and outputs from these tool invocations to the build variables 
LLVM_EXECUTABLES += \
USER_OBJS.exe \


# All Target
all: main-build

# Main-build Target
main-build: anlopencl-core secondary-outputs

# Tool invocations
anlopencl-core: $(BCS) $(USER_OBJS) makefile objects.mk $(OPTIONAL_TOOL_DEPS)
	@echo 'Building target: $@'
	@echo 'Invoking: LLVM Clang C++ linker'
	clang++ -L/home/devent/Projects/dwarf-hustle/googletest/lib -L/home/devent/Projects/dwarf-hustle/spdlog/build -o "anlopencl-core" $(BCS) $(USER_OBJS) $(LIBS)
	@echo 'Finished building target: $@'
	@echo ' '

USER_OBJS.exe: anlopencl-core $(BCS) $(USER_OBJS)

# Other Targets
clean:
	-$(RM) $(BCS)$(CC_DEPS)$(C++_DEPS)$(LLVM_BC_EXECUTABLES)$(C_UPPER_DEPS)$(CXX_DEPS)$(BC_DEPS)$(LLVM_EXECUTABLES)$(C_DEPS)$(CPP_DEPS) anlopencl-core
	-@echo ' '

secondary-outputs: $(LLVM_BC_EXECUTABLES)

.PHONY: all clean dependents

-include ../makefile.targets
