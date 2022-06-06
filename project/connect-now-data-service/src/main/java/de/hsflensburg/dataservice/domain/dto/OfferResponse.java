package de.hsflensburg.dataservice.domain.dto;

import de.hsflensburg.dataservice.domain.model.components.BaseComp;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
public class OfferResponse {
    private String id;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private String owner;
    private String title;
    private Map<String, String> category;
    private boolean visible;
    private List<BaseComp> components;
}
