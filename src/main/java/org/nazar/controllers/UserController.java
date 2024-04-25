package org.nazar.controllers;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import org.nazar.models.dto.UserDto;
import org.nazar.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserDto> registerUser(@RequestBody @Valid UserDto userDto) {
        UserDto createdUser = userService.registerUser(userDto);
        return ResponseEntity.ok(createdUser);
    }

    @PutMapping("/{email}")
    public ResponseEntity<UserDto> updateUser(@PathVariable String email, @RequestBody @Valid UserDto userDto) {
        UserDto updatedUser = userService.updateUser(email, userDto);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{email}")
    public ResponseEntity<Void> deleteUser(@PathVariable @Valid String email) {
        userService.deleteUser(email);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> getUsersByBirthDateRange(@RequestParam("start") LocalDate startDate, @RequestParam("end") LocalDate endDate) {
        List<UserDto> users = userService.getUsersByBirthDateRange(startDate, endDate);
        return ResponseEntity.ok(users);
    }
}
