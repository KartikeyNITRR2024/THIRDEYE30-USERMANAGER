package com.thirdeye3.usermanager.dtos;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ThresholdDto {

    private Long id;

    private ThresholdGroupDto thresholdGroup;

    @NotNull(message = "Time gap is required")
    private Long timeGapInSeconds;

    @AssertTrue(message = "Time gap must be positive, Yesterday's closing price, or Today's opening price")
    private boolean isValidTimeGap() {
        return timeGapInSeconds != null && (timeGapInSeconds > 0 || timeGapInSeconds == -1 || timeGapInSeconds == -2);
    }

    @NotNull(message = "Price gap is required")
    @DecimalMin(value = "-999999999.99", message = "Price gap too small")
    @DecimalMax(value = "999999999.99", message = "Price gap too large")
    private Double priceGap;

    @NotNull(message = "Type is required")
    @Min(value = 0, message = "Type must be 0 or 1")
    @Max(value = 1, message = "Type must be 0 or 1")
    private Integer type;
}
