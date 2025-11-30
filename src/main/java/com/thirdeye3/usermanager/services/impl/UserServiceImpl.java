package com.thirdeye3.usermanager.services.impl;

import com.thirdeye3.usermanager.dtos.UserDto;
import com.thirdeye3.usermanager.entities.Role;
import com.thirdeye3.usermanager.entities.User;
import com.thirdeye3.usermanager.exceptions.ForbiddenException;
import com.thirdeye3.usermanager.exceptions.RoleNotFoundException;
import com.thirdeye3.usermanager.exceptions.UserNotFoundException;
import com.thirdeye3.usermanager.repositories.RoleRepository;
import com.thirdeye3.usermanager.repositories.UserRepository;
import com.thirdeye3.usermanager.services.UserService;
import com.thirdeye3.usermanager.utils.Mapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService, UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Value("${thirdeye.admin.username}")
    private String userName;

    private final Mapper mapper = new Mapper();


    @Cacheable(value = "userCache", key = "#userId")
    @Override
    public UserDto getUserDtoByUserId(Long userId, Long requesterId) {

        logger.info("Fetching UserDto by userId={}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

        if (!Boolean.TRUE.equals(user.getEmailVerified()))
            throw new UserNotFoundException("User is unverified");

        if (!requesterId.equals(user.getUserId()))
            throw new ForbiddenException("Forbidden");

        return mapper.toDto(user);
    }


    @Override
    public List<UserDto> getAllUsers() {
        logger.info("Fetching all UserDto");
        return mapper.toUserDtoList(userRepository.findAll());
    }


    @Cacheable(value = "userCache", key = "#userId")
    @Override
    public User getUserByUserId(Long userId) {
        logger.info("Fetching User entity by userId={}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

        if (!Boolean.TRUE.equals(user.getEmailVerified()))
            throw new UserNotFoundException("User is unverified");

        if (!Boolean.TRUE.equals(user.getActive()))
            throw new UserNotFoundException("User is inactive");

        if (Boolean.TRUE.equals(user.getFirstLogin()))
            throw new UserNotFoundException("User is not updated Name and Mobile Number");

        return user;
    }


    @CachePut(value = "userCache", key = "#userId")
    @Override
    public UserDto updateUser(Long userId, UserDto userDto, Long requesterId) {

        logger.info("Updating User id={}", userId);

        User existing = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

        if (!Boolean.TRUE.equals(existing.getEmailVerified()))
            throw new UserNotFoundException("User is unverified");

        if (!requesterId.equals(existing.getUserId()))
            throw new ForbiddenException("Forbidden");

        User updatedEntity = mapper.toEntity(userDto);
        existing.setFirstName(updatedEntity.getFirstName());
        existing.setLastName(updatedEntity.getLastName());
        existing.setPhoneNumber(updatedEntity.getPhoneNumber());
        existing.setFirstLogin(false);

        User saved = userRepository.save(existing);
        return mapper.toDto(saved);
    }


    @CacheEvict(value = "userCache", key = "#userId")
    @Override
    public void deleteUser(Long userId) {

        logger.info("Deleting user with id={}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

        if (!Boolean.TRUE.equals(user.getEmailVerified()))
            throw new UserNotFoundException("User is unverified");

        if (user.getUserName().equals(userName))
            throw new UserNotFoundException("Cannot delete user: " + userName);

        userRepository.delete(user);
    }


    @CachePut(value = "userCache", key = "#userId")
    @Override
    public void activateUser(Long userId) {

        logger.info("Activating user with id={}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

        if (user.getUserName().equals(userName))
            throw new UserNotFoundException("Cannot change status of user: " + userName);

        user.setActive(true);
        userRepository.save(user);
    }


    @CachePut(value = "userCache", key = "#userId")
    @Override
    public void deactivateUser(Long userId) {

        logger.info("Deactivating user with id={}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

        if (user.getUserName().equals(userName))
            throw new UserNotFoundException("Cannot change status of user: " + userName);

        user.setActive(false);
        userRepository.save(user);
    }


    @Override
    public List<Long> getActiveUserIds() {
        logger.info("Fetching all active userIds");
        return userRepository.findByActiveTrue()
                .stream()
                .map(User::getUserId)
                .toList();
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return new org.springframework.security.core.userdetails.User(
                user.getUserName(),
                user.getPassword(),
                user.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority(role.getName()))
                        .collect(Collectors.toList())
        );
    }


    @CachePut(value = "userCache", key = "#userId")
    @Override
    public void addRoleToUser(Long userId, String roleName) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

        if (user.getUserName().equals(userName))
            throw new UserNotFoundException("Cannot change role of user: " + userName);

        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RoleNotFoundException("Role not found: " + roleName));

        if (!user.getRoles().contains(role)) {
            user.getRoles().add(role);
            userRepository.save(user);
        }
    }


    @CachePut(value = "userCache", key = "#userId")
    @Override
    public void removeRoleFromUser(Long userId, String roleName) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

        if (user.getUserName().equals(userName))
            throw new UserNotFoundException("Cannot change role of user: " + userName);

        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RoleNotFoundException("Role not found: " + roleName));

        if (user.getRoles().contains(role)) {
            user.getRoles().remove(role);
            userRepository.save(user);
        }
    }


    @Override
    public void deleteAllUnverifiedUser() {
        logger.info("Cleaning unverified user records...");
        userRepository.deleteAllUnverifiedUsers();
    }
}
