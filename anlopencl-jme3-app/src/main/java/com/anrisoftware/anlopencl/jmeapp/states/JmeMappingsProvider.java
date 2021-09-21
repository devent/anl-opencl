package com.anrisoftware.anlopencl.jmeapp.states;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Provider;

/**
 * Provides the key mappings for JME3.
 * 
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
public class JmeMappingsProvider implements Provider<Map<String, JmeMapping>> {

    private final Map<String, JmeMapping> map;

    public JmeMappingsProvider() {
        var map = new HashMap<String, JmeMapping>();
        map.put(JmeMapping.CONTROL_MAPPING.name, JmeMapping.CONTROL_MAPPING);
        this.map = Collections.unmodifiableMap(map);
    }

    @Override
    public Map<String, JmeMapping> get() {
        return map;
    }

}
