package de.hsflensburg.dataservice.domain.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ShareResponse {
    private String subject;
    private LocalDateTime createdAt;
    private String status;
    boolean studentActive;
    boolean teacherActive;
}
