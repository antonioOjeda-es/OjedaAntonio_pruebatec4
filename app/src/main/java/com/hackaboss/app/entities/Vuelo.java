package com.hackaboss.app.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Setter
public class Vuelo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String numeroVuelo;
    private String origen;
    private String destino;
    private String tipoAsiento;
    private double precio;
    private LocalDate fecha;
    private boolean alta;

    //hacemos la referencia y referencio la clase principal
    @JsonIgnore
    @JsonManagedReference
    @OneToMany(mappedBy = "vuelo", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Usuario> usuarios;


}
