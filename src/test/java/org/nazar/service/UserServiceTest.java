package org.nazar.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.nazar.exceptions.ResourceNotFoundException;
import org.nazar.mappers.UserMapper;
import org.nazar.models.dto.UserDto;
import org.nazar.models.entity.User;
import org.nazar.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    public UserServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testRegisterUser_NewUser_Success() {
        UserDto requestDto = UserDto.builder()
                .email("test2@example.com")
                .firstName("John")
                .lastName("Doe")
                .birthDate(LocalDate.of(1990, 1, 1))
                .build();
        User userEntity = new User(
                "test2@example.com",
                null,
                null,
                LocalDate.of(1990, 1, 1),
                null,
                null
        );
        when(userRepository.getUserByEmail(requestDto.getEmail())).thenReturn(null);
        when(userMapper.toEntity(requestDto)).thenReturn(userEntity);
        when(userRepository.addNewUser(userEntity)).thenReturn(userEntity);
        when(userMapper.toDto(userEntity)).thenReturn(requestDto);

        UserDto result = userService.registerUser(requestDto);

        assertNotNull(result);
        assertEquals(requestDto.getEmail(), result.getEmail());
        assertEquals(requestDto.getBirthDate(), result.getBirthDate());
        verify(userRepository, times(1)).getUserByEmail(requestDto.getEmail());
        verify(userRepository, times(1)).addNewUser(userEntity);
    }

    @Test
    public void testRegisterUser_ExistingUser_ThrowIllegalArgumentException() {
        UserDto requestDto = UserDto.builder()
                .email("test2@example.com")
                .firstName("John")
                .lastName("Doe")
                .birthDate(LocalDate.of(1990, 1, 1))
                .build();
        when(userRepository.getUserByEmail(any())).thenReturn(mock(User.class));
        assertThrows(IllegalArgumentException.class, () -> userService.registerUser(requestDto));
    }

    @Test
    public void testRegisterUser_UnderAge_ThrowIllegalArgumentException() {
        UserDto requestDto = UserDto.builder()
                .email("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .birthDate(LocalDate.now())
                .build();

        when(userRepository.getUserByEmail("test@example.com")).thenReturn(mock(User.class));
        assertThrows(IllegalArgumentException.class, () -> userService.registerUser(requestDto));
    }

    @Test
    public void testUpdateUser_UserNotFound_RegisterNewUser() {
        UserDto updatedUserDto = UserDto.builder()
                .email("test1@example.com")
                .firstName("John")
                .lastName("Doe")
                .birthDate(LocalDate.of(1990, 1, 1))
                .build();
        when(userRepository.updateUser(eq("test1@example.com"), any(User.class))).thenReturn(null);

        userService.updateUser("test@example.com", updatedUserDto);
    }

    @Test
    public void testDeleteUser_UserExists_DeleteSuccessful() {
        String email = "test@example.com";
        when(userRepository.getUserByEmail(email)).thenReturn(mock(User.class));

        userService.deleteUser(email);

        verify(userRepository, times(1)).deleteUser(email);
    }

    @Test
    public void testDeleteUser_UserNotFound_ExceptionThrown() {
        String email = "nonexistent@example.com";
        when(userRepository.getUserByEmail(email)).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> userService.deleteUser(email));
    }

    @Test
    public void testGetUsersByBirthDateRange_StartDateAfterEndDate_ExceptionThrown() {
        LocalDate fromDate = LocalDate.of(2024, 1, 1);
        LocalDate toDate = LocalDate.of(2023, 1, 1);

        assertThrows(IllegalArgumentException.class, () -> userService.getUsersByBirthDateRange(fromDate, toDate));
    }

    @Test
    public void testGetUsersByBirthDateRange_ValidRange_ReturnUsers() {
        LocalDate fromDate = LocalDate.of(2023, 1, 1);
        LocalDate toDate = LocalDate.of(2024, 1, 1);
        List<User> userList = new ArrayList<>();
        userList.add(new User("user1@example.com", null, null, LocalDate.of(2023, 5, 5), null, null));
        userList.add(new User("user2@example.com", null, null, LocalDate.of(2023, 6, 6), null, null));
        when(userRepository.getUsersByBirthDateRange(fromDate, toDate)).thenReturn(userList);
        when(userMapper.toDto(any())).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            return UserDto.builder()
                    .email(user.getEmail())
                    .birthDate(user.getBirthDate())
                    .build();
        });

        List<UserDto> result = userService.getUsersByBirthDateRange(fromDate, toDate);

        assertEquals(2, result.size());
        verify(userRepository, times(1)).getUsersByBirthDateRange(fromDate, toDate);
    }
}
