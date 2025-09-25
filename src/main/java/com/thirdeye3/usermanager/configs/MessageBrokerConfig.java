package com.thirdeye3.usermanager.configs;

import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import com.thirdeye3.usermanager.dtos.Topic;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Component
@ConfigurationProperties(prefix = "thirdeye.messagebroker")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class MessageBrokerConfig {
    private Map<String, Topic> topics; 
}

