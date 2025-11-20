package transportadora.Conductor

import android.Manifest
import java.util.Base64
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import transportadora.Configuracion.ApiConfig
import transportadora.Login.R
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.withContext
import transportadora.Compartido.Main
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class Act_foto_conductor : AppCompatActivity() {

    private lateinit var imgFotoPerfil: CircleImageView
    private lateinit var btnSeleccionarGaleria: Button
    private lateinit var progressBar: ProgressBar

    private var currentPhotoPath: String? = null
    private var selectedImageUri: Uri? = null
    private var currentPhotoUrl: String? = null
    private var userEmail: String? = null

    // Permisos
    private val storagePermission = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.READ_MEDIA_IMAGES
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }

    // Contract para seleccionar imagen de galería
    private val pickImageResult = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            selectedImageUri = uri
            loadImage(selectedImageUri)
            // Subir automáticamente la imagen cuando se selecciona
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                uploadImage(uri)
            } else {
                Toast.makeText(this, "Requiere Android 8.0 o superior", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        // Permisos manejados
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_act_foto_conductor)

        initViews()
        setupClickListeners()
        checkPermissions()
        receiveAndDisplayCurrentPhoto()

        // Recibir el correo del intent
        userEmail = intent.getStringExtra("USER_EMAIL")
        if (userEmail.isNullOrEmpty()) {
            // Si no viene del intent, obtener de SharedPreferences
            val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
            userEmail = sharedPreferences.getString("user_email", "")
        }

        Log.d("Act_foto_conductor", "Email recibido: $userEmail")
    }

    private fun initViews() {
        imgFotoPerfil = findViewById(R.id.imgFotoPerfil)
        btnSeleccionarGaleria = findViewById(R.id.btnSeleccionarGaleria)
        progressBar = findViewById(R.id.progressBar)
    }

    private fun setupClickListeners() {
        btnSeleccionarGaleria.setOnClickListener {
            selectFromGallery()
        }
    }

    private fun checkPermissions() {
        val permissionsToRequest = mutableListOf<String>()

        // Verificar permiso de almacenamiento/galería
        if (ContextCompat.checkSelfPermission(this, storagePermission) != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(storagePermission)
        }

        if (permissionsToRequest.isNotEmpty()) {
            permissionLauncher.launch(permissionsToRequest.toTypedArray())
        }
    }

    private fun selectFromGallery() {
        pickImageResult.launch("image/*")
    }

    private fun loadImage(uri: Uri?) {
        uri?.let {
            Glide.with(this)
                .load(uri)
                .placeholder(R.drawable.fondo_main)
                .error(R.drawable.fondo_main)
                .into(imgFotoPerfil)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun uploadImage(imageUri: Uri) {
        progressBar.visibility = ProgressBar.VISIBLE
        btnSeleccionarGaleria.isEnabled = false

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val client = OkHttpClient()

                // Obtener el archivo real desde el URI
                val inputStream = contentResolver.openInputStream(imageUri)
                val file = File(cacheDir, "temp_image.jpg")
                inputStream?.use { input ->
                    file.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }

                // Convertir imagen a Base64 para ImgBB
                val fileBytes = file.readBytes()
                val base64Image = Base64.getEncoder().encodeToString(fileBytes)

                // Crear request body para ImgBB CON API KEY
                val requestBody = MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("key", ApiConfig.Key_imgbb)
                    .addFormDataPart("image", base64Image)
                    .build()

                // Usar la API de ImgBB CON API KEY
                val request = Request.Builder()
                    .url("https://api.imgbb.com/1/upload")
                    .post(requestBody)
                    .build()

                val response = client.newCall(request).execute()
                val responseBody = response.body?.string()

                runOnUiThread {
                    progressBar.visibility = ProgressBar.GONE
                    btnSeleccionarGaleria.isEnabled = true

                    Log.d("ImgBB_Response", "Status: ${response.code}")
                    Log.d("ImgBB_Response", "Body: $responseBody")

                    if (response.isSuccessful && !responseBody.isNullOrEmpty()) {
                        try {
                            val json = JSONObject(responseBody)
                            val success = json.getBoolean("success")

                            if (success) {
                                val data = json.getJSONObject("data")
                                val imageUrl = data.getString("url") // URL permanente de ImgBB

                                // Guardar la URL y actualizar en la base de datos
                                currentPhotoUrl = imageUrl
                                Log.d("ImgBB", "✅ Imagen subida exitosamente: $imageUrl")

                                // Actualizar en la base de datos
                                CoroutineScope(Dispatchers.IO).launch {
                                    val exito = Actualizarfoto(imageUrl)
                                    runOnUiThread {
                                        if (exito) {
                                            Toast.makeText(
                                                this@Act_foto_conductor,
                                                "✅ Foto de perfil actualizada exitosamente",
                                                Toast.LENGTH_LONG
                                            ).show()

                                            // Regresar al perfil
                                            val intent = Intent(this@Act_foto_conductor, Perfil_conductor::class.java)
                                            startActivity(intent)
                                            finish()
                                        } else {
                                            Toast.makeText(
                                                this@Act_foto_conductor,
                                                "❌ Error al actualizar en la base de datos",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                    }
                                }

                            } else {
                                val error = json.getJSONObject("error")
                                val errorMessage = error.getString("message")
                                val errorCode = error.getInt("code")
                                Toast.makeText(
                                    this@Act_foto_conductor,
                                    "❌ Error de ImgBB (Código $errorCode): $errorMessage",
                                    Toast.LENGTH_LONG
                                ).show()
                                Log.e("ImgBB", "Error: $errorMessage, Code: $errorCode")
                            }
                        } catch (e: Exception) {
                            Toast.makeText(
                                this@Act_foto_conductor,
                                "❌ Error procesando respuesta de ImgBB: ${e.message}",
                                Toast.LENGTH_LONG
                            ).show()
                            Log.e("ImgBB", "Error parsing: ${e.message}")
                        }
                    } else {
                        Toast.makeText(
                            this@Act_foto_conductor,
                            "❌ Error de conexión con ImgBB. Código: ${response.code}",
                            Toast.LENGTH_LONG
                        ).show()
                        Log.e("ImgBB", "Response not successful. Code: ${response.code}, Body: $responseBody")
                    }
                }

            } catch (e: Exception) {
                runOnUiThread {
                    progressBar.visibility = ProgressBar.GONE
                    btnSeleccionarGaleria.isEnabled = true
                    Toast.makeText(
                        this@Act_foto_conductor,
                        "❌ Error de red: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                    Log.e("ImgBB", "Exception: ${e.message}")
                }
            }
        }
    }

    private suspend fun Actualizarfoto(urlFoto: String): Boolean = withContext(Dispatchers.IO) {
        try {
            // Verificar que tenemos el correo
            if (userEmail.isNullOrEmpty()) {
                Log.e("Actualizarfoto", "Error: No hay correo disponible")
                return@withContext false
            }

            // Llamar al PHP de actualizar
            val url = java.net.URL(ApiConfig.BASE_URL + "consultas/conductor/perfil/actualizar_imagen.php")

            val jsonParams = org.json.JSONObject().apply {
                put("correo", userEmail)
                put("url_foto", urlFoto)
            }

            val params = jsonParams.toString()
            Log.d("Actualizar_imagen", "Enviando: $params")

            val connection = url.openConnection() as java.net.HttpURLConnection
            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8")
            connection.doOutput = true
            connection.outputStream.write(params.toByteArray(java.nio.charset.StandardCharsets.UTF_8))

            val responseCode = connection.responseCode
            Log.d("Actualizar_imagen", "Response code: $responseCode")

            if (responseCode == java.net.HttpURLConnection.HTTP_OK) {
                val response = connection.inputStream.bufferedReader().use { it.readText() }
                connection.disconnect()
                Log.d("Actualizar_imagen", "Respuesta: $response")

                val json = org.json.JSONObject(response)
                return@withContext json.getString("success") == "1"
            } else {
                val errorResponse = connection.errorStream?.bufferedReader()?.use { it.readText() } ?: "No hay mensaje de error"
                Log.e("Actualizar_imagen", "HTTP Error $responseCode. Detalles: $errorResponse")
                connection.disconnect()
                return@withContext false
            }

        } catch (e: Exception) {
            Log.e("Actualizar_imagen", "Error registro completo: ${e.message}", e)
            return@withContext false
        }
    }

    private fun receiveAndDisplayCurrentPhoto() {
        currentPhotoUrl = intent.getStringExtra("CURRENT_PHOTO_URL")

        if (!currentPhotoUrl.isNullOrEmpty() && currentPhotoUrl != "null") {
            // Cargar la imagen actual usando Picasso con transformación de tamaño
            Picasso.get()
                .load(currentPhotoUrl)
                .placeholder(R.drawable.fondo_main)
                .error(R.drawable.fondo_main)
                .resize(400, 400) // Tamaño específico en píxeles
                .centerCrop() // Recortar para que encaje perfectamente
                .into(imgFotoPerfil, object : com.squareup.picasso.Callback {
                    override fun onSuccess() {
                        Log.d("Act_foto_conductor", "✅ Foto actual cargada y redimensionada exitosamente")
                    }

                    override fun onError(e: Exception?) {
                        Log.e("Act_foto_conductor", "❌ Error cargando foto actual: ${e?.message}")
                        imgFotoPerfil.setImageResource(R.drawable.fondo_main)
                    }
                })
        } else {
            imgFotoPerfil.setImageResource(R.drawable.fondo_main)
        }
    }
}