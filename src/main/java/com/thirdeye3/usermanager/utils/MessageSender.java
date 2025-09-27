package com.thirdeye3.usermanager.utils;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.thirdeye3.usermanager.dtos.TelegramMessage;
import com.thirdeye3.usermanager.entities.TelegramChatId;
import com.thirdeye3.usermanager.entities.Threshold;
import com.thirdeye3.usermanager.enums.WorkType;
import com.thirdeye3.usermanager.services.MessageBrokerService;

@Component
public class MessageSender {

    @Autowired
    private MessageBrokerService messageBrokerService;

    @Async
    @Transactional
    public void sendTelegramMessage(String userName, String groupName, List<Threshold> thresholds, List<TelegramChatId> telegramChatIds) {
        String firstNameCamel = toCamelCase(userName);
        StringBuilder message = new StringBuilder()
                .append("<b>THIRDEYE Notification Service</b>\n")
                .append("<i>Your communication gateway</i>\n\n")
                .append("👋 <b>Dear ").append(escapeHtml(firstNameCamel)).append(",</b>\n\n")
                .append("🔔 <b>Thresholds have been updated for your group ")
                .append(escapeHtml(groupName))
                .append(".</b>\n\n");

        for (Threshold t : thresholds) {
            message.append("✅ <b>");
            if(t.getTimeGapInSeconds() == -1)
            {
            	message.append("Previous closing day price & ");
            }
            else if (t.getTimeGapInSeconds() == -2)
            {
            	message.append("Current opening day price & ");
            }
            else
            {
                message.append(escapeHtml(String.valueOf(t.getTimeGapInSeconds())))
                .append(" seconds & ");
            }
            message.append(escapeHtml(String.valueOf(t.getPriceGap())));
            if (t.getType() == 0) {
                message.append(" %</b>\n");
            } else {
                message.append(" units</b>\n");
            }
        }

        message.append("\n💡 Contact support if needed.\n\n")
               .append("👍 Best regards,\n<b>THIRDEYE Team</b>");

        sendMessageToTelegram(message.toString(), WorkType.THRESOLD, telegramChatIds);
    }

    @Async
    @Transactional
    public void sendTelegramMessage(String userName, String action, String groupName, List<TelegramChatId> telegramChatIds) {
        String firstNameCamel = toCamelCase(userName);
        StringBuilder message = new StringBuilder()
                .append("<b>THIRDEYE Notification Service</b>\n")
                .append("<i>Your communication gateway</i>\n\n")
                .append("👋 <b>Dear ").append(firstNameCamel).append(",</b>\n\n")
                .append("🔔 <b>Your Telegram chat ID has been ").append(action)
                .append(" for group ").append(groupName)
                .append(".</b>\n\n")
                .append("💡 Contact support if needed.\n\n")
                .append("👍 Best regards,\n<b>THIRDEYE Team</b>");
   
        sendMessageToTelegram(message.toString(), WorkType.THRESOLD, telegramChatIds);
    }

    private void sendMessageToTelegram(String message, WorkType workType, List<TelegramChatId> telegramChatIds) {
    	
        List<TelegramMessage> telegramMessages = telegramChatIds
                .stream()
                .filter(c -> (workType == null || c.getWorkType() == workType))
                .map(c -> {
                    TelegramMessage telegramMessage = new TelegramMessage();
                    telegramMessage.setChatId(c.getChatId());
                    telegramMessage.setChats(List.of(message));
                    return telegramMessage;
                })
                .collect(Collectors.toList());

        if (!telegramMessages.isEmpty()) {
            messageBrokerService.sendMessages("users", telegramMessages);
        }
    }

    private String toCamelCase(String name) {
        if (name == null || name.isEmpty()) return "";
        String lower = name.toLowerCase();
        return Character.toUpperCase(lower.charAt(0)) + lower.substring(1);
    }
    
    private String escapeHtml(String text) {
        if (text == null) {
            return "";
        }
        return text.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;");
    }
}
