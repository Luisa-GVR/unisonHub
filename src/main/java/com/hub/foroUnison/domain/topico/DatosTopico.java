package com.hub.foroUnison.domain.topico;

import com.hub.foroUnison.domain.curso.Curso;
import com.hub.foroUnison.domain.usuario.Usuario;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

public record DatosTopico(
        @NotBlank
        int id,
        @NotBlank
        String titulo,
        @NotBlank
        String mensaje,
        @NotBlank
        LocalDateTime fechaCreacion,
        @NotBlank
        String status,
        @NotBlank
        Usuario autor,
        @NotBlank
        Curso curso

) {
}
