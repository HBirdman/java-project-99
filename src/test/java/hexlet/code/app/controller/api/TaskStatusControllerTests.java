package hexlet.code.app.controller.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.app.dto.status.TaskStatusDTO;
import hexlet.code.app.mapper.TaskStatusMapper;
import hexlet.code.app.mapper.UserMapper;
import hexlet.code.app.model.TaskStatus;
import hexlet.code.app.repository.TaskStatusRepository;
import hexlet.code.app.service.CustomUserDetailsService;
import hexlet.code.app.util.ModelGenerator;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.assertj.core.api.Assertions.assertThat;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TaskStatusControllerTest {

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TaskStatusRepository statusRepository;

    @Autowired
    private TaskStatusMapper taskStatusMapper;

    private TaskStatus testTaskStatus;

    @Autowired
    private ModelGenerator modelGenerator;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor token;

    @BeforeEach
    public void setUp() {
        statusRepository.deleteAll();

        mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
                .apply(springSecurity())
                .build();
        testTaskStatus = Instancio.of(modelGenerator.getStatusModel()).create();
        statusRepository.save(testTaskStatus);
        token = jwt().jwt(builder -> builder.subject("hexlet@example.com"));
    }

    @Test
    public void testIndex() throws Exception {
        var response = mockMvc.perform(get("/api/task_statuses").with(jwt()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();
        var body = response.getContentAsString();
        List<TaskStatusDTO> statusDTO = om.readValue(body, new TypeReference<>() {
        });
        var actual = statusDTO.stream()
                .map(p -> taskStatusMapper.map(p))
                .toList();
        var expected = statusRepository.findAll();
        assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
    }

    @Test
    public void testShow() throws Exception {
        var response = mockMvc.perform(get("/api/task_statuses/" + testTaskStatus.getId()).with(jwt()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();
        var body = response.getContentAsString();
        assertThatJson(body).and(
                v -> v.node("name").isEqualTo(testTaskStatus.getName()));
    }

    @Test
    public void testCreate() throws Exception {
        var data = Instancio.of(modelGenerator.getStatusModel()).create();

        var request = post("/api/task_statuses")
                .with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(data));

        mockMvc.perform(request)
                .andExpect(status().isCreated());
        var status = statusRepository.findBySlug(data.getSlug()).orElse(null);

        assertThat(status.getName()).isEqualTo(data.getName());
        assertThat(status.getSlug()).isEqualTo(data.getSlug());
    }

    @Test
    public void testUpdate() throws Exception {
        var data = new HashMap<>();
        data.put("name", "newName");
        var request = put("/api/task_statuses/" + testTaskStatus.getId())
                .with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(data));
        mockMvc.perform(request).andExpect(status().isOk());
        var taskStatus = statusRepository.findById(testTaskStatus.getId()).orElseThrow();
        assertThat(taskStatus.getName()).isEqualTo("newName");
    }

    @Test
    public void testDestroy() throws Exception {
        var slug = testTaskStatus.getSlug();
        var request = delete("/api/task_statuses/" + testTaskStatus.getId())
                .with(token);
        mockMvc.perform(request).andExpect(status().isNoContent());
        assertThat(statusRepository.findBySlug(slug).isEmpty());
    }
}
