package com.thirdeye3.usermanager.exceptions;

public class TelegramChatIdNotFoundException extends RuntimeException {
    public TelegramChatIdNotFoundException(String message) {
        super(message);
    }
}

