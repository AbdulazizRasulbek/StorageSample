package uz.drop.storagesample

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class SecondActivity : AppCompatActivity() {
    private val requestCode: Int = 100
    private val TAG = "AAA"
    lateinit var photoURI: Uri
    private val getCameraImage =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                Log.d("AAA", "got image $photoURI")
            }
            findViewById<ImageView>(R.id.imageview).setImageURI(photoURI)
            logFiles()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)
        findViewById<Button>(R.id.cameraOld).setOnClickListener {
            cameraOld()
        }
        val imageFile = createImageFile()
        photoURI = FileProvider.getUriForFile(
            this,
            BuildConfig.APPLICATION_ID,
            imageFile
        )
        findViewById<Button>(R.id.cameraNew).setOnClickListener {
            getCameraImage.launch(photoURI)
        }
        findViewById<Button>(R.id.clearImages).setOnClickListener {
            clearImages()
        }


    }

    private fun clearImages() {
        var success = false
        val externalFilesDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        if (externalFilesDir?.isDirectory == true) {
            val listFiles = externalFilesDir.listFiles()
            if (listFiles?.isEmpty() == true) {
                Toast.makeText(this, "No images", Toast.LENGTH_SHORT).show()
                return
            }
            listFiles?.forEach {
                success = it.delete()
            }
            if (success) {
                Toast.makeText(this, "Successfully deleted", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun logFiles() {
        val externalFilesDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        if (externalFilesDir?.isDirectory == true) {
            val listFiles = externalFilesDir.listFiles()
            listFiles?.forEach {
                Log.d(TAG, ": ${it.name}")
            }
        }
    }

    lateinit var currentPhotoPath: String

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())

        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }

    private fun cameraOld() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        this,
                        BuildConfig.APPLICATION_ID,
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, requestCode)
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (::currentPhotoPath.isInitialized) {
            findViewById<ImageView>(R.id.imageview).setImageURI(Uri.fromFile(File(currentPhotoPath)))
        }
        logFiles()
    }


}