package com.thirdeye3.usermanager.controllers.users;

import com.thirdeye3.usermanager.dtos.Response;
import com.thirdeye3.usermanager.dtos.UserDto;
import com.thirdeye3.usermanager.services.UserService;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/um/admin/users")
public class AdminUserController {

    private static final Logger logger = LoggerFactory.getLogger(AdminUserController.class);

    @Autowired
    private UserService userService;
    
    @GetMapping()
    public Response<List<UserDto>> getUsers() {
        logger.info("Get all users");
        return new Response<>(true, 0, null, userService.getAllUsers());
    }

    @DeleteMapping("/{userId}")
    public Response<Void> deleteUser(@PathVariable("userId") Long userId) {
        logger.info("Deleting user with ID: {}", userId);
        userService.deleteUser(userId);
        return new Response<>(true, 0, null, null);
    }

    @PatchMapping("/{userId}/activate")
    public Response<Void> activateUser(@PathVariable("userId") Long userId) {
        logger.info("Activating user with ID: {}", userId);
        userService.activateUser(userId);
        return new Response<>(true, 0, null, null);
    }

    @PatchMapping("/{userId}/deactivate")
    public Response<Void> deactivateUser(@PathVariable("userId") Long userId) {
        logger.info("Deactivating user with ID: {}", userId);
        userService.deactivateUser(userId);
        return new Response<>(true, 0, null, null);
    }
    
    @PatchMapping("/{userId}/roles/{roleName}/add")
    public Response<Void> addRoleToUser(
            @PathVariable("userId") Long userId,
            @PathVariable("roleName") String roleName) {
        logger.info("Adding role={} to userId={}", roleName, userId);
        userService.addRoleToUser(userId, roleName);
        return new Response<>(true, 0, null, null);
    }

    @PatchMapping("/{userId}/roles/{roleName}/remove")
    public Response<Void> removeRoleFromUser(
            @PathVariable("userId") Long userId,
            @PathVariable("roleName") String roleName) {
        logger.info("Removing role={} from userId={}", roleName, userId);
        userService.removeRoleFromUser(userId, roleName);
        return new Response<>(true, 0, null, null);
    }

}

