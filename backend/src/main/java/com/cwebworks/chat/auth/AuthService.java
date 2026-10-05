package com.cwebworks.chat.auth;

import com.cwebworks.chat.common.DuplicateResourceException;
import com.cwebworks.chat.user.User;
import com.cwebworks.chat.user.UserDto;
import com.cwebworks.chat.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public UserDto register(RegisterRequest request){
        String username = request.username().trim().toLowerCase(Locale.ROOT);
        String email = request.email().trim().toLowerCase(Locale.ROOT);

        if(userRepository.existsByUsername(username)) throw new DuplicateResourceException("Username already taken");
        if(userRepository.existsByEmail(email)) throw new DuplicateResourceException("Email already registered");

        User u = new User();
        u.setUsername(username);
        u.setEmail(email);
        u.setDisplayName(request.displayName().trim());
        u.setPasswordHash(passwordEncoder.encode(request.password()));

        try{
            return UserDto.convertToResponse(userRepository.saveAndFlush(u));
        }
        catch(DataIntegrityViolationException race){
            throw new DuplicateResourceException("Username or email already in use");
        }
    }

    public AuthResponse login(LoginRequest request){
        String username = request.username().trim().toLowerCase(Locale.ROOT);
        Authentication auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, request.password()));
        return new AuthResponse(jwtService.generate(auth.getName()),"Bearer", jwtService.getTtlSeconds());
    }

}
