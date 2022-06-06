package de.hsflensburg.authservice.api.profile;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.hsflensburg.authservice.api.data.UserTestDataFactory;
import de.hsflensburg.authservice.domain.dto.LoginRequest;
import de.hsflensburg.authservice.domain.dto.TokenResponse;
import de.hsflensburg.authservice.domain.dto.UserResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static de.hsflensburg.authservice.util.JsonHelper.fromJson;
import static de.hsflensburg.authservice.util.JsonHelper.toJson;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles({"test-ldap", "ldap"})
@AutoConfigureMockMvc
@AutoConfigureDataMongo
@WithMockUser(username = "great_albus")
@SpringBootTest
public class TestProfileLdapApi {
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private final UserTestDataFactory testDataFactory;

    @Autowired
    public TestProfileLdapApi(MockMvc mockMvc, UserTestDataFactory userTestDataFactory) {
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
        String requestProfileUsername = "ben";

        MvcResult profileResult = this.mockMvc
                .perform(get("/profile/" + requestProfileUsername))
                .andExpect(status().isOk())
                .andReturn();

        UserResponse userResponse = fromJson(
                objectMapper,
                profileResult.getResponse().getContentAsString(),
                UserResponse.class
        );

        assertEquals(userResponse.getUsername(), requestProfileUsername);
    }

    @Test
    public void testOwnProfileSuccess() throws Exception {
        String identifier = "great_albus";

        MvcResult profileResult = this.mockMvc
                .perform(get("/profile"))
                .andExpect(status().isOk())
                .andReturn();

        UserResponse userResponse = fromJson(
                objectMapper,
                profileResult.getResponse().getContentAsString(),
                UserResponse.class
        );

        assertEquals(userResponse.getUsername(), identifier);
    }
}
