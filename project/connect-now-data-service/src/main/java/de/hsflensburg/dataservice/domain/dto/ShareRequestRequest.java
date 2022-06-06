package de.hsflensburg.dataservice.domain.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class ShareRequestRequest {
    @NotNull
    private String subject;
}
