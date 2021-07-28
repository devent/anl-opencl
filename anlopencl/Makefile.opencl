MKDIR   := md
RMDIR   := rd /S /Q
CC      := clang
BIN     := ./OpenCL/bin
OBJ     := ./OpenCL/obj
INCLUDE := ./src/main/cpp
SRC     := ./src/test/cpp
SRCS    := $(wildcard $(SRC)/*.cl)
OBJS    := $(patsubst $(SRC)/%.cl,$(OBJ)/%.ll,$(SRCS))
CFLAGS  := -S -emit-llvm -x cl -I$(INCLUDE)
LDLIBS  := 

.PHONY: all run clean

all: $(OBJS) | $(BIN)
    $(CC) $(LDFLAGS) $^ -o $@ $(LDLIBS)

$(OBJ)/%.ll: $(SRC)/%.cl | $(OBJ)
    $(CC) $(CFLAGS) -c $< -o $@

$(BIN) $(OBJ):
    $(MKDIR) $@

clean:
    $(RMDIR) $(OBJ) $(BIN)
