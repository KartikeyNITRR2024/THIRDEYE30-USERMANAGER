package com.thirdeye3.usermanager.services;

import com.thirdeye3.usermanager.dtos.AuthUserPayload;
import com.thirdeye3.usermanager.dtos.LoginResponsePayload;

public interface AuthService {
	String register(AuthUserPayload authUserPayload);
	LoginResponsePayload login(AuthUserPayload authUserPayload);
}
