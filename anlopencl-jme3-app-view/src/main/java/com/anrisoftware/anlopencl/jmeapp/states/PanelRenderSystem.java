/**
 * Copyright (C) 2021 Erwin Müller <erwin@muellerpublic.de>
 * Released as open-source under the Apache License, Version 2.0.
 *
 * ****************************************************************************
 * ANL-OpenCL :: JME3 - App - Core
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
 * ANL-OpenCL :: JME3 - App - Core is a derivative work based on Josua Tippetts' C++ library:
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
package com.anrisoftware.anlopencl.jmeapp.states;

import javax.inject.Inject;

import com.anrisoftware.anlopencl.jmeapp.components.ImageComponent;
import com.anrisoftware.anlopencl.jmeapp.messages.MainWindowResizedMessage;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IntervalIteratingSystem;
import com.jme3.renderer.Camera;

/**
 * Updates the main panel.
 *
 * @author Erwin Müller {@literal <erwin@muellerpublic.de}
 */
public class PanelRenderSystem extends IntervalIteratingSystem {

    private final Camera camera;

    private int panelWidth;

    private int panelHeight;

    @Inject
    public PanelRenderSystem(Camera camera) {
        super(Family.all(ImageComponent.class).get(), 0.33f);
        this.camera = camera;
        this.panelWidth = camera.getWidth();
        this.panelHeight = camera.getHeight();
    }

    @Override
    protected void processEntity(Entity entity) {
        int camWidth = camera.getWidth();
        int camHeight = camera.getHeight();
        if (panelWidth != camWidth || panelHeight != camHeight) {
            this.panelWidth = camWidth;
            this.panelHeight = camHeight;
            var actor = ImageComponent.m.get(entity).actor;
            actor.tell(new MainWindowResizedMessage(camWidth, camHeight));
        }
    }

}
