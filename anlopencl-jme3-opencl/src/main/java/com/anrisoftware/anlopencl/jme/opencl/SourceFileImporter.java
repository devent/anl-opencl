package com.anrisoftware.anlopencl.jme.opencl;

import java.io.IOException;
import java.io.InputStream;

import com.jme3.asset.AssetInfo;
import com.jme3.asset.AssetManager;
import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.Savable;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SourceFileImporter implements JmeImporter {

    public static class SourceFile implements Savable {

        private final InputStream stream;

        public SourceFile(InputStream stream) {
            this.stream = stream;
        }

        public InputStream getStream() {
            return stream;
        }

        @Override
        public void write(JmeExporter ex) throws IOException {
        }

        @Override
        public void read(JmeImporter im) throws IOException {
        }

    }

    private AssetManager assetManager;

    public void setAssetManager(AssetManager manager) {
        this.assetManager = manager;
    }

    @Override
    public Object load(AssetInfo info) throws IOException {
        try (var is = info.openStream()) {
            Savable s = load(is);
            return s;
        } catch (IOException ex) {
            log.error("An error occurred while loading jME binary object", ex);
        }
        return null;
    }

    private Savable load(InputStream is) {
        return new SourceFile(is);
    }

    @Override
    public InputCapsule getCapsule(Savable id) {
        return null;
    }

    @Override
    public AssetManager getAssetManager() {
        return assetManager;
    }

    @Override
    public int getFormatVersion() {
        return 0;
    }

}
