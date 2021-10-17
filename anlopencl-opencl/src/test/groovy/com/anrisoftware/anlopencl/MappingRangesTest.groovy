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
package com.anrisoftware.anlopencl

import org.junit.jupiter.api.Test

/**
 * @see MappingRanges
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
class MappingRangesTest {

    @Test
    void "ranges default"() {
        def ranges = MappingRanges.createDefaultRanges()
        assert ranges.mapx0 == -1
        assert ranges.mapy0 == -1
        assert ranges.mapz0 == -1
        assert ranges.mapx1 == 1
        assert ranges.mapy1 == 1
        assert ranges.mapz1 == 1
        assert ranges.loopx0 == -1
        assert ranges.loopy0 == -1
        assert ranges.loopz0 == -1
        assert ranges.loopx1 == 1
        assert ranges.loopy1 == 1
        assert ranges.loopz1 == 1
    }

    @Test
    void "ranges map2D"() {
        def x0 = -10
        def x1 = 10
        def y0 = -10
        def y1 = 10
        def ranges = MappingRanges.createRangesMap2D(x0, x1, y0, y1)
        assert ranges.mapx0 == x0
        assert ranges.mapy0 == y0
        assert ranges.mapz0 == 0
        assert ranges.mapx1 == x1
        assert ranges.mapy1 == y1
        assert ranges.mapz1 == 0
        assert ranges.loopx0 == x0
        assert ranges.loopy0 == y0
        assert ranges.loopz0 == 1
        assert ranges.loopx1 == x1
        assert ranges.loopy1 == y1
        assert ranges.loopz1 == 1
    }

    @Test
    void "ranges map3D"() {
        def x0 = -10
        def x1 = 10
        def y0 = -10
        def y1 = 10
        def z0 = -10
        def z1 = 10
        def ranges = MappingRanges.createRangesMap3D(x0, x1, y0, y1, z0, z1)
        assert ranges.mapx0 == x0
        assert ranges.mapy0 == y0
        assert ranges.mapz0 == z0
        assert ranges.mapx1 == x1
        assert ranges.mapy1 == y1
        assert ranges.mapz1 == z1
        assert ranges.loopx0 == x0
        assert ranges.loopy0 == y0
        assert ranges.loopz0 == z0
        assert ranges.loopx1 == x1
        assert ranges.loopy1 == y1
        assert ranges.loopz1 == z1
    }
}
