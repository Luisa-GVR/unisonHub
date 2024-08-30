package com.hub.foroUnison.domain.curso;

import com.hub.foroUnison.domain.categoria.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CursoRepository extends JpaRepository<Curso, Long> {


    Curso findByNombre(String nombreCurso);

    @Query("SELECT c FROM Curso c WHERE c.nombre = :nombreCurso AND c.categoria = :categoria")
    Curso findByNombreAndCategoria(@Param("nombreCurso") String nombreCurso, @Param("categoria") Categoria categoria);

}
