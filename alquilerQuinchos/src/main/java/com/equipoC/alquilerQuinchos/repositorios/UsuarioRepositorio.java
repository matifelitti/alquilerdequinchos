package com.equipoC.alquilerQuinchos.repositorios;

import com.equipoC.alquilerQuinchos.Enumeraciones.Rol;
import com.equipoC.alquilerQuinchos.entidades.Usuario;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepositorio extends JpaRepository<Usuario, String> {

    @Query("SELECT u FROM Usuario u WHERE u.id = :id")
    public Usuario buscarPorId(@Param("id") String id);

    @Query("SELECT u FROM Usuario u WHERE u.username = :username")
    public Usuario buscarPorUsername(@Param("username") String username);
    
    public List<Usuario> findAllByRol(Rol rol);



}
