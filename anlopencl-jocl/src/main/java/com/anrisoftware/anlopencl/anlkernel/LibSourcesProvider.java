package com.anrisoftware.anlopencl.anlkernel;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Provider;

/**
 * Provides the appended ANL-OpenCL sources.
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
public class LibSourcesProvider implements Provider<String> {

    private final String sources;

    @Inject
    public LibSourcesProvider(Map<String, String> sources) {
        this.sources = appendSources(sources);
    }

    private String appendSources(Map<String, String> sources) {
        var s = new StringBuilder();
        s.append(sources.get("opencl_utils.h"));
        s.append(sources.get("opencl_utils.c"));
        s.append(sources.get("qsort.h"));
        s.append(sources.get("qsort.c"));
        s.append(sources.get("utility.h"));
        s.append(sources.get("utility.c"));
        s.append(sources.get("hashing.h"));
        s.append(sources.get("hashing.c"));
        s.append(sources.get("noise_lut.h"));
        s.append(sources.get("noise_lut.c"));
        s.append(sources.get("noise_gen.h"));
        s.append(sources.get("noise_gen.c"));
        s.append(sources.get("imaging.h"));
        s.append(sources.get("imaging.c"));
        s.append(sources.get("kernel.h"));
        s.append(sources.get("kernel.c"));
        return s.toString();
    }

    @Override
    public String get() {
        return sources;
    }

}
