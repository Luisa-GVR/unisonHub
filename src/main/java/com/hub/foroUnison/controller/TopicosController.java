package com.hub.foroUnison.controller;

import com.hub.foroUnison.domain.categoria.Categoria;
import com.hub.foroUnison.domain.curso.Curso;
import com.hub.foroUnison.domain.curso.CursoRepository;
import com.hub.foroUnison.domain.respuesta.Respuesta;
import com.hub.foroUnison.domain.respuesta.RespuestaRepository;
import com.hub.foroUnison.domain.topico.Topico;
import com.hub.foroUnison.domain.topico.TopicoRepository;
import com.hub.foroUnison.domain.usuario.Usuario;
import com.hub.foroUnison.domain.usuario.UsuarioRepository;
import com.hub.foroUnison.infra.security.TokenService;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@SecurityRequirement(name = "bearer-key")
@Controller
@RequestMapping("/topicos")
public class TopicosController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private TopicoRepository topicoRepository;
    @Autowired
    private CursoRepository cursoRepository;
    @Autowired
    private RespuestaRepository respuestaRepository;


    private String correoElectronicoGlobal;

    private String nombreGlobal;



    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @GetMapping("/logout")
    public String logout() {
        nombreGlobal = null;
        correoElectronicoGlobal = null;

        return "redirect:/login";
    }


    @GetMapping("/exportarPDF")
    public void  exportarPDF(@RequestParam(name = "token", required = true) String token,
                                    @RequestParam String correoElectronico,
                                    HttpServletResponse response) throws IOException, DocumentException {

        response.setContentType("application/pdf");
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=topicos.pdf";
        response.setHeader(headerKey, headerValue);

        List<Object[]> resultados = topicoRepository.customQuery();

        Document document = new Document();
        PdfWriter.getInstance(document, response.getOutputStream());
        document.open();

        PdfPTable table = new PdfPTable(10);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);
        table.setSpacingAfter(10f);

        // encabezados
        table.addCell("Título del Tópico");
        table.addCell("Mensaje del Tópico");
        table.addCell("Fecha de Creación");
        table.addCell("Estado del Tópico");
        table.addCell("Nombre del Curso");
        table.addCell("Categoría del Curso");
        table.addCell("Mensaje de la Respuesta");
        table.addCell("Fecha de Creación de la Respuesta");
        table.addCell("Autor de la Respuesta");
        table.addCell("Es Solución");

        // llenar la tabla con los datos
        for (Object[] row : resultados) {
            for (Object column : row) {
                table.addCell(column != null ? column.toString() : "");
            }
        }

        document.add(table);
        document.close();

    }

    @GetMapping("/exportarExcel")
    public void  exportarExcel(@RequestParam(name = "token", required = true) String token,
                                      @RequestParam String correoElectronico,
                                      HttpServletResponse response) throws IOException {

        System.out.println( "ok entre almenos supongo");

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=topicos.xlsx";
        response.setHeader(headerKey, headerValue);

        List<Object[]> resultados = topicoRepository.customQuery();

        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Tópicos");

        // encabezados
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Título del Tópico");
        headerRow.createCell(1).setCellValue("Mensaje del Tópico");
        headerRow.createCell(2).setCellValue("Fecha de Creación");
        headerRow.createCell(3).setCellValue("Estado del Tópico");
        headerRow.createCell(4).setCellValue("Nombre del Curso");
        headerRow.createCell(5).setCellValue("Categoría del Curso");
        headerRow.createCell(6).setCellValue("Mensaje de la Respuesta");
        headerRow.createCell(7).setCellValue("Fecha de Creación de la Respuesta");
        headerRow.createCell(8).setCellValue("Autor de la Respuesta");
        headerRow.createCell(9).setCellValue("Es Solución");

        // rellenar las filas con los datos
        int rowNum = 1;
        for (Object[] row : resultados) {
            Row dataRow = sheet.createRow(rowNum++);
            for (int i = 0; i < row.length; i++) {
                dataRow.createCell(i).setCellValue(row[i] != null ? row[i].toString() : "");
            }
        }

        workbook.write(response.getOutputStream());
        workbook.close();


    }

    //Todos los topicos
    @GetMapping
    public String inicio(@RequestParam(name = "correoElectronico", required = true) String correoElectronico,
                         @RequestParam(name = "token", required = true) String token,
                         Model model){


        model.addAttribute("token", token);
        correoElectronicoGlobal = correoElectronico;

        String nombreUsuario = usuarioRepository.findNombreUsuarioByCorreo(correoElectronico);

        nombreGlobal = nombreUsuario;


        List<Object[]> todosLosTopicos = topicoRepository.findAllTopicsWithDetails();

        model.addAttribute("topicos", todosLosTopicos);
        model.addAttribute("nombre", nombreGlobal);
        return "topicos";

    }


    @PostMapping("/nuevo")
    public String registrarTopico(@RequestParam(name = "titulo") String titulo,
                                  @RequestParam(name = "mensaje") String mensaje,
                                  @RequestParam(name = "nombreCurso") String nombreCurso,
                                  @RequestParam(name = "categoria") String categoria,
                                  @RequestParam(name = "correoElectronico") String correoElectronico,
                                  @RequestParam(name = "token") String token,
                                  Model model) {

        model.addAttribute("token", token);
        model.addAttribute("correoElectronico", correoElectronico);

        //Topico repetido
        Topico existingTopico = topicoRepository.findByTituloAndMensaje(titulo, mensaje);
        if (existingTopico != null) {
            return "redirect:/topicos?token=" + token + "&correoElectronico=" + correoElectronico;
        }


        //Curso repetido
        Curso curso = cursoRepository.findByNombreAndCategoria(nombreCurso, Categoria.valueOf(categoria));

        if (curso == null) {
            curso = new Curso();
            curso.setNombre(nombreCurso);
            curso.setCategoria(Categoria.valueOf(categoria));

            System.out.println("nombre: " + nombreCurso + " categoria: " + Categoria.valueOf(categoria));
            cursoRepository.save(curso);
        }


        // Crear el tópico
        Topico topico = new Topico();
        topico.setTitulo(titulo);
        topico.setMensaje(mensaje);
        topico.setFechaCreacion(LocalDateTime.now());
        topico.setStatus("active");
        topico.setCurso(curso);


        // Obtener el autor usando el correo electrónico
        Usuario autor = usuarioRepository.findUsuarioByCorreoElectronico(correoElectronicoGlobal);


        topico.setAutor(autor);

        topicoRepository.save(topico);

        List<Object[]> todosLosTopicos = topicoRepository.findAllTopicsWithDetails();


        // redirigir de vuelta a la página de tópicos
        model.addAttribute("topicos", todosLosTopicos);

        return "redirect:/topicos?token=" + token + "&correoElectronico=" + correoElectronico;
    }


    //Aqui empiezan los exlusivos de {id}



    //Obtener un topico especifico
    @GetMapping("/{id}")
    public ModelAndView detalleTopico(@PathVariable Long id,
                                      @RequestParam(name = "token", required = true) String token) {

        Optional<Topico> topicoOptional = topicoRepository.findById(id);

        if (topicoOptional.isPresent()) {
            //Obtener topico
            Topico topico = topicoOptional.get();

            // Obtener las respuestas del tópico
            List<Respuesta> respuestas = respuestaRepository.findByTopico(topico);



            // agregar topico al modelview
            ModelAndView modelAndView = new ModelAndView("topicoID");
            modelAndView.addObject("topico", topico);
            modelAndView.addObject("token", token);
            modelAndView.addObject("respuestas", respuestas);
            modelAndView.addObject("correoElectronico", correoElectronicoGlobal);

            return modelAndView;

        } else {
            return new ModelAndView("redirect:/topicos?token=" + token + "&correoElectronico=" + correoElectronicoGlobal);
        }
    }

    //Agregar comentario
    @PostMapping("topicos/{id}/respuestas")
    public String agregarRespuesta(@PathVariable Long id,
                                   @RequestParam(name = "token", required = true) String token,
                                   @RequestParam(name = "mensaje", required = true) String mensaje) {


        Optional<Topico> topicoOptional = topicoRepository.findById(id);


        if (topicoOptional.isPresent()) {
            Topico topico = topicoOptional.get();



            if ("active".equalsIgnoreCase(topico.getStatus())) {


                Usuario autor = usuarioRepository.findUsuarioByCorreoElectronico(correoElectronicoGlobal);
                if (autor != null) {
                    Respuesta respuesta = new Respuesta();
                    respuesta.setMensaje(mensaje);
                    respuesta.setFechaCreacion(LocalDateTime.now());
                    respuesta.setTopico(topico);
                    respuesta.setAutor(autor);
                    respuesta.setSolucion(false);

                    respuestaRepository.save(respuesta);

                    return "redirect:/topicos/" + id + "?token=" + token + "&correoElectronico=" + correoElectronicoGlobal;
                } else {
                    logger.info("no autor");

                    //No se encuentra autor
                }
            } else {
                logger.info("no activo");

                //No activo

            }
        } else {
            logger.info("no topico");

            //No hay topico
        }
        logger.info(correoElectronicoGlobal);

        return "redirect:/topicos/" + id + "?token=" + token + "&correoElectronico=" + correoElectronicoGlobal;


    }

    @PostMapping("/{topicoId}/respuestas/{respuestaId}/solucion")
    @ResponseBody
    public ResponseEntity<String> marcarComoSolucion(@PathVariable Long topicoId,
                                                     @PathVariable Long respuestaId,
                                                     @RequestParam String token,
                                                     @RequestParam String correoElectronico) {


        Optional<Topico> topicoOptional = topicoRepository.findById(topicoId);
        if (topicoOptional.isPresent()) {
            Topico topico = topicoOptional.get();



            if ("active".equalsIgnoreCase(topico.getStatus())) {
                Optional<Respuesta> respuestaOptional = respuestaRepository.findById(respuestaId);
                if (respuestaOptional.isPresent()) {

                    Respuesta respuesta = respuestaOptional.get();
                    respuesta.setSolucion(true);
                    respuestaRepository.save(respuesta);

                    topico.setStatus("inactive");
                    topicoRepository.save(topico);

                    return ResponseEntity.ok("Respuesta marcada como solución");
                } else {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Respuesta no encontrada");
                }
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("El tópico no está activo");
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Tópico no encontrado");
        }
    }

    //Borrar un topico especifico
    @DeleteMapping("/{id}")
    @Transactional
    public ModelAndView eliminarTopico(@PathVariable Long id,
                                       @RequestParam(name = "token", required = true) String token,
                                       @RequestParam(name = "correoElectronico", required = true) String correoElectronico) {


        Optional<Topico> topicoOptional = topicoRepository.findById(id);


        if (topicoOptional.isPresent()) {

            Topico topico = topicoOptional.get();

            //usuario coincide
            Usuario autor = usuarioRepository.findUsuarioByCorreoElectronico(correoElectronico);
            if (topico.getAutor().equals(autor)) {

                respuestaRepository.deleteByTopicoId(id);

                topicoRepository.delete(topico);
                return new ModelAndView("redirect:/topicos?token=" + token + "&correoElectronico=" + correoElectronico);
            } else { //Usuario no coincide

                return new ModelAndView("redirect:/topicos?token=" + token + "&correoElectronico=" + correoElectronico)
                        .addObject("error", "No autorizado");
            }
        } else {
            return new ModelAndView("redirect:/topicos?token=" + token + "&correoElectronico=" + correoElectronico)
                    .addObject("error", "No encontrado");
        }
    }

    @PutMapping("/{id}")
    public ModelAndView editarTopico(@PathVariable Long id,
                                     @RequestParam(name = "token", required = true) String token,
                                     @RequestParam(name = "correoElectronico", required = true) String correoElectronico,
                                     @RequestParam(name = "mensaje", required = true) String mensaje,
                                     @RequestParam(name = "status", required = true) String status) {

        Optional<Topico> topicoOptional = topicoRepository.findById(id);

        if (topicoOptional.isPresent()) {
            Topico topico = topicoOptional.get();

            Usuario autor = usuarioRepository.findUsuarioByCorreoElectronico(correoElectronico);
            if (topico.getAutor().equals(autor)) {

                String originalMensaje = topico.getMensaje();
                if (!originalMensaje.equals(mensaje)) {
                    mensaje = mensaje + " (editado)"; //Agregar el "editado" al final si es que se edito el mensaje
                }


                topico.setMensaje(mensaje);
                topico.setStatus(status);
                topicoRepository.save(topico);
                return new ModelAndView("redirect:/topicos?token=" + token + "&correoElectronico=" + correoElectronico);
            } else {
                return new ModelAndView("redirect:/topicos?token=" + token + "&correoElectronico=" + correoElectronico)
                        .addObject("error", "No autorizado");
            }
        } else {
            return new ModelAndView("redirect:/topicos?token=" + token + "&correoElectronico=" + correoElectronico)
                    .addObject("error", "Topico no encontrado");
        }
    }

}
