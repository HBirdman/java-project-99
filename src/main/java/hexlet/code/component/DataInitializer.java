package hexlet.code.component;

import hexlet.code.model.Label;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.service.CustomUserDetailsService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@AllArgsConstructor
public class DataInitializer implements ApplicationRunner {

    @Autowired
    private CustomUserDetailsService userService;

    @Autowired
    private TaskStatusRepository statusRepository;

    @Autowired
    private LabelRepository labelRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        String email = "hexlet@example.com";
        User userData = new User();
        userData.setEmail(email);
        userData.setPasswordDigest("qwerty");
        userService.createUser(userData);

        HashMap<String, String> nameAndSlug = new HashMap<>(Map.of(
                "Draft", "draft", "ToReview", "to_review", "ToBeFixed", "to_be_fixed",
                "ToPublish", "to_publish", "Published", "published"
        ));
        nameAndSlug.forEach((key, value) -> {
            TaskStatus status = new TaskStatus();
            status.setName(key);
            status.setSlug(value);
            statusRepository.save(status);
        });

        Label feature = new Label();
        feature.setName("feature");
        labelRepository.save(feature);
        Label bug = new Label();
        bug.setName("bug");
        labelRepository.save(bug);
    }
}
