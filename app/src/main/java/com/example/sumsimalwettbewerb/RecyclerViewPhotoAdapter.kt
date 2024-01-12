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
import androidx.core.content.ContentProviderCompat.requireContext

class PhotoAdapter(private val context: Context, private var photos: MutableList<Photo>) :
    RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder>() {

    class PhotoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val photoImageView: ImageView = itemView.findViewById(R.id.iv_photoImageView)
        val ivHeart: ImageView = itemView.findViewById(R.id.iv_heart)
        val tvVoteCount: TextView = itemView.findViewById(R.id.tv_vote_count)
        val tvChildDetails: TextView = itemView.findViewById(R.id.tvChildDetails)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.fragment_item_photo, parent, false)
        return PhotoViewHolder(view)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val photo = photos[position]

        Log.d("MyTag", "Position: $position")

        val childName =photo.childName
        val childAge = photo.childAge

        Log.d("MyTag", "Name: $childName, Alter: $childAge")

        val DEFAULT_WIDTH = 1000
        val DEFAULT_HEIGHT = 700
        val width = if (holder.photoImageView.width > 0) holder.photoImageView.width else DEFAULT_WIDTH
        val height = if (holder.photoImageView.height > 0) holder.photoImageView.height else DEFAULT_HEIGHT


        if (isOnline(context)) {
            photo.imageUrl?.let { url ->
                if (url.isNotBlank()) {
                    Picasso.get()
                        .load(url)
                        .resize(width, height)
                        .into(holder.photoImageView)
                }
            }
        } else {
            photo.localImagePath?.let { path ->
                if (path.isNotBlank()) {
                    val bitmap = BitmapFactory.decodeFile(path)
                    holder.photoImageView.setImageBitmap(bitmap)
                }
            }
        }


        holder.tvVoteCount.text = photo.voteCount.toString()

        holder.ivHeart.setOnClickListener {
            if (!photo.hasVoted) {
                showEmailDialog(photo.id, position)
            } else {
                Toast.makeText(context, (R.string.toast_Sie_haben), Toast.LENGTH_SHORT).show()
            }
        }

        val childDetails = "${photo.childName}, ${photo.childAge}"
        holder.tvChildDetails.text = childDetails


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
            .setTitle(R.string.setTitel_Bewerten)
            .setMessage(R.string.mass_Geben_Sie_Ihre)
            .setPositiveButton((R.string.butto_Abstimmen)) { dialog, _ ->
                val email = emailEditText.text.toString()
                if (email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    storeVoteWithCheck(photoId, email) { newVoteCount ->
                        updateVotesDisplay(newVoteCount)
                    }
                } else {
                    Toast.makeText(context, (R.string.mass_Bitte), Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
            .setNegativeButton((R.string.neBu_Abbrechen), null)
            .show()
    }

    private fun storeVoteWithCheck(submissionId: String, email: String, updateVotesDisplay: (Int) -> Unit) {
        fetchSettings { settings ->
            if (settings?.data?.voting_open == true) {
                storeVote(submissionId, email, updateVotesDisplay)
            } else {
                Toast.makeText(context, "Die Abstimmung ist derzeit nicht möglich.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun fetchSettings(callback: (SettingsResponse?) -> Unit) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://sumsi.dev.webundsoehne.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(ApiService::class.java)
        val authToken = RetrofitClient.getStoredAuthToken(this.context)
        Log.d("PhotoAdapter", "AuthToken: $authToken")

        service.getSettings("Bearer $authToken").enqueue(object : Callback<SettingsResponse> {
            override fun onResponse(call: Call<SettingsResponse>, response: Response<SettingsResponse>) {
                if (response.isSuccessful) {
                    callback(response.body())
                } else {
                    callback(null)
                }
            }

            override fun onFailure(call: Call<SettingsResponse>, t: Throwable) {
                callback(null)
            }
        })
    }

    private fun storeVote(submissionId: String, email: String, updateVotesDisplay: (Int) -> Unit) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://sumsi.dev.webundsoehne.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(ApiService::class.java)
        val authToken = RetrofitClient.getStoredAuthToken(this.context)
        Log.d("PhotoAdapter", "AuthToken: $authToken")
        val voteBody = VoteBody(email)

        service.storeVote("Bearer $authToken", submissionId, voteBody).enqueue(object : Callback<VoteResponse> {
            override fun onResponse(call: Call<VoteResponse>, response: Response<VoteResponse>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()

                    if (responseBody?.status == "error") {
                        val errorMessage = when (responseBody.message) {
                            "Only 5 votes per user allowed." -> "Pro E-Mail Adresse kann man nur 5 Stimmen vergeben"
                            "Only 1 vote per image allowed." -> "Pro Bild kann nur eine Stimme abgegeben werden"
                            else -> "Ein unbekannter Fehler ist aufgetreten"
                        }
                        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                    } else {
                        val voteCount = responseBody?.data?.votes ?: 0
                        updateVotesDisplay(voteCount)
                        Toast.makeText(context, (R.string.toast_3), Toast.LENGTH_SHORT).show()
                        updateVoteCount(submissionId)
                    }
                } else {
                    val errorMessage = when (response.code()) {
                        500 -> (R.string.Ein_Fehler_ist)
                        else -> context.getString(R.string.EinFehler_ist)+ "${response.code()}"
                    }
                    Toast.makeText(context, R.string.errorMessage, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<VoteResponse>, t: Throwable) {
                Toast.makeText(context, "Netzwerkfehler: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateVoteCount(submissionId: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://sumsi.dev.webundsoehne.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val service = retrofit.create(ApiService::class.java)
        val authToken = RetrofitClient.getStoredAuthToken(this.context)

        Log.d("PhotoAdapter", "AuthToken: $authToken")

        service.countVotes("Bearer $authToken", submissionId).enqueue(object : Callback<VoteCountResponse> {
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
        photos.clear()
        photos.addAll(newPhotos)
        notifyDataSetChanged()
    }

    companion object {
        fun isOnline(context: Context): Boolean {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
            val network = connectivityManager?.activeNetwork
            val connection = connectivityManager?.getNetworkCapabilities(network)
            return connection != null && connection.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        }
    }
}