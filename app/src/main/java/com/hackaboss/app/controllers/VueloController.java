package com.hackaboss.app.controllers;

import com.hackaboss.app.dtos.ReservaVueloDTO;
import com.hackaboss.app.dtos.VueloDTO;
import com.hackaboss.app.services.VueloServiceInterface;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;

@RestController
public class VueloController {

    @Autowired
    VueloServiceInterface vueloService;

    // /agency/flights/new
    @PostMapping("/flights/new")
    public ResponseEntity<Object> agregarVuelo(@RequestBody VueloDTO vueloDTO){

        Object respuesta = vueloService.crearVuelo(vueloDTO);

        return ResponseEntity.ok(respuesta);
    }


    //  /agency/flights/{id}
    @GetMapping("/flights/{id}")
    public ResponseEntity<Object> buscarVuelo(@PathVariable("id") Long idVuelo){

        Object respuesta = vueloService.buscarVuelo(idVuelo);

        return ResponseEntity.ok(respuesta);
    }

    // /agency/flights/delete/{id}
    @DeleteMapping("/flights/delete/{id}")
    public ResponseEntity<Object> eliminarVuelo(@PathVariable("id") Long idVuelo){

        Object respuesta = vueloService.eliminarVuelo(idVuelo);

        return ResponseEntity.ok(respuesta);
    }

    //  /agency/flights/edit/{id}
    @PutMapping("/flights/edit/{id}")
    public ResponseEntity<Object> editarVuelo(@RequestBody VueloDTO vueloDTO, @PathVariable("id") Long idVuelo){

        Object respuesta = vueloService.editarVuelo(vueloDTO, idVuelo);

        return ResponseEntity.ok(respuesta);
    }

    // /agency/flights?dateFrom=dd/mm/aaaa&dateTo=dd/mm/aaaa&origin="ciudad1"&destination="ciudad2"
    @GetMapping("/flights")
    public ResponseEntity<Object> listarPorParametros(@RequestParam(required = false) @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate dateFrom,
                                              @RequestParam(required = false) @DateTimeFormat(pattern = "dd/MM/yyyy")LocalDate dateTo,
                                              @RequestParam(required = false)String origin,
                                              @RequestParam(required = false)String destination){
        Object respuesta;

        if(dateFrom == null && dateTo == null && origin == null && destination == null){
            respuesta = vueloService.listarVuelos();
        }else {

            respuesta = vueloService.filtrarVuelosActivosParametros(dateFrom, dateTo, origin, destination);
        }
        return ResponseEntity.ok(respuesta);

    }
    //  /agency/room-booking/new
    @PostMapping("/flight-booking/new")
    public ResponseEntity<Object> reservarVuelo(@RequestBody ReservaVueloDTO reservaVueloDTO){

        Object respuesta = vueloService.reservarVuelo(reservaVueloDTO);

        return ResponseEntity.ok(respuesta);
    }

}
