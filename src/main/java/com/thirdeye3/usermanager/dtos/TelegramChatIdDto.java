package com.thirdeye3.usermanager.dtos;

import com.thirdeye3.usermanager.enums.WorkType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TelegramChatIdDto {
    private Long id;

    @NotNull(message = "WorkType must not be null")
    private WorkType workType;

    @NotBlank(message = "Chat ID must not be blank")
    @Size(min = 8, message = "Chat ID must be at least 8 characters")
    private String chatId;

    @NotBlank(message = "Chat Name must not be blank")
    @Size(min = 5, message = "Chat Name must be at least 5 characters")
    private String chatName;

    private ThresholdGroupDto thresholdGroup;
}
