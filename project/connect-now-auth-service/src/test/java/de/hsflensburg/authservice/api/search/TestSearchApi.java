package de.hsflensburg.authservice.api.search;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.hsflensburg.authservice.api.data.UserTestDataFactory;
import de.hsflensburg.authservice.domain.model.RoleEnum;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static de.hsflensburg.authservice.util.JsonHelper.fromJson;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WithMockUser(username = "thomas", roles = {"ADMIN"})
@ActiveProfiles({"test-basic", "basic"})
@AutoConfigureMockMvc
@AutoConfigureDataMongo
@SpringBootTest
public class TestSearchApi {
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private final UserTestDataFactory testDataFactory;

    @Autowired
    public TestSearchApi(MockMvc mockMvc, UserTestDataFactory userTestDataFactory) {
        this.mockMvc = mockMvc;
        this.objectMapper = new ObjectMapper();
        this.testDataFactory = userTestDataFactory;
    }

    @BeforeEach()
    private void init() {
        testDataFactory.up();
    }

    @AfterEach()
    private void deconstruct() {
        testDataFactory.down();
    }

    @Test
    public void testUserSearchByRoleAndName() throws Exception {
        MvcResult searchResult = this.mockMvc
                .perform(
                        get("/search")
                                .param("role", RoleEnum.ROLE_ADMIN.toString())
                                .param("name", "herbert"))
                .andExpect(status().isOk())
                .andReturn();

        List searchList = fromJson(
                objectMapper,
                searchResult.getResponse().getContentAsString(),
                List.class
        );

        Assert.assertEquals(1, searchList.size());
    }
}
