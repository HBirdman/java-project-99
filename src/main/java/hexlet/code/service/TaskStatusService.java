package hexlet.code.service;

import hexlet.code.dto.status.TaskStatusCreateDTO;
import hexlet.code.dto.status.TaskStatusDTO;
import hexlet.code.dto.status.TaskStatusUpdateDTO;

import java.util.List;

public interface TaskStatusService {
    TaskStatusDTO show(Long id);
    List<TaskStatusDTO> index();
    TaskStatusDTO create(TaskStatusCreateDTO dto);
    TaskStatusDTO update(TaskStatusUpdateDTO dto, Long id);
    void delete(Long id);
}
