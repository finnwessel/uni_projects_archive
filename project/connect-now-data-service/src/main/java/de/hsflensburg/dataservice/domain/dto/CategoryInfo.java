package de.hsflensburg.dataservice.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data @AllArgsConstructor @NoArgsConstructor
public class CategoryInfo {
    private Map<String, String> name;
    private String color;
}
