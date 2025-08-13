package hexlet.code.app.dto.task;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;

@Getter
@Setter
public class TaskUpdateDTO {
    @NotNull
    private int index;

    @NotNull
    private int assigneeId;

    @NotNull
    @Size(min = 1)
    private JsonNullable<String> title;

    @NotNull
    private String content;

    @NotNull
    private String status;
}
