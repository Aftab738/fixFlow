package com.maintenance.fixFlow.controller;

import com.maintenance.fixFlow.dto.UserRequestDto;
import com.maintenance.fixFlow.dto.UserResponseDto;
import com.maintenance.fixFlow.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public UserResponseDto createUser( @Valid @RequestBody UserRequestDto dto){
        return userService.createUser(dto);
    }

    @GetMapping("/{id}")
    public UserResponseDto getUserById(@PathVariable Long id){
        return userService.getUserById(id);
    }

    @GetMapping("/email/{email}")
    public UserResponseDto getUserByEmail(@PathVariable String email){
        return userService.getUserByEmail(email);
    }

    @GetMapping("/getAll")
    public List<UserResponseDto> getAllUser(){
        return userService.getAllUsers();
    }

    @PutMapping("/{id}")
    public UserResponseDto updateUser( @Valid @RequestBody UserRequestDto dto,
                                      @PathVariable Long id){
        return userService.updateUser(dto,id);
    }

    @DeleteMapping("/{id}")
    public String DeleteById(@PathVariable Long id){
        return userService.deleteUserById(id);
    }


}
