package de.hsflensburg.dataservice.domain.mapper;

import de.hsflensburg.dataservice.domain.dto.CategoryResponse;
import de.hsflensburg.dataservice.domain.model.Category;
import de.hsflensburg.dataservice.domain.model.CategoryType;
import org.bson.types.ObjectId;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CategoryMapper  {
    CategoryMapper INSTANCE = Mappers.getMapper(CategoryMapper.class);

    @Mapping(source = "id", target = "id", qualifiedByName = "objectIdToString")
    @Mapping(source = "type", target = "type", qualifiedByName = "categoryTypeToString")
    CategoryResponse categoryToCategoryResponse(Category category);

    @Named("objectIdToString")
    static String objectIdToString(ObjectId objectId) {
        return objectId.toHexString();
    }

    @Named("categoryTypeToString")
    static String categoryTypeToString(CategoryType categoryType) {
        return categoryType.toString().toLowerCase();
    }
}
