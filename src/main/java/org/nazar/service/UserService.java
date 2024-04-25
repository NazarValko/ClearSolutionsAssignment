package org.nazar.service;

import java.time.LocalDate;
import java.util.List;
import org.nazar.exceptions.ResourceNotFoundException;
import org.nazar.mappers.UserMapper;
import org.nazar.models.dto.UserDto;
import org.nazar.models.entity.User;
import org.nazar.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Value("${user.age}")
    private int lowestAge;

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public UserDto registerUser(UserDto requestDto) {
        User existingUser = userRepository.getUserByEmail(requestDto.getEmail());

        if (requestDto.getBirthDate().getYear() + lowestAge > LocalDate.now().getYear()) {
            throw new IllegalArgumentException("User must be 18 years old or older");
        }

        if (existingUser == null) {
            return userMapper.toDto(userRepository.addNewUser(userMapper.toEntity(requestDto)));
        } else {
            throw new IllegalArgumentException("User already exists");
        }
    }

    public UserDto updateUser(String email, UserDto updatedUser) {
        User temp = userRepository.updateUser(email, userMapper.toEntity(updatedUser));
        if (temp == null) {
            return registerUser(updatedUser);
        }
        return userMapper.toDto(temp);
    }

    public void deleteUser(String email) {
        User user = userRepository.getUserByEmail(email);
        if (user == null) {
            throw new ResourceNotFoundException("User not found with email: " + email);
        }
        userRepository.deleteUser(email);
    }

    public List<UserDto> getUsersByBirthDateRange(LocalDate from, LocalDate to) {
        if (from.isAfter(to)) {
            throw new IllegalArgumentException("Start date should be before end date ");
        }
        return userRepository.getUsersByBirthDateRange(from, to)
                .stream().map(userMapper::toDto).toList();
    }
}
