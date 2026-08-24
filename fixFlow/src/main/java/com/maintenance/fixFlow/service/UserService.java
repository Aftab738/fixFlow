package com.maintenance.fixFlow.service;
import com.maintenance.fixFlow.entity.User;
import com.maintenance.fixFlow.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository){
        this.userRepository=userRepository;
    }

    public User createUser(User user) {
        return userRepository.save(user);
    }

    public User getUserById(Long id){
        Optional<User> user=userRepository.findById(id);
        return user.orElse(null);
    }

    public User getUserByEmail(String email){
        Optional<User> user=userRepository.findByEmail(email);
        return user.orElse(null);
    }

    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

    public User updateUser(User user,Long id) {
        Optional<User> us = userRepository.findById(id);

        if (us.isPresent()) {

            User user1 = us.get();

            user1.setName(user.getName());
            user1.setUnit(user.getUnit());
            user1.setRole(user.getRole());
            user1.setPhone(user.getPhone());
            user1.setEmail(user.getEmail());

            return userRepository.save(user1);
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
