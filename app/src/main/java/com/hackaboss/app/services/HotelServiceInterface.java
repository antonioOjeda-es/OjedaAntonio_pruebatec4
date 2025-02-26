package com.hackaboss.app.services;

import com.hackaboss.app.dtos.HotelDTO;

import java.util.List;


public interface HotelServiceInterface {

    //listar hoteles(tienen que estar en alta)
    // /agency/hotels
    Object listarHoteles();

    //buscar un hotel
    // /agency/hotels/{id}
    Object buscarHotel(Long idHotel);

    //crear hotel
    // /agency/hotels/new
    Object crearHotel(HotelDTO hotelDTO);

    // /agency/hotels/edit/{id}
    Object editarHotel (HotelDTO hotelDTO, Long idHotel);

    //eliminar hotel, en este caso
    // /agency/hotels/delete/{id}
    Object eliminarHotel(Long idHotel);

}
