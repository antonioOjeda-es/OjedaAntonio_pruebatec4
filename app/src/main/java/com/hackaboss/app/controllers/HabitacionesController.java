package com.hackaboss.app.controllers;

import com.hackaboss.app.dtos.HabitacionDTO;
import com.hackaboss.app.dtos.ReservaHaitacionDTO;
import com.hackaboss.app.services.HabitacionServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
public class HabitacionesController {

    @Autowired
    HabitacionServiceInterface habitacionService;

    //crear habitación(endpoint extra)
    @PostMapping("/rooms/new/{id}")
    public ResponseEntity<Object> agregarHabitacion(@RequestBody HabitacionDTO habitacionDTO, @PathVariable("id") Long idHotel){

        Object respuesta = habitacionService.crearHabitaciones(habitacionDTO, idHotel);
        return ResponseEntity.ok(respuesta);
    }

    //  /agency/rooms?dateFrom=dd/mm/aaaa&dateTo=dd/mm/aaaa&destination="nombre_destino"
    //En este caso las variables van a ser opcionales por lo que los parámetros no van ser requeridos obligatoriamente
    @GetMapping("/rooms")
    //uso required false para que haga la búsqueda dependiendo de los datos con los que disponga
    //además hago formato a la fecha que viene por la url para que pueda ser reconocido como LocalDate
    public ResponseEntity<Object> listarHabitacionesFiltradas(@RequestParam(required = false) @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate dateFrom,
                                                      @RequestParam(required = false) @DateTimeFormat(pattern = "dd/MM/yyyy")LocalDate dateTo,
                                                      @RequestParam(required = false)String destination){
        Object respuesta = habitacionService.listarDisponibleParametro(dateFrom, dateTo,  destination);
        return ResponseEntity.ok(respuesta);
    }

    //reservar habitaciones
    // /agency/room-booking/new
    @PostMapping("/room-booking/new")
    public ResponseEntity<Object> reservarHabitacion(@RequestBody  ReservaHaitacionDTO reservaHaitacionDTO){

        Object respuesta = habitacionService.reservarHabitacion(reservaHaitacionDTO);
        return ResponseEntity.ok(respuesta);
    }

}
