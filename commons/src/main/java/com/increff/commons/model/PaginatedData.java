package com.increff.commons.model;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class PaginatedData<T> {
    private List<T> data;
    private int page;
    private boolean hasNext;
    private int pageSize;

    public PaginatedData(List<T> data, int page, int pageSize) {
        // If we got more items than pageSize, there are more pages
        this.hasNext = data.size() > pageSize;
        // If there are more pages, remove the extra item we queried
        this.data = hasNext ? data.subList(0, pageSize) : data;
        this.page = page;
        this.pageSize = pageSize;
    }
} 