package com.hub.foroUnison.controller;


import com.hub.foroUnison.domain.usuario.Usuario;
import com.hub.foroUnison.domain.usuario.UsuarioRepository;
import com.hub.foroUnison.services.IEmailService;
import com.hub.foroUnison.services.dto.EmailDTO;
import jakarta.mail.MessagingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.text.DecimalFormat;
import java.util.Random;

@Controller
public class RegisterController {

    @Autowired
    IEmailService iEmailService;
    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private UsuarioRepository usuarioRepository;

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    private String name;
    private String email;
    private String password;
    private String validificacion;

    @GetMapping("/register")
    public String mostrarRegister(Model model) {
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@RequestParam("name") String name,
                               @RequestParam("email") String email,
                               @RequestParam("password") String password,
                               Model model) throws MessagingException {

        // verificar si el correo electrónico ya existe en la base de datos
        if (usuarioRepository.existsByCorreoElectronico(email)) {
            model.addAttribute("error", "El correo electrónico ya está registrado. Por favor, utiliza otro correo.");

            return "register";
        }

        this.name = name;
        this.email = email;
        this.password = password;

        //Generar el numero aleatorio de verificacion
        Random random = new Random();
        int randomNumber = random.nextInt(100000);
        DecimalFormat df = new DecimalFormat("00000");
        validificacion = df.format(randomNumber);

        // Crear el contexto de Thymeleaf y agregar el código de verificación
        Context context = new Context();
        context.setVariable("verificationCode", validificacion);


        // Procesar la plantilla y obtener el contenido HTML
        String contentHTML = templateEngine.process("email", context);

        // logica para enviar el correo electronico

        // Configurar el emailDTO
        EmailDTO emailDTO = new EmailDTO();
        emailDTO.setDestinatario(email);
        emailDTO.setAsunto("Código de verificación");
        emailDTO.setMensaje(contentHTML);

        iEmailService.sendMail(emailDTO);

        // Redirige al usuario a la página de validación
        return "redirect:/register/validation";
    }

    @GetMapping("/register/validation")
    public String validationPage() {
        return "validation";
    }

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @PostMapping("/register/validation")
    public String validateCode(@RequestParam("code") String code, Model model) {
        // validamos que el codigo es el mismo
        boolean isValidCode = code.equals(validificacion);
        System.out.println("codigo validado!");

        if (isValidCode) {
            // Lógica para guardar los datos del usuario en la base de datos

            Usuario nuevoUsuario = new Usuario();
            nuevoUsuario.setNombre(name);
            String encryptedPassword = passwordEncoder.encode(password);
            nuevoUsuario.setContrasena(encryptedPassword);
            nuevoUsuario.setCorreoElectronico(email);

            // Guardar en la base de datos
            usuarioRepository.save(nuevoUsuario);

            // eliminar atributos
            name = null;
            email = null;
            password = null;
            validificacion = null;


            // redirigir al login
            return "redirect:/login";
        } else {
            //redirigir a register
            model.addAttribute("error", "Código de validación incorrecto. Inténtalo nuevamente.");
            return "redirect:/register";
        }
    }


}
