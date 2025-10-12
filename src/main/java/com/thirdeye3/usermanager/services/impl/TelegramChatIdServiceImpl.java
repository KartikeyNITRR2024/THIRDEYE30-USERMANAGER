package com.thirdeye3.usermanager.services.impl;

import com.thirdeye3.usermanager.dtos.TelegramChatIdDto;
import com.thirdeye3.usermanager.entities.TelegramChatId;
import com.thirdeye3.usermanager.entities.Threshold;
import com.thirdeye3.usermanager.entities.ThresholdGroup;
import com.thirdeye3.usermanager.exceptions.TelegramChatIdNotFoundException;
import com.thirdeye3.usermanager.exceptions.ThresholdNotFoundException;
import com.thirdeye3.usermanager.repositories.TelegramChatIdRepository;
import com.thirdeye3.usermanager.services.TelegramChatIdService;
import com.thirdeye3.usermanager.services.ThresholdGroupService;
import com.thirdeye3.usermanager.utils.Mapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class TelegramChatIdServiceImpl implements TelegramChatIdService {

    private static final Logger logger = LoggerFactory.getLogger(TelegramChatIdServiceImpl.class);

    @Autowired
    private TelegramChatIdRepository telegramChatIdRepository;

    @Autowired
    private ThresholdGroupService thresholdGroupService;

    private final Mapper mapper = new Mapper();

    @Override
    public TelegramChatIdDto addTelegramChatId(Long thresholdGroupId, TelegramChatIdDto dto, Long requesterId) {
        logger.info("Adding TelegramChatId for thresholdGroupId={}", thresholdGroupId);
        ThresholdGroup group = thresholdGroupService.getThresholdGroupByThresoldGroupId(thresholdGroupId, requesterId);
        TelegramChatId entity = mapper.toEntity(dto);
        entity.setThresholdGroup(group);
        TelegramChatId saved = telegramChatIdRepository.save(entity);
        thresholdGroupService.sendThresholdToOtherMicroservices(2, thresholdGroupId, "added");
        return mapper.toDto(saved);
    }
    
    @Override
    public void deleteTelegramChatId(Long id, Long requesterId) {
        logger.info("Deleting chat id with id={}", id);
        TelegramChatId telegramChatId = telegramChatIdRepository.findById(id)
                .orElseThrow(() -> new TelegramChatIdNotFoundException("Telegram Chat ID not found with id: " + id));
        ThresholdGroup group = thresholdGroupService.getThresholdGroupByThresoldGroupId(telegramChatId.getThresholdGroup().getId(), requesterId);
        telegramChatIdRepository.deleteById(id);
        telegramChatIdRepository.flush();
        try {
        	thresholdGroupService.sendThresholdToOtherMicroservices(2, telegramChatId.getThresholdGroup().getId(), "deleted");
        } catch (Exception e) {
            logger.error("Async call failed", e.getMessage());
        }
        
    }

    @Override
    public List<TelegramChatIdDto> getTelegramChatIdsByThresholdGroupId(Long thresholdGroupId, Long requesterId) {
    	ThresholdGroup group = thresholdGroupService.getThresholdGroupByThresoldGroupId(thresholdGroupId, requesterId);
        return mapper.toTelegramChatIdDtoList(
                telegramChatIdRepository.findByThresholdGroupId(thresholdGroupId)
        );
    }
}
