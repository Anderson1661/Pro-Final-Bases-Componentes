package transportadora.Modelos

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Usuario(
    @SerializedName("id_usuario") val id: Int,
    @SerializedName("id_tipo_usuario") val tipoUsuarioId: Int,
    @SerializedName("correo") val correo: String,
    @SerializedName("contrasenia") val contrasenia: String
) : Parcelable