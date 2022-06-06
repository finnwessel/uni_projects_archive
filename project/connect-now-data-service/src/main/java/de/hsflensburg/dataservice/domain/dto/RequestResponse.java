package de.hsflensburg.dataservice.domain.dto;

import de.hsflensburg.dataservice.domain.model.components.BaseComp;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class RequestResponse {
    private String id;
    private String templateId;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private String owner;
    private String title;
    private CategoryInfo category;
    private List<String> participants;
    private List<ShareResponse> shares;
    private List<BaseComp> components;
    private boolean active;
}
