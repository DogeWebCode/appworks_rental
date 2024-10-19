package tw.school.rental_backend.controller;

import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tw.school.rental_backend.model.user.User;
import tw.school.rental_backend.service.UserService;
import tw.school.rental_backend.util.UserUtil;

@Log4j2
@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;
    private final UserUtil userUtil;

    public UserController(UserService userService, UserUtil userUtil) {
        this.userService = userService;
        this.userUtil = userUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody User user) {
        User registeredUser = userService.registerUser(user);
        return ResponseEntity.ok(registeredUser);
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody User user) {
        String token = userService.login(user.getEmail(), user.getPassword());
        return ResponseEntity.ok(token);
    }

    @GetMapping("/info")
    public ResponseEntity<?> getCurrentUser() {
        User user = userUtil.getCurrentUser();
        return ResponseEntity.ok(user);
    }
}
