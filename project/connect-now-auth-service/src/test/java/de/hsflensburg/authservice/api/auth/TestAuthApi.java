package de.hsflensburg.authservice.api.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.hsflensburg.authservice.api.data.UserTestDataFactory;
import de.hsflensburg.authservice.domain.dto.LoginRequest;
import de.hsflensburg.authservice.domain.dto.RefreshTokenRequest;
import de.hsflensburg.authservice.domain.dto.RegisterRequest;
import de.hsflensburg.authservice.domain.dto.TokenResponse;
import org.junit.jupiter.api.*;
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles({"test-basic", "basic"})
@AutoConfigureMockMvc
@AutoConfigureDataMongo
@SpringBootTest
public class TestAuthApi {
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private final UserTestDataFactory testDataFactory;

    @Autowired
    public TestAuthApi(MockMvc mockMvc, UserTestDataFactory userTestDataFactory) {
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
    public void testLoginSuccess() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setIdentifier(testDataFactory.basicUsers.get(2).getEmail());
        loginRequest.setPassword(UserTestDataFactory.clearTextPassword);

        MvcResult loginResult = this.mockMvc
                .perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(objectMapper, loginRequest)))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    public void testLoginUnauthorized() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setIdentifier("user@nonexisting.com");
        loginRequest.setPassword("secureAndLongPassword");

        MvcResult loginResult = this.mockMvc
                .perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(objectMapper, loginRequest)))
                .andExpect(status().isUnauthorized())
                .andReturn();
    }

    @Test
    public void testRegisterSuccess() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setFirstName("Peter");
        registerRequest.setLastName("Lustig");
        registerRequest.setEmail("peter.lustig@mail.com");
        registerRequest.setPassword("petersPassword123");

        MvcResult registerResult = this.mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(objectMapper, registerRequest)))
                .andExpect(status().isCreated())
                .andReturn();
    }

    @Test
    public void testRegisterConflict() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setFirstName(testDataFactory.basicUsers.get(0).getFirstName());
        registerRequest.setLastName(testDataFactory.basicUsers.get(0).getLastName());
        registerRequest.setEmail(testDataFactory.basicUsers.get(0).getEmail());
        registerRequest.setPassword(testDataFactory.basicUsers.get(0).getEmail());

        MvcResult registerResult = this.mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(objectMapper, registerRequest)))
                .andExpect(status().isConflict())
                .andReturn();
    }

    @Test
    public void testLogoutSuccess() throws Exception {
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

        MvcResult logoutResult = this.mockMvc
                .perform(post("/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(objectMapper, refreshTokenRequest)))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    public void testRegisterEnabled() throws Exception {
        MvcResult result = this.mockMvc
                .perform(get("/auth/register-enabled"))
                .andExpect(status().isOk())
                .andReturn();

        Boolean enabled = fromJson(
                objectMapper,
                result.getResponse().getContentAsString(),
                Boolean.class
        );

        assertEquals(enabled, true);
    }
}
