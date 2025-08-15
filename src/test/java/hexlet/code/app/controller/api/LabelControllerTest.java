package hexlet.code.app.controller.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.app.dto.label.LabelCreateDTO;
import hexlet.code.app.dto.label.LabelDTO;
import hexlet.code.app.dto.label.LabelUpdateDTO;
import hexlet.code.app.mapper.LabelMapper;
import hexlet.code.app.model.Label;
import hexlet.code.app.repository.LabelRepository;
import hexlet.code.app.repository.TaskRepository;
import hexlet.code.app.util.ModelGenerator;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class LabelControllerTest {

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private Faker faker;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ModelGenerator modelGenerator;

    @Autowired
    private LabelMapper labelMapper;

    private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor token;

    private Label testLabel;

    @BeforeEach
    public void setUp() {
        taskRepository.deleteAll();
        labelRepository.deleteAll();

        mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
                .apply(springSecurity())
                .build();

        token = jwt().jwt(builder -> builder.subject("hexlet@example.com"));
        testLabel = Instancio.of(modelGenerator.getLabelModel()).create();
        labelRepository.save(testLabel);
    }

    @Test
    public void testIndex() throws Exception {
        var request = get("/api/labels").with(token);
        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();

        List<LabelDTO> labelDTO = om.readValue(body, new TypeReference<>() {
        });
        var actual = labelDTO.stream()
                .map(p -> labelMapper.map(p))
                .toList();
        var expected = labelRepository.findAll();
        assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
    }

    @Test
    public void testShow() throws Exception {
        var request = get("/api/labels/{id}", testLabel.getId()).with(token);

        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();

        assertThatJson(body).and(
                v -> v.node("id").isEqualTo(testLabel.getId()),
                v -> v.node("name").isEqualTo(testLabel.getName())
        );
    }

    @Test
    public void testUpdate() throws Exception {
        var data = new LabelUpdateDTO();
        data.setName(JsonNullable.of(faker.name().name()));

        var request = put("/api/labels/{id}", testLabel.getId()).with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(data));

        mockMvc.perform(request)
                .andExpect(status().isOk());

        var updatedLabel = labelRepository.findById(testLabel.getId()).orElse(null);

        assertThat(updatedLabel).isNotNull();
        assertThat(updatedLabel.getName()).isEqualTo(data.getName().get());
    }

    @Test
    public void testCreate() throws Exception {
        var data = new LabelCreateDTO();
        data.setName(faker.name().name());

        var request = post("/api/labels").with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(data));

        var result = mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andReturn();

        var body = result.getResponse().getContentAsString();

        var id = om.readTree(body).get("id").asLong();
        assertThat(labelRepository.findById(id)).isPresent();

        var addedLabel = labelRepository.findById(id).orElse(null);

        assertThat(addedLabel).isNotNull();
        assertThatJson(body).and(
                v -> v.node("id").isEqualTo(addedLabel.getId()),
                v -> v.node("name").isEqualTo(addedLabel.getName())
        );
    }

    @Test
    public void testDestroy() throws Exception {
        var request = delete("/api/labels/{id}", testLabel.getId()).with(token);

        mockMvc.perform(request)
                .andExpect(status().isNoContent());

        testLabel = labelRepository.findById(testLabel.getId()).orElse(null);

        assertThat(testLabel).isNull();
    }
}
