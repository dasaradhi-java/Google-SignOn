package com.javatechie.google.auth.controller;


import java.time.LocalDateTime;
import java.util.List;

import org.apache.velocity.exception.ResourceNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.javatechie.google.auth.Entity.LoginDetails;
import com.javatechie.google.auth.Entity.User;
import com.javatechie.google.auth.Repository.LoginDetailsRepository;
import com.javatechie.google.auth.Repository.UserRepository;

@RequestMapping("/api")
@RestController
public class UserController {

    private final UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private final LoginDetailsRepository loginDetailsRepository;

    public UserController(UserRepository userRepository,PasswordEncoder passwordEncoder,LoginDetailsRepository loginDetailsRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder=passwordEncoder;
        this.loginDetailsRepository=loginDetailsRepository;
    }

    @RequestMapping(value = "/hello", method = RequestMethod.GET)
    public String getMenu() {
        return "Hello User";
    }
    
    @GetMapping("/getAll")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @PostMapping("/save")
    public User createUser(@RequestBody User user) {
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        return userRepository.save(user);
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    @PutMapping("/{id}")
    public User updateUser(@PathVariable Long id, @RequestBody User userDetails) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        user.setUsername(userDetails.getUsername());
        user.setPassword(userDetails.getPassword());
        user.setEnabled(userDetails.isEnabled());

        return userRepository.save(user);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        userRepository.delete(user);

        return ResponseEntity.ok().build();
    }
    @GetMapping("/login")
    public ResponseEntity<String> recordLoginDetails(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            String gmailAddress = extractGmailAddress(authentication);

            if (gmailAddress != null) {
                LocalDateTime loginTime = LocalDateTime.now();

                LoginDetails loginDetails = new LoginDetails();
                loginDetails.setCompleteGmail(gmailAddress);
                loginDetails.setLoginTime(loginTime);

                loginDetailsRepository.save(loginDetails);

                return ResponseEntity.ok("Login details recorded for: " + gmailAddress);
            }
        }

        return ResponseEntity.badRequest().body("Unable to record login details.");
    }

    private String extractGmailAddress(Authentication authentication) {
        if (authentication.getPrincipal() instanceof OAuth2User) {
            OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
            return oauth2User.getAttribute("email");
        }

        return null;
    }
}
