package com.equipoC.alquilerQuinchos.entidades;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.ManyToOne;
import lombok.Data;

@Entity
@Data
public class Inmueble {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String nombre;
    
    private String ubicacion;

    private Double cochera;
    private Double parrilla;
    private Double pileta;
    private Double precioBase;
    private Double PrecioTotal;

    @OneToMany(mappedBy = "inmueble")
    private Set<Reserva> reservaEntidad = new HashSet<>();

    @OneToMany(mappedBy = "inmueble", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Imagen> imagenes = new ArrayList<>();
    
    @OneToMany(mappedBy = "inmueble")
    private Set<Comentarios> comentarios = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "propietario_id")
    private Usuario userProp;

    
}
