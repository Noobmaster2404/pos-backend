package com.increff.commons.model;

import java.time.LocalDate;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderSearchForm {
    @NotNull
    private LocalDate startDate;
    @NotNull
    private LocalDate endDate;
} 