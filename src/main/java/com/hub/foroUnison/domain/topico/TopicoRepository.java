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

    @Query(value = "SELECT " +
            "    t.titulo AS titulo_topico, " +
            "    t.mensaje AS mensaje_topico, " +
            "    t.fecha_Creacion AS fecha_creacion_topico, " +
            "    t.status AS estado_topico, " +
            "    IFNULL(c.nombre, 'Sin Curso') AS nombre_curso, " +
            "    IFNULL(c.categoria, 'Sin Categoria') AS categoria_curso, " +
            "    IFNULL(r.mensaje, 'Sin Respuesta') AS mensaje_respuesta, " +
            "    IFNULL(r.fecha_Creacion, 'Sin Fecha') AS fecha_creacion_respuesta, " +
            "    IFNULL(u.nombre, 'Sin Autor') AS autor_respuesta, " +
            "    CASE " +
            "        WHEN r.solucion = 1 THEN 'Sí' " +
            "        ELSE 'No' " +
            "    END AS es_solucion " +
            "FROM " +
            "    topico t " +
            "LEFT JOIN curso c ON t.curso_id = c.id " +
            "LEFT JOIN respuesta r ON t.id = r.topico_id " +
            "LEFT JOIN usuario u ON r.autor_id = u.id " +
            "ORDER BY " +
            "    t.fecha_Creacion DESC, r.fecha_Creacion ASC",
            nativeQuery = true)
    List<Object[]> customQuery();




}
