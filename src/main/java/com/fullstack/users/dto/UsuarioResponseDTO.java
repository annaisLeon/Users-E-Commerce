package com.fullstack.users.dto;

import com.smartlogix.usuarios.model.EstadoUsuario;
import com.smartlogix.usuarios.model.RolUsuario;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioResponseDTO {
    private Long id;
    private String nombre;
    private String apellido;
    private String correo;
    private String telefono;
    private String direccion;
    private RolUsuario rol;
    private EstadoUsuario estado;
}
