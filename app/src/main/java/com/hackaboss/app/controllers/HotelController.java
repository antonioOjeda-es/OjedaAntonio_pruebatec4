package com.hackaboss.app.controllers;

import com.hackaboss.app.dtos.HotelDTO;
import com.hackaboss.app.services.HotelServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class HotelController {

    @Autowired
    HotelServiceInterface hotelService;

    // /agency/hotels
    @GetMapping("/hotels")
    public ResponseEntity<Object> listarHoteles(){

        Object respuesta = hotelService.listarHoteles();
        return ResponseEntity.ok(respuesta);
    }

    // /agency/hotels/new
    @PostMapping("/hotels/new")
    ResponseEntity<Object> crearHotel(@RequestBody HotelDTO hotelDTO){
        Object respuesta = hotelService.crearHotel(hotelDTO);
        return ResponseEntity.ok(respuesta);
    }

    // /agency/hotels/{id}
    @GetMapping("/hotels/{id}")
    ResponseEntity<Object> buscarHotel(@PathVariable("id") Long idBuscar){
        Object respuesta = hotelService.buscarHotel(idBuscar);
        return ResponseEntity.ok(respuesta);
    }

    // /agency/hotels/edit/{id}
    @PutMapping("/hotels/edit/{id}")
    ResponseEntity<Object> editarHotel(@RequestBody HotelDTO hotelDTO, @PathVariable("id") Long idHotel){

        Object respuesta = hotelService.editarHotel(hotelDTO, idHotel);
        return ResponseEntity.ok(respuesta);
    }

    // /agency/hotels/delete/{id}
    @DeleteMapping("/hotels/delete/{id}")
    ResponseEntity<Object> eliminarHotel(@PathVariable("id") Long idEliminar){
        Object respuesta = hotelService.eliminarHotel(idEliminar);
        return ResponseEntity.ok(respuesta);
    }
}
