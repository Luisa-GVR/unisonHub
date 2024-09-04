package com.hub.foroUnison.infra.security;


import com.hub.foroUnison.domain.usuario.UsuarioRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(SecurityFilter.class);

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UsuarioRepository usuarioRepository;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // Ignorar rutas específicas
        if (request.getRequestURI().startsWith("/register")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Obtener el token de los parameter
        var tokenParameter = request.getParameter("token");

        System.out.println(tokenParameter);
        if (tokenParameter != null) {

            var token = tokenParameter.replace("Bearer ", "");
            var correoElectronico = tokenService.getSubject(token);

            if (correoElectronico != null) {
                // Token valido

                var usuario = usuarioRepository.findByCorreoElectronico(correoElectronico);
                var authentication = new UsernamePasswordAuthenticationToken(usuario, null,
                        usuario.getAuthorities()); // Forzamos un inicio de sesion
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        filterChain.doFilter(request, response);
    }


}


