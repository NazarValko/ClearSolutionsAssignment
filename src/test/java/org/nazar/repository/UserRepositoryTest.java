package org.nazar.repository;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.nazar.models.entity.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserRepositoryTest {

    @Test
    void testAddNewUser() {
        UserRepository userRepository = new UserRepository();
        User user = new User(
                "test@test.com",
                "John",
                "Doe",
                LocalDate.of(2000, 1, 1),
                "123 Street",
                "1234567890");

        userRepository.addNewUser(user);

        assertEquals(1, userRepository.getUsers().size());
    }

    @Test
    void testUpdateUser() {
        UserRepository userRepository = new UserRepository();
        User user = new User("test@test.com",
                "John",
                "Doe",
                LocalDate.of(2000, 1, 1),
                "123 Street",
                "1234567890");
        userRepository.addNewUser(user);

        User updatedUser = new User("test@test.com",
                "Updated",
                "User",
                LocalDate.of(2000, 1, 1),
                "456 Updated Street",
                "0987654321");
        userRepository.updateUser("test@test.com", updatedUser);

        assertEquals("Updated", userRepository.getUsers().getFirst().getFirstName());
        assertEquals("User", userRepository.getUsers().getFirst().getLastName());
        assertEquals("456 Updated Street", userRepository.getUsers().getFirst().getAddress());
    }

    @Test
    void testUpdateNonExistentUser() {
        UserRepository userRepository = new UserRepository();
        User updatedUser = new User("nonexistent@test.com",
                "John",
                "Doe",
                LocalDate.of(2000, 1, 1),
                "123 Street",
                "1234567890");

        assertNull(userRepository.updateUser("nonexistent@test.com", updatedUser));
    }

    @Test
    void testDeleteUser() {
        UserRepository userRepository = new UserRepository();
        User user = new User("test@test.com",
                "John",
                "Doe",
                LocalDate.of(2000, 1, 1),
                "123 Street",
                "1234567890");
        userRepository.addNewUser(user);

        userRepository.deleteUser("test@test.com");

        assertTrue(userRepository.getUsers().isEmpty());
    }

    @Test
    void testGetUsersByBirthDateRange() {
        UserRepository userRepository = new UserRepository();
        User user1 = new User("test1@test.com",
                "John",
                "Doe",
                LocalDate.of(2000, 1, 1),
                "123 Street",
                "1234567890");
        User user2 = new User("test2@test.com",
                "Jane",
                "Doe",
                LocalDate.of(2001, 2, 2),
                "456 Avenue",
                "0987654321");
        userRepository.addNewUser(user1);
        userRepository.addNewUser(user2);

        LocalDate startDate = LocalDate.of(2000, 1, 1);
        LocalDate endDate = LocalDate.of(2001, 12, 31);

        assertEquals(2, userRepository.getUsersByBirthDateRange(startDate, endDate).size());
    }

    @Test
    void testGetUserByEmail() {
        UserRepository userRepository = new UserRepository();
        User user = new User("test@test.com",
                "John",
                "Doe",
                LocalDate.of(2000, 1, 1),
                "123 Street",
                "1234567890");
        userRepository.addNewUser(user);

        assertEquals(user, userRepository.getUserByEmail("test@test.com"));
    }

    @Test
    void testGetNonExistentUserByEmail() {
        UserRepository userRepository = new UserRepository();

        assertNull(userRepository.getUserByEmail("nonexistent@test.com"));
    }
}
