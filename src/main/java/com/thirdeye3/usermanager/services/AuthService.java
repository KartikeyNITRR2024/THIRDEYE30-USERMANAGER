package com.thirdeye3.usermanager.services;

import com.thirdeye3.usermanager.dtos.AuthUserPayload;
import com.thirdeye3.usermanager.dtos.LoginResponsePayload;
import com.thirdeye3.usermanager.dtos.RegisterResponsePayload;
import com.thirdeye3.usermanager.dtos.ResetPasswordPayload;
import com.thirdeye3.usermanager.dtos.ResetPasswordResponsePayload;
import com.thirdeye3.usermanager.dtos.ValidateOtpPayload;
import com.thirdeye3.usermanager.dtos.ValidateOtpResponsePayload;

public interface AuthService {
	RegisterResponsePayload register(AuthUserPayload authUserPayload);
	LoginResponsePayload login(AuthUserPayload authUserPayload);
	ValidateOtpResponsePayload validateOtp(ValidateOtpPayload payload);
	ResetPasswordResponsePayload resetPassword(ResetPasswordPayload resetPasswordPayload);
}
