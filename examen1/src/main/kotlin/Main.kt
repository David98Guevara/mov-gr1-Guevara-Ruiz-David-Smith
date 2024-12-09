import kotlinx.serialization.Serializable // Para marcar clases como serializables
import kotlinx.serialization.builtins.ListSerializer // Para serializar listas de objetos
import kotlinx.serialization.json.Json // Para trabajar con JSON
import java.io.File // Para leer y escribir archivos

// =========================== ENTIDADES ===========================

// La anotación @Serializable permite convertir esta clase a JSON y viceversa.
@Serializable
data class Carrera(
    val id: Int,               // ID único de la carrera
    val nombre: String,        // Nombre de la carrera
    val duracion: Int,         // Duración en años
    val activa: Boolean,       // Indica si la carrera está activa
    val fechaCreacion: String  // Fecha de creación en formato "dd/MM/yyyy"
)

// La clase Materia también es serializable y tiene una relación con Carrera mediante carreraId.
@Serializable
data class Materia(
    val id: Int,               // ID único de la materia
    val nombre: String,        // Nombre de la materia
    val creditos: Float,       // Créditos de la materia
    val obligatoria: Boolean,  // Indica si la materia es obligatoria
    val carreraId: Int         // Relación con una carrera (por ID)
)

// ========================= FUNCIONES DE ARCHIVO =========================

// Función genérica para guardar una lista de objetos en un archivo
// - Parámetros:
//   - archivo: Nombre del archivo donde se guardará la lista
//   - lista: Lista de objetos a guardar
//   - serializer: Serializer para convertir los objetos a JSON
fun <T> guardarEnArchivo(archivo: String, lista: List<T>, serializer: kotlinx.serialization.KSerializer<T>) {
    // Convertir la lista a JSON
    val json = Json.encodeToString(ListSerializer(serializer), lista)
    // Escribir el JSON en el archivo
    File(archivo).writeText(json)
}

// Función genérica para leer una lista de objetos desde un archivo
// - Parámetros:
//   - archivo: Nombre del archivo de donde se leerán los datos
//   - serializer: Serializer para convertir el JSON a objetos
// - Devuelve: Lista de objetos deserializados
fun <T> leerDesdeArchivo(archivo: String, serializer: kotlinx.serialization.KSerializer<T>): List<T> {
    val file = File(archivo)
    // Si el archivo no existe, devolver una lista vacía
    if (!file.exists()) return emptyList()
    // Leer el contenido del archivo y convertirlo de JSON a objetos
    val json = file.readText()
    return Json.decodeFromString(ListSerializer(serializer), json)
}

// ========================= FUNCIONES CRUD =========================

// Archivos donde se almacenan los datos
val archivoCarreras = "carreras.txt"
val archivoMaterias = "materias.txt"

// Crear una nueva carrera
// - Verifica si ya existe una carrera con el mismo ID antes de crearla.
fun crearCarrera(carrera: Carrera) {
    val carreras = leerDesdeArchivo(archivoCarreras, Carrera.serializer()).toMutableList()
    // Verificar si el ID ya existe
    if (carreras.any { it.id == carrera.id }) {
        println("Error: Ya existe una carrera con el ID ${carrera.id}.")
        return
    }
    // Agregar la nueva carrera y guardar en el archivo
    carreras.add(carrera)
    guardarEnArchivo(archivoCarreras, carreras, Carrera.serializer())
    println("Carrera creada exitosamente.")
}

// Leer todas las carreras desde el archivo
fun leerCarreras(): List<Carrera> {
    return leerDesdeArchivo(archivoCarreras, Carrera.serializer())
}

// Actualizar una carrera existente
fun actualizarCarrera(id: Int, nuevaCarrera: Carrera) {
    val carreras = leerDesdeArchivo(archivoCarreras, Carrera.serializer()).toMutableList()
    // Buscar la carrera por ID
    val index = carreras.indexOfFirst { it.id == id }
    if (index != -1) {
        // Reemplazar la carrera existente con la nueva
        carreras[index] = nuevaCarrera
        guardarEnArchivo(archivoCarreras, carreras, Carrera.serializer())
        println("Carrera actualizada exitosamente.")
    } else {
        println("Carrera con ID $id no encontrada.")
    }
}

// Eliminar una carrera
fun eliminarCarrera(id: Int) {
    val carreras = leerDesdeArchivo(archivoCarreras, Carrera.serializer()).toMutableList()
    // Filtrar para eliminar la carrera con el ID especificado
    val nuevaLista = carreras.filter { it.id != id }
    guardarEnArchivo(archivoCarreras, nuevaLista, Carrera.serializer())
    println("Carrera eliminada exitosamente.")
}

// Crear una nueva materia
// - Verifica si el ID ya existe y si la carrera asociada existe.
fun crearMateria(materia: Materia) {
    val materias = leerDesdeArchivo(archivoMaterias, Materia.serializer()).toMutableList()
    // Verificar si el ID ya existe
    if (materias.any { it.id == materia.id }) {
        println("Error: Ya existe una materia con el ID ${materia.id}.")
        return
    }
    // Verificar si la carrera asociada existe
    if (leerCarreras().none { it.id == materia.carreraId }) {
        println("Error: No existe una carrera con el ID ${materia.carreraId}.")
        return
    }
    // Agregar la nueva materia y guardar en el archivo
    materias.add(materia)
    guardarEnArchivo(archivoMaterias, materias, Materia.serializer())
    println("Materia creada exitosamente.")
}

// Leer todas las materias desde el archivo
fun leerMaterias(): List<Materia> {
    return leerDesdeArchivo(archivoMaterias, Materia.serializer())
}

// Actualizar una materia existente
fun actualizarMateria(id: Int, nuevaMateria: Materia) {
    val materias = leerDesdeArchivo(archivoMaterias, Materia.serializer()).toMutableList()
    // Buscar la materia por ID
    val index = materias.indexOfFirst { it.id == id }
    if (index != -1) {
        // Reemplazar la materia existente con la nueva
        materias[index] = nuevaMateria
        guardarEnArchivo(archivoMaterias, materias, Materia.serializer())
        println("Materia actualizada exitosamente.")
    } else {
        println("Materia con ID $id no encontrada.")
    }
}

// Eliminar una materia
fun eliminarMateria(id: Int) {
    val materias = leerDesdeArchivo(archivoMaterias, Materia.serializer()).toMutableList()
    // Filtrar para eliminar la materia con el ID especificado
    val nuevaLista = materias.filter { it.id != id }
    guardarEnArchivo(archivoMaterias, nuevaLista, Materia.serializer())
    println("Materia eliminada exitosamente.")
}

// Listar todas las materias asociadas a una carrera
fun listarMateriasPorCarrera(carreraId: Int) {
    val materias = leerMaterias().filter { it.carreraId == carreraId }
    if (materias.isEmpty()) {
        println("No hay materias asociadas a la carrera con ID $carreraId.")
    } else {
        println("Materias de la carrera con ID $carreraId:")
        materias.forEach { println(it) }
    }
}

// ========================= FUNCIONES PARA REINICIAR DATOS =========================

// Eliminar todos los datos de carreras
fun reiniciarCarreras() {
    guardarEnArchivo(archivoCarreras, emptyList(), Carrera.serializer())
    println("Todos los datos de carreras han sido eliminados.")
}

// Eliminar todos los datos de materias
fun reiniciarMaterias() {
    guardarEnArchivo(archivoMaterias, emptyList(), Materia.serializer())
    println("Todos los datos de materias han sido eliminados.")
}

// ========================= MENÚ POR CONSOLA =========================

// Menú principal del programa
fun menuPrincipal() {
    while (true) {
        println("\n===== Menú Principal =====")
        println("1. Gestionar Carreras")
        println("2. Gestionar Materias")
        println("3. Reiniciar Datos")
        println("4. Salir")
        print("Selecciona una opción: ")
        when (readln().toIntOrNull()) {
            1 -> menuCarreras()
            2 -> menuMaterias()
            3 -> menuReiniciarDatos()
            4 -> {
                println("¡Hasta luego!")
                break
            }
            else -> println("Opción no válida. Intenta de nuevo.")
        }
    }
}

// Menú para gestionar carreras
fun menuCarreras() {
    while (true) {
        println("\n===== Menú Carreras =====")
        println("1. Crear Carrera")
        println("2. Listar Carreras")
        println("3. Actualizar Carrera")
        println("4. Eliminar Carrera")
        println("5. Ver Materias de una Carrera")
        println("6. Volver al Menú Principal")
        print("Selecciona una opción: ")
        when (readln().toIntOrNull()) {
            1 -> {
                val carreras = leerCarreras()
                val id = if (carreras.isEmpty()) 1 else carreras.maxOf { it.id } + 1
                print("Nombre: "); val nombre = readln()
                print("Duración (años): "); val duracion = readln().toInt()
                print("Activa (true/false): "); val activa = readln().toBoolean()
                print("Fecha de Creación (dd/MM/yyyy): "); val fecha = readln()
                crearCarrera(Carrera(id, nombre, duracion, activa, fecha))
            }
            2 -> leerCarreras().forEach { println(it) }
            3 -> {
                print("ID de la carrera a actualizar: "); val id = readln().toInt()
                print("Nuevo Nombre: "); val nombre = readln()
                print("Nueva Duración (años): "); val duracion = readln().toInt()
                print("Activa (true/false): "); val activa = readln().toBoolean()
                print("Nueva Fecha de Creación (dd/MM/yyyy): "); val fecha = readln()
                actualizarCarrera(id, Carrera(id, nombre, duracion, activa, fecha))
            }
            4 -> {
                print("ID de la carrera a eliminar: "); val id = readln().toInt()
                eliminarCarrera(id)
            }
            5 -> {
                print("ID de la carrera: "); val carreraId = readln().toInt()
                listarMateriasPorCarrera(carreraId)
            }
            6 -> break
            else -> println("Opción no válida.")
        }
    }
}

// Menú para gestionar materias
fun menuMaterias() {
    while (true) {
        println("\n===== Menú Materias =====")
        println("1. Crear Materia")
        println("2. Listar Materias")
        println("3. Actualizar Materia")
        println("4. Eliminar Materia")
        println("5. Volver al Menú Principal")
        print("Selecciona una opción: ")
        when (readln().toIntOrNull()) {
            1 -> {
                val materias = leerMaterias()
                val id = if (materias.isEmpty()) 1 else materias.maxOf { it.id } + 1
                print("Nombre: "); val nombre = readln()
                print("Créditos: "); val creditos = readln().toFloat()
                print("Obligatoria (true/false): "); val obligatoria = readln().toBoolean()
                print("ID de la Carrera: "); val carreraId = readln().toInt()
                crearMateria(Materia(id, nombre, creditos, obligatoria, carreraId))
            }
            2 -> leerMaterias().forEach { println(it) }
            3 -> {
                print("ID de la materia a actualizar: "); val id = readln().toInt()
                print("Nuevo Nombre: "); val nombre = readln()
                print("Nuevos Créditos: "); val creditos = readln().toFloat()
                print("Obligatoria (true/false): "); val obligatoria = readln().toBoolean()
                print("ID de la Carrera: "); val carreraId = readln().toInt()
                actualizarMateria(id, Materia(id, nombre, creditos, obligatoria, carreraId))
            }
            4 -> {
                print("ID de la materia a eliminar: "); val id = readln().toInt()
                eliminarMateria(id)
            }
            5 -> break
            else -> println("Opción no válida.")
        }
    }
}

// Menú para reiniciar datos
fun menuReiniciarDatos() {
    println("¿Qué deseas reiniciar?")
    println("1. Carreras")
    println("2. Materias")
    println("3. Ambas")
    println("4. Cancelar")
    print("Selecciona una opción: ")
    when (readln().toIntOrNull()) {
        1 -> reiniciarCarreras()
        2 -> reiniciarMaterias()
        3 -> {
            reiniciarCarreras()
            reiniciarMaterias()
            println("Ambos datos han sido eliminados.")
        }
        4 -> println("Reinicio cancelado.")
        else -> println("Opción no válida.")
    }
}

// ========================= PUNTO DE ENTRADA =========================

fun main() {
    menuPrincipal() // Inicia el programa mostrando el menú principal
}
