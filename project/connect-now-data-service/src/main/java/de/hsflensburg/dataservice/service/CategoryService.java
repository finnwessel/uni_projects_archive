package de.hsflensburg.dataservice.service;

import de.hsflensburg.dataservice.domain.model.Category;
import de.hsflensburg.dataservice.domain.model.CategoryType;
import de.hsflensburg.dataservice.exceptions.BadFormatException;
import de.hsflensburg.dataservice.repository.CategoryRepo;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepo categoryRepo;

    public String createCategory(CategoryType type, Map<String, String> name, String color) {
        Category category = new Category(type, name, color);

        categoryRepo.save(category);

        return category.getId().toHexString();
    }

    public Optional<Category> getCategory(String id) throws BadFormatException {
        if (ObjectId.isValid(id)) {
            return categoryRepo.findById(new ObjectId(id));
        } else {
            throw new BadFormatException();
        }
    }

    public Optional<Category> getCategory(ObjectId id) {
        return categoryRepo.findById(id);
    }

    public List<Category> getAllCategories(CategoryType categoryType) {
        return categoryRepo.findAllByType(categoryType.toString());
    }

    public boolean deleteCategory(String id) throws BadFormatException {
        if (ObjectId.isValid(id)) {
            return categoryRepo.deleteCategoryById(new ObjectId(id)) == 1;
        } else {
            throw new BadFormatException();
        }
    }

    public List<Category> getCategoriesById(List<ObjectId> ids) {
        return (List<Category>) categoryRepo.findAllById(ids);
    }

    public Optional<Category> updateCategory(String id, Map<String, String> newName, String newColor) throws BadFormatException {
        if (!ObjectId.isValid(id)) {
            throw new BadFormatException();
        }

        ObjectId categoryId = new ObjectId(id);

        Optional<Category> optionalCategory = categoryRepo.findById(categoryId);

        if (optionalCategory.isEmpty()) {
            return Optional.empty();
        }

        Category category = optionalCategory.get();
        category.setName(newName);
        category.setColor(newColor);

        categoryRepo.save(category);

        return Optional.of(category);
    }
}
