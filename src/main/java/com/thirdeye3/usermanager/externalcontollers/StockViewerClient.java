package com.thirdeye3.usermanager.externalcontollers;

import com.thirdeye3.usermanager.configs.FeignConfig;
import com.thirdeye3.usermanager.dtos.Response;
import com.thirdeye3.usermanager.dtos.ThresholdGroupDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
		name = "THIRDEYE30-STOCKVIEWER",
		configuration = FeignConfig.class
)
public interface StockViewerClient {

    @PostMapping("/sv/update/thresholds")
    Response<Boolean> updateOrAddThresholdGroupDto(
            @RequestBody ThresholdGroupDto thresholdGroupDto
    );
}
