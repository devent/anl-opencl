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
package com.anrisoftware.anlopencl

import static com.anrisoftware.anlopencl.MappingRanges.createDefaultRanges
import static com.anrisoftware.anlopencl.MappingRanges.createRangesMap2D
import static com.anrisoftware.anlopencl.SeamlessCalc.*
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*
import static org.junit.jupiter.params.provider.Arguments.of

import java.nio.FloatBuffer
import java.util.concurrent.ForkJoinPool
import java.util.stream.Stream

import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

import com.google.inject.Guice
import com.google.inject.Injector

class Map2DTest {

    static Injector injector

    @ParameterizedTest
    @MethodSource
    void "map2d with z"(MapArgs args, def expected) {
        args.with {
            def outt = out
            def outb = FloatBuffer.wrap(outt)
            def map2D = injector.getInstance(Map2DFactory).create(outb, seamless, ranges, width, height, z, threadCount)
            def commonPool = ForkJoinPool.commonPool();
            commonPool.execute(map2D);
            map2D.join();
            for (int i = 0; i < outt.length; i++) {
                assertThat "outt[${i}] == ${expected[i]}", outt[i] as double, closeTo(expected[i] as double, 0.00001 as double)
            }
        }
    }

    static Stream<Arguments> "map2d with z"() {
        Stream.of(
                of(new MapArgs(width: 2, height: 2, dim: 3, seamless: seamlessNone, ranges: createDefaultRanges(), z: 99, threadCount: 1),
                Eval.me("[-1,-1,99, 0,-1,99, -1,0,99,  0,0,99]")),
                of(new MapArgs(width: 2, height: 2, dim: 3, seamless: seamlessNone, ranges: createDefaultRanges(), z: 99, threadCount: 2),
                Eval.me("[-1,-1,99, 0,-1,99, -1,0,99,  0,0,99]")),
                of(new MapArgs(width: 4, height: 4, dim: 3, seamless: seamlessNone, ranges: createDefaultRanges(), z: 99, threadCount: 2),
                Eval.me("[-1.0, -1.0, 99.0, -0.5, -1.0, 99.0, 0.0, -1.0, 99.0, 0.5, -1.0, 99.0, -1.0, -0.5, 99.0, -0.5, -0.5, 99.0, 0.0, -0.5, 99.0, 0.5, -0.5, 99.0, -1.0, 0.0, 99.0, -0.5, 0.0, 99.0, 0.0, 0.0, 99.0, 0.5, 0.0, 99.0, -1.0, 0.5, 99.0, -0.5, 0.5, 99.0, 0.0, 0.5, 99.0, 0.5, 0.5, 99.0]")),
                of(new MapArgs(width: 4, height: 4, dim: 3, seamless: seamlessNone, ranges: createRangesMap2D(-10, 10, -10, 10), z: 99, threadCount: 2),
                Eval.me("[-10.0,-10.0,99.0, -5.0,-10.0,99.0, 0.0,-10,99.0, 5.0,-10.0,99.0, -10.0,-5.0,99.0, -5.0,-5.0,99.0, 0.0,-5.0,99.0, 5.0,-5.0,99.0, -10.0,0.0,99.0, -5.0,0.0,99.0, 0.0,0.0,99.0, 5.0,0.0,99.0, -10.0,5.0,99.0, -5.0,5.0,99.0, 0.0,5.0,99.0, 5.0,5.0,99.0]")),
                of(new MapArgs(width: 2, height: 2, dim: 4, seamless: seamlessX, ranges: createDefaultRanges(), z: 99, threadCount: 2),
                Eval.me("[-0.68169,-1.00000,-1.00000,99.00000, -1.31831,-1.00000,-1.00000,99.00000, -0.68169,-1.00000,0.00000,99.00000, -1.31831,-1.00000,0.00000,99.00000]")),
                of(new MapArgs(width: 2, height: 2, dim: 4, seamless: seamlessY, ranges: createDefaultRanges(), z: 99, threadCount: 2),
                Eval.me("[-1.00000,-0.68169,-1.00000,99.00000, 0.00000,-0.68169,-1.00000,99.00000, -1.00000,-1.31831,-1.00000,99.00000, 0.00000,-1.31831,-1.00000,99.00000]")),
                of(new MapArgs(width: 2, height: 2, dim: 4, seamless: seamlessZ, ranges: createDefaultRanges(), z: 99, threadCount: 2),
                Eval.me("[-1.000000,-1.000000,-0.681690,-1.000021, 0.000000,0.000000,-0.681690,-1.000021, -1.000000,-1.000000,-0.681690,-1.000021, 0.000000,0.000000,-0.681690,-1.000021]")),
                )
    }

    @BeforeAll
    static void setup() {
        injector = Guice.createInjector(new MapModule())
    }
}
