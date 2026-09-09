package com.nextgen.store.auth;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {
    private final UserRepository users; private final PasswordEncoder encoder; private final JwtService jwt;
    public AuthService(UserRepository users,PasswordEncoder encoder,JwtService jwt){this.users=users;this.encoder=encoder;this.jwt=jwt;}
    public AuthDtos.AuthResponse register(AuthDtos.RegisterRequest r){
        String email=r.email().trim().toLowerCase();
        if(users.existsByEmailIgnoreCase(email)) throw new ResponseStatusException(HttpStatus.CONFLICT,"An account with this email already exists");
        User u=new User(); u.setEmail(email); u.setFirstName(r.firstName().trim()); u.setLastName(r.lastName()==null?null:r.lastName().trim());
        u.setPasswordHash(encoder.encode(r.password())); u.setRole(Role.CUSTOMER); u.setActive(true); users.save(u);
        return response(u);
    }
    public AuthDtos.AuthResponse login(AuthDtos.LoginRequest r){
        User u=users.findByEmailIgnoreCase(r.email().trim()).orElseThrow(()->new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Invalid email or password"));
        if(!u.isActive()||!encoder.matches(r.password(),u.getPasswordHash())) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Invalid email or password");
        return response(u);
    }
    public AuthDtos.UserResponse me(String email){ return users.findByEmailIgnoreCase(email).map(AuthDtos.UserResponse::from).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"User not found")); }
    private AuthDtos.AuthResponse response(User u){return new AuthDtos.AuthResponse(jwt.generate(u),jwt.getExpirationMs(),AuthDtos.UserResponse.from(u));}
}
