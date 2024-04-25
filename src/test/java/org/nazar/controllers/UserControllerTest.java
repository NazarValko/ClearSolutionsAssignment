package org.nazar.controllers;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.nazar.models.dto.UserDto;
import org.nazar.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    public void testRegisterUser_Success() throws Exception {
        UserDto responseDto = new UserDto("test@example.com", "John", "Doe", LocalDate.of(1990, 1, 1), null, null);
        when(userService.registerUser(any(UserDto.class))).thenReturn(responseDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"email\": \"test@example.com\", \"firstName\": \"John\", \"lastName\": \"Doe\", \"birthDate\": \"1990-01-01\" }")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.birthDate").value("1990-01-01"));
    }

    @Test
    public void testUpdateUser_Success() throws Exception {
        String email = "test@example.com";
        UserDto responseDto = new UserDto(email, "John", "Doe", LocalDate.of(1990, 1, 1), null, null);

        when(userService.updateUser(eq(email), any(UserDto.class))).thenReturn(responseDto);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/users/{email}", email)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"email\": \"test@example.com\", \"firstName\": \"John\", \"lastName\": \"Doe\", \"birthDate\": \"1990-01-01\" }")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.birthDate").value("1990-01-01"));
    }

    @Test
    public void testDeleteUser_Success() throws Exception {
        String email = "test@example.com";

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/users/{email}", email)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(userService, times(1)).deleteUser(email);
    }

    @Test
    public void testGetUsersByBirthDateRange_Success() throws Exception {
        LocalDate startDate = LocalDate.of(2023, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 1);
        List<UserDto> users = new ArrayList<>();
        users.add(new UserDto("user1@example.com", "John", "Doe", LocalDate.of(2023, 5, 5), null, null));
        users.add(new UserDto("user2@example.com", "Jane", "Doe", LocalDate.of(2023, 6, 6), null, null));
        when(userService.getUsersByBirthDateRange(startDate, endDate)).thenReturn(users);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/users")
                        .param("start", startDate.toString())
                        .param("end", endDate.toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value("user1@example.com"))
                .andExpect(jsonPath("$[1].email").value("user2@example.com"));
    }
}
