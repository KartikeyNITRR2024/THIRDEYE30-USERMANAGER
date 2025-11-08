package com.thirdeye3.usermanager.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ResetPasswordResponsePayload {
    private Long mailId;
    private Integer mailType;
    private Boolean success;
    private String message;
}
