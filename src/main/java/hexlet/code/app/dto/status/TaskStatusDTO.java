package hexlet.code.app.dto.status;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class TaskStatusDTO {
    private Long id;

    private LocalDate createdAt;

    private String name;

    private String slug;

}
