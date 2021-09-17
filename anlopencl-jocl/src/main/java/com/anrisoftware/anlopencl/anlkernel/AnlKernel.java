package com.anrisoftware.anlopencl.anlkernel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import javax.inject.Inject;

import org.jocl.cl_context;
import org.jocl.cl_program;

import com.anrisoftware.easycl.corejocl.Program;
import com.anrisoftware.easycl.corejocl.ProgramFactory;
import com.google.inject.assistedinject.Assisted;

public class AnlKernel implements Runnable, AutoCloseable {

    private static final String ANLOPENCL_USE_OPENCL = "-DANLOPENCL_USE_OPENCL";

    private static final String ANLOPENCL_USE_DOUBLE = "-DANLOPENCL_USE_DOUBLE";

    public interface AnlKernelFactory {

        AnlKernel create(Supplier<cl_context> context);
    }

    private final Supplier<cl_context> context;

    @Inject
    private ProgramFactory programFactory;

    @Inject
    private Map<String, String> sources;

    @Inject
    private LibSourcesProvider libSources;

    private List<Supplier<cl_program>> headers;

    private Supplier<cl_program> lib;

    private Program program;

    private String options;

    @Inject
    public AnlKernel(@Assisted Supplier<cl_context> context) {
        this.context = context;
        this.options = ANLOPENCL_USE_OPENCL;
    }

    public void setUseDouble(boolean useDouble) {
        this.options = useDouble ? ANLOPENCL_USE_OPENCL + " " + ANLOPENCL_USE_DOUBLE : ANLOPENCL_USE_OPENCL;
    }

    public void compileKernel(String kernelSource) throws Exception {
        var source = new StringBuilder(kernelSource);
        source.append(sources.get("kiss09.cl"));
        source.append(sources.get("random.cl"));
        var p = programFactory.create(context, source.toString());
        var program = (Program) p;
        program.compileProgram(options, headers);
        this.program = program;
    }

    public void buildLib() throws Exception {
        createHeaders();
        createLib();
    }

    private void createLib() throws Exception {
        var p = programFactory.create(context, libSources.get());
        try (var program = (Program) p) {
            program.compileProgram(options);
            this.lib = program.linkLibrary();
        } catch (Exception e) {
            throw e;
        }
    }

    private void createHeaders() {
        headers = new ArrayList<>();
        putHeader("opencl_utils.h");
        putHeader("qsort.h");
        putHeader("utility.h");
        putHeader("hashing.h");
        putHeader("noise_lut.h");
        putHeader("noise_gen.h");
        putHeader("imaging.h");
        putHeader("kernel.h");
    }

    private void putHeader(String name) {
        headers.add(programFactory.create(context, sources.get(name), name));
    }

    @Override
    public void run() {

    }

    @Override
    public void close() throws Exception {
        ((AutoCloseable) lib).close();
        for (Supplier<cl_program> header : headers) {
            ((AutoCloseable) header).close();
        }
        program.close();
    }

}
