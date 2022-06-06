package de.hsflensburg.dataservice.unit.service;

import de.hsflensburg.dataservice.domain.model.CategoryType;
import de.hsflensburg.dataservice.domain.model.Template;
import de.hsflensburg.dataservice.exceptions.BadFormatException;
import de.hsflensburg.dataservice.exceptions.CategoryNotFoundException;
import de.hsflensburg.dataservice.repository.CategoryRepo;
import de.hsflensburg.dataservice.repository.TemplateRepo;
import de.hsflensburg.dataservice.service.TemplateService;
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
public class TestTemplateService {

    @Mock
    private TemplateRepo templateRepo;
    @Mock
    private CategoryRepo categoryRepo;
    private TemplateService templateService;

    @BeforeEach
    private void init() {
        templateService = new TemplateService(templateRepo, categoryRepo);
    }

    @Test
    public void testCreateTemplate() throws CategoryNotFoundException, BadFormatException {
        doAnswer(invocationOnMock -> {
            Template template = invocationOnMock.getArgument(0);
            template.setId(new ObjectId());

            return null;
        }).when(templateRepo).save(any());

        ObjectId categoryId = new ObjectId();

        when(categoryRepo.existsByIdAndType(categoryId, CategoryType.REQUEST.toString())).thenReturn(true);

        templateService.createTemplate(Map.of("de", "Titel", "en", "Title"), categoryId.toHexString(), new ArrayList<>());

        verify(templateRepo).save(any());
    }

    @Test
    public void testCreateTemplateBadFormat() {
        assertThrows(BadFormatException.class, () ->
                templateService.createTemplate(Map.of("de", "Titel", "en", "Title"), "123", new ArrayList<>()));

        verify(categoryRepo, never()).existsByIdAndType(any(), any());
        verify(templateRepo, never()).save(any());
    }

    @Test
    public void testCreateCategoryDoesntExist() {
        ObjectId categoryId = new ObjectId();

        when(categoryRepo.existsByIdAndType(categoryId, CategoryType.REQUEST.toString())).thenReturn(false);

        assertThrows(CategoryNotFoundException.class, () ->
                templateService.createTemplate(Map.of("de", "Titel", "en", "Title"), categoryId.toHexString(), new ArrayList<>()));

        verify(templateRepo, never()).save(any());
    }

    @Test
    public void testGetById() throws BadFormatException {
        ObjectId objectId = new ObjectId();

        templateService.getById(objectId.toHexString());

        verify(templateRepo).findById(objectId);
    }

    @Test
    public void testGetByIdBadFormat() {
        assertThrows(BadFormatException.class, () ->
                templateService.getById("123"));

        verify(templateRepo, never()).findById(any());
    }

    @Test
    public void testGetAll() {
        templateService.getAll();

        verify(templateRepo).findAll();
    }

    @Test
    public void testGetAllByCategory() throws BadFormatException {
        ObjectId objectId = new ObjectId();

        templateService.getAllByCategory(objectId.toHexString());

        verify(templateRepo).findByCategoryId(objectId);
    }

    @Test
    public void testGetAllByCategoryBadFormat() {
        assertThrows(BadFormatException.class, () ->
                templateService.getAllByCategory("123"));

        verify(templateRepo, never()).findByCategoryId(any());
    }

    @Test
    public void testExistsById() throws BadFormatException {
        ObjectId objectId = new ObjectId();

        templateService.existsById(objectId.toHexString());

        verify(templateRepo).existsById(objectId);
    }

    @Test
    public void testExistsByIdBadFormat() {
        assertThrows(BadFormatException.class, () ->
                templateService.existsById("123"));

        verify(templateRepo, never()).existsById(any());
    }

    @Test
    public void testDeleteTemplate() throws BadFormatException {
        ObjectId objectId = new ObjectId();

        templateService.deleteTemplate(objectId.toHexString());

        verify(templateRepo).deleteTemplateById(objectId);
    }

    @Test
    public void testDeleteTemplateBadFormat() {
        assertThrows(BadFormatException.class, () ->
                templateService.deleteTemplate("123"));

        verify(templateRepo, never()).deleteTemplateById(any());
    }

    @Test
    public void testUpdateTemplate() throws CategoryNotFoundException, BadFormatException {
        ObjectId templateId = new ObjectId();
        ObjectId categoryId = new ObjectId();

        Template existingTemplate = new Template(Map.of("de", "Titel", "en", "Title"), categoryId, new ArrayList<>());

        when(categoryRepo.existsByIdAndType(categoryId, CategoryType.REQUEST.toString())).thenReturn(true);
        when(templateRepo.findById(templateId)).thenReturn(Optional.of(existingTemplate));

        Map<String, String> newTitle = Map.of("de", "newDe", "en", "newEn");
        templateService.updateTemplate(templateId.toHexString(), newTitle, categoryId.toHexString(), new ArrayList<>());

        ArgumentCaptor<Template> saveArgumentCaptor = ArgumentCaptor.forClass(Template.class);

        verify(templateRepo).save(saveArgumentCaptor.capture());

        assertEquals(newTitle, saveArgumentCaptor.getValue().getTitle());
        assertEquals(categoryId, saveArgumentCaptor.getValue().getCategoryId());
    }

    @Test
    public void testUpdateTemplateBadId() {
        assertThrows(BadFormatException.class, () ->
                templateService.updateTemplate("123", Map.of("de", "Titel", "en", "Title"), new ObjectId().toHexString(), new ArrayList<>()));

        verify(categoryRepo, never()).existsByIdAndType(any(), any());
        verify(templateRepo, never()).findById(any());
        verify(templateRepo, never()).save(any());
    }

    @Test
    public void testUpdateTemplateBadCategoryId() {
        assertThrows(BadFormatException.class, () ->
                templateService.updateTemplate(new ObjectId().toHexString(), Map.of("de", "Titel", "en", "Title"),
                        "123", new ArrayList<>()));

        verify(categoryRepo, never()).existsByIdAndType(any(), any());
        verify(templateRepo, never()).findById(any());
        verify(templateRepo, never()).save(any());
    }

    @Test
    public void testUpdateTemplateCategoryDoesntExist() {
        ObjectId categoryId = new ObjectId();

        when(categoryRepo.existsByIdAndType(categoryId, CategoryType.REQUEST.toString())).thenReturn(false);

        assertThrows(CategoryNotFoundException.class, () ->
                templateService.updateTemplate(new ObjectId().toHexString(), Map.of("de", "Titel", "en", "Title"),
                        categoryId.toHexString(), new ArrayList<>()));

        verify(templateRepo, never()).findById(any());
        verify(templateRepo, never()).save(any());
    }

    @Test
    public void testUpdateTemplateCategoryTemplateDoesntExist() throws CategoryNotFoundException, BadFormatException {
        ObjectId templateId = new ObjectId();
        ObjectId categoryId = new ObjectId();

        when(categoryRepo.existsByIdAndType(categoryId, CategoryType.REQUEST.toString())).thenReturn(true);
        when(templateRepo.findById(templateId)).thenReturn(Optional.empty());

        templateService.updateTemplate(templateId.toHexString(), Map.of("de", "Titel", "en", "Title"), categoryId.toHexString(), new ArrayList<>());

        verify(templateRepo, never()).save(any());
    }
}
