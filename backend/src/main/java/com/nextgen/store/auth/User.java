package com.nextgen.store.auth;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "users")
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true, length = 255)
    private String email;
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;
    @Column(name = "first_name", nullable = false, length = 80)
    private String firstName;
    @Column(name = "last_name", length = 80)
    private String lastName;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 30)
    private Role role = Role.CUSTOMER;
    @Column(nullable = false)
    private boolean active = true;
    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @PrePersist void onCreate(){ var now=OffsetDateTime.now(); createdAt=now; updatedAt=now; }
    @PreUpdate void onUpdate(){ updatedAt=OffsetDateTime.now(); }

    public Long getId(){ return id; }
    public String getEmail(){ return email; }
    public void setEmail(String email){ this.email=email; }
    public String getPasswordHash(){ return passwordHash; }
    public void setPasswordHash(String passwordHash){ this.passwordHash=passwordHash; }
    public String getFirstName(){ return firstName; }
    public void setFirstName(String firstName){ this.firstName=firstName; }
    public String getLastName(){ return lastName; }
    public void setLastName(String lastName){ this.lastName=lastName; }
    public Role getRole(){ return role; }
    public void setRole(Role role){ this.role=role; }
    public boolean isActive(){ return active; }
    public void setActive(boolean active){ this.active=active; }
}
