package com.thirdeye3.usermanager.externalcontollers;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.thirdeye3.usermanager.configs.FeignConfig;
import com.thirdeye3.usermanager.dtos.Response;
import com.thirdeye3.usermanager.dtos.ThresholdGroupDto;

@FeignClient(
		name = "THIRDEYE30-MESSENGER",
		configuration = FeignConfig.class
)
public interface MessengerClient {

	 @PostMapping("/me/update/messsenger")
	    Response<Boolean> updateOrAddMessenger(
	            @RequestBody ThresholdGroupDto thresholdGroupDto
	    );
}