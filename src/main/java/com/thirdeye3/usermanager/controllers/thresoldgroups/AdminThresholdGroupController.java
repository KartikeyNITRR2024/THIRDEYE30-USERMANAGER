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

@RestController
@RequestMapping("/um/admin/threshold-groups")
public class AdminThresholdGroupController {

    private static final Logger logger = LoggerFactory.getLogger(AdminThresholdGroupController.class);

    @Autowired
    private ThresholdGroupService thresholdGroupService;

    @GetMapping("/active/{type}")
    public Response<Map<Long, ThresholdGroupDto>> getAllActiveGroups(@PathVariable("type") Integer type) {
        logger.info("Fetching all active threshold groups of type {}", type);
        Map<Long, ThresholdGroupDto> groups = thresholdGroupService.getAllActiveGroups(type);
        return new Response<>(true, 0, null, groups);
    }
}
