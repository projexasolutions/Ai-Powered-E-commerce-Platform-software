package com.nextgen.store.auth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminInitializer implements CommandLineRunner {
    private final UserRepository users;
    private final PasswordEncoder encoder;

    @Value("${app.admin.email:}")
    private String adminEmail;

    @Value("${app.admin.password:}")
    private String adminPassword;

    public AdminInitializer(UserRepository users, PasswordEncoder encoder) {
        this.users = users;
        this.encoder = encoder;
    }

    @Override
    public void run(String... args) {
        String email = adminEmail == null ? "" : adminEmail.trim().toLowerCase();
        String password = adminPassword == null ? "" : adminPassword;
        if (email.isBlank() || password.isBlank()) return;
        if (password.length() < 12) throw new IllegalStateException("ADMIN_PASSWORD must be at least 12 characters");

        User user = users.findByEmailIgnoreCase(email).orElseGet(User::new);
        user.setEmail(email);
        user.setFirstName(user.getFirstName() == null || user.getFirstName().isBlank() ? "Admin" : user.getFirstName());
        user.setRole(Role.ADMIN);
        user.setActive(true);

        if (user.getPasswordHash() == null || !encoder.matches(password, user.getPasswordHash())) {
            user.setPasswordHash(encoder.encode(password));
        }
        users.save(user);
    }
}
