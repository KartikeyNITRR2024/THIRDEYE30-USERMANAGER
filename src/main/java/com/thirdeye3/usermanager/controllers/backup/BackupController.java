package com.thirdeye3.usermanager.controllers.backup;

import com.thirdeye3.usermanager.dtos.AuthUserPayload;
import com.thirdeye3.usermanager.dtos.LoginResponsePayload;
import com.thirdeye3.usermanager.dtos.Response;
import com.thirdeye3.usermanager.exceptions.CSVException;
import com.thirdeye3.usermanager.services.BackupService;
import com.thirdeye3.usermanager.services.ThresholdGroupService;
import com.thirdeye3.usermanager.services.UserService;
import com.thirdeye3.usermanager.utils.TimeManager;

import jakarta.validation.Valid;

import java.io.ByteArrayInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/um/admin/backup")
public class BackupController {

    private static final Logger logger = LoggerFactory.getLogger(BackupController.class);

    @Autowired
    private ThresholdGroupService thresholdGroupService;

    @Autowired
    private BackupService backupService;

    @Autowired
    private TimeManager timeManager;
    
    @GetMapping()
    public Response<String> sendFullBackupToEmail() {
    	logger.info("Triggered system backup and email process...");
        String filename = backupService.exportAllToZip();
        logger.info("System backup ZIP generated and email sent successfully: {}", filename);
        return new Response<>(true, 0, null, "System backup ZIP generated and email sent successfully: "+ filename);
    }
}
