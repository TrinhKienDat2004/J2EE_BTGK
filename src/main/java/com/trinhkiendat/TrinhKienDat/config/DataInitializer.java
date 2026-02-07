package com.trinhkiendat.TrinhKienDat.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.trinhkiendat.TrinhKienDat.repository.RoleRepository;
import com.trinhkiendat.TrinhKienDat.repository.UserRepository;
import com.trinhkiendat.TrinhKienDat.model.Role;
import com.trinhkiendat.TrinhKienDat.model.User;
import java.util.HashSet;

@Configuration
public class DataInitializer {
    @Bean
    public CommandLineRunner init(RoleRepository roleRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            Role admin = roleRepository.findByName("ADMIN");
            if (admin == null) {
                admin = new Role();
                admin.setName("ADMIN");
                roleRepository.save(admin);
            }
            Role userRole = roleRepository.findByName("USER");
            if (userRole == null) {
                userRole = new Role();
                userRole.setName("USER");
                roleRepository.save(userRole);
            }

            // create default admin if not exists
            User adminUser = userRepository.findByUsername("admin");
            if (adminUser == null) {
                adminUser = new User();
                adminUser.setUsername("admin");
                adminUser.setEmail("admin@example.com");
                adminUser.setPhone("0000000000");
                adminUser.setFullName("Administrator");
                adminUser.setPassword(passwordEncoder.encode("admin"));
                HashSet<Role> roles = new HashSet<>();
                roles.add(admin);
                roles.add(userRole);
                adminUser.setRoles(roles);
                userRepository.save(adminUser);
            }
        };
    }
}
