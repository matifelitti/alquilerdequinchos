package com.equipoC.alquilerQuinchos.servicios;

import com.equipoC.alquilerQuinchos.entidades.Imagen;
import com.equipoC.alquilerQuinchos.entidades.Inmueble;
import com.equipoC.alquilerQuinchos.entidades.Usuario;
import com.equipoC.alquilerQuinchos.excepciones.MiException;
import com.equipoC.alquilerQuinchos.repositorios.InmuebleRepositorio;
import com.equipoC.alquilerQuinchos.repositorios.UsuarioRepositorio;
import java.util.ArrayList;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
public class InmuebleServicio {

    @Autowired
    private InmuebleRepositorio inmuebleRepositorio;

    @Autowired
    private ImagenServicio imagenServicio;

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    @Transactional
    public void crearInmueble(String nombre, String ubicacion, Double cochera, Double parrilla, Double pileta, Double precioBase, Double precioTotal, List<Imagen> imagenes, Usuario usuarioPropietario) throws MiException {
        validarInmueble(nombre, ubicacion, cochera, parrilla, pileta, precioBase, precioTotal);

        Inmueble inmueble = new Inmueble();
        inmueble.setNombre(nombre);
        inmueble.setUbicacion(ubicacion);
        inmueble.setCochera(cochera);
        inmueble.setParrilla(parrilla);
        inmueble.setPileta(pileta);
        inmueble.setPrecioBase(precioBase);

        double precioTotalCalculado = precioBase + (cochera != null ? cochera : 0) + (parrilla != null ? parrilla : 0) + (pileta != null ? pileta : 0);
        inmueble.setPrecioTotal(precioTotalCalculado);

        inmueble.setUserProp(usuarioPropietario);

        inmueble.setImagenes(imagenes);

        inmuebleRepositorio.save(inmueble);
    }

    @Transactional
    public Inmueble modificarInmueble(Long id, String nombre, String ubicacion, Double cochera, Double parrilla, Double pileta,
            Double precioBase, Double precioTotal, List<MultipartFile> archivosImagenes) throws MiException {
        validarInmueble(nombre, ubicacion, cochera, parrilla, pileta, precioBase, precioTotal);

        Inmueble inmueble = inmuebleRepositorio.findById(id).orElse(null);

        if (inmueble == null) {
            throw new MiException("El inmueble no se encuentra.");
        }

        inmueble.setNombre(nombre);
        inmueble.setUbicacion(ubicacion);
        inmueble.setCochera(cochera);
        inmueble.setParrilla(parrilla);
        inmueble.setPileta(pileta);
        inmueble.setPrecioBase(precioBase);

        double precioTotalCalculado = precioBase + (cochera != null ? cochera : 0) + (parrilla != null ? parrilla : 0) + (pileta != null ? pileta : 0);
        inmueble.setPrecioTotal(precioTotalCalculado);

        List<Imagen> nuevasImagenes = new ArrayList<>();
        for (MultipartFile archivoImagen : archivosImagenes) {
            Imagen imagen = imagenServicio.guardar(archivoImagen);
            nuevasImagenes.add(imagen);
        }
        inmueble.getImagenes().addAll(nuevasImagenes);

        return inmuebleRepositorio.save(inmueble);
    }

    @Transactional
    public void eliminarInmueble(Long id) throws MiException {
        if (id == null) {
            throw new MiException("El inmueble no se encuentra.");
        }

        inmuebleRepositorio.deleteById(id);
    }

    @Transactional
    public void eliminarInmueblesPorPropietarioId(String propietarioId) {
        Usuario propietario = usuarioRepositorio.getOne(propietarioId);
        List<Inmueble> inmuebles = inmuebleRepositorio.findByUserProp(propietario);
        for (Inmueble inmueble : inmuebles) {
            inmuebleRepositorio.delete(inmueble);
        }
    }

    public Inmueble getOne(Long id) {
        return inmuebleRepositorio.getOne(id);
    }

    public List<Inmueble> listarInmueblesUsuario(Usuario usuario) {
        return inmuebleRepositorio.findByUserProp(usuario);
    }

    @Transactional
    public List<Inmueble> listarTodosLosInmuebles() {
        return inmuebleRepositorio.findAll();
    }

    public boolean validarInmueble(String nombre, String ubicacion, Double cochera, Double parrilla, Double pileta, Double precioBase, Double precioTotal) throws MiException {
        if (nombre.isEmpty() || nombre == null) {
            throw new MiException("El nombre de usuario no puede ser nulo o estar vacío");
        }

        if (ubicacion.isEmpty() || ubicacion == null) {
            throw new MiException("El nombre de usuario no puede ser nulo o estar vacío");
        }

        if (cochera == null || cochera < 0) {
            throw new MiException("No tiene cochera.");
        }
        if (parrilla == null || parrilla < 0) {
            throw new MiException("No tiene parrilla.");
        }
        if (pileta == null || pileta < 0) {
            throw new MiException("No tiene pileta.");
        }
        if (precioBase == null || precioBase < 0) {
            throw new MiException("El precio base no es válido.");
        }
        if (precioTotal == null || precioTotal < 0) {
            throw new MiException("El precio total no es válido.");
        }
        return true;
    }

}
