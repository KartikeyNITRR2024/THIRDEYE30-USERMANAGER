package com.thirdeye3.usermanager.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class LoginResponsePayload {
    private String token;
    private String userName;
    private String firstName;
    private String lastName;
    private List<String> roles;
    private Boolean firstLogin;
}
