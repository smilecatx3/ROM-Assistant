package com.github.romassistant;

import com.github.romassistant.handler.EventHandlers;
import com.github.romassistant.model.db.cache.UserDbCache;
import com.linecorp.bot.model.event.Event;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.spring.boot.annotation.EventMapping;
import com.linecorp.bot.spring.boot.annotation.LineMessageHandler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;


@LineMessageHandler
@ConditionalOnExpression("${service.line.message.enabled:true}")
public class MessageController {
    private static final Log log = LogFactory.getLog(MessageController.class);

    @Autowired
    private UserDbCache userDbCache;
    @Autowired
    private EventHandlers eventHandlers;


    @EventMapping
    public void handleTextMessageEvent(MessageEvent<TextMessageContent> e) {
        log.debug("Event: " + e);
        userDbCache.update(e.getSource().getUserId());
        var handler = eventHandlers.get(e);
        handler.ifPresent(eventHandler -> eventHandler.handle(e));
    }

    @EventMapping
    public void handleDefaultEvent(Event e) {
        log.debug("Event: " + e);
    }
}
