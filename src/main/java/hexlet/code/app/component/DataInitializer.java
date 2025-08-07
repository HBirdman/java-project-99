package hexlet.code.app.component;

import hexlet.code.app.model.TaskStatus;
import hexlet.code.app.model.User;
import hexlet.code.app.repository.TaskStatusRepository;
import hexlet.code.app.service.CustomUserDetailsService;
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
    }
}
