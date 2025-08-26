package hexlet.code.service;

import hexlet.code.dto.task.TaskCreateDTO;
import hexlet.code.dto.task.TaskDTO;
import hexlet.code.dto.task.TaskParamsDTO;
import hexlet.code.dto.task.TaskUpdateDTO;

import java.util.List;

public interface TaskService {
    TaskDTO show(Long id);
    List<TaskDTO> index(TaskParamsDTO params);
    TaskDTO create(TaskCreateDTO dto);
    TaskDTO update(TaskUpdateDTO dto, Long id);
    void delete(Long id);
}
