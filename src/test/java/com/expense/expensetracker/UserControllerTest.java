package com.expense.expensetracker;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import org.springframework.test.web.servlet.MockMvc;

import com.expense.expensetracker.controller.UserController;
import com.expense.expensetracker.service.UserService;

import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    void testUpdateUserProfile() throws Exception {
        Long userId = 1L;

        doNothing().when(userService)
                .updateProfile(userId, "Sharmila", "sharmila@gmail.com");

        mockMvc.perform(post("/user/update/{userId}", userId)
                        .param("name", "Sharmila")
                        .param("email", "sharmila@gmail.com"))
                .andExpect(status().isOk())
                .andExpect(content().string("User profile updated successfully"));
    }
}
