package com.example.sumsimalwettbewerb

import android.app.AlertDialog
import android.content.Context
import android.graphics.BitmapFactory
import android.net.NetworkCapabilities
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
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import android.net.ConnectivityManager

class PhotoAdapter(private val context: Context, private var photos: MutableList<Photo>) :
    RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder>() {

    class PhotoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val photoImageView: ImageView = itemView.findViewById(R.id.iv_photoImageView)
        val ivHeart: ImageView = itemView.findViewById(R.id.iv_heart)
        val tvVoteCount: TextView = itemView.findViewById(R.id.tv_vote_count)
    }

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


        if (isOnline(context)) {
            Picasso.get()
                .load(photo.imageUrl)
                .resize(width, height)
                .into(holder.photoImageView)
        } else {
            photo.localImagePath?.let { path ->
                val bitmap = BitmapFactory.decodeFile(path)
                holder.photoImageView.setImageBitmap(bitmap)
            }
        }

        holder.tvVoteCount.text = photo.voteCount.toString()

        holder.ivHeart.setOnClickListener {
            if (!photo.hasVoted) {
                showEmailDialog(photo.id, position)
            } else {
                Toast.makeText(context, "Sie haben dieses Bild bereits bewertet.", Toast.LENGTH_SHORT).show()
            }
        }

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

        service.storeVote("Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJSU" +
                "zI1NiJ9.eyJhdWQi" + "OiIxIiwianRpIjoiYTExNjYwNDE0ZTJjM2ExODBmZGN" +
                "mOGIxZTExYzcwMjlkMThkOTA2ZmYxZjZiNGU1YjUwYzJhMTNjZDA1ZTQ4ZmZlZTM3ZGZkYWYy" +
                "ZjQ3N2IiLCJpYXQiOjE3MDUwMDk5NzEuNDAwNDY4LCJuYmYiOjE3MDUwM" +
                "Dk5NzEuNDAwNDc1LCJleHAiOjE3MzY2MzIzNzEuMzkwMjY1LCJzdWIiOiI" +
                "xIiwic2NvcGVzIjpbXX0.NvUJHZ_XC6Lj1M0cWUBvPu9ahG5QR_d-wvoM" +
                "vYXoqjUl8rO6kccbMifthMQA5OuXT1l_8A0EwDkM8LqJTaOdMKM9UNZiy5" +
                "iYFKGm97yksJXYmyk0g2xRjJ6tG2JBqJCL0y3dLs8yj9Ba7rWsOdfSTcJ" +
                "22WE4wmjp9nt9QKHY3paIeV97u5F9FsrIOms2gjQfu2XGk1vrKkHSjnNhbWu" +
                "4Xnmp77lfbYWzOYavVKLWRwByeVOHiSz6o4rW9QazqmG9B2DVbR4NIAc0Euj" +
                "VrAGJ56o5o_NHuk2kGdYXqvR7oexyEILGEFhsF4qGwBNvofahXgLiOAob-rF" +
                "MgUKUxy5Vz-tz5cHsoVxCrljUu8mYl8kwUwacql2YKUto_K7iH5cufFULWBLXQ" +
                "vbZUH8Tw_c-VbguPBK4ZfFDK78Kg4c7VEl7-ICQPCnrGFWX49DjTrSuCq4L_6H" +
                "xY4s-EaM2EcftysebVN4UEZPA49xzyRD8sH71TLFXoliyrghKfo8h7YOU4GJ5" +
                "yoWvR9htc8ZwkhjcMWpQiWUasuQHFr_KPF8fKXI2Gqcr8oymv-363iJaVHJ" +
                "v5KYBoHc7bB5e5ED4ZleVpkdnpB5tBbNyutsnhfrH13r8_AnrMk03O30hGJX" +
                "EsZBJRHBd_JuwGiQTR2KnaS8uCpMeRcj5cmZeK6" +
                "XKrgyNf9k", submissionId, voteBody).enqueue(object : Callback<VoteResponse> {
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

        service.countVotes("Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJSU" +
                "zI1NiJ9.eyJhdWQi" + "OiIxIiwianRpIjoiYTExNjYwNDE0ZTJjM2ExODBmZGN" +
                "mOGIxZTExYzcwMjlkMThkOTA2ZmYxZjZiNGU1YjUwYzJhMTNjZDA1ZTQ4ZmZlZTM3ZGZkYWYy" +
                "ZjQ3N2IiLCJpYXQiOjE3MDUwMDk5NzEuNDAwNDY4LCJuYmYiOjE3MDUwM" +
                "Dk5NzEuNDAwNDc1LCJleHAiOjE3MzY2MzIzNzEuMzkwMjY1LCJzdWIiOiI" +
                "xIiwic2NvcGVzIjpbXX0.NvUJHZ_XC6Lj1M0cWUBvPu9ahG5QR_d-wvoM" +
                "vYXoqjUl8rO6kccbMifthMQA5OuXT1l_8A0EwDkM8LqJTaOdMKM9UNZiy5" +
                "iYFKGm97yksJXYmyk0g2xRjJ6tG2JBqJCL0y3dLs8yj9Ba7rWsOdfSTcJ" +
                "22WE4wmjp9nt9QKHY3paIeV97u5F9FsrIOms2gjQfu2XGk1vrKkHSjnNhbWu" +
                "4Xnmp77lfbYWzOYavVKLWRwByeVOHiSz6o4rW9QazqmG9B2DVbR4NIAc0Euj" +
                "VrAGJ56o5o_NHuk2kGdYXqvR7oexyEILGEFhsF4qGwBNvofahXgLiOAob-rF" +
                "MgUKUxy5Vz-tz5cHsoVxCrljUu8mYl8kwUwacql2YKUto_K7iH5cufFULWBLXQ" +
                "vbZUH8Tw_c-VbguPBK4ZfFDK78Kg4c7VEl7-ICQPCnrGFWX49DjTrSuCq4L_6H" +
                "xY4s-EaM2EcftysebVN4UEZPA49xzyRD8sH71TLFXoliyrghKfo8h7YOU4GJ5" +
                "yoWvR9htc8ZwkhjcMWpQiWUasuQHFr_KPF8fKXI2Gqcr8oymv-363iJaVHJ" +
                "v5KYBoHc7bB5e5ED4ZleVpkdnpB5tBbNyutsnhfrH13r8_AnrMk03O30hGJX" +
                "EsZBJRHBd_JuwGiQTR2KnaS8uCpMeRcj5cmZeK6" +
                "XKrgyNf9k", submissionId).enqueue(object : Callback<VoteCountResponse> {
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

    fun updateData(newPhotos: List<Photo>) {
        this.photos.clear()
        this.photos.addAll(newPhotos)
        notifyDataSetChanged()
    }

    fun isOnline(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        val network = connectivityManager?.activeNetwork
        val connection = connectivityManager?.getNetworkCapabilities(network)
        return connection != null && connection.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}