package com.maintenance.fixFlow.service;
import com.maintenance.fixFlow.dto.UserRequestDto;
import com.maintenance.fixFlow.dto.UserResponseDto;
import com.maintenance.fixFlow.entity.Unit;
import com.maintenance.fixFlow.entity.User;
import com.maintenance.fixFlow.exception.ResourceNotFoundException;
import com.maintenance.fixFlow.mapper.UserMapper;
import com.maintenance.fixFlow.repository.UnitRepository;
import com.maintenance.fixFlow.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UnitRepository unitRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, UnitRepository unitRepository, PasswordEncoder passwordEncoder){
        this.userRepository=userRepository;
        this.unitRepository = unitRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserResponseDto createUser(UserRequestDto dto) {
        Unit unit = unitRepository.findById(dto.getUnitId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Unit not found with id: " + dto.getUnitId()
                        )
                );

        User user = UserMapper.toEntity(dto, unit);

        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        User savedUser = userRepository.save(user);

        return UserMapper.toResponseDto(savedUser);
    }

    public UserResponseDto getUserById(Long id){
        User user=userRepository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("User not found with id:"+id));
        return UserMapper.toResponseDto(user);
    }

    public UserResponseDto getUserByEmail(String email){
        User user=userRepository.findByEmail(email)
                .orElseThrow(()->new ResourceNotFoundException("User not found with email:"+email));
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

    public UserResponseDto updateUser(UserRequestDto dto, Long id) {
        User user=userRepository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("User not found with id:"+id));


            Unit unit = unitRepository.findById(dto.getUnitId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                        "Unit not found with id: " + dto.getUnitId()
                )
                    );

            user.setName(dto.getName());
            user.setUnit(unit);
            user.setRole(dto.getRole());
            user.setPhone(dto.getPhone());
            user.setEmail(dto.getEmail());

            User savedUser = userRepository.save(user);

            return UserMapper.toResponseDto(savedUser);

    }

    public String deleteUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with id: " + id
                        )
                );
        userRepository.delete(user);
        return "User Deleted";
    }

}
