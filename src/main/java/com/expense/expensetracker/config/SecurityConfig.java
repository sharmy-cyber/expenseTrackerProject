package com.expense.expensetracker.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.expense.expensetracker.service.impl.CustomUserDetailsService;

@Configuration
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/login",
                                "/register",
                                "/css/**",
                                "/js/**",
                                "/images/**",
                                "/user/verify-email",
                                "/verify")
                        .permitAll()
                        .anyRequest().authenticated())
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/user/dashboard", true)
                        .failureUrl("/login?error=true")
                        .permitAll())
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout=true")
                        .permitAll());

        return http.build();
    }

    /*
     * @Bean
     * public SecurityFilterChain securityFilterChain(HttpSecurity http) throws
     * Exception {
     * http
     * .csrf(csrf -> csrf.disable())
     * .authorizeHttpRequests(auth -> auth
     * .anyRequest().permitAll())
     * .formLogin(form -> form
     * .loginPage("/login")
     * .loginProcessingUrl("/login")
     * .defaultSuccessUrl("/user/dashboard", true)
     * .failureUrl("/login?error=true")
     * .permitAll()
     * );
     * return http.build();
     * }
     * 
     * /* @Bean
     * public UserDetailsService userDetailsService(UserRepository userRepository) {
     * return username -> {
     * var user = userRepository.findByEmail(username);
     * if (user == null) {
     * throw new UsernameNotFoundException("User not found: " + username);
     * }
     * // Map your domain user to Spring Security's UserDetails
     * return new org.springframework.security.core.userdetails.User(
     * user.getEmail(),
     * user.getPassword(),
     * user.isEnabled(), // enabled
     * true, // accountNonExpired
     * true, // credentialsNonExpired
     * true, // accountNonLocked
     * List.of(new SimpleGrantedAuthority("ROLE_USER"))
     * );
     * };
     * }
     */

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
