package com.anrisoftware.anlopencl.jmeapp.actors

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.nio.file.Files
import java.util.concurrent.TimeUnit

import org.apache.commons.io.FileUtils
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Timeout

import com.anrisoftware.anlopencl.jmeapp.actors.FileWatcher.FileEvent
import com.anrisoftware.anlopencl.jmeapp.actors.FileWatcher.FileListener

import groovy.util.logging.Slf4j

/**
 * Tests that the content of a file was updated.
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
@Slf4j
class FileWatcherTest {

    @Test
    @Timeout(value = 5l, unit = TimeUnit.SECONDS)
    void "watch file on modification"() {
        def watchServices = []
        def temp = Files.createTempFile("watch-file", "anl");
        FileUtils.write(temp.toFile(), "test", UTF_8);
        def fileWatcher = new FileWatcher(temp)
        boolean tempModified = false
        fileWatcher.addListener(new FileListener() {
                    @Override
                    void onModified(FileEvent event) {
                        log.info("onModified {}", event)
                        if (temp == event.path) {
                            tempModified = true
                        }
                    }
                })
        fileWatcher.watch()
        Thread.sleep 100
        FileUtils.write(temp.toFile(), "test new stuff", UTF_8);
        while (!tempModified) {
            Thread.sleep 10
        }
    }

    @Test
    void "watch file on modification with timeout"() {
        def watchServices = []
        def temp = Files.createTempFile("watch-file", "anl");
        FileUtils.write(temp.toFile(), "test", UTF_8);
        def fileWatcher = new FileWatcher(temp)
        boolean tempModified = false
        fileWatcher.addListener(new FileListener() {
                    @Override
                    void onModified(FileEvent event) {
                        log.info("onModified {}", event)
                        if (temp == event.path) {
                            tempModified = true
                        }
                    }
                })
        fileWatcher.watch(5, TimeUnit.SECONDS)
        Thread.sleep 100
        FileUtils.write(temp.toFile(), "test new stuff", UTF_8);
        while (!tempModified) {
            Thread.sleep 10
        }
    }

    @Test
    void "watch file on modification with timeout trigger timeout"() {
        def watchServices = []
        def temp = Files.createTempFile("watch-file", "anl");
        FileUtils.write(temp.toFile(), "test", UTF_8);
        def fileWatcher = new FileWatcher(temp)
        boolean tempModified = false
        fileWatcher.addListener(new FileListener() {
                    @Override
                    void onModified(FileEvent event) {
                        log.info("onModified {}", event)
                        if (temp == event.path) {
                            tempModified = true
                        }
                    }
                })
        fileWatcher.watch(3, TimeUnit.SECONDS)
        Thread.sleep TimeUnit.SECONDS.toMillis(4)
        assertThat fileWatcher.watching, is(false)
    }
}
