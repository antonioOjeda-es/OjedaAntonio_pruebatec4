package com.hackaboss.app.dtos;

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
public class ReservaVueloDTO {

    @JsonProperty("idFly")
    Long idVuelo;

    @JsonProperty("date")
    LocalDate dia;

    @JsonProperty("origin")
    private String origen;

    @JsonProperty("destination")
    private String destino;

    @JsonProperty("flightCode")
    private String codigoVuelo;

    @JsonProperty("peopleQ")
    private Integer cantiDadUsuarios;

    @JsonProperty("seatType")
    private String tipoAsiento;

    @JsonProperty("passengers")
    List<UsuarioDTO> listaUsuarios;
}

