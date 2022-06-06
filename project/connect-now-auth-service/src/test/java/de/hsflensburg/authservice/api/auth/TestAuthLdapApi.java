package de.hsflensburg.authservice.api.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.hsflensburg.authservice.api.data.UserTestDataFactory;
import de.hsflensburg.authservice.domain.dto.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static de.hsflensburg.authservice.util.JsonHelper.fromJson;
import static de.hsflensburg.authservice.util.JsonHelper.toJson;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles({"test-ldap", "ldap"})
@AutoConfigureMockMvc
@AutoConfigureDataMongo
@SpringBootTest
public class TestAuthLdapApi {
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    @Autowired
    public TestAuthLdapApi(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
        this.objectMapper = new ObjectMapper();
    }

    @BeforeEach()
    private void init() {
    }

    @AfterEach()
    private void deconstruct() {
    }

    @Test
    public void testLoginSuccess() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setIdentifier("great_albus");
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
    public void testRegisterNotAllowed() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setFirstName("Peter");
        registerRequest.setLastName("Lustig");
        registerRequest.setEmail("peter.lustig@mail.com");
        registerRequest.setPassword("petersPassword123");

        MvcResult registerResult = this.mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(objectMapper, registerRequest)))
                .andExpect(status().isForbidden())
                .andReturn();
    }

    @Test
    public void testLogoutSuccess() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setIdentifier("admin");
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
    public void testRegisterDisabled() throws Exception {
        MvcResult result = this.mockMvc
                .perform(get("/auth/register-enabled"))
                .andExpect(status().isOk())
                .andReturn();

        Boolean enabled = fromJson(
                objectMapper,
                result.getResponse().getContentAsString(),
                Boolean.class
        );

        assertEquals(enabled, false);
    }

    @Test
    public void testProfileUpdateNotAllowed() throws Exception {
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
                .andExpect(status().isForbidden())
                .andReturn();
    }
}
