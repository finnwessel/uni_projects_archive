package de.hsflensburg.dataservice.domain.converter;

import de.hsflensburg.dataservice.domain.model.CategoryType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class CategoryTypeConverter implements Converter<String, CategoryType> {

    @Override
    public CategoryType convert(String source) {
        return CategoryType.valueOf(source.toUpperCase());
    }
}
