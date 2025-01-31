package com.increff.server.entity;

import lombok.Getter;
import javax.persistence.*;

import java.time.ZonedDateTime;
import com.increff.commons.util.TimeZoneUtil;

@Getter
@MappedSuperclass
public abstract class BaseEntity {

    @Column(nullable = false, updatable = false)
    private ZonedDateTime createdAt;

    @Column(nullable = false)
    private ZonedDateTime updatedAt;

    @Version
    private Long version;

    @PrePersist
    protected void onCreate() {
        createdAt = TimeZoneUtil.toUTC(ZonedDateTime.now());
        updatedAt = TimeZoneUtil.toUTC(ZonedDateTime.now());
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = TimeZoneUtil.toUTC(ZonedDateTime.now());
    }
}