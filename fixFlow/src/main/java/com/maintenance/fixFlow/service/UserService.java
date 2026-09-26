package com.maintenance.fixFlow.service;

import com.maintenance.fixFlow.dto.*;
import com.maintenance.fixFlow.entity.Role;
import com.maintenance.fixFlow.entity.Unit;
import com.maintenance.fixFlow.entity.User;
import com.maintenance.fixFlow.exception.ResourceNotFoundException;
import com.maintenance.fixFlow.mapper.UserMapper;
import com.maintenance.fixFlow.repository.UnitRepository;
import com.maintenance.fixFlow.repository.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UnitRepository unitRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       UnitRepository unitRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.unitRepository = unitRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserResponseDto createUser(UserRegistrationDto dto) {

        Unit unit = unitRepository.findById(dto.getUnitId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Unit not found with id: " + dto.getUnitId())
                );

        User user = UserMapper.toEntity(dto, unit);

        user.setRole(Role.TENANT); //public registration allows only tenant creation

        user.setPassword(passwordEncoder.encode(dto.getPassword()));

        User savedUser = userRepository.save(user);

        return UserMapper.toResponseDto(savedUser);
    }

    public UserResponseDto getUserById(Long id) {

        User user = userRepository.findById(id).orElseThrow(() ->
                        new ResourceNotFoundException("User not found with id: " + id)
                );

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        boolean manager = false;

        for (var a : authentication.getAuthorities()) {
            if (a.getAuthority().equals("ROLE_MANAGER")) {
                manager = true;
                break;
            }
        }

        if (!manager && !user.getEmail().equals(email)) {
            throw new AccessDeniedException("You are not allowed to view this User.");
        }

        return UserMapper.toResponseDto(user);
    }

    public UserResponseDto getUserByEmail(String email) {

        User user = userRepository.findByEmail(email).orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with email: " + email
                        )
                );

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String currentEmail = authentication.getName();

        boolean manager = false;

        for (var a : authentication.getAuthorities()) {
            if (a.getAuthority().equals("ROLE_MANAGER")) {
                manager = true;
                break;
            }
        }
        if (!manager && !email.equals(currentEmail)) {
            throw new AccessDeniedException("You are not allowed to view this User.");
        }

        return UserMapper.toResponseDto(user);
    }

    public List<UserResponseDto> getAllUsers() {

        List<User> users = userRepository.findAll();
        List<UserResponseDto> result = new ArrayList<>();

        for (User user : users) {
            result.add(UserMapper.toResponseDto(user));
        }

        return result;
    }

    public UserResponseDto updateUser(UserUpdateDto dto, Long id) {

        User user = userRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("User not found with id: " + id)
        );

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        if (!user.getEmail().equals(email)) {
            throw new AccessDeniedException("You are not allowed to update this User.");
        }

        user.setName(dto.getName());
        user.setPhone(dto.getPhone());

        User savedUser = userRepository.save(user);

        return UserMapper.toResponseDto(savedUser);
    }

    public UserResponseDto adminUpdateUser(UserAdminUpdateDto dto, Long id) {

        User user = userRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("User not found with id: " + id)
        );

        Unit unit = unitRepository.findById(dto.getUnitId()).orElseThrow(() ->
                new ResourceNotFoundException("Unit not found with id: " + dto.getUnitId())
        );

        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setUnit(unit);
        user.setRole(dto.getRole());

        User savedUser = userRepository.save(user);

        return UserMapper.toResponseDto(savedUser);
    }

    public String deleteUserById(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found with id: " + id)
                );

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        boolean manager = false;

        for (var a : authentication.getAuthorities()) {
            if (a.getAuthority().equals("ROLE_MANAGER")) {
                manager = true;
                break;
            }
        }

        if (!manager && !user.getEmail().equals(email)) {
            throw new AccessDeniedException("You are not allowed to delete this User.");
        }

        userRepository.delete(user);
        return "User Deleted";
    }
}