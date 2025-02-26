package com.hackaboss.app.dtos;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hackaboss.app.entities.Habitacion;
import com.hackaboss.app.entities.Vuelo;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioDTO {

    @JsonIgnore
    private Long id;

    @JsonProperty("name")
    private String nombre;

    @JsonProperty("surname")
    private String apellido;

    @JsonIgnore
    private HabitacionDTO habitacion;

    //@JsonIgnore
    @JsonBackReference(value = "vuelo-usuarios")
    private VueloDTO vuelo;
}
