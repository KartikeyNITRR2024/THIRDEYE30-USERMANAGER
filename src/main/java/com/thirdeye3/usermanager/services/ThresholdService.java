package com.thirdeye3.usermanager.services;

import com.thirdeye3.usermanager.dtos.ThresholdDto;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ThresholdService {

	ThresholdDto createThreshold(Long thresoldGroupId, ThresholdDto thresholdDto);

    ThresholdDto getThresholdById(Long id);

    void deleteThresholdById(Long id);

    List<ThresholdDto> getThresholdsByGroupId(Long groupId);

}
