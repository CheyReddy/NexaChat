package com.cwebworks.chat.user;

import lombok.Builder;

@Builder
public record UserDto(Long id, String username, String email, String displayName) {
    public static UserDto convertToResponse(User u){
        return UserDto.builder()
                .id(u.getId())
                .username(u.getUsername())
                .email(u.getEmail())
                .displayName(u.getDisplayName())
                .build();
    }
}
