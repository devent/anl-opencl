package com.anrisoftware.anlopencl.anlkernel;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.commons.lang3.Validate.notNull;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Provider;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import lombok.SneakyThrows;

/**
 * Loads a source from the classpath.
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
public class SourceResourcesProvider implements Provider<Map<String, String>> {

    /**
     * Loads the specified source from the classpath.
     *
     * @param name the {@link String} name of the source, for example
     *             <code>hashing.h</code>
     * @return the {@link String} content of the resource.
     * @throws IOException if there was an error reading the resource.
     */
    public static String loadResource(String name) throws IOException {
        var resname = String.format("/%s", name);
        var r = AnlKernel.class.getResource(resname);
        notNull(r, "Resource '%s' is null", name);
        return IOUtils.toString(r, UTF_8);
    }

    private final Map<String, String> sources;

    public SourceResourcesProvider() {
        this.sources = Collections.unmodifiableMap(loadResources());
    }

    @SneakyThrows
    private Map<String, String> loadResources() {
        var map = new HashMap<String, String>();
        putResource(map, "opencl_utils.h");
        putResource(map, "opencl_utils.c");
        putResource(map, "qsort.h");
        putResource(map, "qsort.c");
        putResource(map, "utility.h");
        putResource(map, "utility.c");
        putResource(map, "hashing.h");
        putResource(map, "hashing.c");
        putResource(map, "noise_lut.h");
        putResource(map, "noise_lut.c");
        putResource(map, "noise_gen.h");
        putResource(map, "noise_gen.c");
        putResource(map, "imaging.h");
        putResource(map, "imaging.c");
        putResource(map, "kernel.h");
        putResource(map, "kernel.c");
        putResource(map, "extern/RandomCL/generators/kiss09.cl");
        putResource(map, "random.cl");
        return map;
    }

    private void putResource(Map<String, String> map, String name) throws IOException {
        map.put(FilenameUtils.getName(name), loadResource(name));
    }

    @Override
    public Map<String, String> get() {
        return sources;
    }

}
