# 📋 Proyecto de reservas de Vuelos y Hoteles

## 1. 📝 **Descripción**

El proyecto se trata sobre una ApiRest consistente en una interfaz que permite manejar el CRUD tanto de hoteles como de vuelos, pudiéndose ser usado por el cliente de una agencia de viajes o por los empleados, para cada uno (empleado y cliente) existen una serie de funciones (endpoints) las cuales tendrán unos accesos distintos dependiendo de la acción que se lleve a cabo.

### 📂 Estructura del proyecto: 
El proyecto es de tipo Maven y consta de la siguiente estructura:

> app
>
> ├── src
>
> │ ├── main
>
> │ │ ├── java/com/hackaboss/app
>
> │ │ │ ├── config/SecurityConfig.java
>
> │ │ │ ├── controllers/HabitacionesController.java, HotelController.java, VueloController.java
>
> │ │ │ ├── dtos/ExceptionDTO.java, HabitacionDTO.java, HotelDTO.java, ReservaHabitacionDTO.java, ReservaVueloDTO.java, UsuarioDTO.java, VueloDTO.java
>
> │ │ │ ├── entities/Habitacion.java, Hotel.java, Usuario.java, Vuelo.java
>
> │ │ │ ├── repositories/HabitacionRepository.java, HotelRepository.java, VueloRepository.java
>
> │ │ │ └── services/HabitacionService.java, HabitacionServiceInterface.java, HotelService.java, HotelServiceInterface.java, VueloService.java, VueloServiceInterface.java
>
> │ │ └── resources/application.properties, data.sql
>
> │ └── test/java/com/hackaboss/app/services/HotelServiceTest.java
>
> └── pom.xm
>
> │
>
> └── HotelVuelo.sql

## 2. 🔑 **Datos de conexión del proyecto**

Todos estos datos se pueden consultar en el application.properties de mi proyecto.

- 🗃️ La base de datos a la que se conectará será MySql y el nombre será reservas
- 👤 El usuario: root
- 🔒 Contraseña: vacía
- 🔐 Para Autenticación en Spring Security:
  - 👨‍💼 El usuario: empleado
  - 🔑 El password: 1234.

## 3. 🌐 **Endpoints del proyecto**

> Todos los empoints tiene como raíz `/agency` el cual he configurado en el archivo de application.properties con la siguiente instrucción: server.servlet.context-path=/agency. A partir de esta raíz, surgen todos los endpoints anotados en el controlador.

### 🔒 **Control de acceso:**

#### 1. Todos los usuarios y empleados tendrán acceso a los métodos **GET**:

##### Para hotel:

- 📋 Listar Hoteles: 
  ```
  http://localhost:8080/agency/hotels
  ```

- 🏨 Conseguir Hoteles por id: 
  ```
  http://localhost:8080/agency/hotels/{id}
  ```

- 🔍 Filtrar habitaciones con parámetros: 
  ```
  http://localhost:8080/agency/rooms?dateFrom=23/03/2025&dateTo=27/03/2025&destination=málaga
  ```

##### Para vuelo:

- ✈️ Buscar vuelo por id: 
  ```
  http://localhost:8080/agency/flights/{id}
  ```

- 🔍 Buscar vuelo por parámetros: 
  ```
  http://localhost:8080/agency/flights
  ```

#### 2. Todos los usuarios y empleados tendrán accesos a los siguientes métodos **POST**:

##### Para hotel:

- 📝 Hacer una reserva de Hotel: 
  ```
  http://localhost:8080/agency/room-booking/new
  ```

##### Para vuelo:

- 📝 Hacer reserva de vuelo: 
  ```
  http://localhost:8080/agency/flight-booking/new
  ```

#### 3. Resto de endpoints (solo para empleados):

> **⚠️ Solo tendrán acceso los empleados** - Esto implica la creación (método Post), modificación (método Put) o eliminación (método Delete, se eliminará a través de un eliminado lógico) de una reserva, hotel o vuelo de la base de datos.

##### Para Hotel:

- ➕ Agregar Hotel (Post): 
  ```
  http://localhost:8080/agency/hotels/new
  ```

- ✏️ Editar Hotel (Put): 
  ```
  http://localhost:8080/agency/hotels/edit/{id}
  ```

- 🗑️ Eliminar Hotel (Delete): 
  ```
  http://localhost:8080/agency/hotels/delete/{id}
  ```

> Hecho por mí aparte de la premisa de la prueba técnica:

- ➕ Agregar Habitación(Post): 
  ```
  http://localhost:8080/agency/rooms/new/{id}
  ```

##### Para Vuelos:

- ➕ Agregar Vuelo (Post): 
  ```
  http://localhost:8080/agency/flights/new
  ```

- ✏️ Editar Vuelo (Put): 
  ```
  http://localhost:8080/agency/flights/edit/{id}
  ```

- 📝 Hacer reserva Vuelo (Post): 
  ```
  http://localhost:8080/agency/flight-booking/new
  ```

- 🗑️ Eliminar Vuelo (Delete): 
  ```
  http://localhost:8080/agency/flights/delete/{id}
  ```

## 4. 📎 **Archivos adjuntos**

- 📄 Adjunto un archivo para uso de cada endpoint en el mismo directorio donde se encuentra el pom.xml.
- 📋 Archivo para agregar a postman de tipo json: hotelVuelos.json.
- 💾 Archivo sql para agregar a la base de datos MySql: hotelVuelos.sql.
- 📝 Junto a la carpeta del proyecto, un archivo README.md con los datos que contiene este documento junto a la carpeta app del directorio raíz.

## 5. 📊 **Explicación detallada de los endpoints**

Explicaré las principales funciones de cada endpoint haciendo alusión a la lógica de su correspondiente método en el service.

### 🏨 **En HotelService:**

#### Listar Hoteles: http://localhost:8080/agency/hotels

- 🔧 Método: listarHoteles.
- ✅ Listo los hoteles que estén activos (sin eliminado lógico).
- ⚠️ Lanzo Excepción con la clase ExceptionDTO creada por mí si no obtiene resultado.
- 📤 Devuelve List\<HotelDTO\>

#### Conseguir Hoteles por id: http://localhost:8080/agency/hotels/{id}

- 🔧 Método buscarHotel.
- ✅ Filtro que no tenga idHotel en null.
- ⚠️ Lanzo Excepción con la clase ExceptionDTO creada por mí si no obtiene resultado.
- 📤 Devuelve \<HotelDTO\>.

#### Agregar Hotel (Post): http://localhost:8080/agency/hotels/new

- 🔧 Método: crearHotel.
- ✅ Valoro que los datos no sean nulos (hotelDTO).
- ✅ Busco que no coincida con ningún otro hotel (lugar ni nombre).
- ✅ Genero un código para el hotel con el método generarCodigoHotel de la clase.
- ⚠️ Lanzo ExceptionDTO si el hotel a crear ya existe.
- 📤 Devuelve \<HotelDTO\>.

#### Editar Hotel (Put): http://localhost:8080/agency/hotels/edit/{id}

- 🔧 Método editarHotel.
- ✅ Valido que los parámetros estén introducidos, sino lanzo ExceptionDTO.
- ✅ Busco si el hotel está en alta con los parámetros, sino lanzo ExceptionDTO.
- ⚠️ Si existen reservas lanzo una ExceptionDTO.
- 📤 Devuelvo si todo sale bien: HotelDTO.

#### Eliminar Hotel (Delete): http://localhost:8080/agency/hotels/delete/{id}

- 🔧 Método eliminarHotel.
- ✅ Valido que el id no sea nulo, , sino lanzo ExceptionDTO.
- ⚠️ Si no encuentra resultado: ExceptionDTO.
- ⚠️ Si está reservado: ExceptionDTO.
- 📤 Devuelve si todo sale bien: ExceptionDTO con un mensaje favorable.

### 🛏️ **En HabitacionService:**

#### Filtrar habitaciones por parámetros: http://localhost:8080/agency/rooms?dateFrom=23/03/2025&dateTo=27/03/2025&destination=málaga

- 🔧 Método: listarDisponibleParámetro.
- ✅ Busco que no haya ninguna reserva, que el hotel esté en alto y que no esté reservado, sino lanzo ExceptionDTO.
- ✅ Voy haciendo un cribado conforme encuentre parámetros no nulos hasta obtener la lista filtrada final.
- ⚠️ Si no encuentra resultado: ExceptionDTO.

#### Hacer una reserva de Hotel: http://localhost:8080/agency/room-booking/new

- 🔧 Método: reservarHabitacion.
- ✅ Valido que los datos no sean nulos, sino lanzo ExceptionDTO.
- ✅ Verifico que las personas no excedan el número de personas introducidas, en el ReservaHabitacionDTO, si excede: ExceptionDTO.
- ✅ Verifico que exista la habitación, si no existe: ExceptionDTO.
- ✅ Verificar si está reservada, si lo está: ExceptionDTO.
- ✅ Verificar que las fechas estén en el rango de fechas para reservar, si no lo está: ExceptionDTO.
- ✅ Generaré reservas en los días de excedente de las fechas de reserva.
- 📤 Si todo va bien, devuelvo un objeto HabitacionDTO.

#### Agregar habitación (Post): http://localhost:8080/agency/rooms/new/{id}

- ✅ Verifico que exista el hotel, sino lanzo ExceptionDTO.
- ⚠️ Si no existe el hotel: lanzo Excepcion.

### ✈️ **En VueloService:**

#### Buscar vuelo por id: http://localhost:8080/agency/flights/{id}

- 🔧 Método: reservarHabitacion.
- ✅ Valido que no sea nulo el id, sino lanzo ExceptionDTO.
- ✅ Busco vuelo y filtro que esté en alta y que la lista de usuarios esté vacía, sino lanzo ExceptionDTO.
- ⚠️ Si no encuentra vuelos, lanzo ExceptioDTO.
- 📤 Devuelve VueloDTO.

#### Buscar vuelo por parámetros: http://localhost:8080/agency/flights

- 🔧 Método: buscarVuelos.
- ✅ Devuelvo VueloDTO en estado de alta tengan o no pasajeros.
- ⚠️ Si no encuentra: ExceptionDTO.

#### Hacer reserva Vuelo (Post): http://localhost:8080/agency/flight-booking/new

- 🔧 Método: reservarVuelo.
- ✅ Los vuelos reservados sólo se validarán si hay lista de usuarios, ya que otro parámetro de [isAlta] es para el borrado lógico (seguirá en la base de datos).
- ✅ Una vez hecha la reserva ya no se podrá ampliar la reserva (Lista de pasajeros).
- ✅ Filtro todos los parámetros que no sean nulos, sinó ExceptionDTO.
- ✅ Verifico que los usuarios no excedan la cantidad de usuarios puesta en el formulario, verifico si existe la habitación, sinó ExceptionDTO.
- ✅ Verifico que el vuelo esté en alta, sinó ExceptionDTO.
- ✅ Verifico que el vuelo no tenga usuarios, por lo que estára reservado, si hay: ExtionDTO.
- ✅ Verifico que el código y la fecha del vuelo coincida con la base de datos (esto lo hago para mayor verificación por si hay algún error introduciendo el id y se reserve un vuelo equivocado), sino lanzo ExceptionDTO.
- 📤 Devuelve ReservaVueloDTO.

#### Agregar Vuelo (Post): http://localhost:8080/agency/flights/new

- ✅ Filtro que los parámetros no estén nulos ni vacío, uso (isBlank).
- ✅ Filtro que no hayan vuelos con las mismas características, sinó lanzo ExceptionDTO.
- 📤 Devuelvo VueloDTO.

#### Editar Vuelo (Put): http://localhost:8080/agency/flights/edit/{id}

- 🔧 Método: editarVuelo.
- ✅ Verifico que los parámetros introducidos no sean nulos, sinó: ExceptionDTO.
- ✅ Verifico que el vuelo encontrado no contenga reservas (getUsuarios().isEmpty), sino: ExceptionDTO.
- ⚠️ Si no se encuentra el vuelo, ExceptioDTO.
- 📤 Devuelve VueloDTO.

#### Eliminar Vuelo (Delete): http://localhost:8080/agency/flights/delete/{id}

- 🔧 Método: eliminarVuelo.
- ✅ Hago un borrado lógico de la base de datos cambiándolo el alta a false.
- ✅ Encuentro en vuelo en la base de datos, sino: ExceptionDTO.
- ⚠️ Verifico si hay reservas, si hay lanzo una ExceptionDTO.
- 📤 Al final devuelvo un ExceptionDTO informando de que ha sido eliminado.

## 6. 🔄 **Relación entre entidades**

> Estableceré todas las relaciones entre estas entidades que serán todas de tipo EAGER, en cascada y orphanRemoval = true.

- **🔗 Relacion Hotel con Habitacion:**

> Existe una relación \@OneToMany de Hotel respecto a Habitación que lleva la anotación \@ManyToOne, además la entidad Hotel lleva la anotación \@JsonManagedReference para indicar que es la entidad principal y \@JsonBackReference para indicar el final de la relación y evitar así la redundancia de datos.

- **🔗 Relacion Habitación con Usuario:**

> Existe una relación \@OneToMany de Habitación respecto a Usuario que lleva la anotación \@ManyToOne, además la entidad Usuario lleva la anotación \@JsonManagedReference para indicar que es la entidad principal y \@JsonBackReference para indicar el final de la relación y evitar así la redundancia de datos.

- **🔗 Relacion Vuelo con Usuario:**

> Existe una relación \@OneToMany de Habitación respecto a Usuario que lleva la anotación \@ManyToOne, además la entidad Usuario lleva la anotación \@JsonManagedReference para indicar que es la entidad principal y \@JsonBackReference para indicar el final de la relación y evitar así la redundancia de datos.

- **🔗 Relacion de clases DTO:**

> Existen la misma relación de clases que sus homólogos sin DTO mencionados anteriormente, hay algunas diferencias como la anotación \@JsonIgnore para indicar que un atributo no va a ser tenido en cuenta a la hora de renderizar el objeto en la interfaz del usuario.
>
> Por otro lado. Existen dos clases que son ReservaHotelDTO y ReservaVueloDTO, las cuales han sido creadas para adaptar bien la interfaz que va a ser utilizada por el usuario, tanto en el envío como en la recepción de mensajes de reserva. Puesto que hay datos que no tienen que ser almacenados en la base de datos pero que se utilizarán para la lógica de mi programa, he decidido utilizar estas dos clases suplementariamente.

## 7. ⚙️ **Métodos de los services**

Además de los métodos procedentes de los controladores, hay otros métodos que cada clase de servicio usará para llevar a cabo la lógica de negocio, como pueden ser:

- 🏷️ **Generar código de hotel**: generarCodigoHotel.

  - Se encarga de generar un código en mayúscula de dos letras procedentes de la primera letra de cada palabra.
  - irá seguido de un guión (-) y seguidamente por un código de tres ceros (000) seguido del número que será el siguiente al anterior que encuentre en la base de datos.
  - Si no encuentra ninguno le asignará 0001;

- 🏷️ **Generar código de vuelo**: generarCodigoVUelo.

  - Realiza una lógica parecida pero el código del prefijo (letras), las cogerá de la primera letra de origen y de destino

> **🔄 Clases para transformar las clases:**

> Clases para transformar las clases que van al usuario (DTO) a entidades manejadas y viceversa por el back de nuestro proyecto.

- 🔄 HotelDTO a Hotel: hotelDtoToEntity.
- 🔄 Hotel a HotelDTO: hotelEntityToDTO.
- 🔄 Habitación a HabitacionDTO: habitacionDtoToEntity.
- 🔄 HabitacionDTO a Habitacion: habitacionEntityToDto.
- 🔄 Usuario a UsuarioDTO: usuarioEntityToDTO.
- 🔄 UsuarioDTO a Usuario: usuarioDTOToENtity.
- 🔄 Vuelo a VueloDTO: vueloEntityToDTO.
- 🔄 VueloDTO a Vuelo; vueloDTOToEntity.

## 8. 🎮 **Controladores**

He creado tres controladores: HotelService, HabitacionService y VueloService, las cuales son de tipo ApiRest y que recibirán peticiones de tipo Get, Post, Put y Delete de diferentes endpoints y las enviará hacia sus correspondientes métodos en su respectivo service asignado.

Por otro lado, todos los controladores devolverán una respuesta de tipo **ResponseEntity** la cual será la encargada de enviar la respuesta al usuario final. Los controladores también gestionarán la respuesta que procedente de su respectiva clase de service que serán de tipo **Object** hacia ResponseENtity.

Una Peculiaridad diferente, es que en el controlador VueloService en el método de listarPorParametros este maneja dos endpoins, uno se encargará de devolver la lista de todos los vuelos mediante el método listarVuelos si todos los parámetros de entrada son nulos, y el otro ejecutará filtrarVuelosActivosParametros si alguno de los parámetros que reciben no son nulos.

## 9. 🔒 **Clase config**

> Dentro tengo una clase llamada SecurityConfig, en ella he usado csrf que poder usarse con Postman y no lo bloquee, en producción se debería desactivar.
>
> En establezco en este archivo la seguridad de las peticiones de los endpoints de mi proyecto. He dejado todos los métodos **Get** sin seguridad para que puedan ser accedidos por clientes y empleados de la agencia de viajes.
>
> He dejado también dos endpoints de tipo **Post** sin seguridad para la reserva de habitaciones y de vuelos, estos están hechos para que tanto los clientes como los empleados puedan realizar reservas.

## 10. 📦 **Archivo pom.xml**

> Incluyo las siguientes dependencias:

- 🔒 Spring security (Seguridad mediante basic autentication).
- 💾 Jpa (para gestionar la peticiones con la base de datos).
- 🌐 Spring Web (para gestionar solicitudes HTTP).
- 🗄️ MySqlConector (para conectar con la base de datos MySql).
- 🧪 Mockito (para realizar test unitarios)
- 🧩 Lombok (permite ahorrar código al crear entidades u objetos).
- 📝 Swagger (interfaz web para documentación y prueba de los endspoints).
- ➕ Además de las incluidas por defecto en Springboot.

## 11. 🧪 **Test Unitarios**

He creado dentro de la carpeta test.java.com.hackaboss.app una carpeta llamada Services, en ella incluyo un archivo java llamado HotelServiceTest en el que hago una prueba unitaria para testear un método del servicio HotelService.

En este caso voy a hacer un test unitario con Junit de para hoteles llamado "listarHoteles". En este método creo dos objetos de tipo Hotel y valoro la salida del método hotelService.listarHoteles().

En este caso como todas las respuestas son de tipo Object, he tenido que pasar la respuesta al tipo que corresponde (List\<Hotel\> o ExceptionDTO), dependiendo de si se cambia un parámetro u otro (isAlta == true o false) en algún elemento de la lista que he creado, si la lista está vacía en la salida el Object contendrá un ExceptionDTO y si hay algún Hotel que encuentra arrojará una List\<Hotel\>, se procesará uno de estos dos tipos de salida mediante un "if".

## 12. 🏁 **Conclusión**

Con la elaboración de esta prueba técnica he resuelto todos los requisitos de la prueba técnica 4, aunque seguro que hay mas soluciones, he aplicado las que a mi parecer parecen las más acertadas.

Por otro lado, he comentado el código explicando los diferentes pasos de mi proyecto dentro de cada método para que este sea legible y comprensible para cualquiera que quisiera entender o desarrollar sobre él.

Por último, con la elaboración de esta prueba técnica he podido aprender muchísimo sobre los conceptos de java así como desarrollar todo lo aprendido en esta bootcamp, también he aclarado muchos conceptos mal aprendidos por mi parte en el pasado.
