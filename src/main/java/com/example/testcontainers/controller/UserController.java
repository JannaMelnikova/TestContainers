package com.example.testcontainers.controller;
import com.example.testcontainers.entity.User;
import com.example.testcontainers.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@AllArgsConstructor
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping()
    public List<User> findAll() {
        return userRepository.findAll();
    }


    @PostMapping("/add")
    public ResponseEntity<User> save(@RequestBody User user) {
        User savedUser = userRepository.save(user);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(savedUser);
    }

    @GetMapping("/{id}")
    public User findById(@PathVariable long id) {
        return userRepository.findById(id)
                .orElseThrow(()->new RuntimeException("User not found"));
    }

    //дообавить методы удаления по ид и обновления по объекту
}
