package com.example.sumsimalwettbewerb

import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

// RecyclerViewPhotoAdapter
class PhotoAdapter(private val context: Context, private val photos: List<String>) :
    RecyclerView.Adapter<PhotoAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.fragment_item_photo, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val photoPath = photos[position]
        Glide.with(context)
            .load(Uri.parse(photoPath))
            .into(holder.photoImageView)

        // Set up RatingBar
        holder.ratingBar.setOnRatingBarChangeListener { _, rating, _ ->

            Log.d("PhotoAdapter", "Rating changed for position $position: $rating")
        }
    }

    override fun getItemCount(): Int {
        return photos.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val photoImageView: ImageView = itemView.findViewById(R.id.iv_photoImageView)
        val ratingBar: RatingBar = itemView.findViewById(R.id.photoRatingBar)
    }
}
