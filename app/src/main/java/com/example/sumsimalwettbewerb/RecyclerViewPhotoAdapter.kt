package com.example.sumsimalwettbewerb

import android.app.AlertDialog
import android.content.Context
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.sumsimalwettbewerb.fragments.PhotoFragment
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class PhotoAdapter(private val context: Context, private val photos: List<Photo>) :
    RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.fragment_item_photo, parent, false)
        return PhotoViewHolder(view)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val photo = photos[position]

        val DEFAULT_WIDTH = 1000
        val DEFAULT_HEIGHT = 700
        val width = if (holder.photoImageView.width > 0) holder.photoImageView.width else DEFAULT_WIDTH
        val height = if (holder.photoImageView.height > 0) holder.photoImageView.height else DEFAULT_HEIGHT

        Picasso.get()
            .load(photo.imageUrl)
            .resize(width, height)
            .into(holder.photoImageView)

        holder.tvVoteCount.text = photo.voteCount.toString()

        holder.ivHeart.setOnClickListener {
            if (!photo.hasVoted) {
                showEmailDialog(photo.id, position)
            } else {
                Toast.makeText(context, "Sie haben dieses Bild bereits bewertet.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    class PhotoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val photoImageView: ImageView = itemView.findViewById(R.id.iv_photoImageView)
        val ivHeart: ImageView = itemView.findViewById(R.id.iv_heart)
        val tvVoteCount: TextView = itemView.findViewById(R.id.tv_vote_count)
    }


    override fun getItemCount(): Int {
        return photos.size
    }

    private fun showEmailDialog(photoId: String, position: Int) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_email_input, null)
        val emailEditText: EditText = dialogView.findViewById(R.id.emailEditText)

        fun updateVotesDisplay(newVoteCount: Int) {
            val photo = photos[position]
            photo.voteCount = newVoteCount
            photo.hasVoted = true
            notifyItemChanged(position)
        }

        AlertDialog.Builder(context)
            .setView(dialogView)
            .setTitle("Bewerten Sie dieses Foto")
            .setMessage("Geben Sie Ihre E-Mail-Adresse ein, um abzustimmen:")
            .setPositiveButton("Abstimmen") { dialog, _ ->
                val email = emailEditText.text.toString()
                if (email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    storeVote(photoId, email) { newVoteCount ->
                        updateVotesDisplay(newVoteCount)
                    }
                } else {
                    Toast.makeText(context, "Bitte geben Sie eine gültige E-Mail-Adresse ein", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
            .setNegativeButton("Abbrechen", null)
            .show()
    }
    private fun storeVote(submissionId: String, email: String, updateVotesDisplay: (Int) -> Unit) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://sumsi.dev.webundsoehne.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(ApiService::class.java)
        val voteBody = VoteBody(email)

        service.storeVote("Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9." +
                "eyJhdWQiOiIxIiwianRpIjoiOGMxY2RmMTJlMGExMzJkYmYxNGZkZDQ0N" +
                "jI1ZTA3ODFmZjA5MzkwNjZkODU1YzMyZmZhY2FhZDIxODU3ZDM0NTBjN" +
                "ThkZWQ4MmEyNDM2N2EiLCJpYXQiOjE3MDM2MTAwNzQuNzM0Mzk2LCJuYm" +
                "YiOjE3MDM2MTAwNzQuNzM0Mzk5LCJleHAiOjE3MzUyMzI0NzQuNzI3M" +
                "zEzLCJzdWIiOiIxIiwic2NvcGVzIjpbXX0.iJ8zGPtlc78E7c15hTFXp" +
                "wyk8XTj5qcNcEo08H88lsZ8jjeO3f5W0ux4tEYhPOJ5bhkuzKBfTgAjf2" +
                "fS0vu39rqde-wWiCFcZ-B778RnFxAmFgVf_6FICw33_G4lCUdiTPjRBB" +
                "RQWtPV78X5KvJMS3SIcPwW6TC9suJF5D49Zu8OtOPENoH4Off_pWTF3p0" +
                "n8czoLjIf0V4fcFW8fivdq5qhEVHSflW7H5x-3DFnjnueN81CTXgHs2Zl" +
                "Vzgvkhxxlpgz7cNduzj9oCCvMvdNUXI2PWYMzyL8kUwf8BJNEs47EWg2Br" +
                "VS4bi_rbBzIHDwPGbAy6nsql6QxtzcJW6QgK-tzK7UHjEPNPrAH5Hpj1" +
                "Ow--IZ4votVFkT8PW2BwDsGBlqmHqIpONb4O7hCMae7zB2TIgLXvCgl" +
                "Dr5AzVNmFN9K1XGM-uHBw1CCWmoYc_XfyLi3fcOwEnzGAsfAyv5XbRg" +
                "HoYg4djbUO7ZiFuM9ixU7HyJoWhBU9PadHm9YEnqLeWOr3BC_VrSLtG6-" +
                "eerre20WEBZYPMAoSL8psCFhDrySn8eTARKvY2PagKNsYTJCYm9Y3pt2W" +
                "VlpwA-yLC5rUbOqI9CJLjtjBZj5v6wMjPyyb0aXXPPPHIfLoJGRR_9GBi" +
                "TgyCDeGaf8TO5yK1OspgCY1YAUQVh_DEr3eIzgWc", submissionId, voteBody).enqueue(object : Callback<VoteResponse> {
            override fun onResponse(call: Call<VoteResponse>, response: Response<VoteResponse>) {
                if (response.isSuccessful) {
                    val voteCount = response.body()?.data?.votes ?: 0

                    updateVotesDisplay(voteCount)

                    Toast.makeText(context, "Ihre Stimme wurde erfolgreich gespeichert", Toast.LENGTH_SHORT).show()
                } else {
                    Log.e("VoteStoreError", "Error while saving the vote. Code: ${response.code()}, Message: ${response.message()}")
                    try {
                        val errorBody = response.errorBody()?.string()
                        Log.e("VoteStoreError", "Error body: $errorBody")
                    } catch (e: Exception) {
                        Log.e("VoteStoreError", "Error parsing error body", e)
                    }
                    Toast.makeText(context, "Error while saving the vote", Toast.LENGTH_SHORT).show()
                }
                updateVoteCount(submissionId)
            }

            override fun onFailure(call: Call<VoteResponse>, t: Throwable) {
                Log.e("VoteStoreFailure", "Network error", t)
                Toast.makeText(context, "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateVoteCount(submissionId: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://sumsi.dev.webundsoehne.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val service = retrofit.create(ApiService::class.java)

        service.countVotes("Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9." +
                "eyJhdWQiOiIxIiwianRpIjoiOGMxY2RmMTJlMGExMzJkYmYxNGZkZDQ0N" +
                "jI1ZTA3ODFmZjA5MzkwNjZkODU1YzMyZmZhY2FhZDIxODU3ZDM0NTBjN" +
                "ThkZWQ4MmEyNDM2N2EiLCJpYXQiOjE3MDM2MTAwNzQuNzM0Mzk2LCJuYm" +
                "YiOjE3MDM2MTAwNzQuNzM0Mzk5LCJleHAiOjE3MzUyMzI0NzQuNzI3M" +
                "zEzLCJzdWIiOiIxIiwic2NvcGVzIjpbXX0.iJ8zGPtlc78E7c15hTFXp" +
                "wyk8XTj5qcNcEo08H88lsZ8jjeO3f5W0ux4tEYhPOJ5bhkuzKBfTgAjf2" +
                "fS0vu39rqde-wWiCFcZ-B778RnFxAmFgVf_6FICw33_G4lCUdiTPjRBB" +
                "RQWtPV78X5KvJMS3SIcPwW6TC9suJF5D49Zu8OtOPENoH4Off_pWTF3p0" +
                "n8czoLjIf0V4fcFW8fivdq5qhEVHSflW7H5x-3DFnjnueN81CTXgHs2Zl" +
                "Vzgvkhxxlpgz7cNduzj9oCCvMvdNUXI2PWYMzyL8kUwf8BJNEs47EWg2Br" +
                "VS4bi_rbBzIHDwPGbAy6nsql6QxtzcJW6QgK-tzK7UHjEPNPrAH5Hpj1" +
                "Ow--IZ4votVFkT8PW2BwDsGBlqmHqIpONb4O7hCMae7zB2TIgLXvCgl" +
                "Dr5AzVNmFN9K1XGM-uHBw1CCWmoYc_XfyLi3fcOwEnzGAsfAyv5XbRg" +
                "HoYg4djbUO7ZiFuM9ixU7HyJoWhBU9PadHm9YEnqLeWOr3BC_VrSLtG6-" +
                "eerre20WEBZYPMAoSL8psCFhDrySn8eTARKvY2PagKNsYTJCYm9Y3pt2W" +
                "VlpwA-yLC5rUbOqI9CJLjtjBZj5v6wMjPyyb0aXXPPPHIfLoJGRR_9GBi" +
                "TgyCDeGaf8TO5yK1OspgCY1YAUQVh_DEr3eIzgWc", submissionId).enqueue(object : Callback<VoteCountResponse> {
            override fun onResponse(call: Call<VoteCountResponse>, response: Response<VoteCountResponse>) {
                if (response.isSuccessful) {
                    val voteCount = response.body()?.data?.votes ?: 0
                    updateVotesDisplay(submissionId, voteCount)
                } else {
                    Log.e("VoteCountError", "Fehler beim Abfragen der Stimmenanzahl")
                }
            }

            override fun onFailure(call: Call<VoteCountResponse>, t: Throwable) {
                Log.e("VoteCountFailure", "Netzwerkfehler", t)
            }
        })
    }

    private fun updateVotesDisplay(submissionId: String, newVoteCount: Int) {

        val photoIndex = photos.indexOfFirst { it.id == submissionId }
        if (photoIndex != -1) {
            val photo = photos[photoIndex]
            photo.voteCount = newVoteCount
            photo.hasVoted = true
            notifyItemChanged(photoIndex)
        } else {
            Log.e("PhotoFragment", "Foto mit der ID $submissionId nicht gefunden.")
        }
    }

}

