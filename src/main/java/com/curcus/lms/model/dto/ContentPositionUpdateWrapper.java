package com.curcus.lms.model.dto;

import java.util.List;

import com.curcus.lms.model.request.ContentUpdatePositionRequest;

public class ContentPositionUpdateWrapper {
    private List<ContentUpdatePositionRequest> updates;

    // Getter và setter
    public List<ContentUpdatePositionRequest> getUpdates() {
        return updates;
    }

    public void setUpdates(List<ContentUpdatePositionRequest> updates) {
        this.updates = updates;
    }
}
