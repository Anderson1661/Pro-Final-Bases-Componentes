package transportadora.Conductor

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
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
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class Act_foto_conductor : AppCompatActivity() {

    private lateinit var imgFotoPerfil: CircleImageView
    private lateinit var btnTomarFoto: Button
    private lateinit var btnSeleccionarGaleria: Button
    private lateinit var btnActualizarFoto: Button
    private lateinit var progressBar: ProgressBar

    private var currentPhotoPath: String? = null
    private var selectedImageUri: Uri? = null
    private var currentPhotoUrl: String? = null


    // Permisos
    private val cameraPermission = Manifest.permission.CAMERA
    private val storagePermission = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.READ_MEDIA_IMAGES
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }

    // Contracts para resultados de actividad
    private val takePictureResult = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            currentPhotoPath?.let { path ->
                val file = File(path)
                selectedImageUri = FileProvider.getUriForFile(
                    this,
                    "${packageName}.provider",
                    file
                )
                loadImage(selectedImageUri)
                btnActualizarFoto.isEnabled = true
            }
        }
    }

    private val pickImageResult = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            selectedImageUri = uri
            loadImage(selectedImageUri)
            btnActualizarFoto.isEnabled = true
        }
    }

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val cameraGranted = permissions[cameraPermission] ?: false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_act_foto_conductor)

        initViews()
        setupClickListeners()
        checkPermissions()
        receiveAndDisplayCurrentPhoto()
    }

    private fun initViews() {
        imgFotoPerfil = findViewById(R.id.imgFotoPerfil)
        btnSeleccionarGaleria = findViewById(R.id.btnSeleccionarGaleria)
        btnActualizarFoto = findViewById(R.id.btnActualizarFoto)
        progressBar = findViewById(R.id.progressBar)
    }

    private fun setupClickListeners() {
        btnSeleccionarGaleria.setOnClickListener {
            selectFromGallery()
        }

        btnActualizarFoto.setOnClickListener {
            selectedImageUri?.let { uri ->
                uploadImage(uri)
            } ?: run {
                Toast.makeText(this, "Primero selecciona una foto", Toast.LENGTH_SHORT).show()
            }
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

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir = getExternalFilesDir(null)
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        ).apply {
            currentPhotoPath = absolutePath
        }
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

    private fun uploadImage(imageUri: Uri) {
        progressBar.visibility = ProgressBar.VISIBLE
        btnActualizarFoto.isEnabled = false

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

                val requestBody = MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart(
                        "image",
                        "profile_${System.currentTimeMillis()}.jpg",
                        file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                    )
                    .build()

                val request = Request.Builder()
                    .url("${ApiConfig.BASE_URL}consultas/conductor/perfil/upload_photo.php")
                    .post(requestBody)
                    .build()

                val response = client.newCall(request).execute()
                val responseBody = response.body?.string()

                runOnUiThread {
                    progressBar.visibility = ProgressBar.GONE

                    if (response.isSuccessful && !responseBody.isNullOrEmpty()) {
                        try {
                            val json = JSONObject(responseBody)
                            if (json.getString("success") == "1") {
                                val imageUrl = json.getString("url")
                                updateProfilePhoto(imageUrl)
                            } else {
                                Toast.makeText(this@Act_foto_conductor, "Error al subir imagen", Toast.LENGTH_LONG).show()
                                btnActualizarFoto.isEnabled = true
                            }
                        } catch (e: Exception) {
                            Toast.makeText(this@Act_foto_conductor, "Error procesando respuesta", Toast.LENGTH_LONG).show()
                            btnActualizarFoto.isEnabled = true
                        }
                    } else {
                        Toast.makeText(this@Act_foto_conductor, "Error de conexión", Toast.LENGTH_LONG).show()
                        btnActualizarFoto.isEnabled = true
                    }
                }

            } catch (e: Exception) {
                runOnUiThread {
                    progressBar.visibility = ProgressBar.GONE
                    Toast.makeText(this@Act_foto_conductor, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                    btnActualizarFoto.isEnabled = true
                }
            }
        }
    }

    private fun updateProfilePhoto(imageUrl: String) {
        progressBar.visibility = ProgressBar.VISIBLE

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val client = OkHttpClient()

                // Obtener email del usuario desde SharedPreferences
                val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
                val userEmail = sharedPreferences.getString("user_email", "") ?: ""

                val formBody = FormBody.Builder()
                    .add("correo", userEmail)
                    .add("url_foto", imageUrl)
                    .build()

                val request = Request.Builder()
                    .url("${ApiConfig.BASE_URL}consultas/conductor/perfil/update_photo.php")
                    .post(formBody)
                    .build()

                val response = client.newCall(request).execute()
                val responseBody = response.body?.string()

                runOnUiThread {
                    progressBar.visibility = ProgressBar.GONE

                    if (response.isSuccessful && !responseBody.isNullOrEmpty()) {
                        try {
                            val json = JSONObject(responseBody)
                            if (json.getString("success") == "1") {
                                Toast.makeText(this@Act_foto_conductor, "Foto actualizada exitosamente", Toast.LENGTH_LONG).show()
                                finish() // Cerrar actividad después de actualizar
                            } else {
                                Toast.makeText(this@Act_foto_conductor, "Error al actualizar perfil", Toast.LENGTH_LONG).show()
                            }
                        } catch (e: Exception) {
                            Toast.makeText(this@Act_foto_conductor, "Error procesando respuesta", Toast.LENGTH_LONG).show()
                        }
                    } else {
                        Toast.makeText(this@Act_foto_conductor, "Error de conexión", Toast.LENGTH_LONG).show()
                    }
                }

            } catch (e: Exception) {
                runOnUiThread {
                    progressBar.visibility = ProgressBar.GONE
                    Toast.makeText(this@Act_foto_conductor, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
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