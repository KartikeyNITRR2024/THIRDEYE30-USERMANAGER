package com.thirdeye3.usermanager.externalcontollers;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import com.thirdeye3.usermanager.configs.FeignConfig;
import com.thirdeye3.usermanager.dtos.Response;

@FeignClient(
		name = "THIRDEYE30-PROPERTYMANAGER",
		configuration = FeignConfig.class
)
public interface PropertyManagerClient {
    @GetMapping("/pm/properties")
    Response<Map<String, Object>> getProperties();
}
