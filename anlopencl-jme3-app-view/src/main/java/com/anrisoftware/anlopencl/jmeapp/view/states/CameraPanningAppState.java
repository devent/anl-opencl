/*
 * Copyright (C) 2021 Erwin Müller <erwin@muellerpublic.de>
 * Released as open-source under the Apache License, Version 2.0.
 *
 * ****************************************************************************
 * ANL-OpenCL :: JME3 - App - View
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
 * ANL-OpenCL :: JME3 - App - View is a derivative work based on Josua Tippetts' C++ library:
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
 * ANL-OpenCL :: JME3 - App - View bundles and uses the RandomCL library:
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
package com.anrisoftware.anlopencl.jmeapp.view.states;

import static com.jme3.input.MouseInput.AXIS_WHEEL;
import static com.jme3.input.MouseInput.BUTTON_MIDDLE;
import static java.lang.Math.abs;

import javax.inject.Inject;

import com.anrisoftware.anlopencl.jmeapp.model.GameMainPanePropertiesProvider;
import com.anrisoftware.anlopencl.jmeapp.model.ObservableGameMainPaneProperties;
import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.input.InputManager;
import com.jme3.input.RawInputListener;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.input.event.JoyAxisEvent;
import com.jme3.input.event.JoyButtonEvent;
import com.jme3.input.event.KeyInputEvent;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.input.event.MouseMotionEvent;
import com.jme3.input.event.TouchEvent;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;

import lombok.extern.slf4j.Slf4j;

/**
 * Pans the camera in two directions. Zooms the camera in one axis.
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
@Slf4j
public class CameraPanningAppState extends BaseAppState implements ActionListener, AnalogListener, RawInputListener {

    private static final String MIDDLE_BUTTON_MAPPING = "MoveMapMouseState_middle";

    private static final String ZOOM_OUT_MAPPING = "MouseMoveMapState_zoomout";

    private static final String ZOOM_IN_MAPPING = "MouseMoveMapState_zoomin";

    private static final String[] MAPPINGS = new String[] { MIDDLE_BUTTON_MAPPING, ZOOM_IN_MAPPING, ZOOM_OUT_MAPPING };

    private final ObservableGameMainPaneProperties gp;

    private final Vector2f mouse;

    @Inject
    private InputManager inputManager;

    @Inject
    private Camera camera;

    private boolean middleMouseDown;

    @Inject
    public CameraPanningAppState(GameMainPanePropertiesProvider gpp) {
        super(CameraPanningAppState.class.getSimpleName());
        this.gp = gpp.get();
        this.middleMouseDown = false;
        this.mouse = new Vector2f();
    }

    public void resetCamera() {
        camera.setLocation(new Vector3f(-0.021643113f, 0.035976667f, 15.217747f));
        camera.setRotation(new Quaternion(-4.8154507E-6f, 0.9999911f, 0.0012241602f, 0.004027171f));
    }

    @Override
    protected void initialize(Application app) {
        log.debug("initialize");
    }

    @Override
    protected void cleanup(Application app) {
        log.debug("cleanup");
    }

    @Override
    protected void onEnable() {
        log.debug("onEnable");
        initKeys();
    }

    @Override
    protected void onDisable() {
        log.debug("onDisable");
        deleteKeys();
    }

    private void initKeys() {
        inputManager.addListener(this, MAPPINGS);
        inputManager.addRawInputListener(this);
        inputManager.addMapping(MIDDLE_BUTTON_MAPPING, new MouseButtonTrigger(BUTTON_MIDDLE));
        inputManager.addMapping(ZOOM_IN_MAPPING, new MouseAxisTrigger(AXIS_WHEEL, false));
        inputManager.addMapping(ZOOM_OUT_MAPPING, new MouseAxisTrigger(AXIS_WHEEL, true));
    }

    private void deleteKeys() {
        inputManager.removeRawInputListener(this);
        for (int i = 0; i < MAPPINGS.length; i++) {
            inputManager.deleteMapping(MAPPINGS[i]);
        }
    }

    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        switch (name) {
        case MIDDLE_BUTTON_MAPPING:
            middleMouseDown = isPressed;
            return;
        }
    }

    @Override
    public void onAnalog(String name, float value, float tpf) {
        float m = 1f;
        var oldpos = camera.getWorldCoordinates(new Vector2f(camera.getWidth() / 2, camera.getHeight() / 2), 0.0f);
        var newpos = camera.getWorldCoordinates(mouse, 0.0f);
        switch (name) {
        case ZOOM_IN_MAPPING:
            if (checkZoomAllowed(m)) {
                boundMove(oldpos.x - newpos.x, oldpos.y - newpos.y, m);
            }
            return;
        case ZOOM_OUT_MAPPING:
            if (checkZoomAllowed(-m)) {
                boundMove(newpos.x - oldpos.x, newpos.y - oldpos.y, -m);
            }
            return;
        }
    }

    private boolean checkZoomAllowed(float m) {
        Vector3f location = camera.getLocation();
        if (m < 0) {
            return location.z > 8.21f;
        } else {
            return location.z < 50f;
        }
    }

    @Override
    public void beginInput() {
    }

    @Override
    public void endInput() {
    }

    @Override
    public void onJoyAxisEvent(JoyAxisEvent evt) {
    }

    @Override
    public void onJoyButtonEvent(JoyButtonEvent evt) {
    }

    @Override
    public void onMouseMotionEvent(MouseMotionEvent evt) {
        mouse.x = evt.getX();
        mouse.y = evt.getY();
        if (!middleMouseDown) {
            return;
        }
        float dx = evt.getDX();
        float dy = -evt.getDY();
        if (!canMoveX(dx)) {
            dx = 0;
        }
        if (!canMoveY(dy)) {
            dy = 0;
        }
        float s = calcSpeed(Math.max(abs(dx), abs(dy)));
        boundMove(-dx * s, dy * s, 0);
    }

    private void boundMove(float dx, float dy, float dz) {
        var pos = camera.getLocation();
        pos.x += dx;
        pos.y += dy;
        pos.z += dz;
        gp.cameraPosX.set(pos.x);
        gp.cameraPosY.set(pos.y);
        gp.cameraPosZ.set(pos.z);
        camera.update();
    }

    private boolean canMoveX(float dx) {
        Vector3f location = camera.getLocation();
        if (dx < 0) {
            return location.x < 10f;
        } else {
            return location.x > -10f;
        }
    }

    private boolean canMoveY(float dy) {
        Vector3f location = camera.getLocation();
        if (dy > 0) {
            return location.y < 10f;
        } else {
            return location.y > -10f;
        }
    }

    private float calcSpeed(float d) {
        return 0.025f;
    }

    @Override
    public void onMouseButtonEvent(MouseButtonEvent evt) {
    }

    @Override
    public void onKeyEvent(KeyInputEvent evt) {
    }

    @Override
    public void onTouchEvent(TouchEvent evt) {
    }

}
