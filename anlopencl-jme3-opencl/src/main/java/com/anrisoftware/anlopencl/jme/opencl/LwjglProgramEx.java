package com.anrisoftware.anlopencl.jme.opencl;

import static org.apache.commons.lang3.Validate.isTrue;
import static org.lwjgl.PointerBuffer.create;
import static org.lwjgl.opencl.CL10.CL_PROGRAM_BUILD_LOG;
import static org.lwjgl.opencl.CL10.CL_PROGRAM_DEVICES;
import static org.lwjgl.opencl.CL10.clGetProgramBuildInfo;
import static org.lwjgl.opencl.CL10.clGetProgramInfo;
import static org.lwjgl.system.MemoryStack.stackASCII;
import static org.lwjgl.system.MemoryStack.stackMallocInt;
import static org.lwjgl.system.MemoryStack.stackMallocLong;
import static org.lwjgl.system.MemoryStack.stackMallocPointer;
import static org.lwjgl.system.MemoryUtil.memAddress;
import static org.lwjgl.system.MemoryUtil.memByteBuffer;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.function.Function;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.opencl.CL10;
import org.lwjgl.opencl.CL12;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

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

    private final LwjglContext context;

    public LwjglProgramEx(long program, LwjglContext context) {
        super(program, context);
        this.context = context;
    }

    public void compile(Device... devices) throws KernelCompilationException {
        compile(null, (Program[]) null, null, devices);
    }

    public void compile(String options, Device... devices) throws KernelCompilationException {
        compile(options, (Program[]) null, null, devices);
    }

    public void compile(String options, List<Program> headers, List<String> headerNames, Device... devices)
            throws KernelCompilationException {
        compile(options, headers != null ? headers.toArray(new Program[0]) : null,
                headerNames != null ? headerNames.toArray(new String[0]) : null, devices);
    }

    public void compile(String options, Program[] headers, String[] headerNames, Device... devices)
            throws KernelCompilationException {
        PointerBuffer deviceList = null;
        if (devices != null && devices.length > 0) {
            deviceList = createBuffer((d) -> ((LwjglDevice) d).getDevice(), devices);
        }
        PointerBuffer headersList = null;
        if (headers != null && headers.length > 0) {
            headersList = createBuffer((d) -> ((LwjglProgram) d).getProgram(), headers);
        }
        PointerBuffer headerNamesList = null;
        if (headerNames != null && headerNames.length > 0) {
            headerNamesList = createBuffer(headerNames);
        }
        int ret = CL12.clCompileProgram(getProgram(), deviceList, options, headersList, headerNamesList, null, 0);
        if (ret != CL10.CL_SUCCESS) {
            log.warn("Unable to compile program {}", this);
            if (ret == CL10.CL_BUILD_PROGRAM_FAILURE) {
                var device = getDevice();
                var buildLog = getBuildLog(device);
                log.error("Unable to compile program {}: {}", this, buildLog);
                throw new KernelCompilationException("Failed to build program", ret, buildLog);
            } else {
                Utils.checkError(ret, "clBuildProgram");
            }
        } else {
            log.info("Program compiled {}", this);
        }
    }

    public static Program link(LwjglContext context, String options, Program program, Device... devices)
            throws KernelCompilationException {
        return link(context, options, new Program[] { program }, devices);
    }

    public static Program link(LwjglContext context, String options, Program[] programs, Device... devices)
            throws KernelCompilationException {
        PointerBuffer deviceList = null;
        if (devices != null && devices.length > 0) {
            deviceList = createBuffer((d) -> ((LwjglDevice) d).getDevice(), devices);
        }
        isTrue(programs.length > 0, "No programs given.");
        PointerBuffer programList = null;
        programList = createBuffer((p) -> ((LwjglProgram) p).getProgram(), programs);
        var err = stackMallocInt(1);
        long lprogram = CL12.clLinkProgram(context.getContext(), deviceList, options, programList, null, 0, err);
        if (err.get(0) != CL10.CL_SUCCESS) {
            log.warn("Unable to link program {}", programs[0]);
            if (err.get(0) == CL12.CL_LINK_PROGRAM_FAILURE) {
                throw new KernelCompilationException("Failed to link program", err.get(0), "");
            } else {
                Utils.checkError(err.get(0), "clLinkProgram");
            }
        } else {
            log.info("Program linked {}", programs[0]);
        }
        return new LwjglProgramEx(lprogram, context);
    }

    private static <T> PointerBuffer createBuffer(Function<T, Long> pointer, T[] list) {
        var p = stackMallocPointer(list.length);
        for (T d : list) {
            p.put(pointer.apply(d));
        }
        p.flip();
        return p;
    }

    private static PointerBuffer createBuffer(String[] list) {
        var p = stackMallocPointer(list.length);
        for (String s : list) {
            var buffer = stackASCII(s, true);
            p.put(memAddress(buffer));
        }
        p.flip();
        return p;
    }

    private long getDevice() {
        var size = MemoryStack.stackMallocInt(1);
        int ret = clGetProgramInfo(getProgram(), CL_PROGRAM_DEVICES, (ByteBuffer) null, create(memAddress(size), 1));
        Utils.checkError(ret, "clGetProgramInfo");
        int count = size.get(0);
        var buffer = memByteBuffer(stackMallocLong(count));
        ret = clGetProgramInfo(getProgram(), CL_PROGRAM_DEVICES, buffer, null);
        Utils.checkError(ret, "clGetProgramInfo");
        return buffer.getLong(0);
    }

    private String getBuildLog(long device) {
        Utils.pointerBuffers[0].rewind();
        int ret = clGetProgramBuildInfo(getProgram(), device, CL_PROGRAM_BUILD_LOG, (ByteBuffer) null,
                Utils.pointerBuffers[0]);
        Utils.checkError(ret, "clGetProgramBuildInfo");
        int count = (int) Utils.pointerBuffers[0].get(0);
        final ByteBuffer buffer = BufferUtils.createByteBuffer(count);
        ret = clGetProgramBuildInfo(getProgram(), device, CL_PROGRAM_BUILD_LOG, buffer, null);
        Utils.checkError(ret, "clGetProgramBuildInfo");
        return MemoryUtil.memASCII(buffer);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("context", context).append("program", getProgram()).toString();
    }
}
