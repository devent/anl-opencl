package com.anrisoftware.anlopencl.jmeapp.model;

import java.io.File;

import javax.inject.Inject;
import javax.inject.Provider;

import com.anrisoftware.anlopencl.jmeapp.model.ObservableGameMainPaneProperties.GameMainPaneProperties;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * Provides the {@link GameMainPaneProperties} and saves/loads the properties
 * from/to file.
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
@Slf4j
public class GameMainPanePropertiesProvider implements Provider<ObservableGameMainPaneProperties> {

    private final static String LAST_MAIN_PANE_PROPERTIES_FILE = GameMainPanePropertiesProvider.class.getPackageName()
            + ".last_main_pane_properties_file";

    private static final File DEFAULT_LAST_MAIN_PANE_PROPERTIES_FILE = new File(
            System.getProperty("user.home") + "/.anlopencl-last.yaml");

    private final GameMainPaneProperties p;

    private final ObservableGameMainPaneProperties op;

    @Inject
    private ObjectMapper mapper;

    public GameMainPanePropertiesProvider() {
        this.p = new GameMainPaneProperties();
        this.op = new ObservableGameMainPaneProperties(p);
    }

    @Override
    public ObservableGameMainPaneProperties get() {
        return op;
    }

    @SneakyThrows
    public void save() {
        File file = getFile();
        log.debug("Save properties to {}", file);
        mapper.writeValue(file, p);
    }

    @SneakyThrows
    public void load() {
        var file = getFile();
        if (file.exists()) {
            log.debug("Load properties from {}", file);
            var p = mapper.readValue(file, GameMainPaneProperties.class);
            op.copy(p);
        }
    }

    private File getFile() {
        var argsFile = System.getProperty(LAST_MAIN_PANE_PROPERTIES_FILE);
        if (argsFile != null) {
            return new File(argsFile);
        }
        return DEFAULT_LAST_MAIN_PANE_PROPERTIES_FILE;
    }
}
