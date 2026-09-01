package com.maintenance.fixFlow.service;
import com.maintenance.fixFlow.dto.UserRequestDto;
import com.maintenance.fixFlow.dto.UserResponseDto;
import com.maintenance.fixFlow.entity.Unit;
import com.maintenance.fixFlow.entity.User;
import com.maintenance.fixFlow.mapper.UserMapper;
import com.maintenance.fixFlow.repository.UnitRepository;
import com.maintenance.fixFlow.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UnitRepository unitRepository;

    public UserService(UserRepository userRepository, UnitRepository unitRepository){
        this.userRepository=userRepository;
        this.unitRepository = unitRepository;
    }

    public UserResponseDto createUser(UserRequestDto dto) {
        Unit unit = unitRepository.findById(dto.getUnitId())
                .orElse(null);

        User user = UserMapper.toEntity(dto, unit);
        User savedUser = userRepository.save(user);

        return UserMapper.toResponseDto(savedUser);
    }

    public UserResponseDto getUserById(Long id){
        Optional<User> user=userRepository.findById(id);
        if(user.isPresent()){
            return UserMapper.toResponseDto(user.get());
        }
        return null;
    }

    public UserResponseDto getUserByEmail(String email){
        Optional<User> user=userRepository.findByEmail(email);
        if(user.isPresent()){
            return UserMapper.toResponseDto(user.get());
        }
        return null;
    }

    public List<UserResponseDto> getAllUsers() {

        List<User> users = userRepository.findAll();
        List<UserResponseDto> result = new ArrayList<>();

        for (User user : users) {
            result.add(UserMapper.toResponseDto(user));
        }

        return result;
    }

    public UserResponseDto updateUser(UserRequestDto dto, Long id) {
        Optional<User> us = userRepository.findById(id);
        if (us.isPresent()) {

            User user = us.get();

            Unit unit = unitRepository.findById(dto.getUnitId())
                    .orElse(null);

            user.setName(dto.getName());
            user.setUnit(unit);
            user.setRole(dto.getRole());
            user.setPhone(dto.getPhone());
            user.setEmail(dto.getEmail());

            User savedUser = userRepository.save(user);

            return UserMapper.toResponseDto(savedUser);
        }

        return null;
    }

    public String deleteUserById(Long id){
        Optional<User> user = userRepository.findById(id);

        if(user.isPresent()) {
            userRepository.deleteById(id);
            return "User Deleted";
        }

        return "User is not present";
    }

}
