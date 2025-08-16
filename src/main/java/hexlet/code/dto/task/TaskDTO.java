package hexlet.code.dto.task;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class TaskDTO {
    private Long id;

    private int index;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate createAt;

    private int assigneeId;

    private String title;

    private String content;

    private String status;

    private List<Long> taskLabelIds;
}
