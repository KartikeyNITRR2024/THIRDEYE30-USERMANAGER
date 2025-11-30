package com.thirdeye3.usermanager.services;

import com.thirdeye3.usermanager.dtos.ThresholdDto;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ThresholdService {

	ThresholdDto createThreshold(Long thresoldGroupId, ThresholdDto thresholdDto, Long requesterId);

    ThresholdDto getThresholdById(Long id, Long requesterId);

    Long deleteThresholdById(Long id, Long requesterId);

    List<ThresholdDto> getThresholdsByGroupId(Long groupId, Long requesterId);

}
