package com.example.cotobang.service;

import com.example.cotobang.domain.User;
import com.example.cotobang.dto.UserModificationDto;
import com.example.cotobang.dto.UserRegistrationDto;
import com.example.cotobang.errors.UserEmailDuplicationException;
import com.example.cotobang.errors.UserNotFoundException;
import com.example.cotobang.respository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User createUser(UserRegistrationDto userRegistrationDto) {
        String email = userRegistrationDto.getEmail();

        if (userRepository.existsByEmail(email)) {
            throw new UserEmailDuplicationException(email);
        }

        User user = User.builder()
                .email(userRegistrationDto.getEmail())
                .name(userRegistrationDto.getName())
                .password(userRegistrationDto.getPassword())
                .build();

        return userRepository.save(user);
    }

    public User updateUser(Long id, UserModificationDto userModificationDto) {
        User user = getUser(id);

        user.change(
                userModificationDto.getName(),
                userModificationDto.getPassword());

        return user;
    }

    public User delete(Long id) {
        User user = getUser(id);

        if (user.isDeleted()) {
            throw new UserNotFoundException(id);
        }

        user.destory();

        return user;
    }

    private User getUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }
}
