package com.thirdeye3.usermanager.services;

import com.thirdeye3.usermanager.dtos.TelegramChatIdDto;
import com.thirdeye3.usermanager.entities.TelegramChatId;
import com.thirdeye3.usermanager.enums.WorkType;

import java.util.List;
import java.util.Map;

public interface TelegramChatIdService {

	TelegramChatIdDto addTelegramChatId(Long thresholdGroupId, TelegramChatIdDto dto);

    void deleteTelegramChatId(Long id);

    List<TelegramChatIdDto> getTelegramChatIdsByThresholdGroupId(Long thresholdGroupId);
}
