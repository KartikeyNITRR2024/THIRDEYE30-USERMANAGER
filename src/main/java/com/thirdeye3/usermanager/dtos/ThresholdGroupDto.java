package com.thirdeye3.usermanager.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import java.util.List;
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
public class ThresholdGroupDto implements Serializable {

    private Long id;

    @NotBlank(message = "Group name is required")
    @Size(min = 3, message = "Group name must be at least 3 characters long")
    private String groupName;

    private UserDto user;

    private List<TelegramChatIdDto> telegramChatIds;

    private List<ThresholdDto> thresholds;

    @NotNull(message = "Active status is required")
    private Boolean active;

    @NotNull(message = "AllStocks is required")
    private Boolean allStocks;

    @Pattern(regexp = "^(\\d{5})*$", message = "Invalid stock list")
    private String stockList;
}

