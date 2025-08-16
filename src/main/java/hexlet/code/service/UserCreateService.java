package hexlet.code.service;

import hexlet.code.dto.user.UserCreateDTO;
import hexlet.code.dto.user.UserDTO;
import hexlet.code.mapper.UserMapper;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserCreateService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserRepository userRepository;

    public UserDTO createUser(UserCreateDTO createDTO) {
        User user = userMapper.map(createDTO);
        userRepository.save(user);
        return userMapper.map(user);
    }
}
