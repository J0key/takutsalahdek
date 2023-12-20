package com.example.uas.users

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.uas.R
import com.example.uas.database.Movies

class RvUserAdapter(
    private var listMovie: MutableList<Movies>,
    private val onItemClick: (Movies) -> Unit,
) : RecyclerView.Adapter<RvUserAdapter.MovieViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.container_movie_admin, parent, false)
        return MovieViewHolder(view)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val currentReport = listMovie[position]
        holder.title.text = currentReport.title
        holder.year.text = currentReport.year
        Glide.with(holder.itemView.context)
            .load(currentReport.imagePath)
            .into(holder.imageRV)
    }

    override fun getItemCount(): Int {
        return listMovie.size
    }

    inner class MovieViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.title)
        val year: TextView = itemView.findViewById(R.id.year)
        val imageRV: ImageView = itemView.findViewById(R.id.image)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(listMovie[position])
                }
            }
        }
    }

    fun updateData(newMovies: List<Movies>) {
        listMovie.clear()
        listMovie.addAll(newMovies)
        notifyDataSetChanged()
    }
}
