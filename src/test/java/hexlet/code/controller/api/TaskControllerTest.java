package hexlet.code.controller.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.task.TaskCreateDTO;
import hexlet.code.dto.task.TaskDTO;
import hexlet.code.dto.task.TaskUpdateDTO;
import hexlet.code.mapper.TaskMapper;
import hexlet.code.mapper.UserMapper;
import hexlet.code.model.Label;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import hexlet.code.service.CustomUserDetailsService;
import hexlet.code.util.ModelGenerator;
import net.datafaker.Faker;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class TaskControllerTest {

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private TaskMapper taskMapper;

    private Task testTask;

    private User testUser;

    private TaskStatus testTaskStatus;

    private Label testLabel;

    @Autowired
    private ModelGenerator modelGenerator;

    @Autowired
    private Faker faker;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor token;

    @BeforeEach
    public void setUp() {
        taskRepository.deleteAll();
        taskStatusRepository.deleteAll();
        userRepository.deleteAll();

        mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
                .apply(springSecurity())
                .build();

        testUser = Instancio.of(modelGenerator.getUserModel()).create();
        userRepository.save(testUser);

        testTaskStatus = Instancio.of(modelGenerator.getStatusModel()).create();
        taskStatusRepository.save(testTaskStatus);

        testLabel = Instancio.of(modelGenerator.getLabelModel()).create();
        labelRepository.save(testLabel);

        testTask = Instancio.of(modelGenerator.getTaskModel()).create();
        testTask.setAssignee(testUser);
        testTask.setTaskStatus(testTaskStatus);
        testTask.getLabels().add(testLabel);
        taskRepository.save(testTask);
        token = jwt().jwt(builder -> builder.subject("hexlet@example.com"));
    }

    @Test
    public void testIndex() throws Exception {
        var response = mockMvc.perform(get("/api/tasks").with(jwt()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        var body = response.getContentAsString();

        List<TaskDTO> taskDTO = om.readValue(body, new TypeReference<>() {
        });
        var actual = taskDTO.stream()
                .map(p -> taskMapper.map(p))
                .toList();
        var expected = taskRepository.findAll();
        assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
    }

    @Test
    public void testIndexWithFilters() throws Exception {
        var response = mockMvc.perform(get("/api/tasks"
                        + "?titleCont=" + testTask.getName().substring(0, 2).toLowerCase()
                        + "&labelId=" + testTask.getLabels().stream().findFirst().get().getId()
                        + "&status=" + testTask.getTaskStatus().getSlug()
                        + "&assigneeId=" + testTask.getAssignee().getId())
                        .with(jwt()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        var body = response.getContentAsString();

        List<TaskDTO> taskDTO = om.readValue(body, new TypeReference<>() { });
        var actual = taskDTO.stream()
                .map(p -> taskMapper.map(p))
                .toList();
        var expected = taskRepository.findAll();
        assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
    }

    @Test
    public void testShow() throws Exception {
        var request = get("/api/tasks/{id}", testTask.getId()).with(token);

        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();

        assertThatJson(body).and(
                v -> v.node("id").isEqualTo(testTask.getId()),
                v -> v.node("index").isEqualTo(testTask.getIndex()),
                v -> v.node("title").isEqualTo(testTask.getName()),
                v -> v.node("content").isEqualTo(testTask.getDescription()),
                v -> v.node("status").isEqualTo(testTask.getTaskStatus().getSlug().toLowerCase()),
                v -> v.node("assigneeId").isEqualTo(testTask.getAssignee().getId())
        );
    }

    @Test
    public void testCreate() throws Exception {
        taskRepository.deleteById(testTask.getId());

        var data = new TaskCreateDTO();
        data.setIndex(JsonNullable.of(testTask.getIndex()));
        data.setAssigneeId(testTask.getAssignee().getId());
        data.setTitle(testTask.getName());
        data.setContent(testTask.getDescription());
        data.setStatus(testTask.getTaskStatus().getSlug());

        var request = post("/api/tasks").with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(data));

        var result = mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andReturn();

        var body = result.getResponse().getContentAsString();

        var id = om.readTree(body).get("id").asLong();
        assertThat(taskRepository.findById(id)).isPresent();

        var addedTask = taskRepository.findById(id).orElse(null);
        assertThat(addedTask).isNotNull();
        assertThatJson(body).and(
                v -> v.node("id").isEqualTo(addedTask.getId()),
                v -> v.node("index").isEqualTo(addedTask.getIndex()),
                v -> v.node("assigneeId").isEqualTo(addedTask.getAssignee().getId()),
                v -> v.node("content").isEqualTo(addedTask.getDescription()),
                v -> v.node("title").isEqualTo(addedTask.getName()),
                v -> v.node("status").isEqualTo(addedTask.getTaskStatus().getSlug())
        );
    }

    @Test
    public void testUpdate() throws Exception {
        var updateDTO = new TaskUpdateDTO();
        updateDTO.setTitle(JsonNullable.of(faker.lorem().word()));
        updateDTO.setContent(JsonNullable.of(faker.lorem().sentence()));

        var request = put("/api/tasks/{id}", testTask.getId()).with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(updateDTO));

        mockMvc.perform(request)
                .andExpect(status().isOk());

        var updatedTask = taskRepository.findById(testTask.getId()).orElse(null);

        assertThat(updatedTask).isNotNull();
        assertThat(updatedTask.getName()).isEqualTo(updateDTO.getTitle().get());
        assertThat(updatedTask.getDescription()).isEqualTo(updateDTO.getContent().get());
    }

    @Test
    public void testDestroy() throws Exception {
        var request = delete("/api/tasks/{id}", testTask.getId()).with(token);

        mockMvc.perform(request)
                .andExpect(status().isNoContent());

        testTask = taskRepository.findById(testTask.getId()).orElse(null);

        assertThat(testTask).isNull();
    }
}
