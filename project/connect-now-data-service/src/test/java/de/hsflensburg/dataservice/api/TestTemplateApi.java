package de.hsflensburg.dataservice.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.hsflensburg.dataservice.domain.dto.CreateTemplateRequest;
import de.hsflensburg.dataservice.domain.dto.TemplateResponse;
import de.hsflensburg.dataservice.domain.dto.UpdateTemplateRequest;
import de.hsflensburg.dataservice.domain.model.Category;
import de.hsflensburg.dataservice.domain.model.CategoryType;
import de.hsflensburg.dataservice.domain.model.Template;
import de.hsflensburg.dataservice.domain.model.components.*;
import de.hsflensburg.dataservice.repository.CategoryRepo;
import de.hsflensburg.dataservice.repository.TemplateRepo;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static de.hsflensburg.dataservice.util.JsonHelper.fromJson;
import static de.hsflensburg.dataservice.util.JsonHelper.toJson;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@AutoConfigureMockMvc
@AutoConfigureDataMongo
@SpringBootTest
@WithMockUser
public class TestTemplateApi {

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private final TemplateRepo templateRepo;
    private final CategoryRepo categoryRepo;

    private final static ObjectId CATEGORY_ID = new ObjectId();

    @Autowired
    public TestTemplateApi(MockMvc mockMvc, ObjectMapper objectMapper, TemplateRepo templateRepo,
                        CategoryRepo categoryRepo) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
        this.templateRepo = templateRepo;
        this.categoryRepo = categoryRepo;
    }

    @BeforeEach
    private void init() {
        templateRepo.deleteAll();
        categoryRepo.deleteAll();

        Category category = new Category(CategoryType.REQUEST, Map.of("de", "Projekt", "en", "Project"), "red");
        category.setId(CATEGORY_ID);
        categoryRepo.insert(category);
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void testCreateTemplate() throws Exception {
        CreateTemplateRequest createTemplateRequest = new CreateTemplateRequest();
        createTemplateRequest.setTitle(Map.of("de", "Titel", "en", "Title"));
        createTemplateRequest.setCategoryId(CATEGORY_ID.toHexString());

        TextComp comp = new TextComp();
        comp.setType("text");
        comp.setDescription(Map.of("de", "Textfeld", "en", "Textfield"));
        comp.setRequired(true);
        comp.setText("");

        createTemplateRequest.setComponents(List.of(comp));

        this.mockMvc
                .perform(post("/template")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(objectMapper, createTemplateRequest)))
                .andExpect(status().is(201))
                .andReturn();
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void testCreateTemplateAllComponents() throws Exception {
        CreateTemplateRequest createTemplateRequest = new CreateTemplateRequest();
        createTemplateRequest.setTitle(Map.of("de", "Titel", "en", "Title"));
        createTemplateRequest.setCategoryId(CATEGORY_ID.toHexString());

        DateComp dateComp = new DateComp();
        dateComp.setType("date");
        dateComp.setDescription(Map.of("en", "Date"));
        dateComp.setRequired(true);
        dateComp.setDate(new Date());

        DateRangeComp dateRangeComp = new DateRangeComp();
        dateRangeComp.setType("dateRange");
        dateRangeComp.setDescription(Map.of("en", "DateRange"));
        dateRangeComp.setRequired(true);
        dateRangeComp.setStartDate(new Date());
        dateRangeComp.setEndDate(new Date());

        FileComp fileComp = new FileComp();
        fileComp.setType("file");
        fileComp.setDescription(Map.of("en", "File"));
        fileComp.setRequired(true);
        fileComp.setFiles(new ArrayList<>());

        InputComp inputComp = new InputComp();
        inputComp.setType("input");
        inputComp.setDescription(Map.of("en", "Input"));
        inputComp.setRequired(true);
        inputComp.setText("");

        TextComp textComp = new TextComp();
        textComp.setType("text");
        textComp.setDescription(Map.of("en", "Text"));
        textComp.setRequired(true);
        textComp.setText("");

        createTemplateRequest.setComponents(List.of(dateComp, dateRangeComp, fileComp, inputComp, textComp));

        this.mockMvc
                .perform(post("/template")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(objectMapper, createTemplateRequest)))
                .andExpect(status().is(201))
                .andReturn();
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void testCreateTemplateCategoryNotFound() throws Exception {
        CreateTemplateRequest createTemplateRequest = new CreateTemplateRequest();
        createTemplateRequest.setTitle(Map.of("de", "Titel", "en", "Title"));
        createTemplateRequest.setCategoryId(new ObjectId().toHexString());
        createTemplateRequest.setComponents(new ArrayList<>());

        this.mockMvc
                .perform(post("/template")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(objectMapper, createTemplateRequest)))
                .andExpect(status().is(400))
                .andReturn();
    }

    @Test
    public void testGetTemplate() throws Exception {
        Template template = new Template(Map.of("de", "Titel", "en", "Title"), CATEGORY_ID, new ArrayList<>());
        templateRepo.insert(template);

        MvcResult result = this.mockMvc
                .perform(get("/template/" + template.getId().toHexString()))
                .andExpect(status().is(200))
                .andReturn();

        TemplateResponse response = fromJson(
                objectMapper,
                result.getResponse().getContentAsString(),
                TemplateResponse.class);

        assertEquals(template.getId().toHexString(), response.getId());
        assertEquals(template.getTitle(), response.getTitle());
        assertEquals(template.getComponents(), response.getComponents());
    }

    @Test
    public void testGetTemplateNotFound() throws Exception {
        this.mockMvc
            .perform(get("/template/" + new ObjectId().toHexString()))
            .andExpect(status().is(404))
            .andReturn();
    }

    @Test
    public void testGetAllTemplates() throws Exception {
        Template template1 = new Template(Map.of("de", "Titel", "en", "Title"), CATEGORY_ID, new ArrayList<>());
        Template template2 = new Template(Map.of("de", "Titel1", "en", "Title1"), CATEGORY_ID, new ArrayList<>());
        templateRepo.insert(List.of(template1, template2));

        MvcResult result = this.mockMvc
                .perform(get("/template"))
                .andExpect(status().is(200))
                .andReturn();

        List<TemplateResponse> response = fromJson(
                objectMapper,
                result.getResponse().getContentAsString(),
                new TypeReference<>() {});

        assertEquals(2, response.size());
    }

    @Test
    public void testDeleteTemplate() {
        //TODO
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void testUpdateTemplate() throws Exception {
        Template template = new Template(Map.of("de", "Titel", "en", "Title"), CATEGORY_ID, new ArrayList<>());
        templateRepo.insert(template);

        UpdateTemplateRequest updateTemplateRequest = new UpdateTemplateRequest();
        updateTemplateRequest.setTitle(Map.of("de", "Titel", "en", "Title"));
        updateTemplateRequest.setCategoryId(CATEGORY_ID.toHexString());

        TextComp comp = new TextComp();
        comp.setType("text");
        comp.setDescription(Map.of("de", "Textfeld", "en", "Textfield"));
        comp.setRequired(true);
        comp.setText("");

        updateTemplateRequest.setComponents(List.of(comp));

        MvcResult result = this.mockMvc
                .perform(put("/template/" + template.getId().toHexString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(objectMapper, updateTemplateRequest)))
                .andExpect(status().is(200))
                .andReturn();

        TemplateResponse response = fromJson(
                objectMapper,
                result.getResponse().getContentAsString(),
                TemplateResponse.class);

        assertEquals(template.getId().toHexString(), response.getId());
        assertEquals(updateTemplateRequest.getTitle(), response.getTitle());
        assertEquals(updateTemplateRequest.getComponents(), response.getComponents());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void testUpdateTemplateNotFound() throws Exception {
        UpdateTemplateRequest updateTemplateRequest = new UpdateTemplateRequest();
        updateTemplateRequest.setTitle(Map.of("de", "Titel", "en", "Title"));
        updateTemplateRequest.setCategoryId(CATEGORY_ID.toHexString());

        TextComp comp = new TextComp();
        comp.setType("text");
        comp.setDescription(Map.of("de", "Textfeld", "en", "Textfield"));
        comp.setRequired(true);
        comp.setText("");

        updateTemplateRequest.setComponents(List.of(comp));

        this.mockMvc
            .perform(put("/template/" + new ObjectId().toHexString())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(objectMapper, updateTemplateRequest)))
            .andExpect(status().is(404))
            .andReturn();
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void testUpdateTemplateCategoryNotFound() throws Exception {
        Template template = new Template(Map.of("de", "Titel", "en", "Title"), CATEGORY_ID, new ArrayList<>());
        templateRepo.insert(template);

        UpdateTemplateRequest updateTemplateRequest = new UpdateTemplateRequest();
        updateTemplateRequest.setTitle(Map.of("de", "Titel", "en", "Title"));
        updateTemplateRequest.setCategoryId(new ObjectId().toHexString());

        TextComp comp = new TextComp();
        comp.setType("text");
        comp.setDescription(Map.of("de", "Textfeld", "en", "Textfield"));
        comp.setRequired(true);
        comp.setText("");

        updateTemplateRequest.setComponents(List.of(comp));

        this.mockMvc
            .perform(put("/template/" + template.getId().toHexString())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(objectMapper, updateTemplateRequest)))
            .andExpect(status().is(400))
            .andReturn();
    }
}
