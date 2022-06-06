package de.hsflensburg.dataservice.domain.dto;

import de.hsflensburg.dataservice.domain.model.components.*;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class CreateRequestRequest {
    @NotNull
    private String title;
    @NotNull
    private String templateId;
    @NotNull
    private List<String> participants;
    @NotNull @NotEmpty @Valid
    private List<BaseComp> components;

}
