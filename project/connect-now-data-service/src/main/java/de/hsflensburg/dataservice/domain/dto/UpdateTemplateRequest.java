package de.hsflensburg.dataservice.domain.dto;

import de.hsflensburg.dataservice.domain.model.components.BaseComp;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

@Data
public class UpdateTemplateRequest {
    @NotNull @NotEmpty
    private Map<String, String> title;
    @NotNull
    private String categoryId;
    @NotNull @NotEmpty
    private List<BaseComp> components;
}
