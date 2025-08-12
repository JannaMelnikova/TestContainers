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

    //добавить методы удаления по ид и обновления по объекту
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable long id) {
            userRepository.deleteById(id);
            return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateById(@PathVariable long id, @RequestBody User user) {
//        User savedUser = userRepository.save(user);
//        return ResponseEntity.ok(savedUser);
        return userRepository.findById(id)
                .map(existingUser -> {
                    // Обновляем поля существующего пользователя, у нас только поле name
                    existingUser.setName(user.getName());

                    User savedUser = userRepository.save(existingUser);
                    return ResponseEntity.ok(savedUser);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<User> patchById(@PathVariable long id, @RequestBody User user) {
        User savedUser = userRepository.save(user);
        return ResponseEntity.ok(savedUser);
    }


}
