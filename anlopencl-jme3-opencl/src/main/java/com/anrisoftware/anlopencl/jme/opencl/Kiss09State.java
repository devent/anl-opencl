/*
 * Copyright (C) 2021 Erwin Müller <erwin@muellerpublic.de>
 * Released as open-source under the Apache License, Version 2.0.
 *
 * ****************************************************************************
 * ANL-OpenCL :: JME3 - OpenCL
 * ****************************************************************************
 *
 * Copyright (C) 2021 Erwin Müller <erwin@muellerpublic.de>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ****************************************************************************
 * ANL-OpenCL :: JME3 - OpenCL is a derivative work based on Josua Tippetts' C++ library:
 * http://accidentalnoise.sourceforge.net/index.html
 * ****************************************************************************
 *
 * Copyright (C) 2011 Joshua Tippetts
 *
 *   This software is provided 'as-is', without any express or implied
 *   warranty.  In no event will the authors be held liable for any damages
 *   arising from the use of this software.
 *
 *   Permission is granted to anyone to use this software for any purpose,
 *   including commercial applications, and to alter it and redistribute it
 *   freely, subject to the following restrictions:
 *
 *   1. The origin of this software must not be misrepresented; you must not
 *      claim that you wrote the original software. If you use this software
 *      in a product, an acknowledgment in the product documentation would be
 *      appreciated but is not required.
 *   2. Altered source versions must be plainly marked as such, and must not be
 *      misrepresented as being the original software.
 *   3. This notice may not be removed or altered from any source distribution.
 */
package com.anrisoftware.anlopencl.jme.opencl;

import static com.anrisoftware.anlopencl.jme.opencl.LwjglUtils.checkCLError;
import static org.lwjgl.opencl.CL10.CL_MEM_COPY_HOST_PTR;
import static org.lwjgl.opencl.CL10.CL_MEM_READ_ONLY;
import static org.lwjgl.opencl.CL10.clCreateBuffer;
import static org.lwjgl.system.MemoryStack.stackGet;
import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.system.MemoryUtil.memAddress;
import static org.lwjgl.system.MemoryUtil.memCopy;
import static org.lwjgl.system.MemoryUtil.nmemAllocChecked;
import static org.lwjgl.system.MemoryUtil.nmemCallocChecked;

import java.nio.ByteBuffer;

import javax.annotation.Nullable;

import org.lwjgl.BufferUtils;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.NativeResource;
import org.lwjgl.system.Struct;
import org.lwjgl.system.StructBuffer;

/**
 * State of kiss09 RNG.
 *
 * <code>kiss09_state</code>
 *
 * <h3>Layout</h3>
 *
 * <pre>
 * typedef struct {
 *     ulong x,c,y,z;
 * } kiss09_state;
 * </pre>
 */
public class Kiss09State extends Struct implements NativeResource {

    /** The struct size in bytes. */
    public static final int SIZEOF;

    /** The struct alignment in bytes. */
    public static final int ALIGNOF;

    /** The struct member offsets. */
    public static final int X, C, Y, Z;

    static {
        Layout layout = __struct( //
                __member(8), // x
                __member(8), // c
                __member(8), // y
                __member(8) // z
        );

        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();

        int i = 0;
        X = layout.offsetof(i++);
        C = layout.offsetof(i++);
        Y = layout.offsetof(i++);
        Z = layout.offsetof(i++);
    }

    private final ByteBuffer container;

    private Long clBuffer;

    /**
     * Creates a {@code MappingRanges} instance at the current position of the
     * specified {@link ByteBuffer} container. Changes to the buffer's content will
     * be visible to the struct instance and vice versa.
     *
     * <p>
     * The created instance holds a strong reference to the container object.
     * </p>
     */
    public Kiss09State(ByteBuffer container) {
        super(memAddress(container), __checkContainer(container, SIZEOF));
        this.container = container;
    }

    public ByteBuffer getContainer() {
        return container;
    }

    @Override
    public int sizeof() {
        return SIZEOF;
    }

    /**
     * Returns <code>x.</code>
     */
    public long getX() {
        return getX(address());
    }

    /**
     * Returns <code>c.</code>
     */
    public long getC() {
        return getC(address());
    }

    /**
     * Returns <code>y.</code>
     */
    public long getY() {
        return getY(address());
    }

    /**
     * Returns <code>z.</code>
     */
    public long getZ() {
        return getZ(address());
    }

    /**
     * Sets <code>x.</code>
     */
    public Kiss09State setX(long x) {
        setX(address(), x);
        return this;
    }

    /**
     * Sets <code>c.</code>
     */
    public Kiss09State setC(long c) {
        setC(address(), c);
        return this;
    }

    /**
     * Sets <code>y.</code>
     */
    public Kiss09State setY(long y) {
        setY(address(), y);
        return this;
    }

    /**
     * Sets <code>z.</code>
     */
    public Kiss09State setZ(long z) {
        setZ(address(), z);
        return this;
    }

    /**
     * Initializes this struct with the specified values.
     */
    public Kiss09State set(long x, long c, long y, long z) {
        setX(x);
        setC(c);
        setY(y);
        setZ(z);
        return this;
    }

    /**
     * Copies the specified struct data to this struct.
     *
     * @param src the source struct
     * @return this struct
     */
    public Kiss09State set(Kiss09State src) {
        memCopy(src.address(), address(), SIZEOF);
        return this;
    }

    /**
     * Returns a OpenCL buffer object from this struct.
     */
    public long getClBuffer(long clcontext) {
        return getClBuffer(MemoryStack.stackGet(), clcontext);
    }

    /**
     * Returns a OpenCL buffer object from this struct.
     */
    public long getClBuffer(MemoryStack stack, long clcontext) {
        if (clBuffer == null) {
            try (var s = stack.push()) {
                var err = s.mallocInt(1);
                clBuffer = clCreateBuffer(clcontext, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR, container, err);
                checkCLError(err.get(0));
            }
        }
        return clBuffer;
    }

    // -----------------------------------

    /**
     * Returns a new {@code MappingRanges} instance allocated with
     * {@link MemoryUtil#memAlloc memAlloc}. The instance must be explicitly freed.
     */
    public static Kiss09State malloc() {
        return wrap(Kiss09State.class, nmemAllocChecked(SIZEOF));
    }

    /**
     * Returns a new {@code MappingRanges} instance allocated with
     * {@link MemoryUtil#memCalloc memCalloc}. The instance must be explicitly
     * freed.
     */
    public static Kiss09State calloc() {
        return wrap(Kiss09State.class, nmemCallocChecked(1, SIZEOF));
    }

    /**
     * Returns a new {@code MappingRanges} instance from a {@link ByteBuffer}. The
     * instance must be explicitly freed.
     */
    public static Kiss09State createWithBuffer() {
        var container = BufferUtils.createByteBuffer(SIZEOF);
        return new Kiss09State(container);
    }

    /**
     * Returns a new {@code MappingRanges} instance from a {@link ByteBuffer}
     * allocated with the specified {@link MemoryStack}.
     */
    public static Kiss09State createWithBuffer(MemoryStack stack) {
        var branges = stack.calloc(Kiss09State.SIZEOF);
        return new Kiss09State(branges);
    }

    /**
     * Returns a new {@code MappingRanges} instance allocated with
     * {@link BufferUtils}.
     */
    public static Kiss09State create() {
        ByteBuffer container = BufferUtils.createByteBuffer(SIZEOF);
        return wrap(Kiss09State.class, memAddress(container), container);
    }

    /**
     * Returns a new {@code MappingRanges} instance for the specified memory
     * address.
     */
    public static Kiss09State create(long address) {
        return wrap(Kiss09State.class, address);
    }

    /**
     * Like {@link #create(long) create}, but returns {@code null} if
     * {@code address} is {@code NULL}.
     */
    @Nullable
    public static Kiss09State createSafe(long address) {
        return address == NULL ? null : wrap(Kiss09State.class, address);
    }

    /**
     * Returns a new {@link Kiss09State.Buffer} instance allocated with
     * {@link MemoryUtil#memAlloc memAlloc}. The instance must be explicitly freed.
     *
     * @param capacity the buffer capacity
     */
    public static Kiss09State.Buffer malloc(int capacity) {
        return wrap(Buffer.class, nmemAllocChecked(__checkMalloc(capacity, SIZEOF)), capacity);
    }

    /**
     * Returns a new {@link Kiss09State.Buffer} instance allocated with
     * {@link MemoryUtil#memCalloc memCalloc}. The instance must be explicitly
     * freed.
     *
     * @param capacity the buffer capacity
     */
    public static Kiss09State.Buffer calloc(int capacity) {
        return wrap(Buffer.class, nmemCallocChecked(capacity, SIZEOF), capacity);
    }

    /**
     * Returns a new {@link Kiss09State.Buffer} instance allocated with
     * {@link BufferUtils}.
     *
     * @param capacity the buffer capacity
     */
    public static Kiss09State.Buffer create(int capacity) {
        ByteBuffer container = __create(capacity, SIZEOF);
        return wrap(Buffer.class, memAddress(container), capacity, container);
    }

    /**
     * Create a {@link Kiss09State.Buffer} instance at the specified memory.
     *
     * @param address  the memory address
     * @param capacity the buffer capacity
     */
    public static Kiss09State.Buffer create(long address, int capacity) {
        return wrap(Buffer.class, address, capacity);
    }

    /**
     * Like {@link #create(long, int) create}, but returns {@code null} if
     * {@code address} is {@code NULL}.
     */
    @Nullable
    public static Kiss09State.Buffer createSafe(long address, int capacity) {
        return address == NULL ? null : wrap(Buffer.class, address, capacity);
    }

    // -----------------------------------

    /**
     * Deprecated for removal in 3.4.0. Use {@link #malloc(MemoryStack)} instead.
     */
    @Deprecated
    public static Kiss09State mallocStack() {
        return malloc(stackGet());
    }

    /**
     * Deprecated for removal in 3.4.0. Use {@link #calloc(MemoryStack)} instead.
     */
    @Deprecated
    public static Kiss09State callocStack() {
        return calloc(stackGet());
    }

    /**
     * Deprecated for removal in 3.4.0. Use {@link #malloc(MemoryStack)} instead.
     */
    @Deprecated
    public static Kiss09State mallocStack(MemoryStack stack) {
        return malloc(stack);
    }

    /**
     * Deprecated for removal in 3.4.0. Use {@link #calloc(MemoryStack)} instead.
     */
    @Deprecated
    public static Kiss09State callocStack(MemoryStack stack) {
        return calloc(stack);
    }

    /**
     * Deprecated for removal in 3.4.0. Use {@link #malloc(int, MemoryStack)}
     * instead.
     */
    @Deprecated
    public static Kiss09State.Buffer mallocStack(int capacity) {
        return malloc(capacity, stackGet());
    }

    /**
     * Deprecated for removal in 3.4.0. Use {@link #calloc(int, MemoryStack)}
     * instead.
     */
    @Deprecated
    public static Kiss09State.Buffer callocStack(int capacity) {
        return calloc(capacity, stackGet());
    }

    /**
     * Deprecated for removal in 3.4.0. Use {@link #malloc(int, MemoryStack)}
     * instead.
     */
    @Deprecated
    public static Kiss09State.Buffer mallocStack(int capacity, MemoryStack stack) {
        return malloc(capacity, stack);
    }

    /**
     * Deprecated for removal in 3.4.0. Use {@link #calloc(int, MemoryStack)}
     * instead.
     */
    @Deprecated
    public static Kiss09State.Buffer callocStack(int capacity, MemoryStack stack) {
        return calloc(capacity, stack);
    }

    /**
     * Returns a new {@code MappingRanges} instance allocated on the specified
     * {@link MemoryStack}.
     *
     * @param stack the stack from which to allocate
     */
    public static Kiss09State malloc(MemoryStack stack) {
        return wrap(Kiss09State.class, stack.nmalloc(ALIGNOF, SIZEOF));
    }

    /**
     * Returns a new {@code MappingRanges} instance allocated on the specified
     * {@link MemoryStack} and initializes all its bits to zero.
     *
     * @param stack the stack from which to allocate
     */
    public static Kiss09State calloc(MemoryStack stack) {
        return wrap(Kiss09State.class, stack.ncalloc(ALIGNOF, 1, SIZEOF));
    }

    /**
     * Returns a new {@link Kiss09State.Buffer} instance allocated on the
     * specified {@link MemoryStack}.
     *
     * @param stack    the stack from which to allocate
     * @param capacity the buffer capacity
     */
    public static Kiss09State.Buffer malloc(int capacity, MemoryStack stack) {
        return wrap(Buffer.class, stack.nmalloc(ALIGNOF, capacity * SIZEOF), capacity);
    }

    /**
     * Returns a new {@link Kiss09State.Buffer} instance allocated on the
     * specified {@link MemoryStack} and initializes all its bits to zero.
     *
     * @param stack    the stack from which to allocate
     * @param capacity the buffer capacity
     */
    public static Kiss09State.Buffer calloc(int capacity, MemoryStack stack) {
        return wrap(Buffer.class, stack.ncalloc(ALIGNOF, capacity, SIZEOF), capacity);
    }

    // -----------------------------------

    /**
     * Unsafe version of {@link #getX()}.
     */
    public static long getX(long struct) {
        return UNSAFE.getLong(null, struct + Kiss09State.X);
    }

    /**
     * Unsafe version of {@link #getC()}.
     */
    public static long getC(long struct) {
        return UNSAFE.getLong(null, struct + Kiss09State.C);
    }

    /**
     * Unsafe version of {@link #getY()}.
     */
    public static long getY(long struct) {
        return UNSAFE.getLong(null, struct + Kiss09State.Y);
    }

    /**
     * Unsafe version of {@link #getZ()}.
     */
    public static long getZ(long struct) {
        return UNSAFE.getLong(null, struct + Kiss09State.Z);
    }

    /**
     * Unsafe version of {@link #setX(float)}.
     */
    public static void setX(long struct, long value) {
        UNSAFE.putFloat(null, struct + Kiss09State.X, value);
    }

    /**
     * Unsafe version of {@link #setC(float)}.
     */
    public static void setC(long struct, long value) {
        UNSAFE.putFloat(null, struct + Kiss09State.C, value);
    }

    /**
     * Unsafe version of {@link #setY(float)}.
     */
    public static void setY(long struct, long value) {
        UNSAFE.putFloat(null, struct + Kiss09State.Y, value);
    }

    /**
     * Unsafe version of {@link #setZ(float)}.
     */
    public static void setZ(long struct, long value) {
        UNSAFE.putFloat(null, struct + Kiss09State.Z, value);
    }

    // -----------------------------------

    /**
     * An array of {@link Kiss09State} structs.
     */
    public static class Buffer extends StructBuffer<Kiss09State, Buffer> implements NativeResource {

        private static final Kiss09State ELEMENT_FACTORY = Kiss09State.create(-1L);

        /**
         * Creates a new {@code MappingRanges.Buffer} instance backed by the specified
         * container.
         *
         * Changes to the container's content will be visible to the struct buffer
         * instance and vice versa. The two buffers' position, limit, and mark values
         * will be independent. The new buffer's position will be zero, its capacity and
         * its limit will be the number of bytes remaining in this buffer divided by
         * {@link Kiss09State#SIZEOF}, and its mark will be undefined.
         *
         * <p>
         * The created buffer instance holds a strong reference to the container object.
         * </p>
         */
        public Buffer(ByteBuffer container) {
            super(container, container.remaining() / SIZEOF);
        }

        public Buffer(long address, int cap) {
            super(address, null, -1, 0, cap, cap);
        }

        Buffer(long address, @Nullable ByteBuffer container, int mark, int pos, int lim, int cap) {
            super(address, container, mark, pos, lim, cap);
        }

        @Override
        protected Buffer self() {
            return this;
        }

        @Override
        protected Kiss09State getElementFactory() {
            return ELEMENT_FACTORY;
        }

        /**
         * @return the value of the {@link Kiss09State#getX()} field.
         */
        public float getMapx0() {
            return Kiss09State.getX(address());
        }

        /**
         * @return the value of the {@link Kiss09State#getC()} field.
         */
        public float getMapy0() {
            return Kiss09State.getX(address());
        }

        /**
         * @return the value of the {@link Kiss09State#getY()} field.
         */
        public float getMapz0() {
            return Kiss09State.getX(address());
        }

        /**
         * @return the value of the {@link Kiss09State#getX()} field.
         */
        public float getMapx1() {
            return Kiss09State.getZ(address());
        }

        /**
         * @return the value of the {@link Kiss09State#getMapy1()} field.
         */
        public float getMapy1() {
            return Kiss09State.getZ(address());
        }

        /**
         * @return the value of the {@link Kiss09State#getMapz1()} field.
         */
        public float getMapz1() {
            return Kiss09State.getZ(address());
        }

    }

}