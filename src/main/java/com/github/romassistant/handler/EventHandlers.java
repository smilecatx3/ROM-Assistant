package com.github.romassistant.handler;

import com.github.romassistant.MessageController;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.message.TextMessageContent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

import javax.annotation.PostConstruct;


@Repository
@ConditionalOnBean(MessageController.class)
public class EventHandlers {
    private static final Log log = LogFactory.getLog(EventHandlers.class);

    private Set<TextMessageEventHandler> textMessageEventHandlers;

    @Autowired
    private RuinHandler ruinHandler;


    public Optional<EventHandler> get(MessageEvent<TextMessageContent> e) {
        for (var handler : textMessageEventHandlers) {
            if (handler.isApplicable(e.getMessage().getText().trim())) {
                log.trace("Using handler: " + handler);
                return Optional.of(handler);
            }
        }
        log.trace("No applicable handler is found.");
        return Optional.empty();
    }

    @PostConstruct
    private void init() {
        textMessageEventHandlers = Set.of(ruinHandler);
    }
}
