package hexlet.code.service;

import hexlet.code.dto.label.LabelCreateDTO;
import hexlet.code.dto.label.LabelDTO;
import hexlet.code.dto.label.LabelUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.LabelMapper;
import hexlet.code.model.Label;
import hexlet.code.repository.LabelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LabelServiceImpl implements LabelService {

    private final LabelRepository labelRepository;
    private final LabelMapper labelMapper;

    public LabelDTO show(Long id) {
        Label label = labelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Label with id: " + id + " not found."));
        return labelMapper.map(label);
    }

    public List<LabelDTO> index() {
        return labelRepository.findAll().stream()
                .map(labelMapper::map)
                .toList();
    }

    public LabelDTO create(LabelCreateDTO dto) {
        Label label = labelMapper.map(dto);
        labelRepository.save(label);
        return labelMapper.map(label);
    }

    public LabelDTO update(LabelUpdateDTO dto, Long id) {
        Label label = labelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Label with id: " + id + " not found."));
        labelMapper.update(dto, label);
        labelRepository.save(label);
        return labelMapper.map(label);
    }

    public void delete(Long id) {
        labelRepository.deleteById(id);
    }
}
