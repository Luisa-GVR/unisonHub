package com.hub.foroUnison.domain.curso;

import com.hub.foroUnison.domain.categoria.Categoria;
import jakarta.validation.constraints.NotBlank;

public record DatosCurso(
        @NotBlank
        String id,
        @NotBlank
        String nombre,
        @NotBlank
        Categoria categoria
) {
}