package com.fullstack.users.dto;

import java.util.UUID;

import com.fullstack.users.model.UserRol;
import com.fullstack.users.model.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

//este DTO se utiliza para enviar la información del perfil del usuario al cliente, incluyendo su rol y estado, pero sin exponer información sensible como la contraseña.
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponse {

    private UUID id;
    private String name;
    private String lastname;
    private String email;
    private String phone;
    private String address;
    private UserRol rol;
    private UserStatus status;
}
