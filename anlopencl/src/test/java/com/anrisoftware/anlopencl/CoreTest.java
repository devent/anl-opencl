/**
 * Copyright (C) 2021 Erwin Müller <erwin@muellerpublic.de>
 * Released as open-source under the Apache License, Version 2.0.
 *
 * ****************************************************************************
 * ANL-OpenCL :: Core
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
 * ANL-OpenCL :: Core is a derivative work based on Josua Tippetts' C++ library:
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

import static com.sudoplay.joise.noise.Util.clamp;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.aparapi.Kernel;
import com.aparapi.Range;
import com.aparapi.device.Device.TYPE;
import com.aparapi.internal.kernel.KernelManager;
import com.sudoplay.joise.noise.NoiseLUT;

import lombok.RequiredArgsConstructor;

public class CoreTest {

    @BeforeAll
    static void setup_debug() {
        System.setProperty("com.aparapi.enableShowExecutionModes", "true");
        System.setProperty("com.aparapi.enableShowGeneratedOpenCL", "true");
    }

    @RequiredArgsConstructor
    class Billow extends Kernel {

        public final float frequency;

        public final int numOctaves;

        public final float gain;

        public final float lacunarity;

        public final float[][][] out;

        public final byte[] buffer;

        @Override
        public void run() {
        }

    }

    private static final long INIT32 = 0x811c9dc5L;
    private static final int PRIME32 = 0x01000193;

    public static int fnv32ABuf(byte[] buf) {
        long hval = 0x811c9dc5L;
        for (int i = 0; i < buf.length; i++) {
            hval ^= buf[i];
            hval *= 0x01000193;
        }
        return (int) (hval & 0x00000000ffffffffL);
    }

    public static float WorkerNoise3Gradient_calculate(float x, float y, float z, int ix, int iy, int iz, long seed,
            float[][] gradient3DLUT) {
        byte[] buffer = new byte[20];
        return fnv32ABuf(buffer);
    }

    @Test
    void core_bias3() {
        assertThat(KernelManager.instance().bestDevice().getType(), equalTo(TYPE.GPU));
        final int size = 512;
        final float[] a = new float[size];
        final float[] b = new float[size];

        for (int i = 0; i < size; i++) {
            a[i] = (float) Math.random() * 100;
            b[i] = (float) Math.random() * 100;
        }

        final float[] sum = new float[size];

        final float[][] gradient3DLUT = new float[NoiseLUT.gradient3DLUT.length][NoiseLUT.gradient3DLUT[0].length];
        for (int i = 0; i < gradient3DLUT.length; i++) {
            for (int j = 0; j < gradient3DLUT[0].length; j++) {
                gradient3DLUT[i][j] = (float) NoiseLUT.gradient3DLUT[i][j];
            }
        }

        Kernel kernel = new Kernel() {
            @Override
            public void run() {
                int gid = getGlobalId();
                float g = WorkerNoise3Gradient_calculate(0f, 0f, 0f, 1, 1, 1, 10, gradient3DLUT);
                sum[gid] = a[gid] + b[gid] + (float) Math.cos(b[gid]) + (float) clamp(b[gid], b[gid], b[gid]);
            }
        };

        kernel.execute(Range.create(size));

        kernel.dispose();

    }
}
