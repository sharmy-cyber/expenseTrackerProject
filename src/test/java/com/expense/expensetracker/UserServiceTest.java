package com.expense.expensetracker;

import com.example.entity.User;
import com.example.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void testUpdateProfileSuccess() {
        Long userId = 1L;
        String newName = "Sharmila";
        String newEmail = "sharmila@gmail.com";

        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setName("Old Name");
        existingUser.setEmail("old@gmail.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));

        userService.updateProfile(userId, newName, newEmail);

        assertEquals("Sharmila", existingUser.getName());
        assertEquals("sharmila@gmail.com", existingUser.getEmail());

        verify(userRepository, times(1)).save(existingUser);
    }

    @Test
    void testUpdateProfileUserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> userService.updateProfile(1L, "A", "B"));

        assertEquals("User not found", ex.getMessage());
    }
}
