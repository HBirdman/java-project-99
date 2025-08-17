package hexlet.code.dto.task;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;
import java.util.List;

@Getter
@Setter
public class TaskUpdateDTO {
    @NotNull
    private JsonNullable<Long> index;

    @NotNull
    private JsonNullable<Long> assigneeId;

    @NotNull
    @Size(min = 1)
    private JsonNullable<String> title;

    @NotNull
    private JsonNullable<String> content;

    @NotNull
    private JsonNullable<String> status;

    @NotNull
    private JsonNullable<List<Long>> taskLabelIds;
}
