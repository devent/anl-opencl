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
 *
 *
 * ****************************************************************************
 * ANL-OpenCL :: JME3 - OpenCL bundles and uses the RandomCL library:
 * https://github.com/bstatcomp/RandomCL
 * ****************************************************************************
 *
 * BSD 3-Clause License
 *
 * Copyright (c) 2018, Tadej Ciglarič, Erik Štrumbelj, Rok Češnovar. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of the copyright holder nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
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
 * <code>SMappingRanges</code>
 *
 * <h3>Layout</h3>
 *
 * <pre>
 * struct SMappingRanges {
 *     float mapx0, mapy0, mapz0, mapx1, mapy1, mapz1;
 *     float loopx0, loopy0, loopz0, loopx1, loopy1, loopz1;
 * };
 * </pre>
 */
public class MappingRanges extends Struct implements NativeResource {

    /** The struct size in bytes. */
    public static final int SIZEOF;

    /** The struct alignment in bytes. */
    public static final int ALIGNOF;

    /** The struct member offsets. */
    public static final int MAPX0, MAPY0, MAPZ0, MAPX1, MAPY1, MAPZ1, LOOPX0, LOOPY0, LOOPZ0, LOOPX1, LOOPY1, LOOPZ1;

    static {
        Layout layout = __struct( //
                __member(4), // mapx0
                __member(4), // mapy0
                __member(4), // mapz0
                __member(4), // mapx1
                __member(4), // mapy1
                __member(4), // mapz1
                __member(4), // loopx0
                __member(4), // loopy0
                __member(4), // loopz0
                __member(4), // loopx1
                __member(4), // loopy1
                __member(4) // loopz1
        );

        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();

        int i = 0;
        MAPX0 = layout.offsetof(i++);
        MAPY0 = layout.offsetof(i++);
        MAPZ0 = layout.offsetof(i++);
        MAPX1 = layout.offsetof(i++);
        MAPY1 = layout.offsetof(i++);
        MAPZ1 = layout.offsetof(i++);
        LOOPX0 = layout.offsetof(i++);
        LOOPY0 = layout.offsetof(i++);
        LOOPZ0 = layout.offsetof(i++);
        LOOPX1 = layout.offsetof(i++);
        LOOPY1 = layout.offsetof(i++);
        LOOPZ1 = layout.offsetof(i++);
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
    public MappingRanges(ByteBuffer container) {
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
     * Returns <code>mapx0.</code>
     */
    public float getMapx0() {
        return getMapx0(address());
    }

    /**
     * Returns <code>mapy0.</code>
     */
    public float getMapy0() {
        return getMapy0(address());
    }

    /**
     * Returns <code>mapz0.</code>
     */
    public float getMapz0() {
        return getMapz0(address());
    }

    /**
     * Returns <code>mapx1.</code>
     */
    public float getMapx1() {
        return getMapx1(address());
    }

    /**
     * Returns <code>mapy1.</code>
     */
    public float getMapy1() {
        return getMapy1(address());
    }

    /**
     * Returns <code>mapz1.</code>
     */
    public float getMapz1() {
        return getMapz1(address());
    }

    /**
     * Returns <code>loopx0.</code>
     */
    public float getLoopx0() {
        return getLoopx0(address());
    }

    /**
     * Returns <code>loopy0.</code>
     */
    public float getLoopy0() {
        return getLoopy0(address());
    }

    /**
     * Returns <code>loopz0.</code>
     */
    public float getLoopz0() {
        return getLoopz0(address());
    }

    /**
     * Returns <code>loopx1.</code>
     */
    public float getLoopx1() {
        return getLoopx1(address());
    }

    /**
     * Returns <code>loopy1.</code>
     */
    public float getLoopy1() {
        return getLoopy1(address());
    }

    /**
     * Returns <code>loopz1.</code>
     */
    public float getLoopz1() {
        return getLoopz1(address());
    }

    /**
     * Sets <code>mapx0.</code>
     */
    public MappingRanges setMapx0(float mapx0) {
        setMapx0(address(), mapx0);
        return this;
    }

    /**
     * Sets <code>mapy0.</code>
     */
    public MappingRanges setMapy0(float mapy0) {
        setMapy0(address(), mapy0);
        return this;
    }

    /**
     * Sets <code>mapz0.</code>
     */
    public MappingRanges setMapz0(float mapz0) {
        setMapz0(address(), mapz0);
        return this;
    }

    /**
     * Sets <code>mapx1.</code>
     */
    public MappingRanges setMapx1(float mapx1) {
        setMapx1(address(), mapx1);
        return this;
    }

    /**
     * Sets <code>mapy1.</code>
     */
    public MappingRanges setMapy1(float mapy1) {
        setMapy1(address(), mapy1);
        return this;
    }

    /**
     * Sets <code>mapz1.</code>
     */
    public MappingRanges setMapz1(float mapz1) {
        setMapz1(address(), mapz1);
        return this;
    }

    /**
     * Sets <code>loopx0.</code>
     */
    public MappingRanges setLoopx0(float loopx0) {
        setLoopx0(address(), loopx0);
        return this;
    }

    /**
     * Sets <code>loopy0.</code>
     */
    public MappingRanges setLoopy0(float loopy0) {
        setLoopy0(address(), loopy0);
        return this;
    }

    /**
     * Sets <code>loopz0.</code>
     */
    public MappingRanges setLoopz0(float loopz0) {
        setLoopz0(address(), loopz0);
        return this;
    }

    /**
     * Sets <code>loopx1.</code>
     */
    public MappingRanges setLoopx1(float loopx1) {
        setLoopx1(address(), loopx1);
        return this;
    }

    /**
     * Sets <code>loopy1.</code>
     */
    public MappingRanges setLoopy1(float loopy1) {
        setLoopy1(address(), loopy1);
        return this;
    }

    /**
     * Sets <code>loopz1.</code>
     */
    public MappingRanges setLoopz1(float loopz1) {
        setLoopz1(address(), loopz1);
        return this;
    }

    /**
     * Initializes this struct with the specified values.
     */
    public MappingRanges set(float mapx0, float mapy0, float mapz0, float mapx1, float mapy1, float mapz1, float loopx0,
            float loopy0, float loopz0, float loopx1, float loopy1, float loopz1) {
        setMapx0(mapx0);
        setMapy0(mapy0);
        setMapz0(mapz0);
        setMapx1(mapx1);
        setMapy1(mapy1);
        setMapz1(mapz1);
        setLoopx0(loopx0);
        setLoopy0(loopy0);
        setLoopz0(loopz0);
        setLoopx1(loopx1);
        setLoopy1(loopy1);
        setLoopz1(loopz1);
        return this;
    }

    /**
     * Initializes this struct with ranges from [-1..1] in all 3 dimensions.
     */
    public MappingRanges setDefault() {
        setMapx0(-1);
        setMapy0(-1);
        setMapz0(-1);
        setMapx1(1);
        setMapy1(1);
        setMapz1(1);
        setLoopx0(-1);
        setLoopy0(-1);
        setLoopz0(-1);
        setLoopx1(1);
        setLoopy1(1);
        setLoopz1(1);
        return this;
    }

    /**
     * Initializes this struct with ranges for the x-y dimensions and z=[0..1].
     */
    public MappingRanges setMap2D(float x0, float x1, float y0, float y1) {
        setMapx0(x0);
        setMapy0(y0);
        setMapz0(0);
        setMapx1(x1);
        setMapy1(y1);
        setMapz1(1);
        setLoopx0(x0);
        setLoopy0(y0);
        setLoopz0(0);
        setLoopx1(x1);
        setLoopy1(y1);
        setLoopz1(1);
        return this;
    }

    /**
     * Initializes this struct with ranges for the 3 dimensions.
     */
    public MappingRanges setMap3D(float x0, float x1, float y0, float y1, float z0, float z1) {
        setMapx0(x0);
        setMapy0(y0);
        setMapz0(z0);
        setMapx1(x1);
        setMapy1(y1);
        setMapz1(z1);
        setLoopx0(x0);
        setLoopy0(y0);
        setLoopz0(z0);
        setLoopx1(x1);
        setLoopy1(y1);
        setLoopz1(z1);
        return this;
    }

    /**
     * Copies the specified struct data to this struct.
     *
     * @param src the source struct
     * @return this struct
     */
    public MappingRanges set(MappingRanges src) {
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
    public static MappingRanges malloc() {
        return wrap(MappingRanges.class, nmemAllocChecked(SIZEOF));
    }

    /**
     * Returns a new {@code MappingRanges} instance allocated with
     * {@link MemoryUtil#memCalloc memCalloc}. The instance must be explicitly
     * freed.
     */
    public static MappingRanges calloc() {
        return wrap(MappingRanges.class, nmemCallocChecked(1, SIZEOF));
    }

    /**
     * Returns a new {@code MappingRanges} instance from a {@link ByteBuffer}. The
     * instance must be explicitly freed.
     */
    public static MappingRanges createWithBuffer() {
        var container = BufferUtils.createByteBuffer(SIZEOF);
        return new MappingRanges(container);
    }

    /**
     * Returns a new {@code MappingRanges} instance from a {@link ByteBuffer}
     * allocated with the specified {@link MemoryStack}.
     */
    public static MappingRanges createWithBuffer(MemoryStack stack) {
        var branges = stack.calloc(MappingRanges.SIZEOF);
        return new MappingRanges(branges);
    }

    /**
     * Returns a new {@code MappingRanges} instance allocated with
     * {@link BufferUtils}.
     */
    public static MappingRanges create() {
        ByteBuffer container = BufferUtils.createByteBuffer(SIZEOF);
        return wrap(MappingRanges.class, memAddress(container), container);
    }

    /**
     * Returns a new {@code MappingRanges} instance for the specified memory
     * address.
     */
    public static MappingRanges create(long address) {
        return wrap(MappingRanges.class, address);
    }

    /**
     * Like {@link #create(long) create}, but returns {@code null} if
     * {@code address} is {@code NULL}.
     */
    @Nullable
    public static MappingRanges createSafe(long address) {
        return address == NULL ? null : wrap(MappingRanges.class, address);
    }

    /**
     * Returns a new {@link MappingRanges.Buffer} instance allocated with
     * {@link MemoryUtil#memAlloc memAlloc}. The instance must be explicitly freed.
     *
     * @param capacity the buffer capacity
     */
    public static MappingRanges.Buffer malloc(int capacity) {
        return wrap(Buffer.class, nmemAllocChecked(__checkMalloc(capacity, SIZEOF)), capacity);
    }

    /**
     * Returns a new {@link MappingRanges.Buffer} instance allocated with
     * {@link MemoryUtil#memCalloc memCalloc}. The instance must be explicitly
     * freed.
     *
     * @param capacity the buffer capacity
     */
    public static MappingRanges.Buffer calloc(int capacity) {
        return wrap(Buffer.class, nmemCallocChecked(capacity, SIZEOF), capacity);
    }

    /**
     * Returns a new {@link MappingRanges.Buffer} instance allocated with
     * {@link BufferUtils}.
     *
     * @param capacity the buffer capacity
     */
    public static MappingRanges.Buffer create(int capacity) {
        ByteBuffer container = __create(capacity, SIZEOF);
        return wrap(Buffer.class, memAddress(container), capacity, container);
    }

    /**
     * Create a {@link MappingRanges.Buffer} instance at the specified memory.
     *
     * @param address  the memory address
     * @param capacity the buffer capacity
     */
    public static MappingRanges.Buffer create(long address, int capacity) {
        return wrap(Buffer.class, address, capacity);
    }

    /**
     * Like {@link #create(long, int) create}, but returns {@code null} if
     * {@code address} is {@code NULL}.
     */
    @Nullable
    public static MappingRanges.Buffer createSafe(long address, int capacity) {
        return address == NULL ? null : wrap(Buffer.class, address, capacity);
    }

    // -----------------------------------

    /**
     * Deprecated for removal in 3.4.0. Use {@link #malloc(MemoryStack)} instead.
     */
    @Deprecated
    public static MappingRanges mallocStack() {
        return malloc(stackGet());
    }

    /**
     * Deprecated for removal in 3.4.0. Use {@link #calloc(MemoryStack)} instead.
     */
    @Deprecated
    public static MappingRanges callocStack() {
        return calloc(stackGet());
    }

    /**
     * Deprecated for removal in 3.4.0. Use {@link #malloc(MemoryStack)} instead.
     */
    @Deprecated
    public static MappingRanges mallocStack(MemoryStack stack) {
        return malloc(stack);
    }

    /**
     * Deprecated for removal in 3.4.0. Use {@link #calloc(MemoryStack)} instead.
     */
    @Deprecated
    public static MappingRanges callocStack(MemoryStack stack) {
        return calloc(stack);
    }

    /**
     * Deprecated for removal in 3.4.0. Use {@link #malloc(int, MemoryStack)}
     * instead.
     */
    @Deprecated
    public static MappingRanges.Buffer mallocStack(int capacity) {
        return malloc(capacity, stackGet());
    }

    /**
     * Deprecated for removal in 3.4.0. Use {@link #calloc(int, MemoryStack)}
     * instead.
     */
    @Deprecated
    public static MappingRanges.Buffer callocStack(int capacity) {
        return calloc(capacity, stackGet());
    }

    /**
     * Deprecated for removal in 3.4.0. Use {@link #malloc(int, MemoryStack)}
     * instead.
     */
    @Deprecated
    public static MappingRanges.Buffer mallocStack(int capacity, MemoryStack stack) {
        return malloc(capacity, stack);
    }

    /**
     * Deprecated for removal in 3.4.0. Use {@link #calloc(int, MemoryStack)}
     * instead.
     */
    @Deprecated
    public static MappingRanges.Buffer callocStack(int capacity, MemoryStack stack) {
        return calloc(capacity, stack);
    }

    /**
     * Returns a new {@code MappingRanges} instance allocated on the specified
     * {@link MemoryStack}.
     *
     * @param stack the stack from which to allocate
     */
    public static MappingRanges malloc(MemoryStack stack) {
        return wrap(MappingRanges.class, stack.nmalloc(ALIGNOF, SIZEOF));
    }

    /**
     * Returns a new {@code MappingRanges} instance allocated on the specified
     * {@link MemoryStack} and initializes all its bits to zero.
     *
     * @param stack the stack from which to allocate
     */
    public static MappingRanges calloc(MemoryStack stack) {
        return wrap(MappingRanges.class, stack.ncalloc(ALIGNOF, 1, SIZEOF));
    }

    /**
     * Returns a new {@link MappingRanges.Buffer} instance allocated on the
     * specified {@link MemoryStack}.
     *
     * @param stack    the stack from which to allocate
     * @param capacity the buffer capacity
     */
    public static MappingRanges.Buffer malloc(int capacity, MemoryStack stack) {
        return wrap(Buffer.class, stack.nmalloc(ALIGNOF, capacity * SIZEOF), capacity);
    }

    /**
     * Returns a new {@link MappingRanges.Buffer} instance allocated on the
     * specified {@link MemoryStack} and initializes all its bits to zero.
     *
     * @param stack    the stack from which to allocate
     * @param capacity the buffer capacity
     */
    public static MappingRanges.Buffer calloc(int capacity, MemoryStack stack) {
        return wrap(Buffer.class, stack.ncalloc(ALIGNOF, capacity, SIZEOF), capacity);
    }

    // -----------------------------------

    /**
     * Unsafe version of {@link #getMapx0()}.
     */
    public static float getMapx0(long struct) {
        return UNSAFE.getFloat(null, struct + MappingRanges.MAPX0);
    }

    /**
     * Unsafe version of {@link #getMapy0()}.
     */
    public static float getMapy0(long struct) {
        return UNSAFE.getFloat(null, struct + MappingRanges.MAPY0);
    }

    /**
     * Unsafe version of {@link #getMapz0()}.
     */
    public static float getMapz0(long struct) {
        return UNSAFE.getFloat(null, struct + MappingRanges.MAPZ0);
    }

    /**
     * Unsafe version of {@link #getMapx1()}.
     */
    public static float getMapx1(long struct) {
        return UNSAFE.getFloat(null, struct + MappingRanges.MAPX1);
    }

    /**
     * Unsafe version of {@link #getMapy1()}.
     */
    public static float getMapy1(long struct) {
        return UNSAFE.getFloat(null, struct + MappingRanges.MAPY1);
    }

    /**
     * Unsafe version of {@link #getMapz1()}.
     */
    public static float getMapz1(long struct) {
        return UNSAFE.getFloat(null, struct + MappingRanges.MAPZ1);
    }

    /**
     * Unsafe version of {@link #getLoopx0()}.
     */
    public static float getLoopx0(long struct) {
        return UNSAFE.getFloat(null, struct + MappingRanges.LOOPX0);
    }

    /**
     * Unsafe version of {@link #getLoopy0()}.
     */
    public static float getLoopy0(long struct) {
        return UNSAFE.getFloat(null, struct + MappingRanges.LOOPY0);
    }

    /**
     * Unsafe version of {@link #getLoopz0()}.
     */
    public static float getLoopz0(long struct) {
        return UNSAFE.getFloat(null, struct + MappingRanges.LOOPZ0);
    }

    /**
     * Unsafe version of {@link #getLoopx1()}.
     */
    public static float getLoopx1(long struct) {
        return UNSAFE.getFloat(null, struct + MappingRanges.LOOPX1);
    }

    /**
     * Unsafe version of {@link #getLoopy1()}.
     */
    public static float getLoopy1(long struct) {
        return UNSAFE.getFloat(null, struct + MappingRanges.LOOPY1);
    }

    /**
     * Unsafe version of {@link #getLoopz1()}.
     */
    public static float getLoopz1(long struct) {
        return UNSAFE.getFloat(null, struct + MappingRanges.LOOPZ1);
    }

    /**
     * Unsafe version of {@link #setMapx0(float)}.
     */
    public static void setMapx0(long struct, float value) {
        UNSAFE.putFloat(null, struct + MappingRanges.MAPX0, value);
    }

    /**
     * Unsafe version of {@link #setMapy0(float)}.
     */
    public static void setMapy0(long struct, float value) {
        UNSAFE.putFloat(null, struct + MappingRanges.MAPY0, value);
    }

    /**
     * Unsafe version of {@link #setMapz0(float)}.
     */
    public static void setMapz0(long struct, float value) {
        UNSAFE.putFloat(null, struct + MappingRanges.MAPZ0, value);
    }

    /**
     * Unsafe version of {@link #setMapx1(float)}.
     */
    public static void setMapx1(long struct, float value) {
        UNSAFE.putFloat(null, struct + MappingRanges.MAPX1, value);
    }

    /**
     * Unsafe version of {@link #setMapy1(float)}.
     */
    public static void setMapy1(long struct, float value) {
        UNSAFE.putFloat(null, struct + MappingRanges.MAPY1, value);
    }

    /**
     * Unsafe version of {@link #setMapz1(float)}.
     */
    public static void setMapz1(long struct, float value) {
        UNSAFE.putFloat(null, struct + MappingRanges.MAPZ1, value);
    }

    /**
     * Unsafe version of {@link #setLoopx0(float)}.
     */
    public static void setLoopx0(long struct, float value) {
        UNSAFE.putFloat(null, struct + MappingRanges.LOOPX0, value);
    }

    /**
     * Unsafe version of {@link #setLoopy0(float)}.
     */
    public static void setLoopy0(long struct, float value) {
        UNSAFE.putFloat(null, struct + MappingRanges.LOOPY0, value);
    }

    /**
     * Unsafe version of {@link #setLoopz0(float)}.
     */
    public static void setLoopz0(long struct, float value) {
        UNSAFE.putFloat(null, struct + MappingRanges.LOOPZ0, value);
    }

    /**
     * Unsafe version of {@link #setLoopx1(float)}.
     */
    public static void setLoopx1(long struct, float value) {
        UNSAFE.putFloat(null, struct + MappingRanges.LOOPX1, value);
    }

    /**
     * Unsafe version of {@link #setLoopy1(float)}.
     */
    public static void setLoopy1(long struct, float value) {
        UNSAFE.putFloat(null, struct + MappingRanges.LOOPY1, value);
    }

    /**
     * Unsafe version of {@link #setLoopz1(float)}.
     */
    public static void setLoopz1(long struct, float value) {
        UNSAFE.putFloat(null, struct + MappingRanges.LOOPZ1, value);
    }

    // -----------------------------------

    /**
     * An array of {@link MappingRanges} structs.
     */
    public static class Buffer extends StructBuffer<MappingRanges, Buffer> implements NativeResource {

        private static final MappingRanges ELEMENT_FACTORY = MappingRanges.create(-1L);

        /**
         * Creates a new {@code MappingRanges.Buffer} instance backed by the specified
         * container.
         *
         * Changes to the container's content will be visible to the struct buffer
         * instance and vice versa. The two buffers' position, limit, and mark values
         * will be independent. The new buffer's position will be zero, its capacity and
         * its limit will be the number of bytes remaining in this buffer divided by
         * {@link MappingRanges#SIZEOF}, and its mark will be undefined.
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
        protected MappingRanges getElementFactory() {
            return ELEMENT_FACTORY;
        }

        /**
         * @return the value of the {@link MappingRanges#getMapx0()} field.
         */
        public float getMapx0() {
            return MappingRanges.getMapx0(address());
        }

        /**
         * @return the value of the {@link MappingRanges#getMapy0()} field.
         */
        public float getMapy0() {
            return MappingRanges.getMapx0(address());
        }

        /**
         * @return the value of the {@link MappingRanges#getMapz0()} field.
         */
        public float getMapz0() {
            return MappingRanges.getMapx0(address());
        }

        /**
         * @return the value of the {@link MappingRanges#getMapx0()} field.
         */
        public float getMapx1() {
            return MappingRanges.getMapx1(address());
        }

        /**
         * @return the value of the {@link MappingRanges#getMapy1()} field.
         */
        public float getMapy1() {
            return MappingRanges.getMapx1(address());
        }

        /**
         * @return the value of the {@link MappingRanges#getMapz1()} field.
         */
        public float getMapz1() {
            return MappingRanges.getMapx1(address());
        }

        /**
         * @return the value of the {@link MappingRanges#getLoopx0()} field.
         */
        public float getLoopx0() {
            return MappingRanges.getLoopx0(address());
        }

        /**
         * @return the value of the {@link MappingRanges#getLoopy0()} field.
         */
        public float getLoopy0() {
            return MappingRanges.getLoopx0(address());
        }

        /**
         * @return the value of the {@link MappingRanges#getLoopz0()} field.
         */
        public float getLoopz0() {
            return MappingRanges.getLoopx0(address());
        }

        /**
         * @return the value of the {@link MappingRanges#getLoopx0()} field.
         */
        public float getLoopx1() {
            return MappingRanges.getLoopx1(address());
        }

        /**
         * @return the value of the {@link MappingRanges#getLoopy1()} field.
         */
        public float getLoopy1() {
            return MappingRanges.getLoopx1(address());
        }

        /**
         * @return the value of the {@link MappingRanges#getLoopz1()} field.
         */
        public float getLoopz1() {
            return MappingRanges.getLoopx1(address());
        }
    }

}