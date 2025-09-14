package com.curcus.lms.model.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

// không xài permission này nữa mà chuyển sang sử dụng Method Security @PreAuthorize
@RequiredArgsConstructor
public enum Permission {
    DUMMY("dummy")
    ;

    @Getter
    private final String permission;
}
