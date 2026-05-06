package com.fullstack.users.dto;

import java.util.UUID;

import com.fullstack.users.model.UserRol;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class AuthResponse {

    private UUID id;
    private String name;
    private String lastname;
    private String email;
    private UserRol rol;
    private String token;
    private String message;
}