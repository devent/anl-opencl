package com.anrisoftware.anlopencl.aparapi;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.commons.lang3.Validate.notNull;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;

public class Kernel implements Runnable {

    private final com.aparapi.Kernel kernel;

    public Kernel() {
        this.kernel = new com.aparapi.Kernel() {
            @Override
            public void run() {
                // TODO Auto-generated method stub

            }
        };
    }

    public void compileKernel() throws IOException {
        var sources = new HashMap<String, String>();
        addSource(sources, "opencl_utils.h");
    }

    private void addSource(Map<String, String> sources, String name) throws IOException {
        sources.put(name, loadResource(name));
    }

    private String loadResource(String name) throws IOException {
        var resname = String.format("/%s", name);
        URL r = Kernel.class.getResource(resname);
        notNull(r, "Resource '%s' is null", name);
        return IOUtils.toString(r, UTF_8);
    }

    @Override
    public void run() {

    }

}
