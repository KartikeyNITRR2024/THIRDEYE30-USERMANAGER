package com.thirdeye3.usermanager.services;

public interface MessageBrokerService {
	void sendMessages(String topicName, Object messages);
}
