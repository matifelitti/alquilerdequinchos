
package com.equipoC.alquilerQuinchos.repositorios;


import com.equipoC.alquilerQuinchos.entidades.Imagen;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ImagenRepositorio extends JpaRepository<Imagen, String>{

    public Optional<Imagen> findById(Long idImagen);
    
    
    
}
