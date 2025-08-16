package hexlet.code.controller.api;

import hexlet.code.dto.user.UserCreateDTO;
import hexlet.code.dto.user.UserDTO;
import hexlet.code.dto.user.UserUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.UserMapper;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import hexlet.code.service.UserCreateService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
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
@EnableMethodSecurity
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserCreateService userService;

    @GetMapping(path = "/{id}", produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public UserDTO show(@PathVariable Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " not found"));
        return userMapper.map(user);
    }

    @GetMapping(path = "", produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    ResponseEntity<List<UserDTO>> index() {
        List<User> users = userRepository.findAll();
        List<UserDTO> result = users.stream()
                .map(u -> userMapper.map(u))
                .toList();
        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(users.size()))
                .body(result);
    }

    @PostMapping(path = "", produces = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDTO create(@Valid @RequestBody UserCreateDTO createDTO) {
        return userService.createUser(createDTO);
    }

    @PutMapping(path = "/{id}", produces = "application/json")
    @PreAuthorize("@userUtils.isOwner(#id)")
    @ResponseStatus(HttpStatus.OK)
    public UserDTO update(@RequestBody @Valid UserUpdateDTO dto, @PathVariable Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " not found"));
        userMapper.update(dto, user);
        userRepository.save(user);
        return userMapper.map(user);
    }

    @DeleteMapping(path = "/{id}", produces = "application/json")
    @PreAuthorize("@userUtils.isOwner(#id)")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        userRepository.deleteById(id);
    }
}
