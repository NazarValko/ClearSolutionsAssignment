package org.nazar.repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.nazar.models.entity.User;
import org.springframework.stereotype.Component;

@Getter
@Component
@NoArgsConstructor
@AllArgsConstructor
public class UserRepository {

    private List<User> users = new ArrayList<>();

    public User addNewUser(User user) {
        users.add(user);
        return user;
    }

    public User updateUser(String email, User updatedUser) {
        User user = getUserByEmail(email);
        if (user != null) {
            user.setFirstName(updatedUser.getFirstName());
            user.setLastName(updatedUser.getLastName());
            user.setBirthDate(updatedUser.getBirthDate());
            user.setAddress(updatedUser.getAddress());
            user.setPhoneNumber(updatedUser.getPhoneNumber());

            return user;
        }
        return null;
    }

    public void deleteUser(String email) {
        users.removeIf(user -> user.getEmail().equals(email));
    }

    public List<User> getUsersByBirthDateRange(LocalDate startDate, LocalDate endDate) {
        return users.stream()
                .filter(user -> !user.getBirthDate().isBefore(startDate) && !user.getBirthDate().isAfter(endDate))
                .collect(Collectors.toList());
    }

    public User getUserByEmail(String email) {
        return users.stream()
                .filter(user -> email.equals(user.getEmail()))
                .findFirst()
                .orElse(null);
    }
}
