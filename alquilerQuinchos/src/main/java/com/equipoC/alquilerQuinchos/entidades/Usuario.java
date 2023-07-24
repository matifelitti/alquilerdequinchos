package com.equipoC.alquilerQuinchos.entidades;

import com.equipoC.alquilerQuinchos.Enumeraciones.Rol;
import java.util.Date;
import lombok.Data;
import javax.persistence.*;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Data
public class Usuario {
    
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;
    
    private String username;
    private String password;
    private String nombre;
    private String email;
    private String telefono;
    
    @Temporal(TemporalType.DATE)
    private Date alta;
    
    @OneToOne
    private Imagen imagenUsuario;
    
    @Enumerated(EnumType.STRING)
    private Rol rol;

}