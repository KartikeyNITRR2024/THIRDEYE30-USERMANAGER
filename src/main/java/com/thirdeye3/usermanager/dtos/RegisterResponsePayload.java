package com.thirdeye3.usermanager.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class RegisterResponsePayload {
	private Long id;
    private Integer getMailType;
    private String message;
}
