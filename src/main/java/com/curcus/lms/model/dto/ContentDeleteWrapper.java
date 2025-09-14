package com.curcus.lms.model.dto;
import java.util.List;
import com.curcus.lms.model.request.ContentDeleteRequest;
public class ContentDeleteWrapper {
    private List<ContentDeleteRequest> updates;

    // Getter và setter
    public List<ContentDeleteRequest> getUpdates() {
        return updates;
    }

    public void setUpdates(List<ContentDeleteRequest> updates) {
        this.updates = updates;
    }
}
