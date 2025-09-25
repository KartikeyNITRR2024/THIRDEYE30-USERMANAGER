package com.thirdeye3.usermanager.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ThresholdGroupDto {

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

    @NotBlank(message = "Stock list is required")
    @Pattern(regexp = "^(\\d{5})*$", message = "Invalid stock list")
    private String stockList;
}

