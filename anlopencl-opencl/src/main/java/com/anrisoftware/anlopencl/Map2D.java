/*
 * Copyright (C) 2021-2022 Erwin Müller <erwin@muellerpublic.de>
 * Released as open-source under the Apache License, Version 2.0.
 *
 * ****************************************************************************
 * ANL-OpenCL :: OpenCL
 * ****************************************************************************
 *
 * Copyright (C) 2021-2022 Erwin Müller <erwin@muellerpublic.de>
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
 * ANL-OpenCL :: OpenCL is a derivative work based on Josua Tippetts' C++ library:
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
 * ANL-OpenCL :: OpenCL bundles and uses the RandomCL library:
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
package com.anrisoftware.anlopencl;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveAction;

import javax.inject.Inject;

import com.google.inject.assistedinject.Assisted;

/**
 * Maps the {@link MappingRanges} with a {@link SeamlessCalc} seamless mode.
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
@SuppressWarnings("serial")
public class Map2D extends RecursiveAction {

    private final Chunk chunk;

    private final MappingRanges ranges;

    private int threadCount;

    private boolean processing;

    @Inject
    public Map2D(@Assisted FloatBuffer out, @Assisted SeamlessCalc seamless, @Assisted MappingRanges ranges,
            @Assisted("width") int width, @Assisted("height") int height, @Assisted float z,
            @Assisted("threadCount") int threadCount) {
        this(new Chunk(seamless, out, width, height, height, 0, ranges, z), ranges, threadCount);
        this.processing = false;
    }

    public Map2D(Chunk chunk, MappingRanges ranges, int threadCount) {
        this.threadCount = threadCount;
        this.chunk = chunk;
        this.ranges = ranges;
        this.processing = true;
    }

    @Override
    protected void compute() {
        if (processing) {
            processing();
        } else {
            int chunksize = (int) Math.floor(chunk.height / threadCount);
            if (chunksize == 0) {
                chunksize = chunk.height;
                threadCount = 1;
            }
            ForkJoinTask.invokeAll(createSubtasks(chunksize));
        }
    }

    private Collection<Map2D> createSubtasks(int chunksize) {
        var subtasks = new ArrayList<Map2D>();
        for (int thread = 0; thread < threadCount; ++thread) {
            int offsety = thread * chunksize;
            int chunkheight;
            if (thread == threadCount - 1) {
                chunkheight = chunk.height - (chunksize * (threadCount - 1));
            } else {
                chunkheight = chunksize;
            }
            int chunkyoffset = offsety;
            var achunk = chunk.clone(chunkheight, chunkyoffset);
            subtasks.add(new Map2D(achunk, ranges, threadCount));
        }
        return subtasks;
    }

    private void processing() {
        for (int x = 0; x < chunk.width; ++x) {
            for (int y = 0; y < chunk.chunkheight; ++y) {
                int realy = y + chunk.chunkyoffset;
                int index = chunk.chunkyoffset * chunk.height + y * chunk.width + x;
                float p = x / (float) (chunk.width);
                float q = realy / (float) (chunk.height);
                chunk.seamless.call(chunk.out, index, x, y, p, q, chunk, ranges);
            }
        }
    }

}
