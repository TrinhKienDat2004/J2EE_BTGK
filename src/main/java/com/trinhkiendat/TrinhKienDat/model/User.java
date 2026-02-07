package com.trinhkiendat.TrinhKienDat.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Username is required")
    @Column(length = 50, unique = true)
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Email is invalid")
    @Column(length = 50, unique = true)
    private String email;

    @NotBlank(message = "Phone is required")
    @Column(length = 10)
    private String phone;

    private String fullName;

    @NotBlank(message = "Password is required")
    @Column(length = 250)
    private String password;

    // Getter, Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
}