package com.hackaboss.app.services;

import com.hackaboss.app.dtos.*;
import com.hackaboss.app.entities.Habitacion;
import com.hackaboss.app.entities.Usuario;
import com.hackaboss.app.entities.Vuelo;
import com.hackaboss.app.repositories.VueloRepositoryInterface;
import io.micrometer.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class VueloService implements VueloServiceInterface {

    @Autowired
    VueloRepositoryInterface vueloRepository;

    @Override
    public Object listarVuelos() {

        //voy a mostrar los vuelos que estén dados de alta (se efectuarán borrados lógicos)
        List<Vuelo> listaVuelos = vueloRepository.findAll().stream()
                .filter(Vuelo::isAlta).toList();

        if (listaVuelos.isEmpty()) {
            return new ExceptionDTO(404,
                    "No existen vuelos en la base de datos");
        }

        return listaVuelos.stream().map(this::vueloEntityToDto);
    }

    @Override
    public Object filtrarVuelosActivosParametros(LocalDate dateFrom, LocalDate dateTo, String origin, String destination) {

        // Obtener todas los vuelos
        List<Vuelo> listaVuelosFiltrada = vueloRepository.findAll();

        //1º Filtro inicial: se filtran los datos para que solo queden los activos y no reservados(Lista vacía)
        listaVuelosFiltrada = listaVuelosFiltrada.stream()
                .filter(v -> v.isAlta() && v.getUsuarios().isEmpty())
                .toList();

        //2º filtro por fecha de inicio (dateFrom)
        if (dateFrom != null) {
            listaVuelosFiltrada = listaVuelosFiltrada.stream()
                    .filter(h -> h.getFecha().isAfter(dateFrom) ||
                            h.getFecha().isEqual(dateFrom)).toList();
        }

        //3º filtro por fecha de fin (dateTo)
        if (dateTo != null) {
            listaVuelosFiltrada = listaVuelosFiltrada.stream()
                    .filter(h -> h.getFecha().isBefore(dateTo) ||
                            h.getFecha().isEqual(dateTo))
                    .toList();
        }

        //4º filtro por origen (si no hay destino)
        if (origin != null && destination == null) {
            listaVuelosFiltrada = listaVuelosFiltrada.stream()
                    .filter(h -> h.getOrigen().equalsIgnoreCase(origin))
                    .toList();
        }

        //5º filtro por destino (si no hay origen)
        if (destination != null && origin == null) {
            listaVuelosFiltrada = listaVuelosFiltrada.stream()
                    .filter(h -> h.getDestino().equalsIgnoreCase(destination))
                    .toList();
        }

        //6º filtro por destino y origen
        // (voy a buscar tanto el origen como destino indistintamente)
        if (destination != null && origin != null) {
            listaVuelosFiltrada = listaVuelosFiltrada.stream()
                    .filter(h -> h.getDestino().equalsIgnoreCase(destination) || h.getOrigen().equalsIgnoreCase(destination) ||
                            h.getDestino().equalsIgnoreCase(origin) || h.getOrigen().equalsIgnoreCase(origin)
                    )
                    .toList();
        }

        //7º verifico si la lista está vacía después de los filtros
        if (listaVuelosFiltrada.isEmpty()) {
            return new ExceptionDTO(404,
                    "No se encontraron vuelos disponibles con los criterios especificados.");
        }

        //8º convierto la lista de entidades a DTOs y devolverla
        return listaVuelosFiltrada.stream()
                .map(this::vueloEntityToDto)
                .collect(Collectors.toList());

    }

    @Override
    public Object buscarVuelo(Long idVuelo) {

        //si no ha introducido ningún dato
        if (idVuelo == null) {
            return new ExceptionDTO(500, "No se Ha introducido id para buscar");
        }

        //filtro el vuelo que estén en alta y que no esté ya reservado(que no tenga pasajeros)
        Optional<Vuelo> vueloBuscar = vueloRepository.findById(idVuelo).stream().filter(Vuelo::isAlta).filter(v -> v.getUsuarios().isEmpty()).findAny();

        //saco la variable en un optional para poder gestionar después el resultado de la búsqueda
        Vuelo vueloEncontrado = vueloBuscar.orElse(null);

        // si no encuentra el hote (null), lanzo una excepción
        if (vueloEncontrado == null) {
            return new ExceptionDTO(500, "No se encuentra vuelo con el id: " + idVuelo);
        }

        return this.vueloEntityToDto(vueloEncontrado);
    }

    @Override
    public Object crearVuelo(VueloDTO vueloDTO) {

        //1º quiero verificar que todos los campos estén rellenos
        if (vueloDTO.getDestino() == null || vueloDTO.getDestino().isBlank()
                || vueloDTO.getOrigen() == null || vueloDTO.getOrigen().isBlank()
                || vueloDTO.getTipoAsiento() == null || vueloDTO.getTipoAsiento().isBlank()
                || vueloDTO.getPrecio() == 0.0 || vueloDTO.getFecha() == null) {

            return new ExceptionDTO(404,
                    "Deben de estar rellenos todos los campos para poder agregar un vuelo");
        }

        //2º tengo que verificar que no haya ningún vuelo de mismas características(origen, destino y fecha)
        List<Vuelo> listaVuelos = vueloRepository.findAll().stream()
                .filter(v -> v.getOrigen().equalsIgnoreCase(vueloDTO.getOrigen()) &&
                        v.getDestino().equalsIgnoreCase(vueloDTO.getDestino()) &&
                        v.getFecha().isEqual(vueloDTO.getFecha())).toList();

        if ((!listaVuelos.isEmpty())) {
            return new ExceptionDTO(404,
                    "El vuelo que desea agregar, ya existe");
        } else {

            //creo un código de vuelo
            vueloDTO.setNumeroVuelo(this.generarCodigoVuelo(vueloDTO));

            //seteo el valor en alta por defecto
            vueloDTO.setAlta(true);
            //seteo este valor para controlar error al intentar introducir usuarios si haber introducido antes el vuelo
            vueloDTO.setUsuarios(null);

            Vuelo vueloGuardado = vueloRepository.save(this.vueloDtoToEntity(vueloDTO));

            return this.vueloEntityToDto(vueloGuardado);
        }
    }

    @Override
    public Object editarVuelo(VueloDTO vueloDTO, Long idVuelo) {

        //1º valoro que los parámetros no sean nulos
        if (StringUtils.isBlank(vueloDTO.getOrigen()) || StringUtils.isBlank(vueloDTO.getTipoAsiento()) ||
                StringUtils.isBlank(vueloDTO.getDestino()) || vueloDTO.getPrecio() == null ||
                vueloDTO.getFecha() == null) {

            return new ExceptionDTO(500, "No se Ha introducido todos los parámetros modificar");
        }

        if (idVuelo == null) {
            return new ExceptionDTO(500, "No se Ha introducido id del vuelo a modificar en el endpoint");
        }

        //busco la lista de vuelos,
        Optional<Vuelo> vueloEncontrar = vueloRepository.findById(idVuelo);

        Vuelo vueloEncontrado = vueloEncontrar.orElse(null);

        //para valorar si del vuelo
        if (vueloEncontrado != null) {

            List<Usuario> usuariosVuelo = vueloEncontrado.getUsuarios();

            //voy a verificar que el vuelo no contenga reservas, sinó no se podrá modificar
            if (!vueloEncontrado.getUsuarios().isEmpty()) {
                return new ExceptionDTO(500, "Ya hay reservas en el vuelo, no se puede modificar");
            }

            //modifico los valores del objeto de la base de datos con el que insertó el empleado
            vueloEncontrado.setOrigen(vueloDTO.getOrigen());
            vueloEncontrado.setDestino(vueloDTO.getDestino());
            vueloEncontrado.setTipoAsiento(vueloDTO.getTipoAsiento());
            vueloEncontrado.setPrecio(vueloDTO.getPrecio());
            vueloEncontrado.setFecha(vueloDTO.getFecha());

        } else {
            return new ExceptionDTO(500, "No se encuentra vuelo con el id: " + idVuelo + " para modificar");
        }
        //si todas las validaciones han do bien, guardo en la base de datos
        return this.vueloEntityToDto(vueloRepository.save(vueloEncontrado));
    }

    @Override
    public Object eliminarVuelo(Long idVuelo) {
        //para eliminar el vuelo voy a hacer un cambio de la variable activo(para hacer un borrado lógico)
        //por lo cual aparecerá en la base de datos pero no se listará cuando listemos los vuelos

        Optional<Vuelo> hotelBuscar = vueloRepository.findById(idVuelo);

        Vuelo hotelEncontrado = hotelBuscar.orElse(null);

        if (hotelEncontrado != null) {

            //primero tengo que valorar que no tengamos reservas del hotel
            boolean noHayReservas = hotelEncontrado.getUsuarios().isEmpty();

            if (!noHayReservas) {
                return new ExceptionDTO(500,
                        "Hay reservas en el vuelo, no se puede eliminar el vuelo con id: " + idVuelo);
            }

            //con esto consigo que no se borre de la base de datos por si se quiere volver a poner en alta
            hotelEncontrado.setAlta(false);
            vueloRepository.save(hotelEncontrado);
            return new ExceptionDTO(200, "El vuelo con el id: " + idVuelo + " ha sido eliminado");
        }

        return new ExceptionDTO(500, "No se encuentra el vuelo con el id: " + idVuelo);
    }

    @Override
    public Object reservarVuelo(ReservaVueloDTO reservaVueloDTO) {


        //1º valorar que todos los datos estén introducidos, sino, lanzo una excepción
        if (reservaVueloDTO.getIdVuelo() == null || reservaVueloDTO.getDia() == null ||
                reservaVueloDTO.getOrigen() == null || reservaVueloDTO.getDestino() == null ||
                reservaVueloDTO.getTipoAsiento() == null || reservaVueloDTO.getCodigoVuelo() == null ||
                reservaVueloDTO.getCantiDadUsuarios() == null || reservaVueloDTO.getListaUsuarios() == null
        ) {

            return new ExceptionDTO(404,
                    "No se han introducidos todos los parámetros, son obligatorios.");
        }

        //2º verifico datos

        //verifico que la cantidad de personas no superen la lista de usuarios que se quieren inscribir
        if (reservaVueloDTO.getListaUsuarios().size() > reservaVueloDTO.getCantiDadUsuarios()) {
            return new ExceptionDTO(404, "El máximo de usuarios en la habitación es de: "
                    + reservaVueloDTO.getCantiDadUsuarios());
        }

        //verifico si existe la habitación
        Optional<Vuelo> buscarHabitacion = vueloRepository.findById(reservaVueloDTO.getIdVuelo());

        Vuelo vueloEncontrado = buscarHabitacion.orElse(null);

        if (vueloEncontrado == null) {
            return new ExceptionDTO(404, "No se encuentra el vuelo con el id: " +
                    reservaVueloDTO.getIdVuelo());
        }

        //ver que el vuelo no esté en alta (idénticas características)
        if (!vueloEncontrado.isAlta()) {
            return new ExceptionDTO(404, "el vuelo con el id: " +
                    reservaVueloDTO.getIdVuelo() +
                    ", no está dado de alta");
        }

        //voy a verificar que el vuelo no esté reservado, verificando que el la lista no esté vacía
        if (!vueloEncontrado.getUsuarios().isEmpty()) {
            return new ExceptionDTO(404,
                    "El vuelo ya está reservado, no se puede hacer la reserva, escoja otro vuelo");
        }

        //voy a verificar que el código del vuelo coincida con el de la base de datos, para mayor verificación
        if (!vueloEncontrado.getNumeroVuelo().equalsIgnoreCase(reservaVueloDTO.getCodigoVuelo())) {
            return new ExceptionDTO(404,
                    "El código del vuelo no coincide con el de la base de datos, por favor corrija el error");
        }

        //voy a verificar que coincidan las fechas introducidas con las de la base de datos
        if (!vueloEncontrado.getFecha().isEqual(reservaVueloDTO.getDia())) {
            return new ExceptionDTO(404,
                    "Las fecha introducida para el vuelo no coinciden con las de la base de datos," +
                            "por favor corrija el error");
        }

        //3º ahora lo convierto en usuario y le agrego el vuelo a cada usuario
        reservaVueloDTO.getListaUsuarios().stream()
                .map(this::usuarioDtoToEntity)
                .forEach(u -> {
                    //aqui agrego la vuelo a usuario en cada iteración
                    u.setVuelo(vueloEncontrado);
                    //aqui agrego cada usuario a la lista de usuario
                    vueloEncontrado.getUsuarios().add(u);
                });

        vueloRepository.save(vueloEncontrado);

        //4º voy a crear un objetoDTO que mandará la respuesta al usuario,
        // no lo pongo en un métodop porque sólo lo hago una vez
        ReservaVueloDTO reservaVueloDTODevolver = new ReservaVueloDTO();
        reservaVueloDTODevolver.setIdVuelo(vueloEncontrado.getId());
        reservaVueloDTODevolver.setDia(vueloEncontrado.getFecha());
        reservaVueloDTODevolver.setOrigen(vueloEncontrado.getOrigen());
        reservaVueloDTODevolver.setDestino(vueloEncontrado.getDestino());
        reservaVueloDTODevolver.setCodigoVuelo(vueloEncontrado.getNumeroVuelo());
        reservaVueloDTODevolver.setCantiDadUsuarios(vueloEncontrado.getUsuarios().size());
        reservaVueloDTODevolver.setTipoAsiento(vueloEncontrado.getTipoAsiento());
        //inicio la lista de usuarios
        reservaVueloDTODevolver.setListaUsuarios(new ArrayList<>());
        reservaVueloDTODevolver.getListaUsuarios().addAll(vueloEncontrado.getUsuarios().stream()
                .map(this::usuarioEntityToDto).toList());

        return reservaVueloDTODevolver;
    }

    //métodos propios

    public String generarCodigoVuelo(VueloDTO vueloGenerarCodigo) {

        //general el prefijo(las letras)
        String prefijo;

        //lo instancio para concatenar los valores
        StringBuilder prefijoBuilder = new StringBuilder();

        //cogemos la primera letra de la ida y de la vuelta
        prefijoBuilder.append(vueloGenerarCodigo.getOrigen().charAt(0));
        prefijoBuilder.append(vueloGenerarCodigo.getDestino().charAt(0));

        //el prefijo va a estar en mayúscula
        prefijo = prefijoBuilder.toString().toUpperCase();


        //para generar el número

        //voy a buscar los vuelos en la base de datos para ver si existe un prefijo igual
        List<String> codigosEncontrado = vueloRepository.findAll().stream()
                .map(Vuelo::getNumeroVuelo).toList();

        //voy a separar el código de la lista (prefijo-sufijo)
        StringBuilder construirSufijo = new StringBuilder();

        String[] palabraSeparadaSufijo = codigosEncontrado.stream()
                .map(s -> s.split("-"))
                .filter(suf -> suf[0].equalsIgnoreCase(prefijo)).findFirst().orElse(null);

        //si no existe el prefino
        if (palabraSeparadaSufijo == null) {
            construirSufijo.append("01");
        } else {
            int numeroPrefino = Integer.parseInt(palabraSeparadaSufijo[1]) + 1;

            construirSufijo.append(numeroPrefino);
        }

        //envío el prefijo construido con el formato
        return prefijo + "-00" + construirSufijo;
    }


    // Convierte VueloDTO a Vuelo (entidad)
    public Vuelo vueloDtoToEntity(VueloDTO vueloDTO) {
        if (vueloDTO == null) {
            return null;
        }

        Vuelo vuelo = new Vuelo();
        vuelo.setId(vueloDTO.getId());
        vuelo.setNumeroVuelo(vueloDTO.getNumeroVuelo());
        vuelo.setOrigen(vueloDTO.getOrigen());
        vuelo.setDestino(vueloDTO.getDestino());
        vuelo.setTipoAsiento(vueloDTO.getTipoAsiento());
        vuelo.setPrecio(vueloDTO.getPrecio());
        vuelo.setFecha(vueloDTO.getFecha());
        vuelo.setAlta(vueloDTO.isAlta());

        // Convertir List<UsuarioDTO> a List<Usuario> (si existe)
        if (vueloDTO.getUsuarios() != null) {
            List<Usuario> usuarios = vueloDTO.getUsuarios().stream()
                    // Uso la clase HotelService para convertir el usuario, tengo el método allí
                    //convierto usuario en entity
                    .map(this::usuarioDtoToEntity)
                    .collect(Collectors.toList());
            vuelo.setUsuarios(usuarios);
        }

        return vuelo;
    }

    // Convierte Vuelo (entidad) a VueloDTO
    public VueloDTO vueloEntityToDto(Vuelo vuelo) {

        if (vuelo == null) {
            return null;
        }

        VueloDTO vueloDTO = new VueloDTO();
        vueloDTO.setId(vuelo.getId());
        vueloDTO.setNumeroVuelo(vuelo.getNumeroVuelo());
        vueloDTO.setOrigen(vuelo.getOrigen());
        vueloDTO.setDestino(vuelo.getDestino());
        vueloDTO.setTipoAsiento(vuelo.getTipoAsiento());
        vueloDTO.setPrecio(vuelo.getPrecio());
        vueloDTO.setFecha(vuelo.getFecha());
        vuelo.setAlta(vuelo.isAlta());

        // Convertir List<Usuario> a List<UsuarioDTO> (si existe)
        if (vuelo.getUsuarios() != null) {
            List<UsuarioDTO> usuariosDTO = vuelo.getUsuarios().stream()
                    // Uso la clase HotelService para convertir el usuario, tengo el método allí
                    //convierto los usuarios en dto
                    .map(this::usuarioEntityToDto)
                    .collect(Collectors.toList());
            vueloDTO.setUsuarios(usuariosDTO);
        }

        return vueloDTO;
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
