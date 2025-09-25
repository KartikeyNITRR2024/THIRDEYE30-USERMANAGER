package com.thirdeye3.usermanager.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class Response<T> {
    private boolean success;
    private int errorCode;
    private String errorMessage;
    private T response;
}
