MKDIR   := mkdir -p
RMDIR   := rd /S /Q
CC      := clang
OBJ     := ./OpenCL/obj
INCLUDE := ./src/main/cpp
SRC     := ./src/test/cpp
SRCS    := $(wildcard $(SRC)/*.cl)
OBJS    := $(patsubst $(SRC)/%.cl,$(OBJ)/%.ll,$(SRCS))
CFLAGS  := -Xclang -finclude-default-header -S -emit-llvm -x cl -I$(INCLUDE)
LDFLAGS :=
LDLIBS  := 

.PHONY: all clean

all: $(OBJS)

$(OBJ)/%.ll: $(SRC)/%.cl | $(OBJ)
	$(CC) $(CFLAGS) -o $@ $<

$(OBJ):
	$(MKDIR) $@

clean:
	$(RMDIR) $(OBJ)
