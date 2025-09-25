package com.thirdeye3.usermanager.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AuthUserPayload {

    @NotBlank(message = "Username must not be blank")
    @Size(min = 8, message = "Username must not less than 8 characters")
    private String userName;

    @NotBlank(message = "Password must not be blank")
    @Size(min = 8, message = "Password must be more then or equal to 8 characters long")
    private String password;
}
