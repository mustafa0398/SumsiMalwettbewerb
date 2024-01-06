package com.example.sumsimalwettbewerb

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class PhotoAdapter(private val context: Context, private val photos: List<String>) :
    RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.fragment_item_photo, parent, false)
        return PhotoViewHolder(view)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val photoNameOrUrl = photos[position]
        Log.d("PhotoAdapter", "Position $position: $photoNameOrUrl")
        val width = if (holder.photoImageView.width == 0) 800 else holder.photoImageView.width
        val height = if (holder.photoImageView.height == 0) 600 else holder.photoImageView.height

        val fullImageUrl = if (photoNameOrUrl.startsWith("http")) photoNameOrUrl else "https://sumsi.dev.webundsoehne.com$photoNameOrUrl"
        Picasso.get()
            .load(fullImageUrl)
            .resize(width, height)
            .centerInside()
            .into(holder.photoImageView)

        // Static rating bar
        holder.ratingBar.rating = 3.5f

        if (position < 4) {
            val photoId = context.resources.getIdentifier(photoNameOrUrl, "drawable", context.packageName)
            if (photoId != 0) {
                Picasso.get()
                    .load(photoId)
                    .resize(width, height)
                    .centerInside()
                    .into(holder.photoImageView)
            }
        }
    }


    override fun getItemCount(): Int {
        return photos.size
    }

    class PhotoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val photoImageView: ImageView = itemView.findViewById(R.id.iv_photoImageView)
        val ratingBar: RatingBar = itemView.findViewById(R.id.photoRatingBar)
    }
}



