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

import static java.nio.charset.StandardCharsets.UTF_8;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.function.Consumer;

import javax.inject.Inject;

import org.apache.commons.io.FileUtils;

import com.anrisoftware.anlopencl.jmeapp.model.GameMainPanePropertiesProvider;
import com.anrisoftware.anlopencl.jmeapp.model.GameSettingsProvider;
import com.anrisoftware.globalpom.exec.external.command.CommandLineFactory;
import com.anrisoftware.globalpom.exec.external.core.CommandExecFactory;
import com.anrisoftware.globalpom.exec.external.core.ProcessTask;
import com.anrisoftware.globalpom.exec.external.logoutputs.DebugLogCommandOutputFactory;
import com.anrisoftware.globalpom.threads.external.core.ThreadsException;
import com.anrisoftware.globalpom.threads.properties.external.PropertiesThreads;
import com.anrisoftware.globalpom.threads.properties.external.PropertiesThreadsFactory;
import com.google.common.base.Optional;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * Starts the external editor.
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
@SuppressWarnings("deprecation")
@Slf4j
public class ExternalEditorBuild {

    /**
     * Factory to create the {@link ExternalEditorBuild}.
     *
     * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
     */
    public interface ExternalEditorBuildFactory {

        ExternalEditorBuild create();
    }

    /**
     * Information about the opened external editor.
     *
     * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
     */
    @RequiredArgsConstructor
    @ToString(callSuper = true)
    public static class ExternalEditor {

        public final ExternalEditorBuild build;

        public final Future<ProcessTask> process;

        public final Path tempFile;

    }

    private final PropertiesThreads threads;

    @Inject
    private GameSettingsProvider gsp;

    @Inject
    private GameMainPanePropertiesProvider gmppp;

    @Inject
    private CommandExecFactory commandExecFactory;

    @Inject
    private CommandLineFactory commandLineFactory;

    @Inject
    private DebugLogCommandOutputFactory debugLogCommandOutputFactory;

    private Optional<Future<ProcessTask>> task;

    @Inject
    ExternalEditorBuild(PropertiesThreadsFactory propertiesThreadsFactory,
            ThreadsPropertiesProvider threadsPropertiesProvider) throws ThreadsException {
        this.threads = propertiesThreadsFactory.create();
        threads.setProperties(threadsPropertiesProvider.get());
        threads.setName("script");
    }

    /**
     * Starts the editor asynchronously.
     */
    public CompletionStage<ExternalEditor> startEditor(Executor executor, Consumer<Path> commandClosesEvent) {
        return CompletableFuture.supplyAsync(() -> {
            var temp = createKernelTempFile();
            var process = startExternalEditor(temp, commandClosesEvent);
            return new ExternalEditor(ExternalEditorBuild.this, process, temp);
        }, executor);
    }

    /**
     * Shutdowns the command threads.
     */
    public void shutdown() {
        threads.shutdownNow();
    }

    @SneakyThrows
    private Future<ProcessTask> startExternalEditor(Path file, Consumer<Path> commandClosesEvent) {
        var commandExec = commandExecFactory.create();
        commandExec.setThreads(threads);
        var commandLine = commandLineFactory.create(gsp.get().editorPath.get().toFile().toString());
        commandLine.add(file.toFile().toString());
        commandExec.setCommandOutput(debugLogCommandOutputFactory.create(log, commandLine));
        commandExec.setDestroyOnInterrupted(true);
        commandExec.setObserver(new Observer() {
            @Override
            public void update(Observable o, Object arg) {
                commandClosesEvent.accept(file);
            }
        });
        task = Optional.of(commandExec.exec(commandLine));
        return task.get();
    }

    @SneakyThrows
    private Path createKernelTempFile() {
        var temp = Files.createTempFile(gsp.get().tempDir.get(), "kernel", "anl");
        FileUtils.write(temp.toFile(), gmppp.get().kernelCode.get(), UTF_8);
        return temp;
    }

}
