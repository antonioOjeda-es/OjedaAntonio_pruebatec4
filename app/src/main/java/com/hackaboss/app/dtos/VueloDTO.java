package com.hackaboss.app.dtos;

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
public class VueloDTO {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("flyNumber")
    private String numeroVuelo;

    @JsonProperty("origin")
    private String origen;

    @JsonProperty("destination")
    private String destino;

    @JsonProperty("seatType")
    private String tipoAsiento;

    @JsonProperty("price")
    private Double precio;

    @JsonProperty("date")
    private LocalDate fecha;

    @JsonIgnore
    private boolean alta;

    @JsonManagedReference(value = "vuelo-usuarios")
    @JsonProperty("passengers")
    private List<UsuarioDTO> usuarios;
}
