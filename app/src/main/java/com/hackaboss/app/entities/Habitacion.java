package com.hackaboss.app.entities;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Habitacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String tipoHabitacion;

    private Double precioNoche;

    private LocalDate disponibleDesde;

    private LocalDate disponibleHasta;

    private boolean reservado;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_hotel")
    private Hotel hotel;

    //hacemos la referencia y referencio la clase principal
    @JsonManagedReference
    @OneToMany(mappedBy = "habitacion", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Usuario> usuarios;

    public <E> Habitacion(long l, String doble, double v, LocalDate now, LocalDate localDate, boolean b, Hotel hotel, List<E> es) {

    }
}
