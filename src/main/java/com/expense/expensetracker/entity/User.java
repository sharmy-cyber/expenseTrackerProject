package com.expense.expensetracker.entity;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    private String password;

    private boolean enabled = false;

    private LocalDateTime createdAt;

    private String pendingEmail;

    private String verificationToken;

    private LocalDateTime verificationExpiry;

    /*
     * @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
     * private List<Expense> expenses = new ArrayList<>();
     * 
     * @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
     * private List<Category> categories = new ArrayList<>();
     */
}