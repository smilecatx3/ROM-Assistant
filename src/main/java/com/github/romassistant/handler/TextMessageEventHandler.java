package com.github.romassistant.handler;


abstract class TextMessageEventHandler implements EventHandler {
    public abstract boolean isApplicable(String text);
}
