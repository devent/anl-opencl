/**
 * Copyright (C) 2021 Erwin Müller <erwin@muellerpublic.de>
 * Released as open-source under the Apache License, Version 2.0.
 *
 * ****************************************************************************
 * ANL-OpenCL :: OpenCL
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
 */
package com.anrisoftware.anlopencl;

import static com.anrisoftware.anlopencl.MappingRanges.createDefaultRanges;
import static com.anrisoftware.anlopencl.SeamlessCalc.seamlessZ;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.concurrent.ForkJoinPool;

import org.junit.jupiter.api.Test;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class Map2DBenchmarkTest {

    @State(Scope.Benchmark)
    public static class ExecutionPlan {

        @Param({ "1024", "2048", "4096", "8192" })
        public int size;

        public Injector injector;

        public Map2DFactory factory;

        public MapArgs args;

        public FloatBuffer outb;

        @Setup(Level.Invocation)
        public void setUp() {
            injector = Guice.createInjector(new MapModule());
            factory = injector.getInstance(Map2DFactory.class);
            args = new MapArgs();
            args.width = size;
            args.height = size;
            args.dim = 4;
            args.seamless = seamlessZ;
            args.ranges = createDefaultRanges();
            args.z = 99;
            args.threadCount = 4;
            var outt = new float[args.width * args.height * args.dim];
            outb = FloatBuffer.wrap(outt);
        }
    }

    @Fork(value = 1, warmups = 1)
    @Benchmark
    @BenchmarkMode(Mode.All)
    public void bench_map2d_with_z(ExecutionPlan plan, Blackhole blackhole) {
        var map = plan.factory.create(plan.outb, plan.args.seamless, plan.args.ranges, plan.args.width,
                plan.args.height, plan.args.z, plan.args.threadCount);
        var commonPool = ForkJoinPool.commonPool();
        commonPool.execute(map);
        map.join();
        blackhole.consume(plan.outb);
    }

    @Test
    public void doBenchmark() throws IOException {
        var args = new String[] {};
        org.openjdk.jmh.Main.main(args);
    }
}
