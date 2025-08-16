package hexlet.code.dto.status;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class TaskStatusDTO {
    private Long id;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate createdAt;

    private String name;

    private String slug;

}
