package com.anrisoftware.anlopencl.jmeapp.states;

import static com.jme3.input.KeyInput.KEY_Q;
import static javafx.scene.input.KeyCode.Q;
import static javafx.scene.input.KeyCombination.CONTROL_DOWN;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Provider;

import com.anrisoftware.anlopencl.jmeapp.messages.GameQuitMessage;
import com.jme3.input.controls.KeyTrigger;

import javafx.scene.input.KeyCodeCombination;

public class KeyMappingsProvider implements Provider<Map<String, KeyMapping>> {

    private final Map<String, KeyMapping> map;

    public final Map<String, KeyMapping> commandsButtons;

    public KeyMappingsProvider() {
        var map = new HashMap<String, KeyMapping>();
        var commandsButtons = new HashMap<String, KeyMapping>();
        // general
        map.put("QUIT_MAPPING", new KeyMapping("QUIT_MAPPING", new KeyCodeCombination(Q, CONTROL_DOWN),
                new KeyTrigger(KEY_Q), new GameQuitMessage()));
        // done
        map.putAll(commandsButtons);
        this.commandsButtons = Collections.unmodifiableMap(commandsButtons);
        this.map = Collections.unmodifiableMap(map);
    }

    @Override
    public Map<String, KeyMapping> get() {
        return map;
    }

}
