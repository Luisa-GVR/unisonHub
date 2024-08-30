package com.hub.foroUnison.domain.usuario;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record DatosAutenticacionUsuario(
        @NotBlank
        @Email
        String correoElectronico,
        @NotBlank
        String contrasena
        ) {


}
