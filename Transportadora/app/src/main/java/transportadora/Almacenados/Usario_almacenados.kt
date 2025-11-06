package transportadora.Almacenados

import transportadora.Modelos.Usuario
import transportadora.Network.ApiService
import transportadora.Network.RetrofitClient

class Usuario_almacenados {
    private val api = RetrofitClient.retrofit.create(ApiService::class.java)

    suspend fun listar(): List<Usuario>? {
        val response = api.getUsuarios()
        return if (response.success == "1") response.datos else null
    }

    suspend fun crear(usuario: Usuario): Boolean {
        val response = api.crearUsuario(
            tipoUsuarioId = usuario.tipoUsuarioId,
            correo = usuario.correo,
            contrasenia = usuario.contrasenia
        )
        return response.success == "1"
    }

    suspend fun actualizar(usuario: Usuario): Boolean {
        val response = api.actualizarUsuario(
            id = usuario.id,
            tipoUsuarioId = usuario.tipoUsuarioId,
            correo = usuario.correo,
            contrasenia = usuario.contrasenia
        )
        return response.success == "1"
    }

    suspend fun eliminar(id: Int): Boolean {
        val response = api.eliminarUsuario(id)
        return response.success == "1"
    }
}