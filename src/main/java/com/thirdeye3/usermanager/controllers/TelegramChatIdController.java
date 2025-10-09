package com.thirdeye3.usermanager.controllers;

import com.thirdeye3.usermanager.dtos.Response;
import com.thirdeye3.usermanager.dtos.TelegramChatIdDto;
import com.thirdeye3.usermanager.services.TelegramChatIdService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/um/user/telegram-chat-ids")
public class TelegramChatIdController {

    private static final Logger logger = LoggerFactory.getLogger(TelegramChatIdController.class);

    @Autowired
    private TelegramChatIdService telegramChatIdService;

    @PostMapping("/{thresholdGroupId}")
    public Response<TelegramChatIdDto> addTelegramChatId(
            @PathVariable("thresholdGroupId") Long thresholdGroupId,
            @Valid @RequestBody TelegramChatIdDto dto,
            @RequestHeader(value = "TOKEN-USER-ID", required = false) Long requesterId) {
        logger.info("Adding TelegramChatId for thresholdGroupId {}", thresholdGroupId);
        TelegramChatIdDto saved = telegramChatIdService.addTelegramChatId(thresholdGroupId, dto, requesterId);
        return new Response<>(true, 0, null, saved);
    }

    @DeleteMapping("/{id}")
    public Response<Void> deleteTelegramChatId(@PathVariable("id") Long id,
            @RequestHeader(value = "TOKEN-USER-ID", required = false) Long requesterId) {
        logger.info("Deleting TelegramChatId {}", id);
        telegramChatIdService.deleteTelegramChatId(id, requesterId);
        return new Response<>(true, 0, null, null);
    }

    @GetMapping("/group/{groupId}")
    public Response<List<TelegramChatIdDto>> getTelegramChatIdsByGroupId(@PathVariable("groupId") Long groupId,
            @RequestHeader(value = "TOKEN-USER-ID", required = false) Long requesterId) {
        logger.info("Fetching TelegramChatIds for threshold group {}", groupId);
        List<TelegramChatIdDto> chatIds = telegramChatIdService.getTelegramChatIdsByThresholdGroupId(groupId, requesterId);
        return new Response<>(true, 0, null, chatIds);
    }
}
