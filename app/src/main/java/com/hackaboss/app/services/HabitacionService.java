package com.hackaboss.app.services;

import com.hackaboss.app.dtos.*;
import com.hackaboss.app.entities.Habitacion;
import com.hackaboss.app.entities.Hotel;
import com.hackaboss.app.entities.Usuario;
import com.hackaboss.app.entities.Vuelo;
import com.hackaboss.app.repositories.HabitacionRepositoryInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class HabitacionService implements HabitacionServiceInterface {

    @Autowired
    HabitacionRepositoryInterface habitacionRepository;


    @Override
    public Object listarDisponibleParametro(LocalDate dateFrom, LocalDate dateTo, String nombre_destino) {
        //(no hago filtro de parámetros de entrada porque la introducción de datos es flexible)

        // obtener todas las habitaciones
        List<Habitacion> listaHabitacionesFiltrada = habitacionRepository.findAll();

        // Filtro inicial: se filtran los datos para que solo queden los activos y no reservados
        listaHabitacionesFiltrada = listaHabitacionesFiltrada.stream()
                .filter(h -> h.getHotel().isAlta() && !h.isReservado())
                .toList();

        //filtro por fecha de inicio (dateFrom)
        if (dateFrom != null) {
            listaHabitacionesFiltrada = listaHabitacionesFiltrada.stream()
                    .filter(h -> h.getDisponibleDesde().isBefore(dateFrom) ||
                            h.getDisponibleDesde().isEqual(dateFrom)).toList();
        }

        //filtro por fecha de fin (dateTo)
        if (dateTo != null) {
            listaHabitacionesFiltrada = listaHabitacionesFiltrada.stream()
                    .filter(h -> h.getDisponibleHasta().isAfter(dateTo) ||
                            h.getDisponibleHasta().isEqual(dateTo))
                    .toList();
        }

        //filtro por destino (nombre_destino)
        if (nombre_destino != null) {
            listaHabitacionesFiltrada = listaHabitacionesFiltrada.stream()
                    .filter(h -> h.getHotel().getLugar().equalsIgnoreCase(nombre_destino))
                    .toList();
        }

        //verifico si la lista está vacía después de los filtros
        if (listaHabitacionesFiltrada.isEmpty()) {
            return new ExceptionDTO(404,
                    "No se encontraron habitaciones disponibles con los criterios especificados.");
        }

        //convierto la lista de entidades a DTOs y devolverla
        return listaHabitacionesFiltrada.stream()
                .map(this::habitacionEntityToDto)
                .collect(Collectors.toList());
    }

    @Override
    public Object reservarHabitacion(ReservaHaitacionDTO reservaHaitacionDTO) {

        //1º valorar que todos los datos estén introducidos, sino, lanzo una excepción
        if (reservaHaitacionDTO.getIdRoom() == null || reservaHaitacionDTO.getPeopleQ() == null ||
                reservaHaitacionDTO.getDateFrom() == null || reservaHaitacionDTO.getDateto() == null ||
                reservaHaitacionDTO.getRoomType() == null || reservaHaitacionDTO.getHosts() == null) {

            return new ExceptionDTO(404,
                    "No se han introducidos todos los parámetros, son obligatorios.");
        }

        //2º verifico que la cantidad de personas no superen la lista de usuarios que se quieren inscribir
        if (reservaHaitacionDTO.getHosts().size() > reservaHaitacionDTO.getPeopleQ()) {
            return new ExceptionDTO(404, "El máximo de usuarios en la habitación es de: "
                    + reservaHaitacionDTO.getPeopleQ());
        }

        //3º verifico si existe la habitación
        Optional<Habitacion> buscarHabitacion = habitacionRepository.findById(reservaHaitacionDTO.getIdRoom());

        Habitacion habitacionEncontrada = buscarHabitacion.orElse(null);

        if(habitacionEncontrada == null){
            return new ExceptionDTO(404, "No se encuentra la habiación con el id: " +
                    reservaHaitacionDTO.getIdRoom() );
        }

        //voy a validar que los roomType sean iguales en la base de datos y el introducido por el usuario
        if(!habitacionEncontrada.getTipoHabitacion().equalsIgnoreCase(reservaHaitacionDTO.getRoomType())){
            return new ExceptionDTO(404, "El tipo de habitación no coincide, has puesto: " +
                    reservaHaitacionDTO.getRoomType() );
        }

        //4º ver si la habitación encontrada está reservada(idénticas características)
        if(habitacionEncontrada.isReservado()){
            return new ExceptionDTO(404, "La habiación con el id: " +
                    reservaHaitacionDTO.getIdRoom() +
                    ", ya se encuentra reservada con anterioridad, intente otra habitación u hotel");
        }

        //5º voy a verificar que las fechas de la reserva sean iguales o estén dentro del rango de fechas de la habitación
        if(reservaHaitacionDTO.getDateFrom().isBefore(habitacionEncontrada.getDisponibleDesde())){
            return new ExceptionDTO(404,
                    "La fecha de reserva de inicio es anterior al inicio de disponibilidad. " +
                            "Fecha inicio introducida: " +
                            reservaHaitacionDTO.getDateFrom() + " Fecha de inicio de disponibilidad: " +
                            habitacionEncontrada.getDisponibleDesde());
        }

        if(reservaHaitacionDTO.getDateto().isAfter(habitacionEncontrada.getDisponibleHasta())){
            return new ExceptionDTO(404,
                    "La fecha de reserva de salida es posterior al inicio de disponibilidad: " +
                            "Fecha salida introducida: " +
                            reservaHaitacionDTO.getDateto() + " Fecha de limite de disponibilidad: " +
                            habitacionEncontrada.getDisponibleHasta());
        }

        //6º si hay días en los que no se reserve, voy a generar nuevas reservas con los días sobrantes
        if(reservaHaitacionDTO.getDateFrom().isAfter(habitacionEncontrada.getDisponibleDesde())){

            Habitacion habitacionGuardar = new Habitacion();

            //creo una nueva habitación a insertar,
            // hago set del id para nuevo registro en la base de datos y cambio la fecha de fin
            habitacionGuardar.setId(null);
            habitacionGuardar.setTipoHabitacion(habitacionEncontrada.getTipoHabitacion());
            habitacionGuardar.setPrecioNoche(habitacionEncontrada.getPrecioNoche());
            habitacionGuardar.setDisponibleDesde(habitacionEncontrada.getDisponibleDesde());
            habitacionGuardar.setDisponibleHasta(reservaHaitacionDTO.getDateFrom());
            habitacionGuardar.setReservado(false);
            habitacionGuardar.setHotel(habitacionEncontrada.getHotel());
            //guardo el objeto creado
            habitacionRepository.save(habitacionGuardar);
        }

        if(reservaHaitacionDTO.getDateto().isBefore(habitacionEncontrada.getDisponibleHasta())){

            Habitacion habitacionGuardar = new Habitacion();
            //set del id para nuevo registro en la base de datos y cambio la fecha de fin
            //el resto de valores va a ser igual al de la base de datos
            habitacionGuardar.setId(null);
            habitacionGuardar.setTipoHabitacion(habitacionEncontrada.getTipoHabitacion());
            habitacionGuardar.setPrecioNoche(habitacionEncontrada.getPrecioNoche());
            habitacionGuardar.setDisponibleDesde(reservaHaitacionDTO.getDateto());
            habitacionGuardar.setDisponibleHasta(habitacionEncontrada.getDisponibleHasta());
            habitacionGuardar.setReservado(false);
            habitacionGuardar.setHotel(habitacionEncontrada.getHotel());
            habitacionRepository.save(habitacionGuardar);
        }

        //7º preparar habitación para guardar

        //ahora puedo cambiar los valores de la habitación encontrada y actualizarla a estado reservado
        habitacionEncontrada.setReservado(true);

        // no hay que hacer optional porque ya valoré que la lista sea nula en la primera validación
        List<Usuario> usuariosInsertar = reservaHaitacionDTO.getHosts().stream()
                        .map(HotelService::usuarioDtoToEntity).toList();

        //8º inserto la habitación con id de la habitación
        usuariosInsertar.forEach(u ->{
            u.setHabitacion(new Habitacion());
            u.getHabitacion().setId(habitacionEncontrada.getId());

        });

        //voy a cambiar la fecha de inicio y fin a la reserva,
        //ya se han creado las reservas con fechas excedentes
        // (se han creado nuevas habitaciones tanto en fecha anterior o posterior o ambas, según el caso)
        habitacionEncontrada.setDisponibleDesde(reservaHaitacionDTO.getDateFrom());
        habitacionEncontrada.setDisponibleHasta(reservaHaitacionDTO.getDateto());

        //9º guardo habitación

        //inserto los usuarios
        habitacionEncontrada.getUsuarios().addAll(usuariosInsertar);
        Habitacion habitacionGuardada = habitacionRepository.save(habitacionEncontrada);

        //10º creo objeto para la vuelta por un DTO
        //instancio y hago set de los valores para para hacer la respuesta con ReservaHotelDTO
        // no lo pongo en un método porque sólo lo hago una vez
        ReservaHaitacionDTO habitacionReservada = new ReservaHaitacionDTO();

        //este dato lo consigo de habitación guardada porque ya tiene el id
        habitacionReservada.setIdRoom(habitacionGuardada.getId());
        habitacionReservada.setDateFrom(habitacionGuardada.getDisponibleDesde());
        habitacionReservada.setDateto(habitacionGuardada.getDisponibleHasta());
        habitacionReservada.setNoches(ChronoUnit.DAYS.between(habitacionGuardada.getDisponibleDesde(),
                habitacionGuardada.getDisponibleHasta()));
        habitacionReservada.setLugar(habitacionGuardada.getHotel().getLugar());
        habitacionReservada.setCodigoHotel(habitacionGuardada.getHotel().getCodigoHotel());
        habitacionReservada.setPeopleQ(habitacionGuardada.getUsuarios().size());
        habitacionReservada.setRoomType(habitacionGuardada.getTipoHabitacion());

        //agrego la lista de personas del formulario
        habitacionReservada.setHosts(new ArrayList<>());
        habitacionReservada.getHosts().addAll(habitacionGuardada.getUsuarios().stream().map(this::usuarioEntityToDto).toList());

        return habitacionReservada;
    }


    // este método extra aparte del ejercicio, es para crear habiationes de un hotel determinado
    @Override
    public Object crearHabitaciones(HabitacionDTO habitacionDTO, Long idHotel) {

        //lanzará una excepción si no encuentra el id del hotel a la hora de insertar en la base de datos
        try {
            //1º creo el hotel y hago set para introducir el id,
            // lo hago así para que no lance una excepción al buscar el hotel ya que viene como null el json
            HotelDTO hotelEnHabitacion = new HotelDTO();
            hotelEnHabitacion.setId(idHotel);
            habitacionDTO.setHotel(hotelEnHabitacion);

            //2º paso a entidad y la guardo en la base de datos
            Habitacion habitacionGuardada = habitacionRepository.save(habitacionDtoToEntity(habitacionDTO));

            //quiero conseguir el nombre del hotel, para eso me lo tengo que traer de la base de datos
            Optional<Habitacion> habitacionEnviar = habitacionRepository.findAll().stream()
                    .filter(h -> h.getHotel().getId().equals(idHotel)).findFirst();

            Habitacion habitacionFiltrada = habitacionEnviar.orElse(null);

            //paso HabitacionDTO, le seteo manualmente el valor para que devuelva el nombre del hotel
            HabitacionDTO habitacionDevolver = this.habitacionEntityToDto(habitacionGuardada);
           if(habitacionFiltrada != null){
                //seteo el nombre del hotel que viene de la base de datos
               habitacionDevolver.setNombreHotel(habitacionGuardada.getHotel().getNombre());
           }

            return habitacionDevolver;

        } catch (Exception e) {
            // lanzo este mensaje si el id el error de la base de datos al no encontrar el id para insertar habitación
            return new ExceptionDTO(500, "No se encuentra hotel con el id: " + idHotel +
                    " para agregar habitación");
        }

    }


    //métodos propios

    public Habitacion habitacionDtoToEntity(HabitacionDTO habitacionDTO) {

        if (habitacionDTO == null) {
            return null;
        }

        Habitacion habitacion = new Habitacion();
        habitacion.setId(habitacionDTO.getId());
        habitacion.setTipoHabitacion(habitacionDTO.getTipoHabitacion());
        habitacion.setPrecioNoche(habitacionDTO.getPrecioNoche());
        habitacion.setDisponibleDesde(habitacionDTO.getDisponibleDesde());
        habitacion.setDisponibleHasta(habitacionDTO.getDisponibleHasta());
        habitacion.setReservado(habitacionDTO.isReservado());

        // Convertir HotelDTO a Hotel (si existe)
        if (habitacionDTO.getHotel() != null) {
            Hotel hotel = new Hotel();
            hotel.setId(habitacionDTO.getHotel().getId());
            hotel.setCodigoHotel(habitacionDTO.getHotel().getCodigoHotel());
            hotel.setNombre(habitacionDTO.getHotel().getNombre());
            hotel.setLugar(habitacionDTO.getHotel().getLugar());
            hotel.setAlta(habitacionDTO.getHotel().isAlta());
            habitacion.setHotel(hotel);
        }

        // Convertir List<UsuarioDTO> a List<Usuario> (si existe)
        if (habitacionDTO.getUsuarios() != null) {
            List<Usuario> usuarios = habitacionDTO.getUsuarios().stream()
                    .map(this::usuarioDtoToEntity) // Uso HotelService para convertir cada UsuarioDTO a Usuario
                    .collect(Collectors.toList());
            habitacion.setUsuarios(usuarios);
        }

        return habitacion;
    }

    public HabitacionDTO habitacionEntityToDto(Habitacion habitacion) {

        if (habitacion == null) {
            return null;
        }

        HabitacionDTO habitacionDTO = new HabitacionDTO();
        habitacionDTO.setId(habitacion.getId());
        habitacionDTO.setTipoHabitacion(habitacion.getTipoHabitacion());
        habitacionDTO.setPrecioNoche(habitacion.getPrecioNoche());
        habitacionDTO.setDisponibleDesde(habitacion.getDisponibleDesde());
        habitacionDTO.setDisponibleHasta(habitacion.getDisponibleHasta());
        habitacionDTO.setReservado(habitacion.isReservado());


        //hago esto para que se renderice el nombre del hotel solamente
        habitacionDTO.setNombreHotel(habitacion.getHotel().getNombre());

        // Convertir Hotel a HotelDTO (si existe)
        if (habitacion.getHotel() != null) {
            HotelDTO hotelDTO = new HotelDTO();
            hotelDTO.setId(habitacion.getHotel().getId());
            hotelDTO.setCodigoHotel(habitacion.getHotel().getCodigoHotel());
            hotelDTO.setNombre(habitacion.getHotel().getNombre());
            hotelDTO.setLugar(habitacion.getHotel().getLugar());
            hotelDTO.setAlta(habitacion.getHotel().isAlta());
            habitacionDTO.setHotel(hotelDTO);
        }

        // Convertir List<Usuario> a List<UsuarioDTO> (si existe)
        if (habitacion.getUsuarios() != null) {
            List<UsuarioDTO> usuariosDTO = habitacion.getUsuarios().stream()
                    .map(this::usuarioEntityToDto) // Uso HotelService para convertir cada Usuario a UsuarioDTO
                    .collect(Collectors.toList());
            habitacionDTO.setUsuarios(usuariosDTO);
        }

        return habitacionDTO;
    }

    // Convierte UsuarioDTO a Usuario (entidad)
    public Usuario usuarioDtoToEntity(UsuarioDTO usuarioDTO) {

        if (usuarioDTO == null) {
            return null;
        }

        Usuario usuario = new Usuario();
        usuario.setId(usuarioDTO.getId());
        usuario.setNombre(usuarioDTO.getNombre());
        usuario.setApellido(usuarioDTO.getApellido());

        // Convertir HabitacionDTO a Habitacion (si existe)
        if (usuarioDTO.getHabitacion() != null) {
            Habitacion habitacion = new Habitacion();
            habitacion.setId(usuarioDTO.getHabitacion().getId());
            // Aquí puedes mapear otros campos de Habitacion si es necesario
            usuario.setHabitacion(habitacion);
        }

        // Convertir VueloDTO a Vuelo (si existe)
        if (usuarioDTO.getVuelo() != null) {
            Vuelo vuelo = new Vuelo();
            vuelo.setId(usuarioDTO.getVuelo().getId());
            // Aquí puedes mapear otros campos de Vuelo si es necesario
            usuario.setVuelo(vuelo);
        }

        return usuario;
    }


    // Convierte Usuario (entidad) a UsuarioDTO
    public UsuarioDTO usuarioEntityToDto(Usuario usuario) {

        if (usuario == null) {
            return null;
        }

        UsuarioDTO usuarioDTO = new UsuarioDTO();
        usuarioDTO.setId(usuario.getId());
        usuarioDTO.setNombre(usuario.getNombre());
        usuarioDTO.setApellido(usuario.getApellido());

        // Convertir Habitacion a HabitacionDTO (si existe)
        if (usuario.getHabitacion() != null) {
            HabitacionDTO habitacionDTO = new HabitacionDTO();
            habitacionDTO.setId(usuario.getHabitacion().getId());
            // Aquí puedes mapear otros campos de HabitacionDTO si es necesario
            usuarioDTO.setHabitacion(habitacionDTO);
        }

        // Convertir Vuelo a VueloDTO (si existe)
        if (usuario.getVuelo() != null) {
            VueloDTO vueloDTO = new VueloDTO();
            vueloDTO.setId(usuario.getVuelo().getId());
            // Aquí puedes mapear otros campos de VueloDTO si es necesario
            usuarioDTO.setVuelo(vueloDTO);
        }

        return usuarioDTO;
    }

}
