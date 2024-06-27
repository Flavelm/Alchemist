package net.tixan.alchemist.inventory.eventhandler;

import com.google.inject.Inject;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.tixan.alchemist.configuration.Config;
import net.tixan.alchemist.configuration.ConfigLoader;
import org.bukkit.Server;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;

@RequiredArgsConstructor
public abstract class AbstractHandler<T extends Event, C> implements Handler<T> {

    @Getter
    private final Class<T> clazz;
    @Getter
    private final EventPriority eventPriority;
    @Getter(AccessLevel.PROTECTED)
    @Inject
    private Server server;
    @Getter(AccessLevel.PROTECTED)
    @Inject
    private Logger logger;
    @Getter(AccessLevel.PROTECTED)
    @Inject
    private ConfigLoader config;
    @Getter
    private C ctx;

    @Override
    public void execute(@NotNull Listener listener, @NotNull Event event) {
        T e = (T) event;
        ctx = useCtx(e);
        if (predicate(e)) {
            handle(e);
        }
        ctx = null;
    }

    protected C useCtx(T event) {
        return null;
    }

    public boolean predicate(T event) {
        return true;
    }

    public abstract void handle(T event);

    protected Config config() {
        return config.get();
    }
}
