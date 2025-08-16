package hexlet.code.controller.api;

import hexlet.code.dto.status.TaskStatusCreateDTO;
import hexlet.code.dto.status.TaskStatusDTO;
import hexlet.code.dto.status.TaskStatusUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.TaskStatusMapper;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.TaskStatusRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@RestController
@RequestMapping("/api/task_statuses")
public class TaskStatusController {

    @Autowired
    private TaskStatusRepository statusRepository;

    @Autowired
    private TaskStatusMapper statusMapper;

    @GetMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TaskStatusDTO show(@PathVariable Long id) {
        TaskStatus taskStatus = statusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Status with id " + id + " not found"));
        return statusMapper.map(taskStatus);
    }

    @GetMapping(path = "")
    @ResponseStatus(HttpStatus.OK)
    ResponseEntity<List<TaskStatusDTO>> index() {
        List<TaskStatus> statuses = statusRepository.findAll();
        List<TaskStatusDTO> result = statuses.stream()
                .map(s -> statusMapper.map(s))
                .toList();
        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(statuses.size()))
                .body(result);
    }

    @PostMapping(path = "")
    @ResponseStatus(HttpStatus.CREATED)
    public TaskStatusDTO create(@Valid @RequestBody TaskStatusCreateDTO createDTO) {
        TaskStatus taskStatus = statusMapper.map(createDTO);
        statusRepository.save(taskStatus);
        return statusMapper.map(taskStatus);
    }

    @PutMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TaskStatusDTO update(@Valid @RequestBody TaskStatusUpdateDTO dto, @PathVariable Long id) {
        TaskStatus status = statusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Status with id " + id + " not found"));
        statusMapper.update(dto, status);
        statusRepository.save(status);
        return statusMapper.map(status);
    }

    @DeleteMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        statusRepository.deleteById(id);
    }
}
