package uz.drop.storagesample

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.io.FileOutputStream

/**
 * Activity that take image from gallery and save it to app-specific directory in internal storage
 * with old Activity APIs and new ActivityResults APIs
 * */
class MainActivity : AppCompatActivity() {
    private val CODE_REQUEST: Int = 100
    lateinit var imageView: ImageView
    private val TAG = "AAA"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        imageView = findViewById(R.id.imageview)
        findViewById<Button>(R.id.btnOld).setOnClickListener {
            pickImageFromGalleryOld()
        }
        findViewById<Button>(R.id.btnNew).setOnClickListener {
            pickImageFromGalleryNew()
        }
        findViewById<Button>(R.id.clearImages).setOnClickListener {
            clearImages()
        }
    }


    private fun pickImageFromGalleryOld() {
        startActivityForResult(
            Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "image/*"

            },
            CODE_REQUEST
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CODE_REQUEST && resultCode == Activity.RESULT_OK) {
            val uri: Uri = data?.data ?: return
            imageView.setImageURI(uri)
            val ins = contentResolver?.openInputStream(uri)
            val file = File(filesDir, "image.jpg")
            val fileOutputStream = FileOutputStream(file)
            ins?.copyTo(fileOutputStream)
            ins?.close()
            fileOutputStream.close()
            val absolutePath = file.absolutePath
            Toast.makeText(this, "file copied to $absolutePath", Toast.LENGTH_LONG).show()
            Log.d("AAA", absolutePath)
            val fileList = fileList()
            fileList.forEach {
                Log.d(TAG, "onActivityResult: $it")
            }
        }
    }

    private fun pickImageFromGalleryNew() {
        getImageContent.launch("image/*")
    }

    private val getImageContent =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri ?: return@registerForActivityResult
            imageView.setImageURI(uri)
            val ins = contentResolver?.openInputStream(uri)
            val file = File(filesDir, "imageNew.jpg")
            val fileOutputStream = FileOutputStream(file)
            ins?.copyTo(fileOutputStream)
            ins?.close()
            fileOutputStream.close()
            val absolutePath = file.absolutePath
            Toast.makeText(this, "file copied to $absolutePath", Toast.LENGTH_LONG).show()
            Log.d("AAA", absolutePath)
            fileList().forEach {
                Log.d(TAG, "onActivityResult: $it")
            }
        }

    private fun clearImages() {
        var success = false
        val externalFilesDir = filesDir
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
}