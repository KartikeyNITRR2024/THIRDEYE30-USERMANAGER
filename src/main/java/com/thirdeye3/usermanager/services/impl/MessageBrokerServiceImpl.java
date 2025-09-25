package com.thirdeye3.usermanager.services.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.thirdeye3.usermanager.dtos.Response;
import com.thirdeye3.usermanager.exceptions.MessageBrokerException;
import com.thirdeye3.usermanager.externalcontollers.MessageBrokerClient;
import com.thirdeye3.usermanager.services.MessageBrokerService;
import com.thirdeye3.usermanager.configs.MessageBrokerConfig;


@Service
public class MessageBrokerServiceImpl implements MessageBrokerService {
	
    private static final Logger logger = LoggerFactory.getLogger(MessageBrokerServiceImpl.class);
    
    @Autowired
    private MessageBrokerConfig messageBrokerConfig;
    
    @Autowired 
    private MessageBrokerClient messageBroker;
    
    @Override
    public void sendMessages(String topicName, Object messages)
    {
    	if(!messageBrokerConfig.getTopics().containsKey(topicName))
    	{
    		throw new MessageBrokerException("Does not have any topic with topic name "+topicName);
    	}
    	try {
    		Response<String> response = messageBroker.setMessages(topicName, messageBrokerConfig.getTopics().get(topicName).getTopicKey(), messages);
    		if (response.isSuccess()) {
                logger.info("Successfully send messages to message broker with topic name "+topicName);
            }
    		else
    		{
    		    throw new MessageBrokerException("Failed to send messages to message broker with topic name "+topicName+" "+response.getErrorMessage());
    		}
    	} catch (Exception e) {
    		throw new MessageBrokerException("Failed to send messages to message broker with topic name "+topicName+" "+e.getMessage());
        }
    }
}
