package com.anrisoftware.anlopencl.jmeapp.actors;

import static com.anrisoftware.anlopencl.jmeapp.controllers.JavaFxUtil.runFxThread;
import static java.awt.image.BufferedImage.TYPE_INT_ARGB;
import static javafx.embed.swing.SwingFXUtils.toFXImage;

import com.anrisoftware.anlopencl.jmeapp.messages.AttachGuiMessage;
import com.anrisoftware.anlopencl.jmeapp.messages.LocalizeControlsMessage;
import com.anrisoftware.anlopencl.jmeapp.messages.MessageActor.Message;
import com.anrisoftware.anlopencl.jmeapp.model.GameSettingsProvider;
import com.anrisoftware.anlopencl.jmeapp.model.ObservableGameSettings;
import com.anrisoftware.resources.images.external.Images;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.BehaviorBuilder;
import akka.actor.typed.javadsl.Behaviors;
import javafx.scene.control.ContentDisplay;
import javafx.scene.image.ImageView;
import lombok.extern.slf4j.Slf4j;

/**
 * Common actor for main window panes.
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 * @param <T> the controller type.
 */
@Slf4j
public abstract class AbstractPaneActor<T> {

    protected final ActorContext<Message> context;

    protected final GameSettingsProvider gsp;

    protected final Images appIcons;

    protected T controller;

    protected AbstractPaneActor(ActorContext<Message> context, GameSettingsProvider gsp, Images appIcons) {
        this.context = context;
        this.gsp = gsp;
        this.appIcons = appIcons;
        var gs = gsp.get();
        gs.locale.addListener((observable, oldValue, newValue) -> tellLocalizeControlsSelf(gs));
        gs.iconSize.addListener((observable, oldValue, newValue) -> tellLocalizeControlsSelf(gs));
        gs.textPosition.addListener((observable, oldValue, newValue) -> tellLocalizeControlsSelf(gs));
    }

    private void tellLocalizeControlsSelf(ObservableGameSettings gs) {
        context.getSelf().tell(new LocalizeControlsMessage(gs));
    }

    public final Behavior<Message> start() {
        return Behaviors.receive(Message.class)//
                .onMessage(InitialStateMessage.class, this::onInitialState)//
                .onMessage(LocalizeControlsMessage.class, this::onLocalizeControls)//
                .build();
    }

    private Behavior<Message> onInitialState(InitialStateMessage m) {
        log.debug("onInitialState");
        setupController(m);
        initialState(m);
        return getBehaviorInitialState().build();
    }

    protected abstract void initialState(InitialStateMessage m);

    protected abstract void setupController(InitialStateMessage m);

    protected BehaviorBuilder<Message> getBehaviorInitialState() {
        return Behaviors.receive(Message.class)//
                .onMessage(LocalizeControlsMessage.class, this::onLocalizeControls)//
                .onMessage(AttachGuiMessage.class, this::onAttachGui)//
        ;
    }

    private Behavior<Message> onAttachGui(AttachGuiMessage m) {
        log.debug("onAttachGui {}", m);
        return Behaviors.same();
    }

    private Behavior<Message> onLocalizeControls(LocalizeControlsMessage m) {
        log.debug("onLocalizeControls");
        runFxThread(() -> {
            setupIcons(m);
        });
        return Behaviors.same();
    }

    private void setupIcons(LocalizeControlsMessage m) {
        var contentDisplay = ContentDisplay.LEFT;
        switch (m.textPosition) {
        case NONE:
            contentDisplay = ContentDisplay.GRAPHIC_ONLY;
            break;
        case BOTTOM:
            contentDisplay = ContentDisplay.TOP;
            break;
        case LEFT:
            contentDisplay = ContentDisplay.RIGHT;
            break;
        case RIGHT:
            contentDisplay = ContentDisplay.LEFT;
            break;
        case TOP:
            contentDisplay = ContentDisplay.BOTTOM;
            break;
        }
    }

    private ImageView loadCommandIcon(LocalizeControlsMessage m, String name) {
        return new ImageView(
                toFXImage(appIcons.getResource(name, m.locale, m.iconSize).getBufferedImage(TYPE_INT_ARGB), null));
    }

    private ImageView loadControlIcon(LocalizeControlsMessage m, String name) {
        return new ImageView(toFXImage(
                appIcons.getResource(name, m.locale, m.iconSize.getTwoSmaller()).getBufferedImage(TYPE_INT_ARGB),
                null));
    }

}
