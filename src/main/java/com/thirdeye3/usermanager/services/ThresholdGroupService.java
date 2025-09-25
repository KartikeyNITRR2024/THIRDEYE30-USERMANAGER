package com.thirdeye3.usermanager.services;

import com.thirdeye3.usermanager.dtos.ThresholdGroupDto;
import com.thirdeye3.usermanager.entities.ThresholdGroup;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ThresholdGroupService {

	ThresholdGroupDto addThresholdGroup(Long userId, ThresholdGroupDto thresholdGroupDto);

    ThresholdGroupDto updateThresholdGroup(Long id, ThresholdGroupDto thresholdGroupDto);

    void removeThresholdGroup(Long id);

    ThresholdGroupDto getThresholdGroup(Long id);

    List<ThresholdGroupDto> getThresholdGroupsByUserId(Long userId);

	ThresholdGroup getThresholdGroupByThresoldGroupId(Long thresholdGroupId);

	Map<Long, ThresholdGroupDto> getAllActiveGroups(Integer type);

	void sendThresholdToOtherMicroservices(Integer type, Long groupId, String action);
	
	Set<Long> getTimeGapListForThresoldInSeconds();
	
}

