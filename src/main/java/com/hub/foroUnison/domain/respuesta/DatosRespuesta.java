package com.hub.foroUnison.domain.respuesta;


import com.hub.foroUnison.domain.topico.Topico;
import com.hub.foroUnison.domain.usuario.Usuario;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

public record DatosRespuesta(
        @NotBlank
        int id,
        @NotBlank
        String mensaje,
        @NotBlank
        Boolean solucion,
        @NotBlank
        LocalDateTime fechaCreacion,
        @NotBlank
        Usuario autor,
        @NotBlank
        Topico topico
) {
}
