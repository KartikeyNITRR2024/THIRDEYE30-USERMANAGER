package com.thirdeye3.usermanager.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    private Long userId;

    private String userName;

    private String password;

    @NotBlank(message = "First name must not be blank")
    @Size(min = 4, message = "First name must be at least 4 characters")
    private String firstName;

    @NotBlank(message = "Last name must not be blank")
    @Size(min = 4, message = "Last name must be at least 4 characters")
    private String lastName;

    @NotBlank(message = "Phone number must not be blank")
    @Pattern(regexp = "^\\d{10}$", message = "Phone number must be exactly 10 digits")
    private String phoneNumber;

    private Set<RoleDto> roles;

    private List<ThresholdGroupDto> thresholdGroups;

    private Boolean active;

    private Boolean firstLogin;
}
