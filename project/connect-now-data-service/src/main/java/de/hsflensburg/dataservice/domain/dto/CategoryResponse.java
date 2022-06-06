package de.hsflensburg.dataservice.domain.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
public class CategoryResponse {
    private String id;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private String type;
    private Map<String, String> name;
    private String color;
}
