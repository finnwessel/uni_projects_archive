package de.hsflensburg.dataservice.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.hsflensburg.dataservice.domain.dto.*;
import de.hsflensburg.dataservice.domain.model.*;
import de.hsflensburg.dataservice.domain.model.components.BaseComp;
import de.hsflensburg.dataservice.domain.model.components.TextComp;
import de.hsflensburg.dataservice.repository.CategoryRepo;
import de.hsflensburg.dataservice.repository.RequestRepo;
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

import java.time.LocalDateTime;
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
public class TestRequestApi {

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private final RequestRepo requestRepo;
    private final CategoryRepo categoryRepo;
    private final TemplateRepo templateRepo;

    private final static ObjectId CATEGORY_ID = new ObjectId();
    private final static ObjectId TEMPLATE_ID = new ObjectId();

    @Autowired
    public TestRequestApi(MockMvc mockMvc, ObjectMapper objectMapper, RequestRepo requestRepo,
                          CategoryRepo categoryRepo, TemplateRepo templateRepo) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
        this.requestRepo = requestRepo;
        this.categoryRepo = categoryRepo;
        this.templateRepo = templateRepo;
    }

    @BeforeEach
    private void init() {
        requestRepo.deleteAll();
        templateRepo.deleteAll();
        categoryRepo.deleteAll();

        Category category = new Category(CategoryType.REQUEST, Map.of("de", "Projekt", "en", "Project"), "red");
        category.setId(CATEGORY_ID);
        categoryRepo.insert(category);

        List<BaseComp> templateComps = new ArrayList<>();
        TextComp templateComp = new TextComp();
        templateComp.setType("text");
        templateComp.setDescription(Map.of("de", "Textfeld", "en", "Textfield"));
        templateComp.setRequired(true);
        templateComp.setText("");
        templateComps.add(templateComp);

        Template template = new Template(Map.of("de", "Titel", "en", "Title"), CATEGORY_ID, templateComps);
        template.setId(TEMPLATE_ID);
        templateRepo.insert(template);
    }

    @Test
    @WithMockUser(roles = {"STUDENT"})
    public void testCreateRequest() throws Exception {
        CreateRequestRequest createRequestRequest = new CreateRequestRequest();
        createRequestRequest.setTitle("Title");
        createRequestRequest.setTemplateId(TEMPLATE_ID.toHexString());
        createRequestRequest.setParticipants(List.of("participant"));

        TextComp comp = new TextComp();
        comp.setType("text");
        comp.setDescription(Map.of("de", "Textfeld", "en", "Textfield"));
        comp.setRequired(true);
        comp.setText("content");
        createRequestRequest.setComponents(List.of(comp));

        this.mockMvc
                .perform(post("/request/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(objectMapper, createRequestRequest)))
                .andExpect(status().is(201))
                .andReturn();
    }

    @Test
    public void testCreateRequestTemplateNotFound() throws Exception {
        CreateRequestRequest createRequestRequest = new CreateRequestRequest();
        createRequestRequest.setTitle("Title");
        createRequestRequest.setTemplateId(new ObjectId().toHexString());
        createRequestRequest.setParticipants(List.of("participant"));
        createRequestRequest.setComponents(new ArrayList<>());

        this.mockMvc
                .perform(post("/request/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(objectMapper, createRequestRequest)))
                .andExpect(status().is(400))
                .andReturn();
    }

    @Test
    @WithMockUser(roles = {"STUDENT"})
    public void testCreateRequestNotTemplateMatch() throws Exception {
        CreateRequestRequest createRequestRequest = new CreateRequestRequest();
        createRequestRequest.setTitle("Title");
        createRequestRequest.setTemplateId(TEMPLATE_ID.toHexString());
        createRequestRequest.setParticipants(List.of("participant"));

        TextComp comp = new TextComp();
        comp.setType("text");
        comp.setDescription(Map.of("de", "doesnt exist in template", "en", "Textfield"));
        comp.setRequired(true);
        comp.setText("content");

        createRequestRequest.setComponents(List.of(comp));

        this.mockMvc
                .perform(post("/request/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(objectMapper, createRequestRequest)))
                .andExpect(status().is(400))
                .andReturn();
    }

    @Test
    @WithMockUser(username = "owner")
    public void testGetRequest() throws Exception {
        Request request = new Request("owner", "title", new ObjectId(), new ObjectId(), new ArrayList<>(),
                new ArrayList<>(), new ArrayList<>(), true);
        requestRepo.insert(request);

        MvcResult result = this.mockMvc
                .perform(get("/request/" + request.getId().toHexString()))
                .andExpect(status().is(200))
                .andReturn();

        RequestResponse response = fromJson(
                objectMapper,
                result.getResponse().getContentAsString(),
                RequestResponse.class);

        assertEquals(request.getId().toHexString(), response.getId());
        assertEquals(request.getOwner(), response.getOwner());
        assertEquals(request.getTitle(), response.getTitle());
    }

    @Test
    @WithMockUser(username = "notEntitled")
    public void testGetRequestNotEntitled() throws Exception {
        Request request = new Request("owner", "title", new ObjectId(), new ObjectId(), new ArrayList<>(),
                new ArrayList<>(), new ArrayList<>(), true);
        requestRepo.insert(request);

        this.mockMvc
            .perform(get("/request/" + request.getId().toHexString()))
            .andExpect(status().is(404))
            .andReturn();
    }

    @Test
    public void testGetRequestNotFound() throws Exception {
        this.mockMvc
            .perform(get("/request/" + new ObjectId().toHexString()))
            .andExpect(status().is(404))
            .andReturn();

        this.mockMvc
                .perform(get("/request/" + "123"))
                .andExpect(status().is(404))
                .andReturn();
    }

    @Test
    @WithMockUser(username = "owner")
    public void testGetAllRequest() throws Exception {
        Request request1 = new Request("owner", "title1", new ObjectId(), new ObjectId(), new ArrayList<>(),
                new ArrayList<>(), new ArrayList<>(), true);
        Request request2 = new Request("owner", "title2", new ObjectId(), new ObjectId(), new ArrayList<>(),
                new ArrayList<>(), new ArrayList<>(), true);
        Request request3 = new Request("other", "title3", new ObjectId(), new ObjectId(), new ArrayList<>(),
                new ArrayList<>(), new ArrayList<>(), true);
        requestRepo.insert(List.of(request1, request2, request3));

        MvcResult result = this.mockMvc
                .perform(get("/request"))
                .andExpect(status().is(200))
                .andReturn();

        List<RequestResponse> response = fromJson(
                objectMapper,
                result.getResponse().getContentAsString(),
                new TypeReference<>() {});

        assertEquals(2, response.size());
    }

    @Test
    public void testGetAllRequestEmpty() throws Exception {
        MvcResult result = this.mockMvc
                .perform(get("/request"))
                .andExpect(status().is(200))
                .andReturn();

        List<RequestResponse> response = fromJson(
                objectMapper,
                result.getResponse().getContentAsString(),
                new TypeReference<>() {});

        assertEquals(0, response.size());
    }

    @Test
    @WithMockUser(username = "owner", roles = {"STUDENT"})
    public void testUpdateRequest() throws Exception {
        Request request = new Request("owner", "title", TEMPLATE_ID, CATEGORY_ID, new ArrayList<>(),
                new ArrayList<>(), new ArrayList<>(), true);
        requestRepo.insert(request);

        UpdateRequestRequest updateRequestRequest = new UpdateRequestRequest();
        updateRequestRequest.setTitle("newTitle");
        updateRequestRequest.setTemplateId(TEMPLATE_ID.toHexString());
        updateRequestRequest.setParticipants(List.of("participant"));

        TextComp comp = new TextComp();
        comp.setType("text");
        comp.setDescription(Map.of("de", "Textfeld", "en", "Textfield"));
        comp.setRequired(true);
        comp.setText("newContent");

        updateRequestRequest.setComponents(List.of(comp));

        MvcResult result = this.mockMvc
                .perform(put("/request/" + request.getId().toHexString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(objectMapper, updateRequestRequest)))
                .andExpect(status().is(200))
                .andReturn();

        RequestResponse response = fromJson(
                objectMapper,
                result.getResponse().getContentAsString(),
                RequestResponse.class);

        assertEquals(request.getId().toHexString(), response.getId());
        assertEquals(updateRequestRequest.getTitle(), response.getTitle());
        assertEquals(updateRequestRequest.getParticipants(), response.getParticipants());
        assertEquals(updateRequestRequest.getComponents(), response.getComponents());
    }

    @Test
    @WithMockUser(roles = {"STUDENT"})
    public void testUpdateRequestNotFound() throws Exception {
        UpdateRequestRequest updateRequestRequest = new UpdateRequestRequest();
        updateRequestRequest.setTitle("newTitle");
        updateRequestRequest.setTemplateId(TEMPLATE_ID.toHexString());
        updateRequestRequest.setParticipants(List.of("participant"));

        TextComp comp = new TextComp();
        comp.setType("text");
        comp.setDescription(Map.of("de", "Textfeld", "en", "Textfield"));
        comp.setRequired(true);
        comp.setText("newContent");

        updateRequestRequest.setComponents(List.of(comp));

        MvcResult result = this.mockMvc
                .perform(put("/request/" + new ObjectId().toHexString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(objectMapper, updateRequestRequest)))
                .andExpect(status().is(404))
                .andReturn();
    }

    @Test
    @WithMockUser(roles = {"STUDENT"})
    public void testUpdateRequestCategoryNotFound() throws Exception {
        UpdateRequestRequest updateRequestRequest = new UpdateRequestRequest();
        updateRequestRequest.setTitle("newTitle");
        updateRequestRequest.setTemplateId(new ObjectId().toHexString());
        updateRequestRequest.setParticipants(List.of("participant"));

        TextComp comp = new TextComp();
        comp.setType("text");
        comp.setDescription(Map.of("de", "Textfeld", "en", "Textfield"));
        comp.setRequired(true);
        comp.setText("newContent");

        updateRequestRequest.setComponents(List.of(comp));

        this.mockMvc
            .perform(put("/request/" + new ObjectId().toHexString())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(objectMapper, updateRequestRequest)))
            .andExpect(status().is(400))
            .andReturn();
    }

    @Test
    @WithMockUser(username = "owner", roles = {"STUDENT"})
    public void testUpdateRequestTemplateNotMatch() throws Exception {
        Request request = new Request("owner", "title", TEMPLATE_ID, CATEGORY_ID, new ArrayList<>(),
                new ArrayList<>(), new ArrayList<>(), true);
        requestRepo.insert(request);

        UpdateRequestRequest updateRequestRequest = new UpdateRequestRequest();
        updateRequestRequest.setTitle("newTitle");
        updateRequestRequest.setTemplateId(TEMPLATE_ID.toHexString());
        updateRequestRequest.setParticipants(List.of("participant"));

        TextComp comp = new TextComp();
        comp.setType("text");
        comp.setDescription(Map.of("de", "not in template", "en", "Textfield"));
        comp.setRequired(true);
        comp.setText("newContent");

        updateRequestRequest.setComponents(List.of(comp));

        this.mockMvc
            .perform(put("/request/" + request.getId().toHexString())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(objectMapper, updateRequestRequest)))
            .andExpect(status().is(400))
            .andReturn();
    }

    @Test
    @WithMockUser(username = "owner", roles = {"STUDENT"})
    public void testShareRequestStudent() throws Exception {
        Request request = new Request("owner", "title", TEMPLATE_ID, CATEGORY_ID, new ArrayList<>(),
                new ArrayList<>(), new ArrayList<>(), true);
        requestRepo.insert(request);

        ShareRequestRequest shareRequestRequest = new ShareRequestRequest();
        shareRequestRequest.setSubject("subject");

        this.mockMvc
                .perform(post("/request/share/" + request.getId().toHexString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(objectMapper, shareRequestRequest)))
                .andExpect(status().is(201))
                .andReturn();
    }
    @Test
    @WithMockUser(roles = {"STUDENT"})
    public void testShareRequestNotFound() throws Exception {
        ShareRequestRequest shareRequestRequest = new ShareRequestRequest();
        shareRequestRequest.setSubject("subject");

        this.mockMvc
                .perform(post("/request/share/" + new ObjectId().toHexString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(objectMapper, shareRequestRequest)))
                .andExpect(status().is(404))
                .andReturn();
    }

    @Test
    @WithMockUser(username = "owner", roles = {"STUDENT"})
    public void testShareRequestAlreadyShared() throws Exception {
        String toShare = "subject";

        Request request = new Request("owner", "title", TEMPLATE_ID, CATEGORY_ID, new ArrayList<>(),
                List.of(new Share(toShare, LocalDateTime.now(), ShareStatus.PENDING, true, true)),
                new ArrayList<>(), true);
        requestRepo.insert(request);

        ShareRequestRequest shareRequestRequest = new ShareRequestRequest();
        shareRequestRequest.setSubject(toShare);

        this.mockMvc
                .perform(post("/request/share/" + request.getId().toHexString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(objectMapper, shareRequestRequest)))
                .andExpect(status().is(409))
                .andReturn();
    }

    @Test
    @WithMockUser(username = "owner", roles = {"STUDENT"})
    public void testRevokeShareStudent() throws Exception {
        String toRevoke = "subject";

        Request request = new Request("owner", "title", TEMPLATE_ID, CATEGORY_ID, new ArrayList<>(),
                List.of(new Share(toRevoke, LocalDateTime.now(), ShareStatus.PENDING, true, true)),
                new ArrayList<>(), true);
        requestRepo.insert(request);

        ShareRequestRequest shareRequestRequest = new ShareRequestRequest();
        shareRequestRequest.setSubject(toRevoke);

        this.mockMvc
                .perform(put("/request/share/" + request.getId().toHexString() + "/revoke")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(objectMapper, shareRequestRequest)))
                .andExpect(status().is(200))
                .andReturn();
    }

    @Test
    @WithMockUser(username = "owner", roles = {"STUDENT"})
    public void testRevokeShareStudentBadRequest() throws Exception {
        Request request = new Request("owner", "title", TEMPLATE_ID, CATEGORY_ID, new ArrayList<>(),
                List.of(new Share("subject", LocalDateTime.now(), ShareStatus.PENDING, true, true)),
                new ArrayList<>(), true);
        requestRepo.insert(request);

        this.mockMvc
                .perform(put("/request/share/" + request.getId().toHexString() + "/revoke"))
                .andExpect(status().is(400))
                .andReturn();
    }

    @Test
    @WithMockUser(username = "owner", roles = {"STUDENT"})
    public void revokeShareNotFound() throws Exception {
        Request request = new Request("owner", "title", TEMPLATE_ID, CATEGORY_ID, new ArrayList<>(),
                List.of(new Share("other", LocalDateTime.now(), ShareStatus.PENDING, true, true)),
                new ArrayList<>(), true);
        requestRepo.insert(request);

        ShareRequestRequest shareRequestRequest = new ShareRequestRequest();
        shareRequestRequest.setSubject("subject");

        this.mockMvc
            .perform(put("/request/share/" + request.getId().toHexString() + "/revoke")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(objectMapper, shareRequestRequest)))
            .andExpect(status().is(404))
            .andReturn();
    }

    @Test
    @WithMockUser(username = "owner", roles = {"STUDENT"})
    public void revokeShareRequestNotFound() throws Exception {
        ShareRequestRequest shareRequestRequest = new ShareRequestRequest();
        shareRequestRequest.setSubject("subject");

        this.mockMvc
                .perform(put("/request/share/" + new ObjectId().toHexString() + "/revoke")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(objectMapper, shareRequestRequest)))
                .andExpect(status().is(404))
                .andReturn();
    }

    @Test
    @WithMockUser(username = "subject", roles = {"TEACHER"})
    public void testRevokeShareTeacher() throws Exception {
        Request request = new Request("owner", "title", TEMPLATE_ID, CATEGORY_ID, new ArrayList<>(),
                List.of(new Share("subject", LocalDateTime.now(), ShareStatus.PENDING, true, true)),
                new ArrayList<>(), true);
        requestRepo.insert(request);

        this.mockMvc
                .perform(put("/request/share/" + request.getId().toHexString() + "/revoke"))
                .andExpect(status().is(200))
                .andReturn();
    }

    @Test
    @WithMockUser(username = "owner", roles = {"STUDENT"})
    public void testReenactShare() throws Exception {
        Request request = new Request("owner", "title", TEMPLATE_ID, CATEGORY_ID, new ArrayList<>(),
                List.of(new Share("subject", LocalDateTime.now(), ShareStatus.PENDING, false, true)),
                new ArrayList<>(), true);
        requestRepo.insert(request);

        ShareRequestRequest shareRequestRequest = new ShareRequestRequest();
        shareRequestRequest.setSubject("subject");

        this.mockMvc
                .perform(put("/request/share/" + request.getId().toHexString() + "/reenact")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(objectMapper, shareRequestRequest)))
                .andExpect(status().is(200))
                .andReturn();
    }

    @Test
    @WithMockUser(username = "owner", roles = {"STUDENT"})
    public void testReenactShareNotFound() throws Exception {
        Request request = new Request("owner", "title", TEMPLATE_ID, CATEGORY_ID, new ArrayList<>(),
                List.of(new Share("other", LocalDateTime.now(), ShareStatus.PENDING, false, true)),
                new ArrayList<>(), true);
        requestRepo.insert(request);

        ShareRequestRequest shareRequestRequest = new ShareRequestRequest();
        shareRequestRequest.setSubject("subject");

        this.mockMvc
                .perform(put("/request/share/" + request.getId().toHexString() + "/reenact")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(objectMapper, shareRequestRequest)))
                .andExpect(status().is(404))
                .andReturn();
    }

    @Test
    @WithMockUser(username = "owner", roles = {"STUDENT"})
    public void testReenactShareRequestNotFound() throws Exception {
        ShareRequestRequest shareRequestRequest = new ShareRequestRequest();
        shareRequestRequest.setSubject("subject");

        this.mockMvc
                .perform(put("/request/share/" + new ObjectId().toHexString() + "/reenact")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(objectMapper, shareRequestRequest)))
                .andExpect(status().is(404))
                .andReturn();
    }

    @Test
    @WithMockUser(username = "subject", roles = {"TEACHER"})
    public void testUpdateShare() throws Exception {
        Request request = new Request("owner", "title", TEMPLATE_ID, CATEGORY_ID, new ArrayList<>(),
                List.of(new Share("subject", LocalDateTime.now(), ShareStatus.PENDING, false, true)),
                new ArrayList<>(), true);
        requestRepo.insert(request);

        UpdateShareRequest updateShareRequest = new UpdateShareRequest();
        updateShareRequest.setStatus(ShareStatus.ACCEPTED);

        this.mockMvc
                .perform(put("/request/share/" + request.getId().toHexString() + "/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(objectMapper, updateShareRequest)))
                .andExpect(status().is(200))
                .andReturn();
    }

    @Test
    @WithMockUser(username = "subject", roles = {"TEACHER"})
    public void testUpdateShareNotFound() throws Exception {
        Request request = new Request("owner", "title", TEMPLATE_ID, CATEGORY_ID, new ArrayList<>(),
                List.of(new Share("other", LocalDateTime.now(), ShareStatus.PENDING, false, true)),
                new ArrayList<>(), true);
        requestRepo.insert(request);

        UpdateShareRequest updateShareRequest = new UpdateShareRequest();
        updateShareRequest.setStatus(ShareStatus.ACCEPTED);

        this.mockMvc
                .perform(put("/request/share/" + request.getId().toHexString() + "/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(objectMapper, updateShareRequest)))
                .andExpect(status().is(404))
                .andReturn();
    }

    @Test
    @WithMockUser(roles = {"TEACHER"})
    public void testUpdateShareRequestNotFound() throws Exception {
        UpdateShareRequest updateShareRequest = new UpdateShareRequest();
        updateShareRequest.setStatus(ShareStatus.ACCEPTED);

        this.mockMvc
                .perform(put("/request/share/" + new ObjectId().toHexString() + "/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(objectMapper, updateShareRequest)))
                .andExpect(status().is(404))
                .andReturn();
    }
}
