package transportadora.Network

import transportadora.Modelos.Usuario
import retrofit2.http.*

data class ApiResponse<T>(
    val success: String,
    val mensaje: String?,
    val datos: List<T>?
)

interface ApiService {

    // Leer todos los usuarios
    @POST("read.php")
    suspend fun getUsuarios(): ApiResponse<Usuario>

    // Crear usuario
    @FormUrlEncoded
    @POST("create.php")
    suspend fun crearUsuario(
        @Field("id_tipo_usuario") tipoUsuarioId: Int,
        @Field("correo") correo: String,
        @Field("contrasenia") contrasenia: String
    ): ApiResponse<Usuario>

    // Actualizar usuario
    @FormUrlEncoded
    @POST("update.php")
    suspend fun actualizarUsuario(
        @Field("id_usuario") id: Int,
        @Field("id_tipo_usuario") tipoUsuarioId: Int,
        @Field("correo") correo: String,
        @Field("contrasenia") contrasenia: String
    ): ApiResponse<Usuario>

    // Eliminar usuario
    @FormUrlEncoded
    @POST("delete.php")
    suspend fun eliminarUsuario(
        @Field("id_usuario") id: Int
    ): ApiResponse<Usuario>
}