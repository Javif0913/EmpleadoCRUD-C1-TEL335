package org.example.project

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.request.*
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import kotlinx.serialization.Serializable

//CRUD empleado
@Serializable
data class Empleado(
    val id: Int,
    val nombre: String,
    val cargo: String,
    val departamentoId: Int
)

val empleados = mutableListOf(
    Empleado(id = 1, nombre = "Ana Torres", cargo = "Ingeniera de Software", departamentoId = 2),
    Empleado(id = 2, nombre = "Carlos Pérez", cargo = "Analista de Datos",departamentoId = 2),
    Empleado(id = 3, nombre = "Lucía Gómez", cargo = "Diseñadora UX", departamentoId = 3)
)

//Contadpr para asignar id a nuevos empleados
var empleadoIdCounter = 4

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    configureSerialization()

    routing {

        get("/") {
            call.respondText("Hola, soy Javiera Fuentes. CRUD de Empleados con Departamentos :) !!")
        }

        //mostrar todos los empleados
        get("/empleados") {
            call.respond(empleados)
        }

        //mostrar empleado por id
        get("/empleados/{id}") {
            val id = call.parameters["id"]?.toInt()
            val empleado = empleados.find { it.id == id }
            if (empleado != null) {
                call.respond(empleado)
            } else {
                call.respond(HttpStatusCode.NotFound, "Empleado no encontrado")
            }
        }

        //crear nuevo empleado
        post("/empleados") {
            val nuevoEmpleado = call.receive<Empleado>()
            val empleadoConId = nuevoEmpleado.copy(id = empleadoIdCounter++)
            empleados.add(empleadoConId)
            call.respond(HttpStatusCode.Created, empleadoConId)
        }

        //actualizar empleado
        put("/empleados/{id}") {
            val id = call.parameters["id"]?.toInt()
            val empleadoActualizado = call.receive<Empleado>()
            val index = empleados.indexOfFirst { it.id == id }
            if (index != -1) {
                empleados[index] = empleadoActualizado.copy(id = id!!)
                call.respond(HttpStatusCode.OK, empleados[index])
            } else {
                call.respond(HttpStatusCode.NotFound, "Empleado no encontrado")
            }
        }

        //eliminar empleado
        delete("/empleados/{id}") {
            val id = call.parameters["id"]?.toInt()
            val eliminado = empleados.removeIf { it.id == id }
            if (eliminado) {
                call.respond(HttpStatusCode.OK, "Empleado eliminado")
            } else {
                call.respond(HttpStatusCode.NotFound, "Empleado no encontrado")
            }
        }
    }
}

fun Application.configureSerialization() {
    install(ContentNegotiation) {
        json()
    }
}

