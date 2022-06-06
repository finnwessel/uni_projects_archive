package de.hsflensburg.authservice.api.profile;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.hsflensburg.authservice.api.data.UserTestDataFactory;
import de.hsflensburg.authservice.domain.dto.UserResponse;
import de.hsflensburg.authservice.domain.dto.UserUpdateRequest;
import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
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

import static de.hsflensburg.authservice.util.JsonHelper.fromJson;
import static de.hsflensburg.authservice.util.JsonHelper.toJson;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.jupiter.api.Assertions.assertEquals;

@WithMockUser(username = "maria")
@ActiveProfiles({"test-basic", "basic"})
@AutoConfigureMockMvc
@AutoConfigureDataMongo
@SpringBootTest
public class TestProfileApi {
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private final UserTestDataFactory testDataFactory;

    @Autowired
    public TestProfileApi(MockMvc mockMvc, UserTestDataFactory userTestDataFactory) {
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
    public void testProfileSuccess() throws Exception {
        // Other user data
        String requestProfileEmail = testDataFactory.basicUsers.get(1).getEmail();
        String requestProfileUsername = requestProfileEmail.split("@")[0].trim();

        MvcResult profileResult = this.mockMvc
                .perform(get("/profile/" + requestProfileUsername))
                .andExpect(status().isOk())
                .andReturn();

        UserResponse userResponse = fromJson(
                objectMapper,
                profileResult.getResponse().getContentAsString(),
                UserResponse.class
        );

        assertEquals(userResponse.getEmail(), requestProfileEmail);
    }

    @Test
    public void testProfileNotFound() throws Exception {
        MvcResult profileResult = this.mockMvc
                .perform(get("/profile/" + UserTestDataFactory.notExistingUsername))
                .andExpect(status().isNotFound())
                .andReturn();
    }

    @Test
    public void testOwnProfileSuccess() throws Exception {
        String email = testDataFactory.basicUsers.get(2).getEmail();

        MvcResult profileResult = this.mockMvc
                .perform(get("/profile"))
                .andExpect(status().isOk())
                .andReturn();

        UserResponse userResponse = fromJson(
                objectMapper,
                profileResult.getResponse().getContentAsString(),
                UserResponse.class
        );

        assertEquals(userResponse.getEmail(), email);
    }

    @Test
    public void testProfileUpdateSuccess() throws Exception {
        final String currentEmail = "maria@maria.de";

        UserUpdateRequest userUpdateRequest = new UserUpdateRequest(
                "updated_firstname",
                "updated_lastname",
                null
        );

        MvcResult profileResult = this.mockMvc
                .perform(put("/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(objectMapper, userUpdateRequest))
                )
                .andExpect(status().isOk())
                .andReturn();

        UserResponse userResponse = fromJson(
                objectMapper,
                profileResult.getResponse().getContentAsString(),
                UserResponse.class
        );

        assertEquals(userUpdateRequest.firstName(), userResponse.getFirstName());
        assertEquals(userUpdateRequest.lastName(), userResponse.getLastName());
        assertEquals(currentEmail, userResponse.getEmail());
    }
}
