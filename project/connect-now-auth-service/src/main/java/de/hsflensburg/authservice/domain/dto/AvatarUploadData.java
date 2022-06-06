package de.hsflensburg.authservice.domain.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class AvatarUploadData {
    public String key;
    public Map<String, String> formData;
}
