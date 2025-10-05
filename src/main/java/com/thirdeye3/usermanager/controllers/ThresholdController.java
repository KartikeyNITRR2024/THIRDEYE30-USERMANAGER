package com.thirdeye3.usermanager.controllers;

import com.thirdeye3.usermanager.dtos.Response;
import com.thirdeye3.usermanager.dtos.ThresholdDto;
import com.thirdeye3.usermanager.services.ThresholdService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/um/user/thresholds")
public class ThresholdController {

    private static final Logger logger = LoggerFactory.getLogger(ThresholdController.class);

    @Autowired
    private ThresholdService thresholdService;

    @PostMapping("/group/{groupId}")
    public Response<ThresholdDto> createThreshold(@PathVariable("groupId") Long groupId,
                                                  @Valid @RequestBody ThresholdDto thresholdDto) {
        logger.info("Creating new threshold for group id {}", groupId);
        ThresholdDto saved = thresholdService.createThreshold(groupId, thresholdDto);
        return new Response<>(true, 0, null, saved);
    }

    @DeleteMapping("/{id}")
    public Response<Void> deleteThresholdById(@PathVariable("id") Long id) {
        logger.info("Deleting threshold with ID {}", id);
        thresholdService.deleteThresholdById(id);
        return new Response<>(true, 0, null, null);
    }

    @GetMapping("/group/{groupId}")
    public Response<List<ThresholdDto>> getThresholdsByGroupId(@PathVariable("groupId") Long groupId) {
        logger.info("Fetching thresholds for group {}", groupId);
        List<ThresholdDto> thresholds = thresholdService.getThresholdsByGroupId(groupId);
        return new Response<>(true, 0, null, thresholds);
    }
}
