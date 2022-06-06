package de.hsflensburg.authservice.api.credentials;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.hsflensburg.authservice.api.data.UserTestDataFactory;
import de.hsflensburg.authservice.domain.dto.PasswordResetRequest;
import de.hsflensburg.authservice.domain.dto.PasswordUpdateRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static de.hsflensburg.authservice.util.JsonHelper.toJson;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WithMockUser(username = "maria")
@ActiveProfiles({"test-basic", "basic"})
@AutoConfigureMockMvc
@AutoConfigureDataMongo
@SpringBootTest
@Testcontainers
@ContextConfiguration(initializers = TestCredentialsApi.Initializer.class)
public class TestCredentialsApi {
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private final UserTestDataFactory testDataFactory;

    @Autowired
    public TestCredentialsApi(MockMvc mockMvc, UserTestDataFactory userTestDataFactory) {
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

    @Container
    public static GenericContainer<?> rabbit = new GenericContainer<>(DockerImageName.parse("rabbitmq:3-management"))
            .withAccessToHost(true)
            .withExposedPorts(5672, 15672);
            //.withCommand("rabbitmq-plugins enable rabbitmq_stomp");

    public static class Initializer implements
            ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
             TestPropertyValues values = TestPropertyValues.of(
                    "spring.rabbitmq.host=" + rabbit.getHost(),
                    "spring.rabbitmq.port=" + rabbit.getMappedPort(5672)
            );
            values.applyTo(configurableApplicationContext);
        }
    }

    @Test
    public void testUpdatePasswordSuccess() throws Exception {
        final String oldPassword = UserTestDataFactory.clearTextPassword;
        final String newPassword = "87654321";
        PasswordUpdateRequest passwordUpdateRequest = new PasswordUpdateRequest(
                oldPassword,
                newPassword
        );

        this.mockMvc
                .perform(
                      post("/auth/update-password")
                              .contentType(MediaType.APPLICATION_JSON)
                              .content(toJson(objectMapper, passwordUpdateRequest))
                ).andExpect(status().isOk())
                .andReturn();
    }

    @Test
    public void testResetPasswordSuccess() throws Exception {
        PasswordResetRequest passwordResetRequest =
                new PasswordResetRequest("maria@maria.de");

        this.mockMvc
                .perform(
                        post("/auth/reset-password")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(toJson(objectMapper, passwordResetRequest))
                ).andExpect(status().isOk())
                .andReturn();
    }
}
