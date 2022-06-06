package de.hsflensburg.dataservice.domain.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Map;

@Data
public class UpdateCategoryRequest {
    @NotNull
    @NotEmpty
    private Map<String, String> name;
    @NotNull
    @Size(min = 1, max = 50)
    private String color;
}
