package com.floodeer.throwout.util.update;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class UpdateEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private final UpdateType type;

    public UpdateEvent(UpdateType paramUpdateType) {
        this.type = paramUpdateType;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public UpdateType getType() {
        return this.type;
    }

    public HandlerList getHandlers() {
        return handlers;
    }
}