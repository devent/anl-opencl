/*
 * Copyright (C) 2021-2022 Erwin Müller <erwin@muellerpublic.de>
 * Released as open-source under the Apache License, Version 2.0.
 *
 * ****************************************************************************
 * ANL-OpenCL :: JME3 - App - JavaFX
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
 * ANL-OpenCL :: JME3 - App - JavaFX is a derivative work based on Josua Tippetts' C++ library:
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
 * ANL-OpenCL :: JME3 - App - JavaFX bundles and uses the RandomCL library:
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
package com.anrisoftware.anlopencl.jmeapp.controllers;

import static java.awt.image.BufferedImage.TYPE_INT_ARGB;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.anrisoftware.resources.images.external.ImageResource;
import com.jayfella.jme.jfx.JavaFxUI;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.ImageView;

/**
 * Utils methods to run on the different threats.
 *
 * @author Erwin Müller
 */
public class JavaFxUtil {

    /**
     * Runs the task on the JME thread.
     */
    public static void runInJmeThread(Runnable task) {
        JavaFxUI.getInstance().runInJmeThread(task);
    }

    /**
     * Runs the task on the JavaFX thread.
     */
    public static void runFxThread(Runnable task) {
        if (Platform.isFxApplicationThread()) {
            task.run();
        } else {
            Platform.runLater(task);
        }
    }

    /**
     * Run the task on the JavaFx thread and wait for its completion. Taken from
     * {@link http://www.java2s.com/example/java/javafx/run-and-wait-on-javafx-thread.html}
     *
     * @param runnable the {@link Runnable} task to run.
     * @param result   the result of the task.
     * @return the result of the task.
     * @throws InterruptedException
     * @throws ExecutionException
     * @throws TimeoutException
     */
    public static <V> V runFxAndWait(long timeout, TimeUnit unit, Runnable runnable, V result)
            throws InterruptedException, ExecutionException, TimeoutException {
        if (Platform.isFxApplicationThread()) {
            runnable.run();
            return result;
        } else {
            var futureTask = new FutureTask<>(runnable, result);
            Platform.runLater(futureTask);
            return futureTask.get(timeout, unit);
        }
    }

    /**
     * Run the task on the JavaFx thread and wait for its completion. Taken from
     * {@link http://www.java2s.com/example/java/javafx/run-and-wait-on-javafx-thread.html}
     *
     * @param callable the {@link Callable} task to run.
     * @return the result of the task.
     * @throws ExecutionException
     * @throws InterruptedException
     * @throws TimeoutException
     */
    public static <V> V runFxAndWait(long timeout, TimeUnit unit, Callable<V> callable)
            throws InterruptedException, ExecutionException, TimeoutException {
        if (Platform.isFxApplicationThread()) {
            try {
                return callable.call();
            } catch (Exception e) {
                throw new ExecutionException(e);
            }
        } else {
            var futureTask = new FutureTask<>(callable);
            Platform.runLater(futureTask);
            return futureTask.get(timeout, unit);
        }
    }

    /**
     * Returns a {@link ImageView} from the image resource.
     */
    public static ImageView toGraphicFromResource(ImageResource res) {
        return new ImageView(SwingFXUtils.toFXImage(res.getBufferedImage(TYPE_INT_ARGB), null));
    }

    private JavaFxUtil() {
        // Utility class
    }
}
