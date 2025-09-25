package com.thirdeye3.usermanager.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.thirdeye3.usermanager.dtos.Response;
import com.thirdeye3.usermanager.utils.Initiatier;

@RestController
@RequestMapping("/api/updateinitiatier")
public class InitiatierController {

    private static final Logger logger = LoggerFactory.getLogger(InitiatierController.class);

    @Autowired
    private Initiatier initiatier;

    @Value("${thirdeye.priority}")
    private Integer priority;

    @GetMapping("/{priority}")
    public Response<String> updateInitiatier(@PathVariable("priority") Integer requestPriority) {
        try {
            logger.info("Updating initiatier with priority: {}", requestPriority);
            if(requestPriority<priority)
            {
            	initiatier.init();
                return new Response<>(true, 0, null, "Initiatier updated with priority: " + requestPriority);
            }
            return new Response<>(true, 0, null, "Initiatier not insitiatied because priority is low: " + requestPriority);
        } catch (Exception e) {
            logger.error("Error updating initiatier", e);
            return new Response<>(false, 0, "Failed to update initiatier", null);
        }
    }
}