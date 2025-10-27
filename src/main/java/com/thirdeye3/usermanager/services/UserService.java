package com.thirdeye3.usermanager.services;

import com.thirdeye3.usermanager.dtos.UserDto;
import com.thirdeye3.usermanager.entities.User;

import java.io.ByteArrayInputStream;
import java.util.List;

public interface UserService {
	User getUserByUserId(Long userId);
    UserDto updateUser(Long userId, UserDto userDto, Long requesterId);
    void deleteUser(Long userId);
    void activateUser(Long userId);
    void deactivateUser(Long userId);
	List<Long> getActiveUserIds();
	UserDto getUserDtoByUserId(Long userId, Long requesterId);
	void addRoleToUser(Long userId, String roleName);
	void removeRoleFromUser(Long userId, String roleName);
}

