package com.equipoC.alquilerQuinchos.entidades;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Data
public class Comentarios {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;
    
    private String comentarios;
    
    @ManyToOne(fetch = FetchType.LAZY)
    private Inmueble inmueble;
}
