package com.curcus.lms.model.dto;

import java.util.List;

import com.curcus.lms.model.request.SectionUpdatePositionRequest;

public class SectionPositionUpdateWrapper {
    private List<SectionUpdatePositionRequest> updates;

    // Getter và setter
    public List<SectionUpdatePositionRequest> getUpdates() {
        return updates;
    }

    public void setUpdates(List<SectionUpdatePositionRequest> updates) {
        this.updates = updates;
    }
}