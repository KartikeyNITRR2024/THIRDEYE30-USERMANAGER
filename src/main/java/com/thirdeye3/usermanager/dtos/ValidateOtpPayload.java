package com.thirdeye3.usermanager.dtos;

import jakarta.validation.constraints.AssertTrue;
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
public class ValidateOtpPayload {

    private Long mailId;
    private Integer mailType;
    private String otp;
    private String newPassword;

    @AssertTrue(message = "Invalid password based on mailType rules")
    public boolean isValidNewPassword() {

        if (mailType == null) return false;
        if (mailType == 1) {
            return newPassword == null || newPassword.trim().isEmpty();
        }
        if (mailType == 2) {
            return newPassword != null && newPassword.trim().length() >= 8;
        }
        return true;
    }
}
