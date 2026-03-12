package com.example.demo.repository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.example.demo.model.User;

//user profile medeellig hadgalna
@Repository
public class UserRepository {

    private final List<User> users = new ArrayList<>();

    public UserRepository() {
        users.add(new User(1, "Bat-Erdene", "bat@example.com", "Student", "99112233"));
        users.add(new User(2, "Sarnai", "sarnai@example.com", "Designer", "88112233"));
    }
    
    //buh useriin medeelling awah
    public List<User> findAll() {
        return users;
    }
    
    //id-aar haih
    public User findById(int id) {
        for (User user : users) {
            if (user.getId() == id) {
                return user;
            }
        }
        return null;
    }

    //user hadaglah
    public User save(User user) {
        users.add(user);
        return user;
    }

    //useriin medeelel shinechileh
    public User update(int id, User updatedUser) {
        User existingUser = findById(id);

        if (existingUser == null) {
            return null;
        }

        existingUser.setName(updatedUser.getName());
        existingUser.setEmail(updatedUser.getEmail());
        existingUser.setBio(updatedUser.getBio());
        existingUser.setPhone(updatedUser.getPhone());

        return existingUser;
    }

    //user ustgah
    public boolean delete(int id) {
        User existingUser = findById(id);

        if (existingUser == null) {
            return false;
        }

        users.remove(existingUser);
        return true;
    }
}