/**
 * Copyright (C) 2021 Erwin Müller <erwin@muellerpublic.de>
 * Released as open-source under the Apache License, Version 2.0.
 *
 * ****************************************************************************
 * ANL-OpenCL :: JME3 - App - JavaFX
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
 */
package com.anrisoftware.anlopencl.jmeapp.controllers

import static com.anrisoftware.anlopencl.jmeapp.controllers.JavaFxUtil.*

import javax.inject.Inject
import javax.inject.Named

import com.anrisoftware.anlopencl.jmeapp.actors.ActorSystemProvider
import com.anrisoftware.anlopencl.jmeapp.states.JmeMapping
import com.anrisoftware.anlopencl.jmeapp.states.KeyMapping
import com.jayfella.jme.jfx.JavaFxUI
import com.jme3.input.InputManager
import com.jme3.input.controls.ActionListener

import groovy.util.logging.Slf4j
import javafx.scene.Scene
import javafx.scene.input.KeyCombination.ModifierValue

/**
 * Setups the global keys.
 *
 * @author Erwin Müller
 */
@Slf4j
class GlobalKeys implements ActionListener {

    @Inject
    InputManager inputManager

    @Inject
    ActorSystemProvider actor

    @Inject
    @Named("keyMappings")
    Map<String, KeyMapping> keyMappings;

    @Inject
    @Named("jmeMappings")
    Map<String, JmeMapping> jmeMappings;

    boolean controlDown = false

    void setup(JavaFxUI instance) {
        setupControls(instance.scene)
        initKeys()
    }

    void remove(KeyMapping mapping, JmeMapping jmeMapping) {
        runFxThread {
            def acc = JavaFxUI.getInstance().scene.accelerators
            acc.remove(mapping.code)
        }
        runInJmeThread {
            inputManager.deleteMapping(mapping.name)
        }
        if (jmeMapping) {
            runInJmeThread {
                inputManager.deleteMapping(jmeMapping.name)
            }
        }
    }

    void add(KeyMapping mapping, JmeMapping jmeMapping) {
        runFxThread {
            def acc = JavaFxUI.getInstance().scene.accelerators
            acc.put mapping.code, { runInJmeThread({ runAction(mapping) }) }
        }
        runInJmeThread {
            inputManager.addMapping(mapping.name, mapping.trigger)
        }
        if (jmeMapping) {
            runInJmeThread {
                inputManager.addMapping(jmeMapping.name, jmeMapping.trigger)
            }
        }
    }

    void setupControls(Scene scene) {
        def acc = scene.accelerators
        keyMappings.values().find { it.code.present }.each { m ->
            acc.put m.code.get(), { runInJmeThread({ runAction(m) }) }
        }
    }

    void initKeys() {
        inputManager.addListener(this, keyMappings.values().findAll { it.trigger.present }.inject([]) { l, v ->
            inputManager.addMapping(v.name, v.trigger.get())
            l << v.name
        } as String[])
        inputManager.addListener(this, jmeMappings.values().inject([]) { l, v ->
            inputManager.addMapping(v.name, v.trigger)
            l << v.name
        } as String[])
    }

    @Override
    void onAction(String name, boolean isPressed, float tpf) {
        switch (name) {
            case JmeMapping.CONTROL_MAPPING.name:
                controlDown = isPressed
                return
        }
        if (!isPressed) {
            return
        }
        def mapping = keyMappings[name]
        def code = mapping.code
        if (code.present) {
            if (code.get().control != ModifierValue.ANY) {
                if (code.get().control == ModifierValue.DOWN && !controlDown) {
                    return;
                }
                if (code.get().control == ModifierValue.UP && controlDown) {
                    return;
                }
            }
        }
        runAction(mapping)
    }

    void runAction(KeyMapping mapping) {
        log.debug("Post message: {}", mapping.message);
        actor.get().tell(mapping.message);
    }
}
