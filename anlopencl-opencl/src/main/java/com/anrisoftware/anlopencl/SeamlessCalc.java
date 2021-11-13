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

import static java.lang.Math.cos;
import static java.lang.Math.sin;

import java.nio.FloatBuffer;

/**
 * {@link FunctionalInterface} to calculate the coordinates for a seamless mode.
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
@FunctionalInterface
public interface SeamlessCalc {

    /**
     * Calculates the coordinates and stored them in the output buffer.
     *
     * @param out the output {@link FloatBuffer} for the calculated coordinates.
     */
    void call(FloatBuffer out, int index, int x, int y, float p, float q, Chunk chunk, MappingRanges ranges);

    /**
     * Pre-calculated value of pi*2.
     */
    public static float PI2 = 2.0f * 3.141592f;

    /**
     * Enumerates the seamless modes. Returns the {@link SeamlessCalc} and the
     * number of dimension for the output buffer.
     *
     * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
     */
    public enum SeamlessMode {

        NONE(SeamlessCalc.seamlessNone, 3),

        X(SeamlessCalc.seamlessX, 4),

        Y(SeamlessCalc.seamlessY, 4),

        Z(SeamlessCalc.seamlessZ, 4),

        XY(SeamlessCalc.seamlessXy, 8),

        XZ(SeamlessCalc.seamlessXz, 8),

        YZ(SeamlessCalc.seamlessYz, 8),

        XYZ(SeamlessCalc.seamlessXyz, 8),

        ;

        /**
         * The {@link SeamlessCalc}.
         */
        public final SeamlessCalc seamless;

        /**
         * The number of dimension for the output buffer.
         */
        public final int dim;

        private SeamlessMode(SeamlessCalc seamless, int dim) {
            this.seamless = seamless;
            this.dim = dim;
        }

    }

    /**
     * Returns a {@link SeamlessCalc} that calculates the seamless none mode. Needs
     * 3 floats available in the output buffer.
     */
    public static SeamlessCalc seamlessNone = (out, index, x, y, p, q, chunk, ranges) -> {
        float nx = ranges.mapx0 + p * (ranges.mapx1 - ranges.mapx0);
        float ny = ranges.mapy0 + q * (ranges.mapy1 - ranges.mapy0);
        float nz = chunk.z;
        out.put(index * 3 + 0, nx);
        out.put(index * 3 + 1, ny);
        out.put(index * 3 + 2, nz);
    };

    /**
     * Returns a {@link SeamlessCalc} that calculates the seamless X mode. Needs 4
     * floats available in the output buffer.
     */
    public static SeamlessCalc seamlessX = (out, index, x, y, p, q, chunk, ranges) -> {
        float dx = ranges.loopx1 - ranges.loopx0;
        float dy = ranges.mapy1 - ranges.mapy0;
        p = p * (ranges.mapx1 - ranges.mapx0) / (ranges.loopx1 - ranges.loopx0);
        float nx = ranges.loopx0 + (float) cos(p * PI2) * dx / PI2;
        float ny = ranges.loopx0 + (float) sin(p * PI2) * dx / PI2;
        float nz = ranges.mapy0 + q * dy;
        float nw = chunk.z;
        out.put(index * 4 + 0, nx);
        out.put(index * 4 + 1, ny);
        out.put(index * 4 + 2, nz);
        out.put(index * 4 + 3, nw);
    };

    /**
     * Returns a {@link SeamlessCalc} that calculates the seamless Y mode. Needs 4
     * floats available in the output buffer.
     */
    public static SeamlessCalc seamlessY = (out, index, x, y, p, q, chunk, ranges) -> {
        float dx = ranges.mapx1 - ranges.mapx0;
        float dy = ranges.loopy1 - ranges.loopy0;
        q = q * (ranges.mapy1 - ranges.mapy0) / (ranges.loopy1 - ranges.loopy0);
        float nx = ranges.mapx0 + p * dx;
        float ny = ranges.loopy0 + (float) cos(q * PI2) * dy / PI2;
        float nz = ranges.loopy0 + (float) sin(q * PI2) * dy / PI2;
        float nw = chunk.z;
        out.put(index * 4 + 0, nx);
        out.put(index * 4 + 1, ny);
        out.put(index * 4 + 2, nz);
        out.put(index * 4 + 3, nw);
    };

    /**
     * Returns a {@link SeamlessCalc} that calculates the seamless Z mode. Needs 4
     * floats available in the output buffer.
     */
    public static SeamlessCalc seamlessZ = (out, index, x, y, p, q, chunk, ranges) -> {
        float dx = ranges.mapx1 - ranges.mapx0;
        float dz = ranges.loopz1 - ranges.loopz0;
        float nx = ranges.mapx0 + p * dx;
        float ny = ranges.mapy0 + p * dx;
        float r = (chunk.z - ranges.mapz0) / (ranges.mapz1 - ranges.mapz0);
        float zval = r * (ranges.mapz1 - ranges.mapz0) / (ranges.loopz1 - ranges.loopz0);
        float nz = ranges.loopz0 + (float) cos(zval * PI2) * dz / PI2;
        float nw = ranges.loopz0 + (float) sin(zval * PI2) * dz / PI2;
        out.put(index * 4 + 0, nx);
        out.put(index * 4 + 1, ny);
        out.put(index * 4 + 2, nz);
        out.put(index * 4 + 3, nw);
    };

    /**
     * Returns a {@link SeamlessCalc} that calculates the X-Y mode. Needs 8 floats
     * available in the output buffer.
     */
    public static SeamlessCalc seamlessXy = (out, index, x, y, p, q, chunk, ranges) -> {
        float dx = ranges.loopx1 - ranges.loopx0;
        float dy = ranges.loopy1 - ranges.loopy0;
        p = p * (ranges.mapx1 - ranges.mapx0) / (ranges.loopx1 - ranges.loopx0);
        q = q * (ranges.mapy1 - ranges.mapy0) / (ranges.loopy1 - ranges.loopy0);
        float nx = ranges.loopx0 + (float) cos(p * PI2) * dx / PI2;
        float ny = ranges.loopx0 + (float) sin(p * PI2) * dx / PI2;
        float nz = ranges.loopy0 + (float) cos(q * PI2) * dy / PI2;
        float nw = ranges.loopy0 + (float) sin(q * PI2) * dy / PI2;
        float nv = chunk.z;
        float nu = 0;
        out.put(index * 8 + 0, nx);
        out.put(index * 8 + 1, ny);
        out.put(index * 8 + 2, nz);
        out.put(index * 8 + 3, nw);
        out.put(index * 8 + 4, nv);
        out.put(index * 8 + 5, nu);
        out.put(index * 8 + 6, 0);
        out.put(index * 8 + 7, 0);
    };

    /**
     * Returns a {@link SeamlessCalc} that calculates the seamless X-Z mode. Needs 8
     * floats available in the output buffer.
     */
    public static SeamlessCalc seamlessXz = (out, index, x, y, p, q, chunk, ranges) -> {
        float dx = ranges.loopx1 - ranges.loopx0;
        float dy = ranges.mapy1 - ranges.mapy0;
        float dz = ranges.loopz1 - ranges.loopz0;
        float r = (chunk.z - ranges.mapz0) / (ranges.mapz1 - ranges.mapz0);
        float zval = r * (ranges.mapx1 - ranges.mapz0) / (ranges.loopz1 - ranges.loopz0);
        p = p * (ranges.mapx1 - ranges.mapx0) / (ranges.loopx1 - ranges.loopx0);
        float nx = ranges.loopx0 + (float) cos(p * PI2) * dx / PI2;
        float ny = ranges.loopx0 + (float) sin(p * PI2) * dx / PI2;
        float nz = ranges.mapy0 + q * dy;
        float nw = ranges.loopz0 + (float) cos(zval * PI2) * dz / PI2;
        float nv = ranges.loopz0 + (float) sin(zval * PI2) * dz / PI2;
        float nu = 0;
        out.put(index * 8 + 0, nx);
        out.put(index * 8 + 1, ny);
        out.put(index * 8 + 2, nz);
        out.put(index * 8 + 3, nw);
        out.put(index * 8 + 4, nv);
        out.put(index * 8 + 5, nu);
        out.put(index * 8 + 6, 0);
        out.put(index * 8 + 7, 0);
    };

    /**
     * Returns a {@link SeamlessCalc} that calculates the seamless Y-Z mode. Needs 8
     * floats available in the output buffer.
     */
    public static SeamlessCalc seamlessYz = (out, index, x, y, p, q, chunk, ranges) -> {
        float dx = ranges.mapx1 - ranges.mapx0;
        float dy = ranges.loopy1 - ranges.loopy0;
        float dz = ranges.loopz1 - ranges.loopz0;
        float r = (chunk.z - ranges.mapz0) / (ranges.mapz1 - ranges.mapz0);
        float zval = r * (ranges.mapz1 - ranges.mapz0) / (ranges.loopz1 - ranges.loopz0);
        q = q * (ranges.mapy1 - ranges.mapy0) / (ranges.loopy1 - ranges.loopy0);
        float nx = ranges.mapx0 + p * dx;
        float ny = ranges.loopy0 + (float) cos(q * PI2) * dy / PI2;
        float nz = ranges.loopy0 + (float) sin(q * PI2) * dy / PI2;
        float nw = ranges.loopz0 + (float) cos(zval * PI2) * dz / PI2;
        float nv = ranges.loopz0 + (float) sin(zval * PI2) * dz / PI2;
        float nu = 0;
        out.put(index * 8 + 0, nx);
        out.put(index * 8 + 1, ny);
        out.put(index * 8 + 2, nz);
        out.put(index * 8 + 3, nw);
        out.put(index * 8 + 4, nv);
        out.put(index * 8 + 5, nu);
        out.put(index * 8 + 6, 0);
        out.put(index * 8 + 7, 0);
    };

    /**
     * Returns a {@link SeamlessCalc} that calculates the seamless X-Y-Z mode. Needs
     * 8 floats available in the output buffer.
     */
    public static SeamlessCalc seamlessXyz = (out, index, x, y, p, q, chunk, ranges) -> {
        float dx = ranges.loopx1 - ranges.loopx0;
        float dy = ranges.loopy1 - ranges.loopy0;
        float dz = ranges.loopz1 - ranges.loopz0;
        p = p * (ranges.mapx1 - ranges.mapx0) / (ranges.loopx1 - ranges.loopx0);
        q = q * (ranges.mapy1 - ranges.mapy0) / (ranges.loopy1 - ranges.loopy0);
        float r = (chunk.z - ranges.mapz0) / (ranges.mapz1 - ranges.mapz0);
        float zval = r * (ranges.mapz1 - ranges.mapz0) / (ranges.loopz1 - ranges.loopz0);
        float nx = ranges.loopx0 + (float) cos(p * PI2) * dx / PI2;
        float ny = ranges.loopx0 + (float) sin(p * PI2) * dx / PI2;
        float nz = ranges.loopy0 + (float) cos(q * PI2) * dy / PI2;
        float nw = ranges.loopy0 + (float) sin(q * PI2) * dy / PI2;
        float nv = ranges.loopz0 + (float) cos(zval * PI2) * dz / PI2;
        float nu = ranges.loopz0 + (float) sin(zval * PI2) * dz / PI2;
        out.put(index * 8 + 0, nx);
        out.put(index * 8 + 1, ny);
        out.put(index * 8 + 2, nz);
        out.put(index * 8 + 3, nw);
        out.put(index * 8 + 4, nv);
        out.put(index * 8 + 5, nu);
        out.put(index * 8 + 6, 0);
        out.put(index * 8 + 7, 0);
    };
}