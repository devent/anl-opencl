package com.anrisoftware.anlopencl.jme.opencl;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.opencl.CL10;
import org.lwjgl.opencl.CL12;
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
            System.out.println(Arrays.toString(headers));
            headersList = createBuffer((d) -> ((LwjglProgram) d).getProgram(), headers);
        }
        PointerBuffer headerNamesList = null;
        if (headerNames != null && headerNames.length > 0) {
            System.out.println(Arrays.toString(headerNames));
            headerNamesList = createBuffer(headerNames);
        }
        int ret = CL12.clCompileProgram(getProgram(), deviceList, options, headersList, headerNamesList, null, 0);
        if (ret != CL10.CL_SUCCESS) {
            log.warn("Unable to compile program {}", this);
            if (ret == CL10.CL_BUILD_PROGRAM_FAILURE) {
                var device = getDevice();
                var buildLog = getBuildLog(device);
                throw new KernelCompilationException("Failed to build program", ret, buildLog);
            } else {
                Utils.checkError(ret, "clBuildProgram");
            }
        } else {
            log.info("Program compiled {}", this);
        }
    }

    public Program link(Device... devices) throws KernelCompilationException {
        return link("", devices);
    }

    public Program link(String options, Device... devices) throws KernelCompilationException {
        PointerBuffer deviceList = null;
        if (devices != null && devices.length > 0) {
            deviceList = createBuffer((d) -> ((LwjglDevice) d).getDevice(), devices);
        }
        // var ret = BufferUtils.createIntBuffer(1);
        long lprogram = CL12.clLinkProgram(context.getContext(), deviceList, options, getProgram(), null, 0);
        if (lprogram == 0) {
            log.error("Unable to compile program {}", this);
        } else {
            log.info("Program compiled {}", this);
        }
        return new LwjglProgramEx(lprogram, context);
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
        var p = PointerBuffer.allocateDirect(list.length);
        p.rewind();
        for (String s : list) {
            var bytes = s.getBytes();
            var buffer = BufferUtils.createByteBuffer(s.length());
            buffer.rewind();
            buffer.put(bytes);
            buffer.flip();
            p.put(MemoryUtil.memAddress(buffer));
        }
        p.flip();
        return p;
    }

    private long getDevice() {
        Utils.pointerBuffers[0].rewind();
        int ret = CL10.clGetProgramInfo(getProgram(), CL10.CL_PROGRAM_DEVICES, (ByteBuffer) null,
                Utils.pointerBuffers[0]);
        Utils.checkError(ret, "clGetProgramInfo");
        int count = (int) Utils.pointerBuffers[0].get(0);
        final ByteBuffer buffer = BufferUtils.createByteBuffer(count);
        ret = CL10.clGetProgramInfo(getProgram(), CL10.CL_PROGRAM_DEVICES, buffer, null);
        Utils.checkError(ret, "clGetProgramInfo");
        return buffer.getLong(0);
    }

    private String getBuildLog(long device) {
        Utils.pointerBuffers[0].rewind();
        int ret = CL10.clGetProgramBuildInfo(getProgram(), device, CL10.CL_PROGRAM_BUILD_LOG, (ByteBuffer) null,
                Utils.pointerBuffers[0]);
        Utils.checkError(ret, "clGetProgramBuildInfo");
        int count = (int) Utils.pointerBuffers[0].get(0);
        final ByteBuffer buffer = BufferUtils.createByteBuffer(count);
        ret = CL10.clGetProgramBuildInfo(getProgram(), device, CL10.CL_PROGRAM_BUILD_LOG, buffer, null);
        Utils.checkError(ret, "clGetProgramBuildInfo");
        return MemoryUtil.memASCII(buffer);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("context", context).append("program", getProgram()).toString();
    }
}
