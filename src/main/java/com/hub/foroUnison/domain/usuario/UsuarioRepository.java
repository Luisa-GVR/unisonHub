package com.hub.foroUnison.domain.usuario;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.security.core.userdetails.UserDetails;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    UserDetails findByCorreoElectronico(String correo);

    @Query ("SELECT nombre FROM Usuario u WHERE u.correoElectronico = :correo")
    String findNombreUsuarioByCorreo(String correo);

    Usuario findUsuarioByCorreoElectronico(String correo);

}
