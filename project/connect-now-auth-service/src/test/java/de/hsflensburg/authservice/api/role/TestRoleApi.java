package de.hsflensburg.authservice.api.role;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.hsflensburg.authservice.api.data.UserTestDataFactory;
import de.hsflensburg.authservice.domain.dto.UserRole;
import de.hsflensburg.authservice.domain.model.BasicUser;
import de.hsflensburg.authservice.domain.model.RoleEnum;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
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

import java.util.Set;

import static de.hsflensburg.authservice.util.JsonHelper.fromJson;
import static de.hsflensburg.authservice.util.JsonHelper.toJson;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WithMockUser(username = "thomas", roles = {"ADMIN"})
@ActiveProfiles({"test-basic", "basic"})
@AutoConfigureMockMvc
@AutoConfigureDataMongo
@SpringBootTest
public class TestRoleApi {
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private final UserTestDataFactory testDataFactory;

    @Autowired
    public TestRoleApi(MockMvc mockMvc, UserTestDataFactory userTestDataFactory) {
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
    public void testGetUserRolesSuccess() throws Exception {
        BasicUser user = testDataFactory.basicUsers.get(0);
        MvcResult roleResult = this.mockMvc
                .perform(get("/role/" + user.getUsername()))
                .andExpect(status().isOk())
                .andReturn();

        Set roleResponse =  fromJson(
                objectMapper,
                roleResult.getResponse().getContentAsString(),
                Set.class
        );

        Assertions.assertTrue(
                roleResponse.contains("ROLE_STUDENT")
        );
    }

    @Test
    public void testGetUserRolesNotFound() throws Exception {
        String username = "not_existing_username";
        MvcResult roleResult = this.mockMvc
                .perform(get("/role/" + username))
                .andExpect(status().isNotFound())
                .andReturn();
    }

    @Test
    public void testAddRoleToUserSuccess() throws Exception {
        String username = "maria";
        UserRole userRole = new UserRole();
        userRole.setRole(RoleEnum.ROLE_ADMIN);

        MvcResult roleResult = this.mockMvc
                .perform(post("/role/" + username)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(objectMapper, userRole)))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    public void testAddRoleToUserConflict() throws Exception {
        String username = "maria";
        UserRole userRole = new UserRole();
        userRole.setRole(RoleEnum.ROLE_TEACHER);

        MvcResult roleResult = this.mockMvc
                .perform(post("/role/" + username)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(objectMapper, userRole)))
                .andExpect(status().isConflict())
                .andReturn();
    }

    @Test
    public void testAddRoleToUserBadRequest() throws Exception {
        String username = "maria";
        UserRole userRole = new UserRole();
        // We do not set the role, because we simulate a non-existing user role

        MvcResult roleResult = this.mockMvc
                .perform(post("/role/" + username)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(objectMapper, userRole)))
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    public void testRemoveRoleFromUserSuccess() throws Exception {
        String username = "maria";
        UserRole userRole = new UserRole();
        userRole.setRole(RoleEnum.ROLE_TEACHER);

        MvcResult roleResult = this.mockMvc
                .perform(delete("/role/" + username)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(objectMapper, userRole)))
                .andExpect(status().isNoContent())
                .andReturn();
    }

    @Test
    public void testRemoveRoleFromUserBadRequest() throws Exception {
        String username = "maria";
        UserRole userRole = new UserRole();
        // We do not set the role, because we simulate a non-existing user role

        MvcResult roleResult = this.mockMvc
                .perform(delete("/role/" + username)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(objectMapper, userRole)))
                .andExpect(status().isBadRequest())
                .andReturn();
    }
}
