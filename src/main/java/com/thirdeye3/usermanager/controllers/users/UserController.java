package com.thirdeye3.usermanager.controllers.users;

import com.thirdeye3.usermanager.dtos.Response;
import com.thirdeye3.usermanager.dtos.UserDto;
import com.thirdeye3.usermanager.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/um/user/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @PutMapping("/{userId}")
    public Response<UserDto> updateUser(
            @PathVariable("userId") Long userId,
            @Valid @RequestBody UserDto userDto) {

        logger.info("Updating user with ID: {}", userId);
        UserDto updatedUser = userService.updateUser(userId, userDto);
        return new Response<>(true, 0, null, updatedUser);
    }
}

