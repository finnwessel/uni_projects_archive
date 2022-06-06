package de.hsflensburg.dataservice.domain.mapper;

import de.hsflensburg.dataservice.domain.dto.TemplateResponse;
import de.hsflensburg.dataservice.domain.model.Template;

import java.util.Map;

public class TemplateMapper {
    public static TemplateResponse templateToTemplateResponse(Template template, Map<String, String> categoryNames) {
        TemplateResponse resp = new TemplateResponse();

        resp.setId(template.getId().toHexString());
        resp.setCreatedAt(template.getCreatedAt());
        resp.setModifiedAt(template.getModifiedAt());
        resp.setTitle(template.getTitle());
        resp.setCategoryName(categoryNames);
        resp.setCategoryId(template.getCategoryId().toHexString());
        resp.setComponents(template.getComponents());

        return resp;
    }
}
