package de.hsflensburg.dataservice.domain.dto;

import de.hsflensburg.dataservice.domain.model.CategoryType;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Map;

@Data
public class CreateCategoryRequest {
    @NotNull
    private CategoryType type;
    @NotNull @NotEmpty
    private Map<String, String> name;
    @NotNull @Size(min = 1, max = 50)
    private String color;
}
