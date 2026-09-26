package com.maintenance.fixFlow.config;

import com.maintenance.fixFlow.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration) throws Exception {

        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

                http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth ->
                        auth.requestMatchers("/auth/login",
                                        "/api/users").permitAll()

                                // Property
                                .requestMatchers(HttpMethod.GET, "/api/property/**")
                                .hasAnyRole("TENANT", "VENDOR", "MANAGER")

                                .requestMatchers(HttpMethod.POST, "/api/property/**")
                                .hasRole("MANAGER")

                                .requestMatchers(HttpMethod.PUT, "/api/property/**")
                                .hasRole("MANAGER")

                                .requestMatchers(HttpMethod.DELETE, "/api/property/**")
                                .hasRole("MANAGER")

                                // Unit
                                .requestMatchers(HttpMethod.GET, "/api/unit/**")
                                .hasAnyRole("TENANT", "VENDOR", "MANAGER")

                                .requestMatchers(HttpMethod.POST, "/api/unit/**")
                                .hasRole("MANAGER")

                                .requestMatchers(HttpMethod.PUT, "/api/unit/**")
                                .hasRole("MANAGER")

                                .requestMatchers(HttpMethod.DELETE, "/api/unit/**")
                                .hasRole("MANAGER")

                                // Assignment
                                .requestMatchers(HttpMethod.GET, "/api/assignment/getAll")
                                .hasRole("MANAGER")

                                .requestMatchers(HttpMethod.POST, "/api/assignment/**")
                                .hasRole("MANAGER")

                                .requestMatchers(HttpMethod.DELETE, "/api/assignment/**")
                                .hasRole("MANAGER")

                                //Maintenance Request
                                .requestMatchers(HttpMethod.GET,
                                        "/api/maintenanceRequest/getAll"
                                )
                                .hasRole("MANAGER")

                                //Attachment
                                .requestMatchers(HttpMethod.GET, "/api/attachment/getAll")
                                .hasRole("MANAGER")

                                //Comment
                                .requestMatchers(HttpMethod.GET, "/api/comment/getAll")
                                .hasRole("MANAGER")

                                //workUpdate
                                .requestMatchers(HttpMethod.GET, "/api/workUpdate/getAll")
                                .hasRole("MANAGER")

                                //Notification
                                .requestMatchers(HttpMethod.GET, "/api/notification/getAll")
                                .hasRole("MANAGER")

                                //Rating
                                .requestMatchers(HttpMethod.GET, "/api/rating/getAll")
                                .hasRole("MANAGER")

                                //User
                                .requestMatchers(HttpMethod.GET, "/api/users/getAll")
                                .hasRole("MANAGER")

                                // other all requests requires login
                                .anyRequest().authenticated()
                        )

                        .addFilterBefore(
                                jwtAuthenticationFilter,
                                UsernamePasswordAuthenticationFilter.class
                        );


        return http.build();
    }
}