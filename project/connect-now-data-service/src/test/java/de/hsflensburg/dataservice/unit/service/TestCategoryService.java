package de.hsflensburg.dataservice.unit.service;

import de.hsflensburg.dataservice.domain.model.Category;
import de.hsflensburg.dataservice.domain.model.CategoryType;
import de.hsflensburg.dataservice.exceptions.BadFormatException;
import de.hsflensburg.dataservice.repository.CategoryRepo;
import de.hsflensburg.dataservice.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@AutoConfigureDataMongo
@SpringBootTest
@RequiredArgsConstructor
@ExtendWith(MockitoExtension.class)
public class TestCategoryService {

    @Mock
    private CategoryRepo categoryRepo;
    private CategoryService categoryService;

    @BeforeEach
    private void init() {
        categoryService = new CategoryService(categoryRepo);
    }

    @Test
    public void testCreateCategory() {
        doAnswer(invocationOnMock -> {
            Category category = invocationOnMock.getArgument(0);
            category.setId(new ObjectId());

            return null;
        }).when(categoryRepo).save(any());

        categoryService.createCategory(CategoryType.REQUEST, Map.of("de", "Projekt", "en", "Project"), "red");

        verify(categoryRepo).save(any());
    }

    @Test
    public void testGetCategoryByStringId() throws BadFormatException {
        ObjectId objectId = new ObjectId();
        categoryService.getCategory(objectId.toHexString());

        verify(categoryRepo).findById(objectId);
    }

    @Test
    public void testGetCategoryByStringIdBadFormat() {
       assertThrows(BadFormatException.class, () ->
               categoryService.getCategory("123"));

        verify(categoryRepo, never()).findById(any());
    }

    @Test
    public void testGetCategoryByObjectId() {
        ObjectId objectId = new ObjectId();
        categoryService.getCategory(objectId);

        verify(categoryRepo).findById(objectId);
    }

    @Test
    public void testGetAllCategories() {
        CategoryType type = CategoryType.REQUEST;
        categoryService.getAllCategories(type);

        verify(categoryRepo).findAllByType(type.toString());
    }

    @Test
    public void testDeleteCategory() throws BadFormatException {
        ObjectId objectId = new ObjectId();
        categoryService.deleteCategory(objectId.toHexString());

        verify(categoryRepo).deleteCategoryById(objectId);
    }

    @Test
    public void testDeleteCategoryBadFormat() {
        assertThrows(BadFormatException.class, () ->
                categoryService.deleteCategory("123"));

        verify(categoryRepo, never()).deleteCategoryById(any());
    }

    @Test
    public void testGetCategoriesById() {
        categoryService.getCategoriesById(new ArrayList<>());

        verify(categoryRepo).findAllById(any());
    }

    @Test
    public void testUpdateCategory() throws BadFormatException {
        Category existingCategory = new Category(CategoryType.REQUEST, Map.of("de", "Projekt", "en", "Project"), "red");

        ObjectId categoryId = new ObjectId();

        when(categoryRepo.findById(categoryId)).thenReturn(Optional.of(existingCategory));

        Map<String, String> newMap = Map.of("de", "Test", "en", "Test123");
        String newColor = "blue";
        categoryService.updateCategory(categoryId.toHexString(), newMap, newColor);

        ArgumentCaptor<Category> saveArgumentCaptor = ArgumentCaptor.forClass(Category.class);

        verify(categoryRepo).save(saveArgumentCaptor.capture());

        assertEquals(newMap, saveArgumentCaptor.getValue().getName());
    }

    @Test
    public void testUpdateCategoryBadFormat() {
        assertThrows(BadFormatException.class, () ->
                categoryService.updateCategory("123", new HashMap<>(), "red"));

        verify(categoryRepo, never()).findById(any());
        verify(categoryRepo, never()).save(any());
    }

    @Test
    public void testUpdateCategoryNotFound() throws BadFormatException {
        ObjectId categoryId = new ObjectId();

        when(categoryRepo.findById(categoryId)).thenReturn(Optional.empty());

        Map<String, String> newMap = Map.of("de", "Test", "en", "Test123");
        String newColor = "blue";
        categoryService.updateCategory(categoryId.toHexString(), newMap, newColor);

        verify(categoryRepo, never()).save(any());
    }
}
