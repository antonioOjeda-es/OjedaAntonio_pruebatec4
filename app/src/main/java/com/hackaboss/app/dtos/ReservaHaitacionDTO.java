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
public class ReservaHaitacionDTO {

    @JsonProperty("idRoom")
    private Long idRoom;

    @JsonProperty("dateFrom")
    private LocalDate dateFrom;

    @JsonProperty("dateTo")
    private LocalDate dateto;

    @JsonProperty("nights")
    private Long noches;

    @JsonProperty("place")
    private String lugar;

    @JsonProperty("hotelCode")
    private String codigoHotel;

    @JsonProperty("peopleQ")
    private Integer peopleQ;

    @JsonProperty("roomType")
    private String roomType;

    @JsonProperty("passengers")
    private List<UsuarioDTO> hosts;

}
