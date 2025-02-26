package com.hackaboss.app.services;

import com.hackaboss.app.dtos.HabitacionDTO;
import com.hackaboss.app.dtos.ReservaHaitacionDTO;

import java.time.LocalDate;

public interface HabitacionServiceInterface {

    //listar con parámetros
    //  /agency/rooms?dateFrom=dd/mm/aaaa&dateTo=dd/mm/aaaa&destination="nombre_destino"
    Object listarDisponibleParametro(LocalDate dateFrom, LocalDate dateTo, String nombre_destino);

    //reserva de habitación
    // /agency/room-booking/new
    Object reservarHabitacion(ReservaHaitacionDTO reservaHaitacionDTO);

    //endpoint extra por mi parte
    // /agency/room/new
    Object crearHabitaciones (HabitacionDTO habitacionDTO, Long id);
}
