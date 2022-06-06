package de.hsflensburg.dataservice.domain.dto;

import de.hsflensburg.dataservice.domain.model.components.BaseComp;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class UpdateOfferRequest {
    @NotNull
    private String title;
    @NotNull
    private String categoryId;
    @NotNull
    private boolean visible;
    @NotNull @NotEmpty
    private List<BaseComp> components;
}
