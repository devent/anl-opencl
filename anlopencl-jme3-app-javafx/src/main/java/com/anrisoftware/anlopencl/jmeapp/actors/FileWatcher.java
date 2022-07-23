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
package com.anrisoftware.anlopencl.jmeapp.actors;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.EventListener;
import java.util.EventObject;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import javax.inject.Inject;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.apache.commons.lang3.event.EventListenerSupport;

import com.google.inject.assistedinject.Assisted;

import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * Watches for changes in a directory. Taken from
 * {@link https://dzone.com/articles/listening-to-fileevents-with-java-nio}
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
@Slf4j
public class FileWatcher {

    /**
     * Factory to create a new {@link FileWatcher}.
     *
     * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
     */
    public interface FileWatcherFactory {

        /**
         * Creates a new {@link FileWatcher} for the file or directory.
         *
         * @param path the {@link Path} of the file or directory to watch. If the path
         *             is a file then the parent directory will be watched for changes.
         * @return the {@link FileWatcher}.
         */
        FileWatcher create(Path path);
    }

    /**
     * Event that the file was changed. Taken from
     * {@link https://dzone.com/articles/listening-to-fileevents-with-java-nio}
     *
     * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
     */
    @ToString(callSuper = true)
    public static class FileEvent extends EventObject {

        private static final long serialVersionUID = -2404240475900948602L;

        public FileEvent(Path path) {
            super(path);
        }

        public Path getPath() {
            return (Path) getSource();
        }
    }

    /**
     * Informed that the file was changed. Taken from
     * {@link https://dzone.com/articles/listening-to-fileevents-with-java-nio}
     *
     * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
     */
    public interface FileListener extends EventListener {

        public default void onCreated(FileEvent event) {
        }

        public default void onModified(FileEvent event) {
        }

        public default void onDeleted(FileEvent event) {
        }
    }

    private final EventListenerSupport<FileListener> listeners;

    private final Path path;

    private boolean watching;

    @Inject
    FileWatcher(@Assisted Path path) {
        assertThat(Files.exists(path), is(true));
        this.listeners = new EventListenerSupport<>(FileListener.class);
        this.path = getParent(path);
    }

    private Path getParent(Path path) {
        return Files.isDirectory(path) ? path : path.getParent();
    }

    /**
     * Starts to watch for changes in a daemon thread.
     * 
     * @return this {@link FileWatcher}
     */
    public FileWatcher watch() {
        return watch(createDaemonExecutor());
    }

    /**
     * Starts to watch for changes in a thread created by the executor.
     *
     * @param executor the {@link Executor}
     * @return this {@link FileWatcher}
     */
    public FileWatcher watch(long timeout, TimeUnit unit) {
        return watch(createDaemonExecutor(), timeout, unit);
    }

    private ExecutorService createDaemonExecutor() {
        return Executors.newFixedThreadPool(1,
                new BasicThreadFactory.Builder().namingPattern(FileWatcher.class.getName()).daemon(true).build());
    }

    /**
     * Starts to watch for changes in a thread created by the executor.
     *
     * @param executor the {@link Executor}
     * @return this {@link FileWatcher}
     */
    public FileWatcher watch(Executor executor) {
        executor.execute(() -> {
            run0((watchService) -> {
                try {
                    return watchService.take();
                } catch (InterruptedException e) {
                    log.error(null, e);
                    Thread.currentThread().interrupt();
                    return null;
                }
            });
        });
        return this;
    }

    /**
     * Starts to watch for changes in a thread created by the executor.
     *
     * @param executor the {@link Executor}
     * @return this {@link FileWatcher}
     */
    public FileWatcher watch(Executor executor, long timeout, TimeUnit unit) {
        executor.execute(() -> {
            run0((watchService) -> {
                try {
                    return watchService.poll(timeout, unit);
                } catch (InterruptedException e) {
                    log.error(null, e);
                    Thread.currentThread().interrupt();
                    return null;
                }
            });
        });
        return this;
    }

    private void run0(Function<WatchService, WatchKey> watchServiceTake) {
        try (WatchService watchService = FileSystems.getDefault().newWatchService()) {
            log.debug("Watch path: {}", path);
            path.register(watchService, ENTRY_CREATE, ENTRY_MODIFY, ENTRY_DELETE);
            watching = true;
            while (watching) {
                watching = pollEvents(watchService, watchServiceTake);
            }
        } catch (IOException e) {
            log.error(null, e);
            Thread.currentThread().interrupt();
        }
    }

    private boolean pollEvents(WatchService watchService, Function<WatchService, WatchKey> watchServiceTake) {
        WatchKey key = watchServiceTake.apply(watchService);
        if (key == null) {
            return false;
        }
        Path path = (Path) key.watchable();
        for (WatchEvent<?> event : key.pollEvents()) {
            notifyListeners(event.kind(), path.resolve((Path) event.context()));
        }
        return key.reset();
    }

    private void notifyListeners(WatchEvent.Kind<?> kind, Path path) {
        FileEvent event = new FileEvent(path);
        log.debug("notifyListeners {}", event);
        if (kind == ENTRY_CREATE) {
            listeners.fire().onCreated(event);
        } else if (kind == ENTRY_MODIFY) {
            listeners.fire().onModified(event);
        } else if (kind == ENTRY_DELETE) {
            listeners.fire().onDeleted(event);
        }
    }

    public boolean isWatching() {
        return watching;
    }

    public FileWatcher addListener(FileListener listener) {
        listeners.addListener(listener);
        return this;
    }

    public FileWatcher removeListener(FileListener listener) {
        listeners.removeListener(listener);
        return this;
    }
}