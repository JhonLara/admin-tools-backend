package com.admintools.domain.port;

public interface NotificationPort {
    String sendMessage(String chatId, String message);
}
