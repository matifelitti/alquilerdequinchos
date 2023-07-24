package com.equipoC.alquilerQuinchos.controladores;

import com.equipoC.alquilerQuinchos.Enumeraciones.Rol;
import com.equipoC.alquilerQuinchos.entidades.Imagen;
import com.equipoC.alquilerQuinchos.entidades.Inmueble;
import com.equipoC.alquilerQuinchos.entidades.Reserva;
import com.equipoC.alquilerQuinchos.entidades.Usuario;
import com.equipoC.alquilerQuinchos.excepciones.MiException;
import com.equipoC.alquilerQuinchos.servicios.ImagenServicio;
import com.equipoC.alquilerQuinchos.servicios.InmuebleServicio;
import com.equipoC.alquilerQuinchos.servicios.ReservaServicio;
import com.equipoC.alquilerQuinchos.servicios.UsuarioServicio;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/")
public class PortalControlador {

    @Autowired
    private UsuarioServicio usuarioServicio;

    @Autowired
    private ReservaServicio reservaServicio;

    @Autowired
    private InmuebleServicio inmuebleServicio;

    @Autowired
    private ImagenServicio imagenServicio;

    @GetMapping("/")
    public String index() {

        return "index.html";
    }

    @GetMapping("/inicio")
    public String inicio(HttpSession session, ModelMap modelo) {
        Usuario usuario = (Usuario) session.getAttribute("usuariosession");
        if (usuario != null) {
            modelo.addAttribute("usuario", usuario);
            List<Inmueble> inmueblesUsuario = inmuebleServicio.listarInmueblesUsuario(usuario);
            modelo.addAttribute("inmueblesUsuario", inmueblesUsuario);
        }

        List<Reserva> misReservas = reservaServicio.listarReservasUsuario(usuario);
        modelo.addAttribute("misReservas", misReservas);

        List<Inmueble> todosLosInmuebles = inmuebleServicio.listarTodosLosInmuebles();
        modelo.addAttribute("todosLosInmuebles", todosLosInmuebles);

        boolean esPropietario = usuario != null && usuario.getRol() == Rol.PROPIETARIO;
        modelo.addAttribute("esPropietario", esPropietario);

        boolean esCliente = usuario != null && usuario.getRol() == Rol.CLIENTE;
        modelo.addAttribute("esCliente", esCliente);

        boolean esAdmin = usuario != null && usuario.getRol() == Rol.ADMIN;
        modelo.addAttribute("esAdmin", esAdmin);

        if (esAdmin) {
            List<Usuario> usuarios = usuarioServicio.listarUsuarios();
            modelo.addAttribute("usuarios", usuarios);
        }

        return "inicio.html";
    }

    @GetMapping("/registrar")
    public String registrar() {
        return "registro.html";
    }

    @PostMapping("/registro")
    public String registro(@RequestParam String username, @RequestParam String password, @RequestParam String nombre, @RequestParam String email, @RequestParam String telefono, @RequestParam("rol") String rol, MultipartFile archivo, ModelMap modelo) throws MiException {

        try {

            usuarioServicio.crearUsuario(username, password, nombre, email, telefono, rol, archivo);

            return "index.html";

        } catch (MiException ex) {
            modelo.put("error", ex.getMessage());
            return "registro.html";
        }

    }

    @GetMapping("/login")
    public String login(@RequestParam(required = false) String error, ModelMap modelo) {

        if (error != null) {
            modelo.put("error", "Usuario o Contraseña invalidos!");
        }

        return "login.html";
    }

    @GetMapping("/perfil/{id}")
    public String perfil(@PathVariable String id, ModelMap modelo, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuariosession");
        modelo.addAttribute("usuario", usuario);
        return "usuario_modificar.html";
    }

    @PostMapping("/perfil/{id}")
    public String actualizar(@PathVariable String id, String username, String password, String nombre, String email,
            String telefono, String rol, MultipartFile archivo, HttpSession session, ModelMap modelo) {

        try {

            Usuario usuarioSession = (Usuario) session.getAttribute("usuariosession");
            usuarioServicio.modificarUsuario(usuarioSession.getId(), username, password, nombre, email, telefono, rol, archivo);

            usuarioSession = usuarioServicio.getOne(usuarioSession.getId());
            session.setAttribute("usuariosession", usuarioSession);

            return "inicio.html";
        } catch (MiException ex) {

            modelo.put("error", ex.getMessage());
            modelo.put("nombre", username);

            return "usuario_modificar.html";
        }
    }

    @PostMapping("/perfil/eliminar")
    public String eliminarUsuario(HttpSession session) {
        Usuario usuarioSession = (Usuario) session.getAttribute("usuariosession");
        usuarioServicio.borrarUsuarioPorId(usuarioSession.getId());
        session.invalidate();
        return "index.html";
    }

    // METODOS PARA PROPIETARIOS
    @GetMapping("/crear_inmueble")
    public String crearInmuebleHTML(ModelMap modelo) {
        modelo.addAttribute("titulo", "Crear Inmueble");
        return "crear_inmueble.html";
    }

    @PostMapping("/crear_inmueble")
    public String crearInmueble(@RequestParam String nombre, @RequestParam String ubicacion, @RequestParam Double cochera, @RequestParam Double parrilla,
            @RequestParam Double pileta, @RequestParam Double precioBase, @RequestParam("archivosImagenes") List<MultipartFile> archivosImagenesList,
            ModelMap modelo, HttpSession session) {

        try {
            List<Imagen> imagenes = new ArrayList<>();

            for (MultipartFile archivoImagen : archivosImagenesList) {
                Imagen imagen = imagenServicio.guardar(archivoImagen);
                imagenes.add(imagen);
            }

            Usuario usuarioPropietario = (Usuario) session.getAttribute("usuariosession");

            inmuebleServicio.crearInmueble(nombre, ubicacion, cochera, parrilla, pileta, precioBase, precioBase, imagenes, usuarioPropietario);

            modelo.addAttribute("mensaje", "Inmueble creado exitosamente.");

            return "redirect:/inicio";
        } catch (MiException ex) {
            modelo.addAttribute("error", "Error al crear el inmueble: " + ex.getMessage());
        }

        return "crear_inmueble.html";
    }

    @GetMapping("/modificar_inmueble/{id}")
    public String mostrarFormularioModificarInmueble(@PathVariable Long id, ModelMap modelo) {
        Inmueble inmueble = inmuebleServicio.getOne(id);
        modelo.addAttribute("inmueble", inmueble);
        return "inmueble_modificar.html";
    }

    @PostMapping("/modificar_inmueble/{id}")
    public String actualizarInmueble(@PathVariable Long id, @RequestParam String nombre, @RequestParam String ubicacion,
            @RequestParam Double cochera, @RequestParam Double parrilla, @RequestParam Double pileta,
            @RequestParam Double precioBase, @RequestParam("archivosImagenes") List<MultipartFile> archivosImagenes, ModelMap modelo) {
        try {
            Inmueble inmueble = inmuebleServicio.modificarInmueble(id, nombre, ubicacion, cochera, parrilla, pileta, precioBase, precioBase, archivosImagenes);
            modelo.addAttribute("inmueble", inmueble);
            return "redirect:/inicio";
        } catch (MiException ex) {
            return "redirect:/inicio";
        }
    }

    @PostMapping("/eliminar_inmueble/{id}")
    public String eliminarInmueble(@PathVariable Long id) throws MiException {
        inmuebleServicio.eliminarInmueble(id);
        return "redirect:/inicio";
    }

    // METODOS PARA CLIENTES
    @GetMapping("/crear_reserva/{id}")
    public String crearReserva(@PathVariable Long id, ModelMap model, HttpSession session) {
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuariosession");
        model.addAttribute("idInmueble", id);
        model.addAttribute("usuarioLogueado", usuarioLogueado);
        return "crear_reserva.html";
    }

    @PostMapping("/guardar_reserva")
    public String guardarReserva(@ModelAttribute("idInmueble") Long idInmueble,
            @ModelAttribute("fechaAlta") @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaAlta,
            @ModelAttribute("fechaBaja") @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaBaja,
            HttpSession session) throws MiException {
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuariosession");

        try {
            reservaServicio.crearReserva(fechaAlta, fechaBaja, idInmueble, usuarioLogueado.getId());
        } catch (MiException e) {
            return "redirect:/inicio";
        }
        return "redirect:/inicio";
    }

    @GetMapping("/mis_reservas")
    public String misReservas(ModelMap model, HttpSession session) {
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuariosession");
        List<Reserva> misReservas = reservaServicio.listarReservasUsuario(usuarioLogueado);
        model.addAttribute("misReservas", misReservas);
        return "mis_reservas.html";
    }

    @GetMapping("/modificar_reserva/{id}")
    public String mostrarFormularioModificarReserva(@PathVariable Long id, ModelMap modelo) {
        Reserva reserva = reservaServicio.getOne(id);
        modelo.addAttribute("reserva", reserva);
        return "reserva_modificar.html";
    }

    @PostMapping("/modificar_reserva/{id}")
    public String actualizarReserva(@PathVariable Long id,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaAlta,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaBaja,
            ModelMap modelo) {

        try {
            reservaServicio.modificarReserva(id, fechaAlta, fechaBaja);
            return "redirect:/inicio";
        } catch (MiException ex) {
            return "redirect:/inicio";
        }
    }

    @PostMapping("/eliminar_reserva/{id}")
    public String eliminarReserva(@PathVariable Long id) throws MiException {
        reservaServicio.eliminarReserva(id);
        return "redirect:/inicio";
    }

    //METODOS PARA ADMIN
    public String mostrarFormularioEliminarUsuarios(ModelMap modelo) {
        List<Usuario> usuarios = usuarioServicio.listarUsuarios();
        modelo.addAttribute("usuarios", usuarios);
        return "inicio.html";
    }

    @PostMapping("/eliminar_usuario/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String eliminarUsuario(@PathVariable String id) {
        reservaServicio.eliminarReservasPorUsuarioId(id);

        inmuebleServicio.eliminarInmueblesPorPropietarioId(id);

        usuarioServicio.borrarUsuarioPorId(id);

        return "redirect:/inicio";
    }

}
