package com.increff.commons.model;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class PaginatedData<T> {
    private List<T> data;
    private int page;
    private int totalPages;
    private boolean hasNext;
    private long totalItems;
    private int pageSize;

    public PaginatedData(List<T> data, int page, long totalItems, int pageSize) {
        this.data = data;
        this.page = page;
        this.totalItems = totalItems;
        this.pageSize = pageSize;
        this.totalPages = (int) Math.ceil((double) totalItems / pageSize);
        this.hasNext = page < totalPages - 1;
    }
} 