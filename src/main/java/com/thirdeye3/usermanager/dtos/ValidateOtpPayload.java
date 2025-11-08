package com.thirdeye3.usermanager.dtos;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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

    @NotBlank(message = "Password must not be blank")
    @Size(min = 8, message = "Password must be more then or equal to 8 characters long")
    private String newPassword;

    @AssertTrue(message = "Invalid newPassword based on mailType rules")
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
