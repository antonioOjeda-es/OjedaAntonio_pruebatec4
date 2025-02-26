package com.hackaboss.app.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class HotelDTO {

    @JsonIgnore
    private Long id;

    @JsonProperty("hotelCode")
    private String codigoHotel;

    @JsonProperty("name")
    private String nombre;

    @JsonProperty("place")
    private String lugar;

    @JsonIgnore
    @JsonProperty("active")
    private boolean alta;

    //para evitar la recursividad, utilizo el JsonManagedReference para indicar que es la entidad principal
    @JsonManagedReference(value = "hotel-habitaciones")
    @JsonProperty("rooms")
    //@JsonIgnore
    private List<HabitacionDTO> habitacionesDto;

    public HotelDTO(String nombre, String lugar) {
        this.nombre = nombre;
        this.lugar = lugar;
    }
}
