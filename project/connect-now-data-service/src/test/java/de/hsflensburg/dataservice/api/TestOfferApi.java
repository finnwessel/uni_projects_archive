package de.hsflensburg.dataservice.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.hsflensburg.dataservice.domain.dto.*;
import de.hsflensburg.dataservice.domain.model.Category;
import de.hsflensburg.dataservice.domain.model.CategoryType;
import de.hsflensburg.dataservice.domain.model.Offer;
import de.hsflensburg.dataservice.domain.model.components.TextComp;
import de.hsflensburg.dataservice.repository.CategoryRepo;
import de.hsflensburg.dataservice.repository.OfferRepo;
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
public class TestOfferApi {

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private final OfferRepo offerRepo;
    private final CategoryRepo categoryRepo;

    private final static ObjectId CATEGORY_ID = new ObjectId();

    @Autowired
    public TestOfferApi(MockMvc mockMvc, ObjectMapper objectMapper, OfferRepo offerRepo,
                          CategoryRepo categoryRepo) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
        this.offerRepo = offerRepo;
        this.categoryRepo = categoryRepo;
    }

    @BeforeEach
    private void init() {
        offerRepo.deleteAll();
        categoryRepo.deleteAll();

        Category category = new Category(CategoryType.OFFER, Map.of("de", "Projekt", "en", "Project"), "red");
        category.setId(CATEGORY_ID);
        categoryRepo.insert(category);
    }

    @Test
    @WithMockUser(roles = {"TEACHER"})
    public void testCreateOffer() throws Exception {
        CreateOfferRequest createOfferRequest = new CreateOfferRequest();
        createOfferRequest.setTitle("title");
        createOfferRequest.setCategoryId(CATEGORY_ID.toHexString());
        createOfferRequest.setVisible(true);

        TextComp comp = new TextComp();
        comp.setType("text");
        comp.setDescription(Map.of("de", "Textfeld", "en", "Textfield"));
        comp.setRequired(true);
        comp.setText("content");

        createOfferRequest.setComponents(List.of(comp));

        this.mockMvc
                .perform(post("/offer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(objectMapper, createOfferRequest)))
                .andExpect(status().is(201))
                .andReturn();
    }

    @Test
    @WithMockUser(roles = {"TEACHER"})
    public void testCreateOfferCategoryNotFound() throws Exception {
        CreateOfferRequest createOfferRequest = new CreateOfferRequest();
        createOfferRequest.setTitle("title");
        createOfferRequest.setCategoryId(new ObjectId().toHexString());
        createOfferRequest.setVisible(true);

        TextComp comp = new TextComp();
        comp.setType("text");
        comp.setDescription(Map.of("de", "Textfeld", "en", "Textfield"));
        comp.setRequired(true);
        comp.setText("content");

        createOfferRequest.setComponents(List.of(comp));

        this.mockMvc
                .perform(post("/offer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(objectMapper, createOfferRequest)))
                .andExpect(status().is(400))
                .andReturn();
    }

    @Test
    public void testGetOffer() throws Exception {
        Offer offer = new Offer("owner", "title", CATEGORY_ID, true, new ArrayList<>());
        offerRepo.insert(offer);

        MvcResult result = this.mockMvc
            .perform(get("/offer/" + offer.getId().toHexString()))
            .andExpect(status().is(200))
            .andReturn();

        OfferResponse response = fromJson(
                objectMapper,
                result.getResponse().getContentAsString(),
                OfferResponse.class);

        assertEquals(offer.getId().toHexString(), response.getId());
        assertEquals(offer.getOwner(), response.getOwner());
        assertEquals(offer.getTitle(), response.getTitle());
    }

    @Test
    public void testGetOfferNotFound() throws Exception {
        this.mockMvc
            .perform(get("/offer/" + new ObjectId().toHexString()))
            .andExpect(status().is(404))
            .andReturn();

        this.mockMvc
            .perform(get("/offer/" + "123"))
            .andExpect(status().is(404))
            .andReturn();
    }

    @Test
    public void testGetAllOffersWhereEntitled() throws Exception {
        Offer offer1 = new Offer("owner", "title", CATEGORY_ID, true, new ArrayList<>());
        Offer offer2 = new Offer("other", "title2", CATEGORY_ID, false, new ArrayList<>());
        offerRepo.insert(List.of(offer1, offer2));

        MvcResult result = this.mockMvc
                .perform(get("/offer"))
                .andExpect(status().is(200))
                .andReturn();

        List<OfferResponse> response = fromJson(
                objectMapper,
                result.getResponse().getContentAsString(),
                new TypeReference<>() {});

        assertEquals(1, response.size());
    }

    @Test
    public void testGetAllOffersWhereEntitledByOwner() throws Exception {
        Offer offer1 = new Offer("owner", "title", CATEGORY_ID, true, new ArrayList<>());
        Offer offer2 = new Offer("other", "title2", CATEGORY_ID, true, new ArrayList<>());
        offerRepo.insert(List.of(offer1, offer2));

        MvcResult result = this.mockMvc
                .perform(get("/offer")
                        .param("owner", "other"))
                .andExpect(status().is(200))
                .andReturn();

        List<OfferResponse> response = fromJson(
                objectMapper,
                result.getResponse().getContentAsString(),
                new TypeReference<>() {});

        assertEquals(1, response.size());
    }

    @Test
    public void testGetAllOffersWhereEntitledEmpty() throws Exception {
        MvcResult result = this.mockMvc
                .perform(get("/offer")
                        .param("owner", "other"))
                .andExpect(status().is(200))
                .andReturn();

        List<OfferResponse> response = fromJson(
                objectMapper,
                result.getResponse().getContentAsString(),
                new TypeReference<>() {});

        assertEquals(0, response.size());
    }

    @Test
    public void testDeleteOffer() {
        //TODO
    }

    @Test
    @WithMockUser(username = "owner", roles = {"TEACHER"})
    public void testUpdateOffer() throws Exception {
        Offer offer = new Offer("owner", "title", CATEGORY_ID, true, new ArrayList<>());
        offerRepo.insert(offer);

        UpdateOfferRequest updateOfferRequest = new UpdateOfferRequest();
        updateOfferRequest.setTitle("newTitle");
        updateOfferRequest.setCategoryId(CATEGORY_ID.toHexString());
        updateOfferRequest.setVisible(true);

        TextComp comp = new TextComp();
        comp.setType("text");
        comp.setDescription(Map.of("de", "Textfeld", "en", "Textfield"));
        comp.setRequired(true);
        comp.setText("newContent");

        updateOfferRequest.setComponents(List.of(comp));

        MvcResult result = this.mockMvc
                .perform(put("/offer/" + offer.getId().toHexString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(objectMapper, updateOfferRequest)))
                .andExpect(status().is(200))
                .andReturn();

        OfferResponse response = fromJson(
                objectMapper,
                result.getResponse().getContentAsString(),
                OfferResponse.class);

        assertEquals(offer.getId().toHexString(), response.getId());
        assertEquals(updateOfferRequest.getTitle(), response.getTitle());
        assertEquals(updateOfferRequest.isVisible(), response.isVisible());
        assertEquals(updateOfferRequest.getComponents(), response.getComponents());
    }

    @Test
    @WithMockUser(roles = {"TEACHER"})
    public void testUpdateOfferNotFound() throws Exception {
        UpdateOfferRequest updateOfferRequest = new UpdateOfferRequest();
        updateOfferRequest.setTitle("newTitle");
        updateOfferRequest.setCategoryId(CATEGORY_ID.toHexString());
        updateOfferRequest.setVisible(true);

        TextComp comp = new TextComp();
        comp.setType("text");
        comp.setDescription(Map.of("de", "Textfeld", "en", "Textfield"));
        comp.setRequired(true);
        comp.setText("newContent");

        updateOfferRequest.setComponents(List.of(comp));

        this.mockMvc
                .perform(put("/offer/" + new ObjectId().toHexString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(objectMapper, updateOfferRequest)))
                .andExpect(status().is(404))
                .andReturn();
    }

    @Test
    @WithMockUser(username = "owner", roles = {"TEACHER"})
    public void testUpdateOfferCategoryNotFound() throws Exception {
        Offer offer = new Offer("owner", "title", CATEGORY_ID, true, new ArrayList<>());
        offerRepo.insert(offer);

        UpdateOfferRequest updateOfferRequest = new UpdateOfferRequest();
        updateOfferRequest.setTitle("newTitle");
        updateOfferRequest.setCategoryId(new ObjectId().toHexString());
        updateOfferRequest.setVisible(true);

        TextComp comp = new TextComp();
        comp.setType("text");
        comp.setDescription(Map.of("de", "Textfeld", "en", "Textfield"));
        comp.setRequired(true);
        comp.setText("newContent");

        updateOfferRequest.setComponents(List.of(comp));

        this.mockMvc
            .perform(put("/offer/" + offer.getId().toHexString())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(objectMapper, updateOfferRequest)))
            .andExpect(status().is(400))
            .andReturn();
    }
}
