package com.example.uas.admin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.uas.R
import com.example.uas.account.TabLayoutActivity
import com.example.uas.database.Movies
import com.example.uas.databinding.ActivityMainAdminBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainAdminActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainAdminBinding

    private val db = FirebaseFirestore.getInstance()
    private val reportCollectionRef = db.collection("movies")
    private lateinit var executorService: ExecutorService
    private val movieListLiveData: MutableLiveData<List<Movies>> by lazy {
        MutableLiveData<List<Movies>>()
    }
    private val listMovies = mutableListOf<Movies>() // Tambahkan list untuk menyimpan data yang akan ditampilkan
    private lateinit var rvAdapter: RvAdminAdapter // Deklarasikan adapter di sini
    private var store: StorageReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        executorService = Executors.newSingleThreadExecutor()
        binding = ActivityMainAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        rvAdapter = RvAdminAdapter(listMovies,
            onItemClick = { movie ->
                executorService.execute {
                    val position = listMovies.indexOf(movie)
                    val selectedMovies = listMovies[position] // posisi gambar yang diklik
                    val intent = Intent(this@MainAdminActivity, EditActivity::class.java)
                    intent.putExtra("SELECTED_MOVIES", selectedMovies)
                    startActivity(intent)
//                    startActivityForResult(intent, 2)
                }
            },
            onItemLongClick = { movie ->
                deleteMovies(movie = movie) }
        )

        binding.MyRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = rvAdapter

        }

        binding.addBtn.setOnClickListener{
            val intent = Intent(this@MainAdminActivity, AddActivity::class.java)
            startActivity(intent)
        }

        binding.backBtn.setOnClickListener{
            val intent = Intent(this@MainAdminActivity, TabLayoutActivity::class.java)
            startActivity(intent)
        }

        observeMovies()
        getAllMovies()

        Log.d("MainAdminActivity", "RecyclerView initialization")

    }

    private fun getAllMovies() {
        observeMoviesChanges()
    }
    private fun observeMovies() {
        movieListLiveData.removeObservers(this) // Hapus observer sebelum menambahkan yang baru
        movieListLiveData.observe(this) { report->
            listMovies.clear()
            listMovies.addAll(report)
            runOnUiThread {
                rvAdapter.notifyDataSetChanged()
            }
        }
        Log.d("observe", "masuk")
    }
    private fun observeMoviesChanges() {
        reportCollectionRef.addSnapshotListener { snapshots, error->
            if (error != null) {
                Log.d("MainActivity", "Error listening for budget changes: ", error)
                return@addSnapshotListener
            }
            val reports = snapshots?.toObjects(Movies::class.java)
            if (reports != null) {
                movieListLiveData.postValue(reports)
            }
        }
    }
    private fun deleteMovies(movie: Movies) {
        if (movie.id.isEmpty()) {
            Log.d("MainActivity", "Error deleting: movie ID is empty!")
            return
        }
        reportCollectionRef.document(movie.id).delete()
            .addOnFailureListener {
                Log.d("MainActivity", "Error deleting movie: ", it)
            }
    }
}