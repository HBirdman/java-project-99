package hexlet.code.service;

import hexlet.code.dto.user.UserCreateDTO;
import hexlet.code.dto.user.UserDTO;
import hexlet.code.dto.user.UserUpdateDTO;

import java.util.List;

public interface UserService {
    UserDTO show(Long id);
    List<UserDTO> index();
    UserDTO create(UserCreateDTO createDTO);
    UserDTO update(UserUpdateDTO dto, Long id);
    void delete(Long id);
}
