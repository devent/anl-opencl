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
