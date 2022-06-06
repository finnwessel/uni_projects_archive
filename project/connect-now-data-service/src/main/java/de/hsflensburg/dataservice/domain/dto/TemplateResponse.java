package de.hsflensburg.dataservice.domain.dto;

import de.hsflensburg.dataservice.domain.model.components.BaseComp;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
public class TemplateResponse {
    private String id;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private Map<String, String> title;
    private String categoryId;
    private Map<String, String> categoryName;
    private List<BaseComp> components;
}
