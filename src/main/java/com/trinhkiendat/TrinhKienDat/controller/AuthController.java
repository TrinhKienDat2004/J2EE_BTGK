package com.trinhkiendat.TrinhKienDat.controller;

import com.trinhkiendat.TrinhKienDat.model.User;
import com.trinhkiendat.TrinhKienDat.repository.UserRepository;
import com.trinhkiendat.TrinhKienDat.repository.RoleRepository;
import com.trinhkiendat.TrinhKienDat.model.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@Controller
public class AuthController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleRepository roleRepository;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/signup")
    public String signup(Model model) {
        model.addAttribute("user", new User());
        return "signup";
    }

    @PostMapping("/signup")
    public String registerUser(@Valid @ModelAttribute("user") User user, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "signup";
        }
        if (userRepository.findByUsername(user.getUsername()) != null) {
            model.addAttribute("usernameError", "Username already exists");
            return "signup";
        }
        if (userRepository.findByEmail(user.getEmail()) != null) {
            model.addAttribute("emailError", "Email already exists");
            return "signup";
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        Role userRole = roleRepository.findByName("USER");
        if (userRole != null) {
            user.getRoles().add(userRole);
        }
        userRepository.save(user);
        return "redirect:/login?signupSuccess";
    }
}
