package com.hackaboss.app.dtos;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class HabitacionDTO {

    @JsonIgnore
    private Long id;

    @JsonProperty("roomType")
    private String tipoHabitacion;

    @JsonProperty("price")
    private Double precioNoche;

    @JsonProperty("disponibilityDateFrom")
    private LocalDate disponibleDesde;

    @JsonProperty("disponibilityDateTo")
    private LocalDate disponibleHasta;

    //@JsonProperty("isBocked")
    private boolean reservado;

    //para evitar la recursividad, utilizo el backreference para indicar que es la entidad secundaria
    @JsonBackReference(value = "hotel-habitaciones")
    @JsonProperty("hotel")
    private HotelDTO hotel;

    //este valor no lo necesito para mostrar
    @JsonManagedReference(value = "habitacion-usuarios")
    @JsonProperty("hosts")
    @JsonIgnore
    private List<UsuarioDTO> usuarios;

    @JsonProperty("Hotel")
    private String nombreHotel;
}