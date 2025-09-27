package com.thirdeye3.usermanager.services.impl;

import com.thirdeye3.usermanager.dtos.UserDto;
import com.thirdeye3.usermanager.entities.Role;
import com.thirdeye3.usermanager.entities.ThresholdGroup;
import com.thirdeye3.usermanager.entities.User;
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
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
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

    @Override
    public UserDto getUserDtoByUserId(Long userId) {
        logger.info("Fetching UserDto by userId={}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));
        return mapper.toDto(user);
    }

    @Override
    public User getUserByUserId(Long userId) {
        logger.info("Fetching User entity by userId={}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

        if (!Boolean.TRUE.equals(user.getActive())) {
            logger.warn("User with id={} is inactive", userId);
            throw new UserNotFoundException("User with id " + userId + " is inactive");
        }
        
        if (Boolean.TRUE.equals(user.getFirstLogin())) {
            logger.warn("User with id={} is not updated Name and Mobile Number", userId);
            throw new UserNotFoundException("User with id " + userId + " is not updated Name and Mobile Number");
        }

        return user;
    }


    @Override
    public UserDto updateUser(Long userId, UserDto userDto) {
        logger.info("Updating User id={}", userId);
        
        User existing = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));
        
        User updatedEntity = mapper.toEntity(userDto);
        existing.setFirstName(updatedEntity.getFirstName());
        existing.setLastName(updatedEntity.getLastName());
        existing.setPhoneNumber(updatedEntity.getPhoneNumber());
        existing.setFirstLogin(false);
        User saved = userRepository.save(existing);
        logger.info("Updated User id={}", saved.getUserId());
        return mapper.toDto(saved);
    }


    @Override
    public void deleteUser(Long userId) {
        logger.info("Deleting user with id={}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));
        userRepository.delete(user);
        logger.info("User deleted with id={}", user.getUserId());
    }

    @Override
    public void activateUser(Long userId) {
        logger.info("Activating user with id={}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));
        user.setActive(true);
        userRepository.save(user);
        logger.info("User activated with id={}", userId);
    }

    @Override
    public void deactivateUser(Long userId) {
        logger.info("Deactivating user with id={}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));
        user.setActive(false);
        userRepository.save(user);
        logger.info("User deactivated with id={}", userId);
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
	
	@Override
	public void addRoleToUser(Long userId, String roleName) {
	    User user = userRepository.findById(userId)
	            .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));
	    
	    if(user.getUserName().equals(userName))
	    {
	    	throw new UserNotFoundException("Cannot change role of user: "+userName);
	    }

	    Role role = roleRepository.findByName(roleName)
	            .orElseThrow(() -> new RoleNotFoundException("Role not found: " + roleName));

	    if (user.getRoles().contains(role)) {
	        logger.warn("User id={} already has role={}", userId, roleName);
	        return;
	    }

	    user.getRoles().add(role);
	    userRepository.save(user);
	    logger.info("Role={} added to userId={}", roleName, userId);
	}

	@Override
	public void removeRoleFromUser(Long userId, String roleName) {
	    User user = userRepository.findById(userId)
	            .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));
	    
	    if(user.getUserName().equals(userName))
	    {
	    	throw new UserNotFoundException("Cannot change role of user: "+userName);
	    }

	    Role role = roleRepository.findByName(roleName)
	            .orElseThrow(() -> new RoleNotFoundException("Role not found: " + roleName));

	    if (!user.getRoles().contains(role)) {
	        logger.warn("User id={} does not have role={}", userId, roleName);
	        return;
	    }

	    user.getRoles().remove(role);
	    userRepository.save(user);
	    logger.info("Role={} removed from userId={}", roleName, userId);
	}

}
