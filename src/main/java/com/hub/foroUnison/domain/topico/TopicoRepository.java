package com.hub.foroUnison.domain.topico;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TopicoRepository extends JpaRepository<Topico, Long> {
    @Query("SELECT t.titulo, t.mensaje, t.fechaCreacion, t.status, t.autor.nombre AS autorNombre, t.curso.nombre AS cursoNombre, t.curso.categoria AS categoriaCurso, t.id " +
            "FROM topico t")
    List<Object[]> findAllTopicsWithDetails();

    @Query("SELECT t FROM topico t WHERE t.titulo = :titulo AND t.mensaje = :mensaje")
    Topico findByTituloAndMensaje(@Param("titulo") String titulo, @Param("mensaje") String mensaje);


}
