package com.hackaboss.app.services;

import com.hackaboss.app.dtos.ReservaVueloDTO;
import com.hackaboss.app.dtos.VueloDTO;
import com.hackaboss.app.entities.Vuelo;

import java.time.LocalDate;
import java.util.List;

public interface VueloServiceInterface {
    //listar los vuelos
    // /agency/flights
    Object listarVuelos();

    //filtrar vuelos por
    // /agency/flights?dateFrom=dd/mm/aaaa&dateTo=dd/mm/aaaa&origin="ciudad1"&destination="ciudad2"
    Object filtrarVuelosActivosParametros (LocalDate dateFrom, LocalDate dateTo, String origin, String destination);

    //buscar un vuelo
    // /agency/flights/{id}
    Object buscarVuelo(Long idVuelo);

    //crear vuelo
    // /agency/flights/new
    Object crearVuelo(VueloDTO vueloDTO);

    //editar vuelos
    // /agency/flights/edit/{id}
    Object editarVuelo(VueloDTO vueloDTO, Long idVuelo);

    //eliminar vuelo
    // /agency/flights/delete/{id}
    Object eliminarVuelo(Long idVuelo);

     // /agency/flight-booking/new
    Object reservarVuelo (ReservaVueloDTO reservaVueloDTO);
}
