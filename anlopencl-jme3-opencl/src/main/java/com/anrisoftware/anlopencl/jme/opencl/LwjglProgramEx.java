package com.anrisoftware.anlopencl.jme.opencl;

import java.nio.ByteBuffer;
import java.util.function.Function;

import org.lwjgl.PointerBuffer;
import org.lwjgl.opencl.CL10;
import org.lwjgl.opencl.CL12;

import com.jme3.opencl.Device;
import com.jme3.opencl.KernelCompilationException;
import com.jme3.opencl.Program;
import com.jme3.opencl.lwjgl.LwjglContext;
import com.jme3.opencl.lwjgl.LwjglDevice;
import com.jme3.opencl.lwjgl.LwjglProgram;
import com.jme3.opencl.lwjgl.Utils;

import lombok.extern.slf4j.Slf4j;

/**
 * Extend {@link LwjglProgram} with compile and link.
 * 
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
@Slf4j
public class LwjglProgramEx extends LwjglProgram {

    public LwjglProgramEx(long program, LwjglContext context) {
        super(program, context);
    }

    public void compile(String args, Program[] headers, String[] headerNames, Device... devices)
            throws KernelCompilationException {
        var deviceList = createBuffer((d) -> ((LwjglDevice) d).getDevice(), devices);
        var headersList = createBuffer((d) -> ((LwjglProgram) d).getProgram(), headers);
        var headerNamesList = createBuffer(headerNames);
        int ret = CL12.clCompileProgram(getProgram(), deviceList, args, headersList, headerNamesList, null, 0);
        if (ret != CL10.CL_SUCCESS) {
            log.warn("Unable to compile program {}", this);
            if (ret == CL10.CL_BUILD_PROGRAM_FAILURE) {
                throw new KernelCompilationException("Failed to build program", ret, "CL_BUILD_PROGRAM_FAILURE");
            } else {
                Utils.checkError(ret, "clBuildProgram");
            }
        } else {
            log.info("Program compiled {}", this);
        }
    }

    private static <T> PointerBuffer createBuffer(Function<T, Long> pointer, T[] list) {
        var p = PointerBuffer.allocateDirect(list.length);
        p.rewind();
        for (T d : list) {
            p.put(pointer.apply(d));
        }
        p.flip();
        return p;
    }

    private static PointerBuffer createBuffer(String[] list) {
        var size = 0;
        for (int i = 0; i < list.length; i++) {
            size += list[i].length();
        }
        var p = PointerBuffer.allocateDirect(size);
        p.rewind();
        for (String s : list) {
            var buffer = ByteBuffer.wrap(s.getBytes());
            p.put(buffer);
        }
        p.flip();
        return p;
    }

}
