package hexlet.code.app.dto.task;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskCreateDTO {
    private int index;

    private int assigneeId;

    @NotNull
    @Size(min = 1)
    private String title;

    private String content;

    @NotBlank
    private String status;
}
