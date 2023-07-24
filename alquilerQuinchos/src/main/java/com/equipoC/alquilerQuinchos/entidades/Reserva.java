package com.equipoC.alquilerQuinchos.entidades;

import javax.persistence.*;
import java.util.Date;

import lombok.Data;

@Entity
@Data
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Temporal(TemporalType.DATE)
    private Date fechaAlta;
    
    @Temporal(TemporalType.DATE)
    private Date fechaBaja;
    
    
    @ManyToOne
    private Inmueble inmueble;
    
    @OneToOne
    private Usuario usuario;
    
    
    
}
