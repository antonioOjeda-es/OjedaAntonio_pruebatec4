package com.hackaboss.app.services;


import com.hackaboss.app.dtos.ExceptionDTO;
import com.hackaboss.app.dtos.HotelDTO;
import com.hackaboss.app.entities.Habitacion;
import com.hackaboss.app.entities.Hotel;
import com.hackaboss.app.entities.Usuario;
import com.hackaboss.app.repositories.HotelRepositoryInterface;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class HotelServiceTest {

    //inyectar el mock en el servicio
    @InjectMocks
    private HotelService hotelService;

    //simulación del mock de mi repositorio
    @Mock
    private HotelRepositoryInterface hotelRepository;

    //pruebas unitarias
    @Test
    @DisplayName("Encuentra la lista de especificaciones")
    void listarHoteles() {
        // Crear los hoteles
        Hotel hotel1 = new Hotel(1L, "HOT123", "Hotel Sunshine", "Barcelona", true, new ArrayList<>());
        Hotel hotel2 = new Hotel(2L, "HOT456", "Hotel Moonlight", "Madrid", false, new ArrayList<>());

        // Crear las habitaciones y asignarles el hotel correspondiente
        Habitacion habitacion1 = new Habitacion(1L, "Individual", 100.0, LocalDate.now(), LocalDate.now().plusDays(7), false, hotel1, List.of(new Usuario(1L, "Juan", "Pérez", null, null), new Usuario(2L, "Ana", "Gómez", null, null)));
        Habitacion habitacion2 = new Habitacion(2L, "Doble", 150.0, LocalDate.now(), LocalDate.now().plusDays(7), false, hotel2, List.of(new Usuario(3L, "Carlos", "López", null, null), new Usuario(4L, "María", "Martínez", null, null)));

        // Asignar las habitaciones a los hoteles
        hotel1.setHabitaciones(List.of(habitacion1));
        hotel2.setHabitaciones(List.of(habitacion2));

        // Crear la lista de hoteles
        List<Hotel> listahoteles = Arrays.asList(hotel1, hotel2);

        // Definir el comportamiento del mock del repositorio
        when(hotelRepository.findAll()).thenReturn(listahoteles);

        // Llamar al servicio que quiero testear
        Object resultado = hotelService.listarHoteles();


        //antes tengo que verificar la salida del object
        // si es una lista:
        if(resultado instanceof List<?>){

            // Convertir el resultado a List<HotelDTO>
            List<HotelDTO> listaHotelesDTO = (List<HotelDTO>) resultado;

            // Verificar los valores de los atributos de los objetos DTO
            // (al estar uno en alta y el otro no, solo obtiene un resultado
            AssertionsForClassTypes.assertThat(listaHotelesDTO.get(0).getId()).isEqualTo(1L);
            AssertionsForClassTypes.assertThat(listaHotelesDTO.get(0).getCodigoHotel()).isEqualTo("HOT123");
            AssertionsForClassTypes.assertThat(listaHotelesDTO.get(0).getNombre()).isEqualTo("Hotel Sunshine");
            AssertionsForClassTypes.assertThat(listaHotelesDTO.get(0).isAlta()).isEqualTo(true);

            // Verificar el tamaño de la lista
            assertThat(listaHotelesDTO.size()).isEqualTo(1);

        }else if(resultado instanceof ExceptionDTO exceptionDTO){
            //si es una exceptionDTO
            //la salida sería esta
            // (solo bastaría poner el alta de la istancia isAlta en false en el primer elemento de la lista)
            AssertionsForClassTypes.assertThat(exceptionDTO.getStatus()).isEqualTo(500);
            AssertionsForClassTypes.assertThat(exceptionDTO.getMensaje()).isEqualTo("No hay hoteles en la lista");
        }

    }

/*
    @Test
    @DisplayName("Inserta una especificacion")
    void crearEspecificaciones() throws ExceptionDTO {
        // Crear el producto
        Producto producto = new Producto(1L, "Pan Integral",
                LocalDateTime.of(2025, 3, 19, 15, 44, 39),
                LocalDateTime.of(2025, 4, 19, 15, 44, 39),
                new ArrayList<>());

        // Crear la especificación
        Especificacion especificacion1 = new Especificacion(1L, false, true,
                "Harina de trigo", producto);

        // Crear el DTO para el producto
        ProductoDTO productoDTO = new ProductoDTO();
        productoDTO.setId_producto(1L);
        productoDTO.setNombre("Pan Integral");
        productoDTO.setFechaInicio(LocalDateTime.of(2025, 3, 19, 15, 44, 39));
        productoDTO.setFechaFin(LocalDateTime.of(2025, 4, 19, 15, 44, 39));

        // Crear el DTO para la especificación
        EspecificacionDTO especificacionDTO = new EspecificacionDTO();
        especificacionDTO.setId_ingrediente(1L);
        especificacionDTO.setAlergenos(false);
        especificacionDTO.setGluten(true);
        especificacionDTO.setIngrediente("Harina de trigo");
        especificacionDTO.setProductoNombre("Pan Integral");
        especificacionDTO.setProducto(productoDTO);

        // Configurar comportamiento del repositorio
        when(especificacionRepository.save(any(Especificacion.class))).thenReturn(especificacion1);

        // Configurar el servicio para que devuelva el DTO esperado
//        EspecificacionService servicioEspia = spy(especificacionService);
//        doReturn(especificacionDTO).when(servicioEspia).pasarEntityADTO(any(Especificacion.class));
//        doReturn(especificacion1).when(servicioEspia).DtoAEntity(any(EspecificacionDTO.class));

        // Llamar al metodo que queremos probar (ajusta la firma según tu implementación)
        EspecificacionDTO especificacionIntroducida = especificacionService.crearEspecificacion(especificacionDTO, 1L);

        // Verificaciones
        assertThat(especificacionIntroducida.getId_ingrediente()).isEqualTo(1L);
        assertThat(especificacionIntroducida.isAlergenos()).isFalse();
        assertThat(especificacionIntroducida.isGluten()).isTrue();
        assertThat(especificacionIntroducida.getIngrediente()).isEqualTo("Harina de trigo");
        assertThat(especificacionIntroducida.getProductoNombre()).isEqualTo("Pan Integral");

        // Verificar que se llamó al método save del repositorio
        verify(especificacionRepository, times(1)).save(any(Especificacion.class));
    }

 */

}
