package com.example.uas.admin

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.uas.R
import com.example.uas.database.Movies
import com.example.uas.databinding.ActivityAddBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.text.SimpleDateFormat

class AddActivity : AppCompatActivity() {
    private lateinit var bindingAddMovies: ActivityAddBinding
    private var imageUri: Uri? = null
    private var store: StorageReference? = null
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)

        bindingAddMovies = ActivityAddBinding.inflate(layoutInflater)
        setContentView(bindingAddMovies.root)

//         buat dropdown rating
//        val rate = resources.getStringArray(R.array.rate)
//        val arrayAdapter = ArrayAdapter(this, R.layout.item_container_rating, rate)
//        bindingAddMovies.EditMovieRating.setAdapter(arrayAdapter)

        // buat upload image
        bindingAddMovies.imageMovie.setOnClickListener {
            selectImage()
        }

        with(bindingAddMovies) {
            ButtonSubmitMovie.setOnClickListener {
                if (!isAllFieldsFilled()) {
                    Toast.makeText(this@AddActivity, "Please fill all fields including image", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                else{
                    uploadData()
                }
            }
        }
    }

    // fungsi terkait CRUD
    private fun isAllFieldsFilled(): Boolean {
        with(bindingAddMovies) {
            return EditMovieTitle.text?.isNotBlank() == true &&
                    EditMovieYear.text?.isNotBlank() == true &&
//                    EditMovieRating.text?.isNotBlank() == true &&
                    EditMovieDescription.text?.isNotBlank() == true &&
                    imageUri != null
        }
    }
    private fun addMovie(movie: Movies) {
        db.collection("movies")
            .add(movie)
            .addOnSuccessListener { documentReference->
                val createdMovieId = documentReference.id
                movie.id = createdMovieId
                documentReference.set(movie)
                    .addOnFailureListener {
                        Log.d("MainActivity", "Error updating movie ID: ", it)
                    }
                val intent = Intent(this@AddActivity, mainAdminActivity::class.java)
                startActivity(intent)
                finish()
            }
    }


    // fungsi terkait image
    private fun selectImage(){
        val intentImage = Intent()
        intentImage.type = "image/*"
        intentImage.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intentImage, 100)
    }
    private fun uploadData() {

        // nambahin proses loading
        val progression = android.app.ProgressDialog(this)
        progression.setTitle("Uploading Movie Image...")
        progression.show()

        // untuk nama in imagenya
        val sdf = SimpleDateFormat("ddMMyyhhmmss")
        val imageName = sdf.format(System.currentTimeMillis())
        val imageExtension = ".jpg"

        // buat upload foto
        store = FirebaseStorage.getInstance().getReference("image/$imageName$imageExtension")
        store!!.putFile(imageUri!!)
            .addOnSuccessListener {
                bindingAddMovies.imageMovie.setImageURI(null)
                Toast.makeText(this, "Upload Image Success", Toast.LENGTH_SHORT).show()
                store!!.downloadUrl.addOnSuccessListener { uri ->
                    val downloadUrl = uri.toString()
                    // Now you can use the download URL as needed
                    Log.d("Download URL", downloadUrl.toString())
                    with(bindingAddMovies) {
                        val title = EditMovieTitle.text.toString()
                        val director = EditMovieYear.text.toString()
                        val description = EditMovieDescription.text.toString()
                        val rating = EditMovieRating.text.toString()
                        val movie = Movies(
                            imagePath = downloadUrl,
                            title = title,
                            year = director,
                            description = description,
                            rating = rating,
                            isTrending = false
                        )
                        addMovie(movie)
                    }
                    progression.dismiss()
                }
            }
    }
    // nyambungin request code ke image
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK && requestCode == 100 && data != null && data.data != null){
            imageUri = data.data
            bindingAddMovies.imageMovie.setImageURI(imageUri)
        }
    }
}