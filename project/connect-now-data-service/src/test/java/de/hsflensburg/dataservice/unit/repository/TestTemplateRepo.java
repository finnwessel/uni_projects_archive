package de.hsflensburg.dataservice.unit.repository;

import de.hsflensburg.dataservice.domain.model.Template;
import de.hsflensburg.dataservice.repository.TemplateRepo;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@AutoConfigureDataMongo
@SpringBootTest
@RequiredArgsConstructor
public class TestTemplateRepo {

    @Autowired
    private TemplateRepo templateRepo;

    @BeforeEach
    private void init() {
        templateRepo.deleteAll();
    }

    @Test
    public void testFindByCategoryId() {
        ObjectId categoryId = new ObjectId();

        Template template1 = new Template(Map.of("de", "Titel", "en", "Title"), categoryId, new ArrayList<>());
        Template template2 = new Template(Map.of("de", "Titel", "en", "Title"), categoryId, new ArrayList<>());
        templateRepo.insert(List.of(template1, template2));

        List<Template> templates = templateRepo.findByCategoryId(categoryId);

        assertEquals(2, templates.size());
    }

    @Test
    public void testFindByCategoryIdWrongId() {
        ObjectId categoryId = new ObjectId();

        Template template1 = new Template(Map.of("de", "Titel", "en", "Title"), categoryId, new ArrayList<>());
        Template template2 = new Template(Map.of("de", "Titel", "en", "Title"), categoryId, new ArrayList<>());
        templateRepo.insert(List.of(template1, template2));

        List<Template> templates = templateRepo.findByCategoryId(new ObjectId());

        assertEquals(0, templates.size());
    }

    @Test
    public void testExistsById() {
        Template template = new Template(Map.of("de", "Titel", "en", "Title"), new ObjectId(), new ArrayList<>());
        templateRepo.insert(template);

        boolean exists = templateRepo.existsById(template.getId());

        assertTrue(exists);
    }

    @Test
    public void testExistsByIdWrongId() {
        Template template = new Template(Map.of("de", "Titel", "en", "Title"), new ObjectId(), new ArrayList<>());
        templateRepo.insert(template);

        boolean exists = templateRepo.existsById(new ObjectId());

        assertFalse(exists);
    }

    @Test
    public void testDeleteTemplateById() {
        Template template = new Template(Map.of("de", "Titel", "en", "Title"), new ObjectId(), new ArrayList<>());
        templateRepo.insert(template);

        long success = templateRepo.deleteTemplateById(template.getId());

        assertEquals(1, success);
    }

    @Test
    public void testDeleteTemplateByIdWrongId() {
        Template template = new Template(Map.of("de", "Titel", "en", "Title"), new ObjectId(), new ArrayList<>());
        templateRepo.insert(template);

        long success = templateRepo.deleteTemplateById(new ObjectId());

        assertEquals(0, success);
    }

}
