package com.thirdeye3.usermanager.utils;

import com.thirdeye3.usermanager.dtos.*;
import com.thirdeye3.usermanager.entities.*;
import com.thirdeye3.usermanager.projections.ThresholdGroupProjection;

import java.util.Set;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class Mapper {
	
	public Role toEntity(RoleDto dto)
	{
		if(dto == null)
		{
			return null;
		}
		Role role = new Role();
		role.setId(dto.getId());
		role.setName(dto.getName());
		return role;
	}
	
	public RoleDto toDto(Role role)
	{
		if(role == null)
		{
			return null;
		}
		RoleDto dto = new RoleDto();
		dto.setId(role.getId());
		dto.setName(role.getName());
		return dto;
		
	}

	public User toEntity(UserDto dto) {
        if (dto == null) {
            return null;
        }
        User user = new User();
        user.setUserId(dto.getUserId());
        user.setUserName(dto.getUserName());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setRoles(null);
        user.setActive(dto.getActive());
        user.setThresholdGroups(null);
        user.setFirstLogin(dto.getFirstLogin());
        return user;
    }

    public UserDto toDto(User entity) {
        if (entity == null) {
            return null;
        }
        UserDto dto = new UserDto();
        dto.setUserId(entity.getUserId());
        dto.setUserName(entity.getUserName());
        dto.setFirstName(entity.getFirstName());
        dto.setLastName(entity.getLastName());
        dto.setPhoneNumber(entity.getPhoneNumber());
        dto.setRoles(null);
        dto.setActive(entity.getActive());
        dto.setThresholdGroups(null);
        dto.setFirstLogin(entity.getFirstLogin());
        return dto;
    }
    
    public ThresholdGroup toEntity(ThresholdGroupDto dto) {
        if (dto == null) {
            return null;
        }
        ThresholdGroup group = new ThresholdGroup();
        group.setGroupName(dto.getGroupName());
        group.setId(dto.getId());
        group.setUser(null);
        group.setTelegramChatIds(null);
        group.setThresholds(null);
        group.setActive(dto.getActive());
        group.setAllStocks(dto.getAllStocks());
        group.setStockList(dto.getStockList());
        return group;
    }

    public ThresholdGroupDto toDto(ThresholdGroup entity) {
        if (entity == null) {
            return null;
        }
        ThresholdGroupDto dto = new ThresholdGroupDto();
        dto.setGroupName(entity.getGroupName());
        dto.setId(entity.getId());
        dto.setUser(null);
        dto.setTelegramChatIds(null);
        dto.setThresholds(null);
        dto.setActive(entity.getActive());
        dto.setAllStocks(entity.getAllStocks());
        dto.setStockList(entity.getStockList());
        return dto;
    }
    
    public ThresholdGroupDto toDto(ThresholdGroupProjection projection) {
        if (projection == null) {
            return null;
        }
        ThresholdGroupDto dto = new ThresholdGroupDto();
        dto.setGroupName(projection.getGroupName());
        dto.setId(projection.getId());
        dto.setActive(projection.getActive());
        return dto;
    }
    
    public Threshold toEntity(ThresholdDto dto) {
        if (dto == null) {
            return null;
        }
        Threshold threshold = new Threshold();
        threshold.setId(dto.getId());
        threshold.setThresholdGroup(null);
        threshold.setTimeGapInSeconds(dto.getTimeGapInSeconds());
        threshold.setPriceGap(dto.getPriceGap());
        threshold.setType(dto.getType());
        return threshold;
    }

    public ThresholdDto toDto(Threshold entity) {
        if (entity == null) {
            return null;
        }
        ThresholdDto dto = new ThresholdDto();
        dto.setId(entity.getId());
        dto.setThresholdGroup(null);
        dto.setTimeGapInSeconds(entity.getTimeGapInSeconds());
        dto.setPriceGap(entity.getPriceGap());
        dto.setType(entity.getType());
        return dto;
    }
    
    public TelegramChatId toEntity(TelegramChatIdDto dto) {
        if (dto == null) {
            return null;
        }
        TelegramChatId entity = new TelegramChatId();
        entity.setId(dto.getId());
        entity.setWorkType(dto.getWorkType());
        entity.setChatId(dto.getChatId());
        entity.setChatName(dto.getChatName());
        entity.setThresholdGroup(null);
        return entity;
    }

    public TelegramChatIdDto toDto(TelegramChatId entity) {
        if (entity == null) {
            return null;
        }
        TelegramChatIdDto dto = new TelegramChatIdDto();
        dto.setId(entity.getId());
        dto.setWorkType(entity.getWorkType());
        dto.setChatId(entity.getChatId());
        dto.setChatName(entity.getChatName());
        dto.setThresholdGroup(null);
        return dto;
    }
    
    public List<UserDto> toUserDtoList(List<User> entities) {
        if (entities == null) {
            return null;
        }
        return entities.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<User> toUserEntityList(List<UserDto> dtos) {
        if (dtos == null) {
            return null;
        }
        return dtos.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }
    
    public List<ThresholdGroupDto> toThresholdGroupDtoList(List<ThresholdGroup> entities) {
        if (entities == null) {
            return null;
        }
        return entities.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
    
    public List<ThresholdGroupDto> toThresholdGroupDtoList1(List<ThresholdGroupProjection> projections) {
        if (projections == null) {
            return null;
        }
        return projections.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<ThresholdGroup> toThresholdGroupEntityList(List<ThresholdGroupDto> dtos) {
        if (dtos == null) {
            return null;
        }
        return dtos.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }

    public List<ThresholdDto> toThresholdDtoList(List<Threshold> entities) {
        if (entities == null) {
            return null;
        }
        return entities.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<Threshold> toThresholdEntityList(List<ThresholdDto> dtos) {
        if (dtos == null) {
            return null;
        }
        return dtos.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }

    public List<TelegramChatIdDto> toTelegramChatIdDtoList(List<TelegramChatId> entities) {
        if (entities == null) {
            return null;
        }
        return entities.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<TelegramChatId> toTelegramChatIdEntityList(List<TelegramChatIdDto> dtos) {
        if (dtos == null) {
            return null;
        }
        return dtos.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }
    
    public Set<Role> toEntitySet(Set<RoleDto> dtoSet) {
        if (dtoSet == null) {
            return null;
        }
        return dtoSet.stream()
                .map(this::toEntity)
                .collect(Collectors.toSet());
    }

    public Set<RoleDto> toDtoSet(Set<Role> roleSet) {
        if (roleSet == null) {
            return null;
        }
        return roleSet.stream()
                .map(this::toDto)
                .collect(Collectors.toSet());
    }
}
