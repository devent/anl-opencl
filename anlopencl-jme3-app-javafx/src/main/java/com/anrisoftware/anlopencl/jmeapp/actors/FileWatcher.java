package com.anrisoftware.anlopencl.jmeapp.actors;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.EventListener;
import java.util.EventObject;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import javax.inject.Inject;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.apache.commons.lang3.event.EventListenerSupport;

import com.google.inject.assistedinject.Assisted;

import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * Watches for changes in a directory. Taken from
 * {@link https://dzone.com/articles/listening-to-fileevents-with-java-nio}
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
@Slf4j
public class FileWatcher {

    /**
     * Factory to create a new {@link FileWatcher}.
     *
     * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
     */
    public interface FileWatcherFactory {

        /**
         * Creates a new {@link FileWatcher} for the file or directory.
         *
         * @param path the {@link Path} of the file or directory to watch. If the path
         *             is a file then the parent directory will be watched for changes.
         * @return the {@link FileWatcher}.
         */
        FileWatcher create(Path path);
    }

    /**
     * Event that the file was changed. Taken from
     * {@link https://dzone.com/articles/listening-to-fileevents-with-java-nio}
     *
     * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
     */
    @ToString(callSuper = true)
    public static class FileEvent extends EventObject {

        private static final long serialVersionUID = -2404240475900948602L;

        public FileEvent(Path path) {
            super(path);
        }

        public Path getPath() {
            return (Path) getSource();
        }
    }

    /**
     * Informed that the file was changed. Taken from
     * {@link https://dzone.com/articles/listening-to-fileevents-with-java-nio}
     *
     * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
     */
    public interface FileListener extends EventListener {

        public default void onCreated(FileEvent event) {
        }

        public default void onModified(FileEvent event) {
        }

        public default void onDeleted(FileEvent event) {
        }
    }

    private final EventListenerSupport<FileListener> listeners;

    private final Path path;

    private boolean watching;

    @Inject
    FileWatcher(@Assisted Path path) {
        assertThat(Files.exists(path), is(true));
        this.listeners = new EventListenerSupport<>(FileListener.class);
        this.path = getParent(path);
    }

    private Path getParent(Path path) {
        return Files.isDirectory(path) ? path : path.getParent();
    }

    /**
     * Starts to watch for changes in a daemon thread.
     * 
     * @return this {@link FileWatcher}
     */
    public FileWatcher watch() {
        return watch(createDaemonExecutor());
    }

    /**
     * Starts to watch for changes in a thread created by the executor.
     *
     * @param executor the {@link Executor}
     * @return this {@link FileWatcher}
     */
    public FileWatcher watch(long timeout, TimeUnit unit) {
        return watch(createDaemonExecutor(), timeout, unit);
    }

    private ExecutorService createDaemonExecutor() {
        return Executors.newFixedThreadPool(1,
                new BasicThreadFactory.Builder().namingPattern(FileWatcher.class.getName()).daemon(true).build());
    }

    /**
     * Starts to watch for changes in a thread created by the executor.
     *
     * @param executor the {@link Executor}
     * @return this {@link FileWatcher}
     */
    public FileWatcher watch(Executor executor) {
        executor.execute(() -> {
            run0((watchService) -> {
                try {
                    return watchService.take();
                } catch (InterruptedException e) {
                    log.error(null, e);
                    Thread.currentThread().interrupt();
                    return null;
                }
            });
        });
        return this;
    }

    /**
     * Starts to watch for changes in a thread created by the executor.
     *
     * @param executor the {@link Executor}
     * @return this {@link FileWatcher}
     */
    public FileWatcher watch(Executor executor, long timeout, TimeUnit unit) {
        executor.execute(() -> {
            run0((watchService) -> {
                try {
                    return watchService.poll(timeout, unit);
                } catch (InterruptedException e) {
                    log.error(null, e);
                    Thread.currentThread().interrupt();
                    return null;
                }
            });
        });
        return this;
    }

    private void run0(Function<WatchService, WatchKey> watchServiceTake) {
        try (WatchService watchService = FileSystems.getDefault().newWatchService()) {
            log.debug("Watch path: {}", path);
            path.register(watchService, ENTRY_CREATE, ENTRY_MODIFY, ENTRY_DELETE);
            watching = true;
            while (watching) {
                watching = pollEvents(watchService, watchServiceTake);
            }
        } catch (IOException e) {
            log.error(null, e);
            Thread.currentThread().interrupt();
        }
    }

    private boolean pollEvents(WatchService watchService, Function<WatchService, WatchKey> watchServiceTake) {
        WatchKey key = watchServiceTake.apply(watchService);
        if (key == null) {
            return false;
        }
        Path path = (Path) key.watchable();
        for (WatchEvent<?> event : key.pollEvents()) {
            notifyListeners(event.kind(), path.resolve((Path) event.context()));
        }
        return key.reset();
    }

    private void notifyListeners(WatchEvent.Kind<?> kind, Path path) {
        FileEvent event = new FileEvent(path);
        log.debug("notifyListeners {}", event);
        if (kind == ENTRY_CREATE) {
            listeners.fire().onCreated(event);
        } else if (kind == ENTRY_MODIFY) {
            listeners.fire().onModified(event);
        } else if (kind == ENTRY_DELETE) {
            listeners.fire().onDeleted(event);
        }
    }

    public boolean isWatching() {
        return watching;
    }

    public FileWatcher addListener(FileListener listener) {
        listeners.addListener(listener);
        return this;
    }

    public FileWatcher removeListener(FileListener listener) {
        listeners.removeListener(listener);
        return this;
    }
}
