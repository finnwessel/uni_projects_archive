package de.hsflensburg.dataservice.domain.dto;

import de.hsflensburg.dataservice.domain.model.ShareStatus;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class UpdateShareRequest {
    @NotNull
    private ShareStatus status;
}
