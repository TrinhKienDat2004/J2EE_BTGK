package com.trinhkiendat.TrinhKienDat.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import java.util.UUID;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import com.trinhkiendat.TrinhKienDat.repository.UserRepository;
import com.trinhkiendat.TrinhKienDat.model.User;
import com.trinhkiendat.TrinhKienDat.model.Role;
import com.trinhkiendat.TrinhKienDat.repository.RoleRepository;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, OAuth2UserService<OAuth2UserRequest, OAuth2User> oauth2UserService) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/login", "/signup", "/css/**", "/js/**", "/images/**", "/books/**", "/cart/**").permitAll()
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/", true)
                .permitAll()
            )
            .oauth2Login(oauth -> oauth
                .loginPage("/login")
                .userInfoEndpoint(userInfo -> userInfo.userService(oauth2UserService))
                .defaultSuccessUrl("/", true)
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            );
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public OAuth2UserService<OAuth2UserRequest, OAuth2User> oauth2UserService(com.trinhkiendat.TrinhKienDat.repository.UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();
        return (OAuth2UserRequest userRequest) -> {
            OAuth2User oauth2User = delegate.loadUser(userRequest);
            String email = oauth2User.getAttribute("email");
            if (email != null) {
                com.trinhkiendat.TrinhKienDat.model.User user = userRepository.findByEmail(email);
                if (user == null) {
                    user = new com.trinhkiendat.TrinhKienDat.model.User();
                    user.setUsername(email);
                    user.setEmail(email);
                    user.setPhone("");
                    user.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
                    // map Google display name if available
                    String name = oauth2User.getAttribute("name");
                    if (name != null) {
                        user.setFullName(name);
                    }
                    // assign default USER role if exists
                    Role userRole = roleRepository.findByName("USER");
                    if (userRole != null) {
                        user.getRoles().add(userRole);
                    }
                    userRepository.save(user);
                } else {
                    // update fullName if missing
                    String name = oauth2User.getAttribute("name");
                    if (name != null && (user.getFullName() == null || user.getFullName().isEmpty())) {
                        user.setFullName(name);
                        userRepository.save(user);
                    }
                }
            }
            return oauth2User;
        };
    }

    @Bean
    public UserDetailsService userDetailsService(UserRepository userRepository) {
        return username -> {
            User user = userRepository.findByUsername(username);
            if (user == null) {
                throw new UsernameNotFoundException("User not found");
            }
            String[] roles = user.getRoles().stream().map(Role::getName).toArray(String[]::new);
            return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .roles(roles)
                .build();
        };
    }
}
