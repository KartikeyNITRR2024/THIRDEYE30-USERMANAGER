package com.thirdeye3.usermanager.services.impl;

import com.thirdeye3.usermanager.dtos.ThresholdGroupDto;
import com.thirdeye3.usermanager.entities.TelegramChatId;
import com.thirdeye3.usermanager.entities.Threshold;
import com.thirdeye3.usermanager.entities.ThresholdGroup;
import com.thirdeye3.usermanager.entities.User;
import com.thirdeye3.usermanager.exceptions.ForbiddenException;
import com.thirdeye3.usermanager.exceptions.ThresholdGroupNotFoundException;
import com.thirdeye3.usermanager.repositories.ThresholdGroupRepository;
import com.thirdeye3.usermanager.services.MessengerService;
import com.thirdeye3.usermanager.services.PropertyService;
import com.thirdeye3.usermanager.services.StockViewerService;
import com.thirdeye3.usermanager.services.ThresholdGroupService;
import com.thirdeye3.usermanager.services.UserService;
import com.thirdeye3.usermanager.utils.Mapper;
import com.thirdeye3.usermanager.utils.MessageSender;
import com.thirdeye3.usermanager.utils.StockListSorter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ThresholdGroupServiceImpl implements ThresholdGroupService {

    private static final Logger logger = LoggerFactory.getLogger(ThresholdGroupServiceImpl.class);

    @Autowired
    private ThresholdGroupRepository thresholdGroupRepository;

    @Autowired
    private UserService userService;
    
    @Autowired
    private StockViewerService stockViewerService;
    
    @Autowired
    private MessageSender messageSender;
    
    @Autowired
    private MessengerService messengerService;
    
    @Autowired
    private PropertyService propertyService;

    private final Mapper mapper = new Mapper();

    @Override
    public ThresholdGroup getThresholdGroupByThresoldGroupId(Long thresholdGroupId) {
        logger.info("Fetching ThresholdGroup by id={}", thresholdGroupId);

        ThresholdGroup group = thresholdGroupRepository.findById(thresholdGroupId)
                .orElseThrow(() -> new ThresholdGroupNotFoundException(
                        "Threshold Group not found with id: " + thresholdGroupId));

        if (!Boolean.TRUE.equals(group.getActive())) {
            logger.warn("ThresholdGroup with id={} is inactive", thresholdGroupId);
            throw new ThresholdGroupNotFoundException(
                    "Threshold Group with id " + thresholdGroupId + " is inactive");
        }

        return group;
    }
    
    
    @Override
    public ThresholdGroup getThresholdGroupByThresoldGroupId(Long thresholdGroupId, Long requesterId) {
        logger.info("Fetching ThresholdGroup by id={}", thresholdGroupId);

        ThresholdGroup group = thresholdGroupRepository.findById(thresholdGroupId)
                .orElseThrow(() -> new ThresholdGroupNotFoundException(
                        "Threshold Group not found with id: " + thresholdGroupId));
        
        if(requesterId.longValue() != group.getUser().getUserId().longValue()) 
        {
        	throw new ForbiddenException("Forbidden");
        }

        if (!Boolean.TRUE.equals(group.getActive())) {
            logger.warn("ThresholdGroup with id={} is inactive", thresholdGroupId);
            throw new ThresholdGroupNotFoundException(
                    "Threshold Group with id " + thresholdGroupId + " is inactive");
        }

        return group;
    }


    @CachePut(value = "thresholdGroupCache", key = "#result.id")
    @CacheEvict(value = "thresholdGroupsCache", key = "#userId")
    @Override
    public ThresholdGroupDto addThresholdGroup(Long userId, ThresholdGroupDto thresholdGroupDto, Long requesterId) {
        logger.info("Adding ThresholdGroup for userId={}", userId);
        User user = userService.getUserByUserId(userId);
        if(requesterId.longValue() != user.getUserId().longValue()) 
        {
        	throw new ForbiddenException("Forbidden");
        }
        if(thresholdGroupRepository.countActiveByUserId(user.getUserId()) >= propertyService.getMaximumNoOfGroupPerUser())
        {
        	throw new ThresholdGroupNotFoundException(
                    "Maximum number of group reached");
        }
        ThresholdGroup entity = mapper.toEntity(thresholdGroupDto);
        entity.setUser(user);
        if ((entity.getAllStocks() == true && entity.getStockList() != null && !entity.getStockList().isEmpty()) ||
        	    (entity.getAllStocks() == false && (entity.getStockList() == null))) {

        	    throw new ThresholdGroupNotFoundException("Invalid stock list");
        }
        
        ThresholdGroup saved = thresholdGroupRepository.save(entity);
        logger.info("Added ThresholdGroup id={} for userId={}", saved.getId(), user.getUserId());
        return mapper.toDto(saved);
    }

    @CachePut(value = "thresholdGroupCache", key = "#id")
    @CacheEvict(value = "thresholdGroupsCache", key = "#requesterId")
    @Override
    public ThresholdGroupDto updateThresholdGroup(Long id, ThresholdGroupDto thresholdGroupDto, Long requesterId) {
        logger.info("Updating ThresholdGroup id={}", id);

        int type1 = -1;
        int type2 = -1;

        ThresholdGroup existing = thresholdGroupRepository.findById(id)
                .orElseThrow(() -> new ThresholdGroupNotFoundException("Threshold Group not found with id: " + id));

        if (requesterId.longValue() != existing.getUser().getUserId().longValue()) {
            throw new ForbiddenException("Forbidden");
        }

        if ((thresholdGroupDto.getAllStocks() == true && thresholdGroupDto.getStockList() != null && !thresholdGroupDto.getStockList().isEmpty()) ||
                (thresholdGroupDto.getAllStocks() == false && (thresholdGroupDto.getStockList() == null))) {
            throw new ThresholdGroupNotFoundException("Invalid stock list");
        }

        if (!thresholdGroupDto.getActive().equals(existing.getActive())) {
            type1 = thresholdGroupDto.getActive() ? 4 : 3;
        }

        if (!thresholdGroupDto.getAllStocks().equals(existing.getAllStocks()) ||
                !thresholdGroupDto.getStockList().equals(existing.getStockList())) {
            type2 = 5;
        }

        existing.setGroupName(thresholdGroupDto.getGroupName());
        existing.setActive(thresholdGroupDto.getActive());
        existing.setAllStocks(thresholdGroupDto.getAllStocks());
        existing.setStockList(StockListSorter.shorter(thresholdGroupDto.getStockList()));

        ThresholdGroup updated = thresholdGroupRepository.save(existing);
        thresholdGroupRepository.flush();

        try { sendThresholdToOtherMicroservices(type1, id, null); }
        catch (Exception e) { logger.error("Async call failed", e.getMessage()); }

        try { sendThresholdToOtherMicroservices(type2, id, null); }
        catch (Exception e) { logger.error("Async call failed", e.getMessage()); }

        logger.info("Updated ThresholdGroup id={}", updated.getId());
        return mapper.toDto(updated);
    }


    @Caching(
    	    evict = {
    	        @CacheEvict(value = "thresholdGroupCache", key = "#id"),
    	        @CacheEvict(value = "thresholdGroupsCache", key = "#requesterId")
    	    }
    )
    @Override
    public void removeThresholdGroup(Long id, Long requesterId) {
        logger.info("Removing ThresholdGroup id={}", id);
        ThresholdGroup existing = thresholdGroupRepository.findById(id)
                .orElseThrow(() -> new ThresholdGroupNotFoundException("Threshold Group not found with id: " + id));
        if(requesterId.longValue() != existing.getUser().getUserId().longValue()) 
        {
        	throw new ForbiddenException("Forbidden");
        }
        try {
        	sendThresholdToOtherMicroservices(3, id, null);
        } catch (Exception e) {
            logger.error("Async call failed", e.getMessage());
        }
        thresholdGroupRepository.deleteById(id);
        thresholdGroupRepository.flush();;
        logger.info("Removed ThresholdGroup id={}", id);
    }

    @Cacheable(value = "thresholdGroupCache", key = "#id")
    @Override
    public ThresholdGroupDto getThresholdGroup(Long id, Long requesterId) {
        logger.info("Fetching ThresholdGroup id={}", id);
        ThresholdGroup existing = thresholdGroupRepository.findById(id)
                .orElseThrow(() -> new ThresholdGroupNotFoundException("Threshold Group not found with id: " + id));
        if(requesterId.longValue() != existing.getUser().getUserId().longValue()) 
        {
        	throw new ForbiddenException("Forbidden");
        }
        return mapper.toDto(existing);
    }

    @Cacheable(value = "thresholdGroupsCache", key = "#userId")
    @Override
    public List<ThresholdGroupDto> getThresholdGroupsByUserId(Long userId, Long requesterId) {
        logger.info("Fetching ThresholdGroups for userId={}", userId);
        if(requesterId.longValue() != userId.longValue()) 
        {
        	throw new ForbiddenException("Forbidden");
        }
        List<ThresholdGroup> groups = thresholdGroupRepository.findByUserUserId(userId);
        return mapper.toThresholdGroupDtoList(groups);
    }

    @Override
    public Map<Long, ThresholdGroupDto> getAllActiveGroups(Integer type) {
        logger.info("Fetching all active ThresholdGroups for microservices type={}", type);
        List<ThresholdGroup> activeGroups = thresholdGroupRepository.findByActiveTrue();
        if (activeGroups.isEmpty()) {
            logger.warn("No active ThresholdGroups found");
            return Map.of();
        }

        List<ThresholdGroupDto> dtos = mapper.toThresholdGroupDtoList(activeGroups);
        for (int i = 0; i < activeGroups.size(); i++) {
            ThresholdGroup group = activeGroups.get(i);
            ThresholdGroupDto dto = dtos.get(i);
            if (type == 1) {
            	logger.info("Thresold group data for viewer");
                dto.setThresholds(mapper.toThresholdDtoList(group.getThresholds()));
            } else if (type == 2) {
            	logger.info("Thresold group data for messenger");
                dto.setTelegramChatIds(mapper.toTelegramChatIdDtoList(group.getTelegramChatIds()));
            }
        }
        return dtos.stream()
                .collect(Collectors.toMap(ThresholdGroupDto::getId, dto -> dto));
    }
    
    @Override
    public void sendThresholdToOtherMicroservices(Integer type, Long groupId, String action)
    {
    	logger.info("Fetching all ThresholdGroups for type={} and groupid={}", type, groupId);
        ThresholdGroup thresholdGroup = thresholdGroupRepository.findById(groupId)
                .orElseThrow(() -> new ThresholdGroupNotFoundException("Threshold Group not found with id: " + groupId));
        ThresholdGroupDto thresholdGroupDto = mapper.toDto(thresholdGroup);
        List<TelegramChatId> telegramChatIds = thresholdGroup.getTelegramChatIds();
        if (type == 1) {
        	logger.info("Thresold group data for viewer");
            List<Threshold> thresholds = thresholdGroup.getThresholds();
            thresholdGroupDto.setThresholds(mapper.toThresholdDtoList(thresholds));
            messageSender.sendTelegramMessage(
                thresholdGroup.getUser().getFirstName(),
                thresholdGroup.getGroupName(),
                thresholds,
                telegramChatIds
            );
            stockViewerService.updateThresholdGroup(thresholdGroupDto);
        } else if (type == 2) {
        	logger.info("Thresold group data for messenger");
        	thresholdGroupDto.setTelegramChatIds(mapper.toTelegramChatIdDtoList(thresholdGroup.getTelegramChatIds()));
        	messageSender.sendTelegramMessage(thresholdGroup.getUser().getFirstName(), 
        		action, 
        		thresholdGroup.getGroupName(), 
        		telegramChatIds
        	);
        	messengerService.updateMessenger(thresholdGroupDto);
        } else if (type == 3 || type == 4) {
        	logger.info("Thresold group activation/deativation data for viewer");
            List<Threshold> thresholds = thresholdGroup.getThresholds();
            if(type == 4)
            {
            	thresholdGroupDto.setThresholds(mapper.toThresholdDtoList(thresholds));
            }
            messageSender.sendGroupActivationStatus(
            	thresholdGroup.getUser().getFirstName(), 
            	thresholdGroup.getGroupName(), 
            	type == 4, telegramChatIds
            );
            stockViewerService.updateThresholdGroup(thresholdGroupDto);
        } else if (type == 5) {
        	logger.info("Thresold update stock list");
            List<Threshold> thresholds = thresholdGroup.getThresholds();
            thresholdGroupDto.setThresholds(mapper.toThresholdDtoList(thresholds));
            messageSender.sendGroupStockListUpdate(
                thresholdGroup.getUser().getFirstName(),
                thresholdGroup.getGroupName(),
                telegramChatIds
            );
            stockViewerService.updateThresholdGroup(thresholdGroupDto);
        }
    	
    }


	@Override
	public Set<Long> getTimeGapListForThresoldInSeconds() {
		return propertyService.getTimeGapListForThresoldInSeconds();
	}
    
    
}
