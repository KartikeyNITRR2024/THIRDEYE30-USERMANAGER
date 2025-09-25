package com.thirdeye3.usermanager.externalcontollers;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.thirdeye3.usermanager.configs.FeignConfig;
import com.thirdeye3.usermanager.dtos.Response;

@FeignClient(
    name = "${spring.application.name}", 
    url = "${self.url:}",
    configuration = FeignConfig.class
)
public interface SelfClient {

    @GetMapping("/api/statuschecker/{id}/{code}")
    Response<String> statusChecker(
        @PathVariable("id") Integer id,
        @PathVariable("code") String code
    );
}
