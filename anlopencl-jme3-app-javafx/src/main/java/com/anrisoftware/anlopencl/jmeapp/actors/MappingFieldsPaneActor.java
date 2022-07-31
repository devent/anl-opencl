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

import static com.anrisoftware.anlopencl.jmeapp.controllers.JavaFxUtil.runFxThread;
import static com.anrisoftware.anlopencl.jmeapp.messages.CreateActorMessage.createNamedActor;

import java.time.Duration;
import java.util.Locale;
import java.util.concurrent.CompletionStage;

import javax.inject.Inject;
import javax.inject.Named;

import com.anrisoftware.anlopencl.jmeapp.controllers.GameMainPaneController;
import com.anrisoftware.anlopencl.jmeapp.controllers.MappingFieldsPaneController;
import com.anrisoftware.anlopencl.jmeapp.messages.BuildStartMessage.BuildFailedMessage;
import com.anrisoftware.anlopencl.jmeapp.messages.BuildStartMessage.BuildFinishedMessage;
import com.anrisoftware.anlopencl.jmeapp.messages.BuildTriggeredMessage;
import com.anrisoftware.anlopencl.jmeapp.messages.ColumnsRowsChangedMessage;
import com.anrisoftware.anlopencl.jmeapp.messages.MessageActor.Message;
import com.anrisoftware.anlopencl.jmeapp.model.GameMainPanePropertiesProvider;
import com.anrisoftware.anlopencl.jmeapp.model.GameSettingsProvider;
import com.anrisoftware.resources.images.external.IconSize;
import com.anrisoftware.resources.images.external.Images;
import com.google.inject.Injector;
import com.google.inject.assistedinject.Assisted;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.BehaviorBuilder;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.receptionist.Receptionist;
import akka.actor.typed.receptionist.ServiceKey;
import lombok.extern.slf4j.Slf4j;

/**
 * Mapping fields panel actor.
 *
 * @author Erwin Müller
 */
@Slf4j
public class MappingFieldsPaneActor extends AbstractPaneActor<MappingFieldsPaneController> {

    public static final ServiceKey<Message> KEY = ServiceKey.create(Message.class,
            MappingFieldsPaneActor.class.getSimpleName());

    public static final String NAME = MappingFieldsPaneActor.class.getSimpleName();

    public static final int ID = KEY.hashCode();

    /**
     *
     * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
     */
    public interface MappingFieldsPaneActorFactory {

        MappingFieldsPaneActor create(ActorContext<Message> context);
    }

    public static Behavior<Message> create(Injector injector) {
        return Behaviors.setup((context) -> {
            context.getSystem().receptionist().tell(Receptionist.register(KEY, context.getSelf()));
            return injector.getInstance(MappingFieldsPaneActorFactory.class).create(context).start();
        });
    }

    public static CompletionStage<ActorRef<Message>> create(Duration timeout, Injector injector) {
        var system = injector.getInstance(ActorSystemProvider.class).getActorSystem();
        return createNamedActor(system, timeout, ID, KEY, NAME, create(injector));
    }

    @Inject
    private GameMainPanePropertiesProvider gmppp;

    @Inject
    private ActorSystemProvider actor;

    @Inject
    MappingFieldsPaneActor(@Assisted ActorContext<Message> context, GameSettingsProvider gsp,
            @Named("AppIcons") Images appIcons) {
        super(context, gsp, appIcons);
    }

    @Override
    protected void initialState(InitialStateMessage m) {
        runFxThread(() -> {
            controller.updateLocale(Locale.US, appIcons, IconSize.SMALL);
            controller.initializeListeners(actor.get(), Locale.US, gmppp.get());
            controller.columnsField.valueProperty().addListener((observable, oldValue, newValue) -> {
                actor.getActorSystem()
                        .tell(new ColumnsRowsChangedMessage(gmppp.get().columns.get(), gmppp.get().rows.get()));
            });
            controller.rowsField.valueProperty().addListener((observable, oldValue, newValue) -> {
                actor.getActorSystem()
                        .tell(new ColumnsRowsChangedMessage(gmppp.get().columns.get(), gmppp.get().rows.get()));
            });
        });
    }

    @Override
    protected void setupController(InitialStateMessage m) {
        var mainController = (GameMainPaneController) m.controller;
        this.controller = mainController.mappingFieldsPaneController;
    }

    @Override
    protected BehaviorBuilder<Message> getBehaviorInitialState() {
        return super.getBehaviorInitialState()//
                .onMessage(BuildTriggeredMessage.class, this::onBuildClicked)//
                .onMessage(BuildFinishedMessage.class, this::onBuildFinished)//
                .onMessage(BuildFailedMessage.class, this::onBuildFailed)//
        ;
    }

    private Behavior<Message> onBuildClicked(BuildTriggeredMessage m) {
        log.debug("onBuildClicked {}", m);
        setDisableControlButtons(true);
        return Behaviors.same();
    }

    private Behavior<Message> onBuildFinished(BuildFinishedMessage m) {
        log.debug("onBuildFinished {}", m);
        setDisableControlButtons(false);
        return Behaviors.same();
    }

    private Behavior<Message> onBuildFailed(BuildFailedMessage m) {
        log.debug("onBuildFailed {}", m);
        setDisableControlButtons(false);
        return Behaviors.same();
    }

    private void setDisableControlButtons(boolean disable) {
        runFxThread(() -> {
            controller.mappingX0Field.setDisable(disable);
            controller.mappingX1Field.setDisable(disable);
            controller.mappingY0Field.setDisable(disable);
            controller.mappingY1Field.setDisable(disable);
            controller.threeDMappingBox.setDisable(disable);
            if (!disable && gmppp.get().map3d.get()) {
                controller.mappingZ0Field.setDisable(false);
                controller.mappingZ1Field.setDisable(false);
            } else if (disable) {
                controller.mappingZ0Field.setDisable(true);
                controller.mappingZ1Field.setDisable(true);
            }
        });
    }

}
