package com.anrisoftware.anlopencl.jmeapp.states;

import static com.jme3.input.KeyInput.KEY_F10;
import static com.jme3.input.KeyInput.KEY_Q;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static javafx.scene.input.KeyCode.F10;
import static javafx.scene.input.KeyCode.Q;
import static javafx.scene.input.KeyCombination.CONTROL_DOWN;

import java.util.Optional;

import com.anrisoftware.anlopencl.jmeapp.messages.AboutDialogMessage.AboutClickedMessage;
import com.anrisoftware.anlopencl.jmeapp.messages.BuildClickedMessage;
import com.anrisoftware.anlopencl.jmeapp.messages.GameQuitMessage;
import com.anrisoftware.anlopencl.jmeapp.messages.GuiMessage;
import com.anrisoftware.anlopencl.jmeapp.messages.ResetCameraMessage;
import com.anrisoftware.anlopencl.jmeapp.messages.SettingsDialogMessage.SettingsClickedMessage;
import com.jme3.input.controls.KeyTrigger;

import javafx.scene.input.KeyCodeCombination;
import lombok.RequiredArgsConstructor;

/**
 * Default key mappings.
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
@RequiredArgsConstructor
public enum DefaultKeyMappings {

    // general
    QUIT_MAPPING(new GameQuitMessage(), of(new KeyCodeCombination(Q, CONTROL_DOWN)), of(new KeyTrigger(KEY_Q))),

    BUILD_MAPPING(new BuildClickedMessage(), empty(), empty()),

    SETTINGS_MAPPING(new SettingsClickedMessage(), empty(), empty()),

    ABOUT_DIALOG_MAPPING(new AboutClickedMessage(), empty(), empty()),

    // camera
    RESET_CAMERA_MAPPING(new ResetCameraMessage(), of(new KeyCodeCombination(F10)), of(new KeyTrigger(KEY_F10)));

    public final GuiMessage message;

    public final Optional<KeyCodeCombination> keyCode;

    public final Optional<KeyTrigger> keyTrigger;

}
