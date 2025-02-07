package com.increff.commons.model;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;

@Getter
@Setter
public class DailySalesForm {
    @NotNull(message = "Start date cannot be null")
    @PastOrPresent(message = "Start date cannot be in future")
    private LocalDate startDate;

    @NotNull(message = "End date cannot be null")
    @PastOrPresent(message = "End date cannot be in future")
    private LocalDate endDate;
}