package com.increff.commons.model;

import java.time.ZonedDateTime;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderSearchForm {
    @NotNull
    private ZonedDateTime startDate;
    @NotNull
    private ZonedDateTime endDate;
} 