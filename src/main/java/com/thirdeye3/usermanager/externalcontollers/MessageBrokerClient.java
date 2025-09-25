package com.thirdeye3.usermanager.externalcontollers;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.thirdeye3.usermanager.configs.FeignConfig;
import com.thirdeye3.usermanager.dtos.Response;

@FeignClient(
		name = "THIRDEYE30-MESSAGEBROKER",
		configuration = FeignConfig.class
)
public interface MessageBrokerClient {
    @PostMapping("/mb/message/multiple/{topicname}/{topickey}")
    Response<String> setMessages(
            @PathVariable("topicname") String topicName,
            @PathVariable("topickey") String topicKey,
            @RequestBody Object messages
    );
}

