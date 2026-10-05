package com.cwebworks.chat.user;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Data
@Table(name = "users")
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, name = "password_hash", length = 100)
    private String passwordHash;

    @Column(nullable = false, name = "display_name", length = 100)
    private String displayName;

    @Column(nullable = false, name = "created_at", updatable = false)
    private Instant createdAt = Instant.now();

}
