package de.hsflensburg.dataservice.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.hsflensburg.dataservice.domain.dto.CategoryResponse;
import de.hsflensburg.dataservice.domain.dto.CreateCategoryRequest;
import de.hsflensburg.dataservice.domain.dto.UpdateCategoryRequest;
import de.hsflensburg.dataservice.domain.model.Category;
import de.hsflensburg.dataservice.domain.model.CategoryType;
import de.hsflensburg.dataservice.repository.CategoryRepo;
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

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;

import static de.hsflensburg.dataservice.util.JsonHelper.fromJson;
import static de.hsflensburg.dataservice.util.JsonHelper.toJson;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("test")
@AutoConfigureMockMvc
@AutoConfigureDataMongo
@SpringBootTest
@WithMockUser
public class TestCategoryApi {

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private final CategoryRepo categoryRepo;

    @Autowired
    public TestCategoryApi(MockMvc mockMvc, ObjectMapper objectMapper, CategoryRepo categoryRepo) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
        this.categoryRepo = categoryRepo;
    }

    @BeforeEach
    private void deleteAll() {
        categoryRepo.deleteAll();
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void testCreateCategory() throws Exception {
        CreateCategoryRequest createCategoryRequest = new CreateCategoryRequest();
        createCategoryRequest.setType(CategoryType.REQUEST);
        createCategoryRequest.setName(Map.of("de", "Projekt", "en", "Project"));
        createCategoryRequest.setColor("red");

        this.mockMvc
                .perform(post("/category")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(objectMapper, createCategoryRequest)))
                .andExpect(status().is(201))
                .andReturn();
    }

    @Test
    public void testGetCategory() throws Exception {
        Category category = new Category(CategoryType.REQUEST, Map.of("de", "Projekt", "en", "Project"), "red");
        categoryRepo.insert(category);

        String id = category.getId().toHexString();

        MvcResult result = this.mockMvc
                .perform(get("/category/" + id))
                .andExpect(status().is(200))
                .andReturn();

        CategoryResponse response = fromJson(
                objectMapper,
                result.getResponse().getContentAsString(),
                CategoryResponse.class
        );

        assertEquals(id, response.getId());

        long now = LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli();
        long createdAtDiff = now - response.getCreatedAt().toInstant(ZoneOffset.UTC).toEpochMilli();
        long modifiedAtDiff = now - response.getModifiedAt().toInstant(ZoneOffset.UTC).toEpochMilli();
        assertEquals(0, createdAtDiff, 200L);
        assertEquals(0, modifiedAtDiff, 200L);

        assertEquals(category.getType().toString().toLowerCase(), response.getType());
        assertEquals(category.getName(), response.getName());
        assertEquals(category.getColor(), response.getColor());
    }

    @Test
    public void testGetCategoryDoesntExist() throws Exception {
        Category category = new Category(CategoryType.REQUEST, Map.of("de", "Projekt", "en", "Project"), "red");
        categoryRepo.insert(category);

        String id = new ObjectId().toHexString();

        this.mockMvc
            .perform(get("/category/" + id))
            .andExpect(status().is(404))
            .andReturn();
    }

    @Test
    public void testGetCategoryBadObjectId() throws Exception {
        Category category = new Category(CategoryType.REQUEST, Map.of("de", "Projekt", "en", "Project"), "red");
        categoryRepo.insert(category);

        String id = "123";

        this.mockMvc
                .perform(get("/category/" + id))
                .andExpect(status().is(404))
                .andReturn();
    }

    @Test
    public void testGetAllCategories() throws Exception {
        Category category1 = new Category(CategoryType.REQUEST, Map.of("de", "Projekt", "en", "Project"), "red");
        Category category2 = new Category(CategoryType.OFFER, Map.of("de", "Mitarbeiter", "en", "Employee"), "red");
        categoryRepo.insert(List.of(category1, category2));

        MvcResult resultRequests = this.mockMvc
                .perform(get("/category?type=request"))
                .andExpect(status().is(200))
                .andReturn();

        List<CategoryResponse> responseRequests = fromJson(
                objectMapper,
                resultRequests.getResponse().getContentAsString(),
                new TypeReference<>() {}
        );

        assertEquals(1, responseRequests.size());
        assertEquals(category1.getName(), responseRequests.get(0).getName());

        MvcResult resultOffers = this.mockMvc
                .perform(get("/category?type=offer"))
                .andExpect(status().is(200))
                .andReturn();

        List<CategoryResponse> responseOffers = fromJson(
                objectMapper,
                resultOffers.getResponse().getContentAsString(),
                new TypeReference<>() {}
        );

        assertEquals(1, responseOffers.size());
        assertEquals(category2.getName(), responseOffers.get(0).getName());
        assertEquals(category2.getColor(), responseOffers.get(0).getColor());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void testUpdateCategory() throws Exception {
        Category category = new Category(CategoryType.REQUEST, Map.of("de", "Projekt", "en", "Project"), "red");
        categoryRepo.insert(category);

        String id = category.getId().toHexString();

        UpdateCategoryRequest updateCategoryRequest = new UpdateCategoryRequest();
        updateCategoryRequest.setName(Map.of("de", "das ist ein projekt", "en", "this is a project"));
        updateCategoryRequest.setColor("blue");

        MvcResult result = this.mockMvc
                .perform(put("/category/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(objectMapper, updateCategoryRequest)))
                .andExpect(status().is(200))
                .andReturn();

        CategoryResponse response = fromJson(
                objectMapper,
                result.getResponse().getContentAsString(),
                CategoryResponse.class
        );

        assertEquals(id, response.getId());

        long now = LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli();
        long modifiedAtDiff = now - response.getModifiedAt().toInstant(ZoneOffset.UTC).toEpochMilli();
        assertEquals(0, modifiedAtDiff, 200L);

        assertEquals(category.getType().toString().toLowerCase(), response.getType());
        assertEquals(updateCategoryRequest.getName(), response.getName());
        assertEquals(updateCategoryRequest.getColor(), response.getColor());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void testUpdateCategoryDoesntExist() throws Exception {
        Category category = new Category(CategoryType.REQUEST, Map.of("de", "Projekt", "en", "Project"), "red");
        categoryRepo.insert(category);

        String id = new ObjectId().toHexString();

        UpdateCategoryRequest updateCategoryRequest = new UpdateCategoryRequest();
        updateCategoryRequest.setName(Map.of("de", "das ist ein projekt", "en", "this is a project"));
        updateCategoryRequest.setColor("red");

        this.mockMvc
            .perform(put("/category/" + id)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(objectMapper, updateCategoryRequest)))
            .andExpect(status().is(404))
            .andReturn();
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void testUpdateCategoryBadObjectId() throws Exception {
        Category category = new Category(CategoryType.REQUEST, Map.of("de", "Projekt", "en", "Project"), "red");
        categoryRepo.insert(category);

        String id = "123";

        UpdateCategoryRequest updateCategoryRequest = new UpdateCategoryRequest();
        updateCategoryRequest.setName(Map.of("de", "das ist ein projekt", "en", "this is a project"));
        updateCategoryRequest.setColor("red");

        this.mockMvc
                .perform(put("/category/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(objectMapper, updateCategoryRequest)))
                .andExpect(status().is(404))
                .andReturn();
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void testDeleteCategory() throws Exception {
        Category category = new Category(CategoryType.REQUEST, Map.of("de", "Projekt", "en", "Project"), "red");
        categoryRepo.insert(category);

        String id = category.getId().toHexString();

        this.mockMvc
                .perform(delete("/category/" + id))
                .andExpect(status().is(204))
                .andReturn();
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void testDeleteCategoryDoesntExist() throws Exception {
        Category category = new Category(CategoryType.REQUEST, Map.of("de", "Projekt", "en", "Project"), "red");
        categoryRepo.insert(category);

        String id = new ObjectId().toHexString();

        this.mockMvc
                .perform(delete("/category/" + id))
                .andExpect(status().is(404))
                .andReturn();
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void testDeleteCategoryBadObjectId() throws Exception {
        Category category = new Category(CategoryType.REQUEST, Map.of("de", "Projekt", "en", "Project"), "red");
        categoryRepo.insert(category);

        String id = "123";

        this.mockMvc
                .perform(delete("/category/" + id))
                .andExpect(status().is(404))
                .andReturn();
    }

}
