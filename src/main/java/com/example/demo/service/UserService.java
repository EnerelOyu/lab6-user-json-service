package com.example.demo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User createUser(User user) {
        return userRepository.save(user);
    }

    public Optional<User> getUserById(int id) {
        return userRepository.findById(id);
    }

    public User updateUser(int id, User updatedUser) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        existingUser.setUsername(updatedUser.getUsername());
        existingUser.setName(updatedUser.getName());
        existingUser.setEmail(updatedUser.getEmail());
        existingUser.setBio(updatedUser.getBio());
        existingUser.setPhone(updatedUser.getPhone());
        existingUser.setImageUrl(updatedUser.getImageUrl());

        return userRepository.save(existingUser);
    }

    public void deleteUser(int id) {
        userRepository.deleteById(id);
    }
}