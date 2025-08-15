package hexlet.code.app.mapper;

import hexlet.code.app.dto.task.TaskCreateDTO;
import hexlet.code.app.dto.task.TaskDTO;
import hexlet.code.app.dto.task.TaskUpdateDTO;
import hexlet.code.app.exception.ResourceNotFoundException;
import hexlet.code.app.model.BaseEntity;
import hexlet.code.app.model.Label;
import hexlet.code.app.model.Task;
import hexlet.code.app.model.TaskStatus;
import hexlet.code.app.repository.LabelRepository;
import hexlet.code.app.repository.TaskStatusRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.ArrayList;
import java.util.List;

@Mapper(
        uses = { JsonNullableMapper.class, ReferenceMapper.class },
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)

public abstract class TaskMapper implements BaseEntity {

    @Autowired
    private TaskStatusRepository statusRepository;

    @Autowired
    private LabelRepository labelRepository;

    @Mapping(source = "title", target = "name")
    @Mapping(source = "content", target = "description")
    @Mapping(source = "status", target = "taskStatus", qualifiedByName = "slugToModel")
    @Mapping(source = "assigneeId", target = "assignee")
    @Mapping(source = "taskLabelIds", target = "labels", qualifiedByName = "idToLabel")
    public abstract Task map(TaskCreateDTO dto);

    @Mapping(source = "title", target = "name")
    @Mapping(source = "content", target = "description")
    @Mapping(source = "status", target = "taskStatus", qualifiedByName = "slugToModel")
    @Mapping(source = "assigneeId", target = "assignee")
    @Mapping(source = "taskLabelIds", target = "labels", qualifiedByName = "idToLabel")
    public abstract void update(TaskUpdateDTO dto, @MappingTarget Task model);

    @Mapping(source = "name", target = "title")
    @Mapping(source = "description", target = "content")
    @Mapping(source = "taskStatus.slug", target = "status")
    @Mapping(source = "assignee.id", target = "assigneeId")
    @Mapping(source = "labels", target = "taskLabelIds", qualifiedByName = "labelToId")
    public abstract TaskDTO map(Task model);

    @Mapping(source = "title", target = "name")
    @Mapping(source = "content", target = "description")
    @Mapping(source = "status", target = "taskStatus", qualifiedByName = "slugToModel")
    @Mapping(source = "assigneeId", target = "assignee")
    @Mapping(source = "taskLabelIds", target = "labels", qualifiedByName = "idToLabel")
    public abstract Task map(TaskDTO dto);

    @Named("slugToModel")
    public TaskStatus slugToModel(String slug) {
        return statusRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Status with such id is not found"));
    }

    @Named("idToLabel")
    public List<Label> idToLabel(List<Long> ids) {
        if (ids != null) {
            List<Label> labels = new ArrayList<>();
            for (var id : ids) {
                var label = labelRepository.findById(id).orElseThrow();
                labels.add(label);
            }
            return labels;
        }
        return null;
    }

    @Named("labelToId")
    public List<Long> labelToIds(List<Label> labels) {
        if (labels != null) {
            List<Long> ids = new ArrayList<>();
            for (var label : labels) {
                ids.add(label.getId());
            }
            return ids;
        }
        return null;
    }
}
