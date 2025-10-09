package com.thirdeye3.usermanager.services;

import com.thirdeye3.usermanager.dtos.ThresholdGroupDto;
import com.thirdeye3.usermanager.entities.ThresholdGroup;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ThresholdGroupService {

	ThresholdGroupDto addThresholdGroup(Long userId, ThresholdGroupDto thresholdGroupDto, Long requesterId);

    ThresholdGroupDto updateThresholdGroup(Long id, ThresholdGroupDto thresholdGroupDto, Long requesterId);

    void removeThresholdGroup(Long id, Long requesterId);

    ThresholdGroupDto getThresholdGroup(Long id, Long requesterId);

    List<ThresholdGroupDto> getThresholdGroupsByUserId(Long userId, Long requesterId);

	ThresholdGroup getThresholdGroupByThresoldGroupId(Long thresholdGroupId);
	
	ThresholdGroup getThresholdGroupByThresoldGroupId(Long thresholdGroupId, Long requesterId);

	Map<Long, ThresholdGroupDto> getAllActiveGroups(Integer type);

	void sendThresholdToOtherMicroservices(Integer type, Long groupId, String action);
	
	Set<Long> getTimeGapListForThresoldInSeconds();
	
}

