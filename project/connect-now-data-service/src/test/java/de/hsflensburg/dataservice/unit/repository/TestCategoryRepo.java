package de.hsflensburg.dataservice.unit.repository;

import de.hsflensburg.dataservice.domain.model.Category;
import de.hsflensburg.dataservice.domain.model.CategoryType;
import de.hsflensburg.dataservice.repository.CategoryRepo;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@AutoConfigureDataMongo
@SpringBootTest
@RequiredArgsConstructor
public class TestCategoryRepo {

    @Autowired
    private CategoryRepo categoryRepo;

    @BeforeEach
    private void init() {
        categoryRepo.deleteAll();
    }

    @Test
    public void testFindAllByTypeRequest() {
        Category category1 = new Category(CategoryType.REQUEST, Map.of("de", "Projekt", "en", "Project"), "red");
        Category category2 = new Category(CategoryType.OFFER, Map.of("de", "Projekt", "en", "Project"), "red");
        categoryRepo.insert(List.of(category1, category2));

        List<Category> categories = categoryRepo.findAllByType(CategoryType.REQUEST.toString());
        assertEquals(1, categories.size());
        assertEquals(category1.getName(), categories.get(0).getName());
        assertEquals(category1.getColor(), categories.get(0).getColor());
        assertEquals(CategoryType.REQUEST, categories.get(0).getType());
    }

    @Test
    public void testFindAllByTypeOffer() {
        Category category1 = new Category(CategoryType.REQUEST, Map.of("de", "Projekt", "en", "Project"), "red");
        Category category2 = new Category(CategoryType.OFFER, Map.of("de", "Projekt", "en", "Project"), "red");
        categoryRepo.insert(List.of(category1, category2));

        List<Category> categories = categoryRepo.findAllByType(CategoryType.OFFER.toString());
        assertEquals(1, categories.size());
        assertEquals(category2.getName(), categories.get(0).getName());
        assertEquals(category2.getColor(), categories.get(0).getColor());
        assertEquals(CategoryType.OFFER, categories.get(0).getType());
    }

    @Test
    public void testDeleteCategoryById() {
        Category category = new Category(CategoryType.REQUEST, Map.of("de", "Projekt", "en", "Project"), "red");
        categoryRepo.insert(category);

        long success = categoryRepo.deleteCategoryById(category.getId());

        assertEquals(1L, success);
        assertEquals(0, categoryRepo.findAll().size());
    }

    @Test
    public void testDeleteCategoryByIdWrongId() {
        Category category = new Category(CategoryType.REQUEST, Map.of("de", "Projekt", "en", "Project"), "red");
        categoryRepo.insert(category);

        long success = categoryRepo.deleteCategoryById(new ObjectId());

        assertEquals(0L, success);
        assertEquals(1, categoryRepo.findAll().size());
    }

    @Test
    public void testExistsByIdAndTypeRequest() {
        Category category = new Category(CategoryType.REQUEST, Map.of("de", "Projekt", "en", "Project"), "red");
        categoryRepo.insert(category);

        boolean exists = categoryRepo.existsByIdAndType(category.getId(), CategoryType.REQUEST.toString());

        assertTrue(exists);
    }


    @Test
    public void testExistsByIdAndTypeOffer() {
        Category category = new Category(CategoryType.OFFER, Map.of("de", "Projekt", "en", "Project"), "red");
        categoryRepo.insert(category);

        boolean exists = categoryRepo.existsByIdAndType(category.getId(), CategoryType.OFFER.toString());

        assertTrue(exists);
    }

    @Test
    public void testExistsByIdAndTypeDoesntExist() {
        Category category = new Category(CategoryType.OFFER, Map.of("de", "Projekt", "en", "Project"), "red");
        categoryRepo.insert(category);

        boolean exists = categoryRepo.existsByIdAndType(category.getId(), CategoryType.REQUEST.toString());

        assertFalse(exists);
    }
}

