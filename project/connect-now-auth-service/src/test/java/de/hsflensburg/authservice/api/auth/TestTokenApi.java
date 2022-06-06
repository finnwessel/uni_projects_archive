package de.hsflensburg.authservice.api.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.hsflensburg.authservice.api.data.UserTestDataFactory;
import de.hsflensburg.authservice.domain.dto.LoginRequest;
import de.hsflensburg.authservice.domain.dto.RefreshTokenRequest;
import de.hsflensburg.authservice.domain.dto.TokenResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static de.hsflensburg.authservice.util.JsonHelper.fromJson;
import static de.hsflensburg.authservice.util.JsonHelper.toJson;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles({"test-basic", "basic"})
@AutoConfigureMockMvc
@AutoConfigureDataMongo
@SpringBootTest
public class TestTokenApi {
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private final UserTestDataFactory testDataFactory;

    @Autowired
    public TestTokenApi(MockMvc mockMvc, UserTestDataFactory userTestDataFactory) {
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
    public void testRefreshSuccess() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setIdentifier(testDataFactory.basicUsers.get(2).getEmail());
        loginRequest.setPassword(UserTestDataFactory.clearTextPassword);

        MvcResult loginResult = this.mockMvc
                .perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(objectMapper, loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        TokenResponse tokenResponse = fromJson(
                objectMapper,
                loginResult.getResponse().getContentAsString(),
                TokenResponse.class
        );
        RefreshTokenRequest refreshTokenRequest = new RefreshTokenRequest(tokenResponse.getRefreshToken());

        MvcResult refreshResult = this.mockMvc
                .perform(post("/token/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(objectMapper, refreshTokenRequest)))
                .andExpect(status().isOk())
                .andReturn();
    }
}
