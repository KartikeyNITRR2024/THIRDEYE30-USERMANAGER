package com.thirdeye3.usermanager.services.impl;

import com.thirdeye3.usermanager.dtos.ThresholdDto;
import com.thirdeye3.usermanager.entities.Threshold;
import com.thirdeye3.usermanager.entities.ThresholdGroup;
import com.thirdeye3.usermanager.exceptions.ThresholdNotFoundException;
import com.thirdeye3.usermanager.repositories.ThresholdRepository;
import com.thirdeye3.usermanager.services.PropertyService;
import com.thirdeye3.usermanager.services.ThresholdGroupService;
import com.thirdeye3.usermanager.services.ThresholdService;
import com.thirdeye3.usermanager.utils.Mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.*;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Service
public class ThresholdServiceImpl implements ThresholdService {

    private static final Logger logger = LoggerFactory.getLogger(ThresholdServiceImpl.class);

    @Autowired
    private ThresholdRepository thresholdRepository;

    @Autowired
    private ThresholdGroupService thresholdGroupService;

    @Autowired
    private PropertyService propertyService;

    @Autowired
    private Mapper mapper;

    @Caching(
        put = {
            @CachePut(value = "thresholdCache", key = "#result.id")
        },
        evict = {
            @CacheEvict(value = "thresholdsByGroupCache", key = "#thresoldGroupId")
        }
    )
    @Override
    public ThresholdDto createThreshold(Long thresoldGroupId, ThresholdDto thresholdDto, Long requesterId) {

        logger.info("Creating threshold for groupId={}", thresoldGroupId);

        Threshold entity = mapper.toEntity(thresholdDto);

        if (thresholdRepository.countByThresholdGroupId(thresoldGroupId)
                == propertyService.getMaximumNoOfThresoldPerGroup()) {
            throw new ThresholdNotFoundException("Maximum number of threshold used");
        }

        if (!propertyService.getTimeGapListForThresoldInSeconds()
                .contains(thresholdDto.getTimeGapInSeconds())
                && thresholdDto.getTimeGapInSeconds() != -1
                && thresholdDto.getTimeGapInSeconds() != -2) {
            throw new ThresholdNotFoundException("Invalid time gap");
        }

        ThresholdGroup thresholdGroup =
                thresholdGroupService.getThresholdGroupByThresoldGroupId(thresoldGroupId, requesterId);

        entity.setThresholdGroup(thresholdGroup);

        Threshold saved = thresholdRepository.save(entity);

        thresholdGroupService.sendThresholdToOtherMicroservices(1, thresoldGroupId, null);

        return mapper.toDto(saved);
    }

    @Cacheable(value = "thresholdCache", key = "#id")
    @Override
    public ThresholdDto getThresholdById(Long id, Long requesterId) {

        logger.info("Fetching Threshold id={}", id);

        Threshold threshold = thresholdRepository.findById(id)
                .orElseThrow(() -> new ThresholdNotFoundException("Threshold not found with id: " + id));

        thresholdGroupService.getThresholdGroupByThresoldGroupId(
                threshold.getThresholdGroup().getId(), requesterId
        );

        return mapper.toDto(threshold);
    }
    
    

    @CacheEvict(value = "thresholdsByGroupCache", key = "#result")
    @Override
    public Long deleteThresholdById(Long id, Long requesterId) {

        logger.info("Deleting threshold with id={}", id);

        Threshold threshold = thresholdRepository.findById(id)
                .orElseThrow(() -> new ThresholdNotFoundException("Threshold not found with id :" + id));

        Long groupId = threshold.getThresholdGroup().getId();

        thresholdGroupService.getThresholdGroupByThresoldGroupId(groupId, requesterId);

        thresholdRepository.delete(threshold);
        thresholdRepository.flush();

        try {
            thresholdGroupService.sendThresholdToOtherMicroservices(1, groupId, null);
        } catch (Exception e) {
            logger.error("Async call failed {}", e.getMessage());
        }

        return groupId;
    }

    
    

    @Cacheable(value = "thresholdsByGroupCache", key = "#groupId")
    @Override
    public List<ThresholdDto> getThresholdsByGroupId(Long groupId, Long requesterId) {

        logger.info("Fetching thresholds for groupId={}", groupId);

        thresholdGroupService.getThresholdGroupByThresoldGroupId(groupId, requesterId);

        return mapper.toThresholdDtoList(thresholdRepository.findByThresholdGroupId(groupId));
    }
}
