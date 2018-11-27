package com.github.romassistant.handler;

import com.linecorp.bot.model.event.Event;


@FunctionalInterface
public interface EventHandler {
    void handle(Event e);
}
