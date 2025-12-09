package com.thirdeye3.usermanager.controllers.thresoldgroups;

import com.thirdeye3.usermanager.dtos.Response;
import com.thirdeye3.usermanager.dtos.ThresholdGroupDto;
import com.thirdeye3.usermanager.services.ThresholdGroupService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/um/user/threshold-groups")
public class ThresholdGroupController {

    private static final Logger logger = LoggerFactory.getLogger(ThresholdGroupController.class);

    @Autowired
    private ThresholdGroupService thresholdGroupService;

    @PostMapping("/user/{userId}")
    public Response<ThresholdGroupDto> addThresholdGroup(
            @PathVariable("userId") Long userId,
            @Valid @RequestBody ThresholdGroupDto dto,
            @RequestHeader(value = "TOKEN-USER-ID", required = false) Long requesterId) {
        logger.info("Adding new threshold group for user {}", userId);
        ThresholdGroupDto saved = thresholdGroupService.addThresholdGroup(userId, dto, requesterId);
        return new Response<>(true, 0, null, saved);
    }

    @GetMapping("/{id}")
    public Response<ThresholdGroupDto> getThresholdGroup(@PathVariable("id") Long id,
            @RequestHeader(value = "TOKEN-USER-ID", required = false) Long requesterId) {
        logger.info("Fetching threshold group with id {}", id);
        ThresholdGroupDto group = thresholdGroupService.getThresholdGroup(id, requesterId);
        return new Response<>(true, 0, null, group);
    }
    
    @GetMapping("/gettimegap")
    public Response<List<Long>> getTimeGap()
    {
    	logger.info("Fetching time gap for groups");
    	return new Response<>(true, 0, null, null);
    }

    @PutMapping("/{id}")
    public Response<ThresholdGroupDto> updateThresholdGroup(
            @PathVariable("id") Long id,
            @Valid @RequestBody ThresholdGroupDto dto,
            @RequestHeader(value = "TOKEN-USER-ID", required = false) Long requesterId) {
        logger.info("Updating threshold group with id {}", id);
        ThresholdGroupDto updated = thresholdGroupService.updateThresholdGroup(id, dto, requesterId);
        return new Response<>(true, 0, null, updated);
    }

    @DeleteMapping("/{id}")
    public Response<Void> removeThresholdGroup(@PathVariable("id") Long id,
            @RequestHeader(value = "TOKEN-USER-ID", required = false) Long requesterId) {
        logger.info("Deleting threshold group with id {}", id);
        thresholdGroupService.removeThresholdGroup(id, requesterId);
        return new Response<>(true, 0, null, null);
    }

    @GetMapping("/user/{userId}")
    public Response<List<ThresholdGroupDto>> getThresholdGroupsByUser(@PathVariable("userId") Long userId,
            @RequestHeader(value = "TOKEN-USER-ID", required = false) Long requesterId) {
        logger.info("Fetching threshold groups for user {}", userId);
        //List<ThresholdGroupDto> groups = thresholdGroupService.getThresholdGroupsByUserId(userId, requesterId);
        List<ThresholdGroupDto> groups = thresholdGroupService.getLiteGroupsByUserIdUsingProjection(userId, requesterId);
        return new Response<>(true, 0, null, groups);
    }
    
    @GetMapping("/getTimeSet")
    public Response<Set<Long>> getTimeSet() {
    	logger.info("Fetching thresold time set");
        return new Response<>(true, 0, null, thresholdGroupService.getTimeGapListForThresoldInSeconds());
    }
}
