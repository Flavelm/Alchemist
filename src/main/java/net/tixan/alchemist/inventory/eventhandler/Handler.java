package net.tixan.alchemist.inventory.eventhandler;

import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;

public interface Handler<T extends Event> extends Listener, EventExecutor {
    Class<T> getClazz();

    EventPriority getEventPriority();
}
