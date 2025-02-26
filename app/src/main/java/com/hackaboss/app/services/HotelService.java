package com.hackaboss.app.services;

import com.hackaboss.app.dtos.*;
import com.hackaboss.app.entities.Habitacion;
import com.hackaboss.app.entities.Hotel;
import com.hackaboss.app.entities.Usuario;
import com.hackaboss.app.entities.Vuelo;
import com.hackaboss.app.repositories.HotelRepositoryInterface;
import io.micrometer.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class HotelService implements HotelServiceInterface {

    @Autowired
    HotelRepositoryInterface hotelRepository;

    @Override
    public Object listarHoteles() {

        //filtro los que estén en alta
        List<Hotel> listaHoteles = hotelRepository.findAll().stream()
                .filter(h -> h.isAlta()).toList();

        if (listaHoteles.isEmpty()) {
            return new ExceptionDTO(500, "No hay hoteles en la lista");
        }

        return listaHoteles.stream().map(this::hotelEntityToDto).toList();
    }

    @Override
    public Object buscarHotel(Long idHotel) {

        //si no ha introducido ningún dato
        if (idHotel == null) {
            return new ExceptionDTO(500, "No se Ha introducido id para buscar");
        }

        Optional<Hotel> hotelBuscar = hotelRepository.findById(idHotel);

        //saco la variable en un optional para poder gestionar después el resultado de la búsqueda
        Hotel hotelEncontrado = hotelBuscar.orElse(null);

        // si no encuentra el hotel (null) o que no esté en alta(borrado lógico) , lanzo una excepción
        if (hotelEncontrado == null || !hotelEncontrado.isAlta()) {
            return new ExceptionDTO(500, "No se encuentra hotel con el id: " + idHotel);
        }

        return this.hotelEntityToDto(hotelEncontrado);
    }

    @Override
    public Object crearHotel(HotelDTO hotelDTO) {


        //1º si algún campo contiene null o cadena vacía(he utilizado StrigUtils ya que me busca las dos cosas a la vez
        //(null y cadena vacía)
        if (StringUtils.isBlank(hotelDTO.getNombre()) && StringUtils.isBlank(hotelDTO.getLugar())) {
            return new ExceptionDTO(500, "No ha introducido los parámetros para agregar un hotel");
        }

        //2º busco los hoteles
        List<Hotel> listaHoteles = hotelRepository.findAll();

        //3º filtro en el caso de que coincida el hotel, por el nombre y el lugar
        Hotel hotelRepetido = listaHoteles.stream()
                .filter(h -> h.getLugar().equalsIgnoreCase(hotelDTO.getLugar()) &&
                        h.getNombre().equalsIgnoreCase(hotelDTO.getNombre()))
                .findAny()
                .orElse(null);

        //4º si el código del hotel existe, lanzo un exceptionDTO
        if (hotelRepetido != null) {
            return new ExceptionDTO(500, "Ya existe un hotel con estas características");
        }

        //voy a generar el código del nuevo hotel
        hotelDTO.setCodigoHotel(this.generarCodigoHotel(hotelDTO));

        //al ser un nuevo hotel le doy de alta por defecto.
        hotelDTO.setAlta(true);

        //retorno el hotel introducido
        Hotel hotelGuardado = hotelRepository.save(hotelDtoToEntity(hotelDTO));
        return this.hotelEntityToDto(hotelGuardado);
    }

    @Override
    public Object editarHotel(HotelDTO hotelDTO, Long idHotel) {

        //1º filtro datos de entrada de hotelDTO
        if(StringUtils.isBlank(hotelDTO.getNombre()) && StringUtils.isBlank(hotelDTO.getLugar())){
            return new ExceptionDTO(500, "Debe introducir todos los parámetros del hotel");
        }

        if (idHotel == null) {
            return new ExceptionDTO(500, "No se Ha introducido id del hotel a modificar en el endpoint");
        }

        //2º busco la lista de hoteles,
        Optional<Hotel> hotelEncontrar = hotelRepository.findById(idHotel);

        Hotel hotelEncontrado = hotelEncontrar.orElse(null);

        //3º para valorar si el hotel existe en la base de datos y que esté en alta(sin borrado lógico)
        if (hotelEncontrado != null && hotelEncontrado.isAlta()) {

            //valorar si existen reservas
            boolean existenReservas = hotelEncontrado.getHabitaciones().stream().anyMatch(Habitacion::isReservado);

            if(existenReservas){
                return new ExceptionDTO(500, "Existen reservas con en este hotel," +
                        " no se puede modificar el hotel con nombre: " + hotelEncontrado.getNombre());
            }

            //modifico los valores del objeto de la base de datos con el que insertó el empleado
            hotelEncontrado.setNombre(hotelDTO.getNombre());
            hotelEncontrado.setLugar(hotelDTO.getLugar());

        } else {
            return new ExceptionDTO(500, "No se encuentra hotel a modificar con el id: "
                    + idHotel + " para modificar");
        }
        //4º si todas las validaciones han do bien, guardo en la base de datos
        return hotelEntityToDto(hotelRepository.save(hotelEncontrado));
    }

    @Override
    public Object eliminarHotel(Long idHotel) {

        //para eliminar el hotel voy a hacer un cambio de la variable activo(para hacer un borrado lógico)
        //por lo cual aparecerá en la base de datos pero no se listará cuando listemos los hoteles

        //1º valoro si el campo hotel está introducido en la base de datos
        if(idHotel == null){
            return new ExceptionDTO(500, "No se Ha introducido id del hotel");
        }

        Optional<Hotel> hotelBuscar = hotelRepository.findById(idHotel);

        Hotel hotelEncontrado = hotelBuscar.orElse(null);

        if (hotelEncontrado != null) {

            //primero tengo que valorar que no tengamos reservas del hotel
            boolean hayReservas = hotelEncontrado.getHabitaciones().stream().anyMatch(Habitacion::isReservado);

            if(hayReservas){
                return new ExceptionDTO(500,
                        "Hay reservas en el hotel, no se puede eliminar el hotel con id: " + idHotel);
            }

            //2º con esto consigo que no se borre de la base de datos por si se quiere volver a poner en alta
            hotelEncontrado.setAlta(false);
            hotelRepository.save(hotelEncontrado);
            return new ExceptionDTO(200, "El hotel con el id: " + idHotel + " ha sido eliminado");
        }

        return new ExceptionDTO(500, "No se encuentra el hotel con el id: " + idHotel);
    }


    //métodos propios

    public String generarCodigoHotel(HotelDTO hotelGenerarCodigo) {

        //1º general el prefijo(las letras)
        String prefijo;

        //separo las palabras del nombre del hotel para generar el código
        String[] palabras = hotelGenerarCodigo.getNombre().split(" ");

        //lo instancio para concatenar los valores
        StringBuilder prefijoBuilder = new StringBuilder();

        //en el caso de solo tengamos una palabra
        if (palabras.length == 1) {

            //si la unica palabra tiene solo una letra
            if (palabras[0].length() == 1) {
                prefijoBuilder.append(palabras[0].charAt(0));
            } else {
                prefijoBuilder.append(palabras[0].charAt(0));
                prefijoBuilder.append(palabras[0].charAt(1));
            }

        } else {
            //mas de una palabra, agregamos la primera letra de cada palabra
            prefijoBuilder.append(palabras[0].charAt(0));
            prefijoBuilder.append(palabras[1].charAt(0));
        }
        //el prefijo va a estar en mayúscula
        prefijo = prefijoBuilder.toString().toUpperCase();


        //2º para generar el número

        //voy a buscar en la base de datos de hoteles para ver si existe el prefino igual
        List<String> codigosEncontrado = hotelRepository.findAll().stream()
                .map(Hotel::getCodigoHotel).toList();

        //voy a separar el código de la lista (prefijo-sufijo)
        StringBuilder construirSufijo = new StringBuilder();

        String[] palabraSeparadaSufijo = codigosEncontrado.stream()
                .map(s -> s.split("-"))
                .filter(suf -> suf[0].equalsIgnoreCase(prefijo)).findFirst().orElse(null);

        //si no existe el prefino
        if (palabraSeparadaSufijo == null) {
            construirSufijo.append("1");
        } else {
            int numeroPrefino = Integer.parseInt(palabraSeparadaSufijo[1]) + 1;

            construirSufijo.append(numeroPrefino);
        }

        //envío el prefijo construido con el formato
        return prefijo + "-000" + construirSufijo;
    }


    //métodos propios

    //Convierte HotelDTO a Hotel (entidad)
    public Hotel hotelDtoToEntity(HotelDTO hotelDTO) {

        if (hotelDTO == null) {
            return null;
        }

        Hotel hotel = new Hotel();
        hotel.setId(hotelDTO.getId());
        hotel.setCodigoHotel(hotelDTO.getCodigoHotel());
        hotel.setNombre(hotelDTO.getNombre());
        hotel.setLugar(hotelDTO.getLugar());
        hotel.setAlta(hotelDTO.isAlta());

        //convertir List<HabitacionDTO> a List<Habitacion> (si existe)
        if (hotelDTO.getHabitacionesDto() != null) {
            List<Habitacion> habitaciones = hotelDTO.getHabitacionesDto().stream()
                    //uso HabitacionService para convertir cada HabitacionDTO a Habitacion
                    .map(this::habitacionDtoToEntity)
                    .collect(Collectors.toList());
            hotel.setHabitaciones(habitaciones);
        } else {
            hotel.setHabitaciones(new ArrayList<>());
        }

        return hotel;
    }

    //convertir Hotel (entidad) a HotelDTO
    public HotelDTO hotelEntityToDto(Hotel hotel) {
        if (hotel == null) {
            return null;
        }

        HotelDTO hotelDTO = new HotelDTO();
        hotelDTO.setId(hotel.getId());
        hotelDTO.setCodigoHotel(hotel.getCodigoHotel());
        hotelDTO.setNombre(hotel.getNombre());
        hotelDTO.setLugar(hotel.getLugar());
        hotelDTO.setAlta(hotel.isAlta());

        //convierto List<Habitacion> a List<HabitacionDTO> (si existe)
        if (hotel.getHabitaciones() != null) {
            List<HabitacionDTO> habitacionesDto = hotel.getHabitaciones().stream()
                    //uso la clase HabitacionService para pasar habitacion
                    .map(this::habitacionEntityToDto)
                    .collect(Collectors.toList());
            hotelDTO.setHabitacionesDto(habitacionesDto);
        } else {
            hotelDTO.setHabitacionesDto(new ArrayList<>());
        }

        return hotelDTO;
    }

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

        //convertir HotelDTO a Hotel (si existe)
        if (habitacionDTO.getHotel() != null) {
            Hotel hotel = new Hotel();
            hotel.setId(habitacionDTO.getHotel().getId());
            hotel.setCodigoHotel(habitacionDTO.getHotel().getCodigoHotel());
            hotel.setNombre(habitacionDTO.getHotel().getNombre());
            hotel.setLugar(habitacionDTO.getHotel().getLugar());
            hotel.setAlta(habitacionDTO.getHotel().isAlta());
            habitacion.setHotel(hotel);
        }

        //ahora convierto List<UsuarioDTO> a List<Usuario> (si existe)
        if (habitacionDTO.getUsuarios() != null) {
            List<Usuario> usuarios = habitacionDTO.getUsuarios().stream()
                    // Uso HotelService para convertir cada UsuarioDTO a Usuario
                    .map(HotelService::usuarioDtoToEntity)
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

        //comprobar si el hotel no es nulo antes de acceder a sus propiedades
        if (habitacion.getHotel() != null) {

            //convertir Hotel a HotelDTO (si existe)
            HotelDTO hotelDTO = new HotelDTO();
            hotelDTO.setId(habitacion.getHotel().getId());
            hotelDTO.setCodigoHotel(habitacion.getHotel().getCodigoHotel());
            hotelDTO.setNombre(habitacion.getHotel().getNombre());
            hotelDTO.setLugar(habitacion.getHotel().getLugar());
            hotelDTO.setAlta(habitacion.getHotel().isAlta());
            habitacionDTO.setHotel(hotelDTO);
        } else {
            habitacionDTO.setHotel(null);
        }

        //convierto List<Usuario> a List<UsuarioDTO> (si existe)
        if (habitacion.getUsuarios() != null) {
            List<UsuarioDTO> usuariosDTO = habitacion.getUsuarios().stream()
                    // Uso HotelService para convertir cada Usuario a UsuarioDTO
                    .map(HotelService::usuarioEntityToDto)
                    .collect(Collectors.toList());
            habitacionDTO.setUsuarios(usuariosDTO);
        } else {
            habitacionDTO.setUsuarios(new ArrayList<>());
        }

        return habitacionDTO;
    }

    //voy a convertir UsuarioDTO a Usuario (entidad)
    public static Usuario usuarioDtoToEntity(UsuarioDTO usuarioDTO) {
        if (usuarioDTO == null) {
            return null;
        }

        Usuario usuario = new Usuario();
        usuario.setId(usuarioDTO.getId());
        usuario.setNombre(usuarioDTO.getNombre());
        usuario.setApellido(usuarioDTO.getApellido());

        //convierto HabitacionDTO a Habitacion (si existe)
        if (usuarioDTO.getHabitacion() != null) {
            Habitacion habitacion = new Habitacion();
            habitacion.setId(usuarioDTO.getHabitacion().getId());
            usuario.setHabitacion(habitacion);
        }

        //convierto VueloDTO a Vuelo (si existe)
        if (usuarioDTO.getVuelo() != null) {
            Vuelo vuelo = new Vuelo();
            vuelo.setId(usuarioDTO.getVuelo().getId());
            usuario.setVuelo(vuelo);
        }

        return usuario;
    }


    //convierto Usuario (entidad) a UsuarioDTO
    public static UsuarioDTO usuarioEntityToDto(Usuario usuario) {
        if (usuario == null) {
            return null;
        }

        UsuarioDTO usuarioDTO = new UsuarioDTO();
        usuarioDTO.setId(usuario.getId());
        usuarioDTO.setNombre(usuario.getNombre());
        usuarioDTO.setApellido(usuario.getApellido());

        //convierto Habitacion a HabitacionDTO (si existe)
        if (usuario.getHabitacion() != null) {
            HabitacionDTO habitacionDTO = new HabitacionDTO();
            habitacionDTO.setId(usuario.getHabitacion().getId());
            usuarioDTO.setHabitacion(habitacionDTO);
        }

        //convierto Vuelo a VueloDTO (si existe)
        if (usuario.getVuelo() != null) {
            VueloDTO vueloDTO = new VueloDTO();
            vueloDTO.setId(usuario.getVuelo().getId());
            usuarioDTO.setVuelo(vueloDTO);
        }

        return usuarioDTO;
    }

}
