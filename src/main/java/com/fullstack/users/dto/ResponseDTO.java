package com.fullstack.users.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResponseDTO {
    private boolean exito;
    private String mensaje;
    private T data;
}
