package de.hsflensburg.dataservice.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class Share implements Serializable {
    private String subject;
    private LocalDateTime createdAt;
    private ShareStatus status;
    boolean studentActive;
    boolean teacherActive;
}
