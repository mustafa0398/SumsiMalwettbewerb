package com.example.sumsimalwettbewerb

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import android.Manifest
import android.app.AlertDialog
import android.provider.Settings
import android.widget.ImageView

class UploadFragment : Fragment() {

    private lateinit var webView: WebView
    private lateinit var etLegalGuardianFirstName: EditText
    private lateinit var etLegalGuardianLastName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etChildFirstName: EditText
    private lateinit var etChildAge: EditText
    private lateinit var cbTerms: CheckBox
    private lateinit var cbParticipation: CheckBox
    private lateinit var cbMailNotification: CheckBox
    private lateinit var selectedImageView: ImageView

    private var selectedImageUri: Uri? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.upload_images, container, false)

        webView = view.findViewById(R.id.information_contest)
        webView.webViewClient = WebViewClient()
        webView.settings.javaScriptEnabled = true

        webView.setBackgroundColor(Color.parseColor("#FBF315"))

        val css = resources.getString(R.string.custom_css)
        val htmlString = getString(R.string.information_contest)
        val html = """<html><head><style type="text/css">$css</style></head><body>$htmlString</body></html>"""
        webView.loadDataWithBaseURL(null, html, "text/html", "utf-8", null)

        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        etLegalGuardianFirstName = view.findViewById(R.id.etLegalGuardianFirstName)
        etLegalGuardianLastName = view.findViewById(R.id.etLegalGuardianLastName)
        etEmail = view.findViewById(R.id.etEmail)
        etChildFirstName = view.findViewById(R.id.etChildFirstName)
        etChildAge = view.findViewById(R.id.etChildAge)
        cbTerms = view.findViewById(R.id.cbTerms)
        cbParticipation = view.findViewById(R.id.cbParticipation)
        cbMailNotification = view.findViewById(R.id.cbMailNotification)
        selectedImageView = view.findViewById(R.id.selectedImage)

        view.findViewById<Button>(R.id.btnUpload).setOnClickListener {
            if (cbTerms.isChecked) {
                openGalleryForImage()
            } else {
                Toast.makeText(context, "Bitte akzeptieren Sie die Nutzungsbedingungen.", Toast.LENGTH_SHORT).show()
            }
        }

        view.findViewById<Button>(R.id.btnSubmit).setOnClickListener {
            selectedImageUri?.let { uri ->
                uploadImageToServer(uri)
            } ?: run {
                Toast.makeText(context, "Bitte wählen Sie zuerst ein Bild aus.", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun openGalleryForImage() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_MEDIA_IMAGES
            ) == PackageManager.PERMISSION_GRANTED -> {
                startImagePicker()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.READ_MEDIA_IMAGES) -> {
                showRationaleDialog()
            }
            else -> {
                showSettingsDialog()
            }
        }
    }

    private fun showSettingsDialog() {
        val alertDialog = AlertDialog.Builder(requireContext())
        alertDialog.apply {
            setTitle("Berechtigung erforderlich")
            setMessage("Die Berechtigung zum Lesen des externen Speichers wurde verweigert. Bitte öffnen Sie die Einstellungen und erteilen Sie die Berechtigung manuell.")
            setPositiveButton("Einstellungen öffnen") { _, _ ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package", requireActivity().packageName, null)
                intent.data = uri
                startActivity(intent)
            }
            setNegativeButton("Ablehnen") { dialog, _ ->
                dialog.dismiss()
            }
            create().show()
        }
    }

    private fun showRationaleDialog() {
        val alertDialog = AlertDialog.Builder(requireContext())
        alertDialog.apply {
            setTitle("Berechtigung erforderlich")
            setMessage("Diese App benötigt Zugriff auf Ihr Speichergerät, um Bilder hochzuladen. Bitte erlauben Sie den Zugriff.")
            setPositiveButton("Erlauben") { _, _ ->
                requestPermissions(
                    arrayOf(Manifest.permission.READ_MEDIA_IMAGES),
                    REQUEST_PERMISSION_READ_MEDIA_IMAGES
                )
            }
            setNegativeButton("Ablehnen") { dialog, _ ->
                dialog.dismiss()
            }
            create().show()
        }
    }

    private fun startImagePicker() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_CODE_IMAGE_PICK)
    }

    companion object {
        private const val REQUEST_CODE_IMAGE_PICK = 1000
        private const val REQUEST_PERMISSION_READ_MEDIA_IMAGES = 2000
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_IMAGE_PICK) {
            selectedImageUri = data?.data
            selectedImageUri?.let { uri ->
                selectedImageView.setImageURI(uri)
                selectedImageView.visibility = View.VISIBLE
            }
        }
    }

    private fun uploadImageToServer(imageUri: Uri) {
        val file = File(getRealPathFromURI(imageUri))
        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
        val imagePart = MultipartBody.Part.createFormData("image", file.name, requestFile)

        val legalGuardianFirstName = MultipartBody.Part.createFormData(
            "legalguardian_firstname",
            etLegalGuardianFirstName.text.toString().trim()
        )
        val legalGuardianLastName = MultipartBody.Part.createFormData(
            "legalguardian_lastname",
            etLegalGuardianLastName.text.toString().trim()
        )
        val email = MultipartBody.Part.createFormData(
            "email",
            etEmail.text.toString().trim()
        )
        val childFirstName = MultipartBody.Part.createFormData(
            "child_firstname",
            etChildFirstName.text.toString().trim()
        )
        val childAge = MultipartBody.Part.createFormData(
            "child_age",
            etChildAge.text.toString().trim()
        )
        val privacyPolicyValue = if (cbTerms.isChecked) "1" else "0"
        val participationValue = if (cbParticipation.isChecked) "1" else "0"
        val mailNotificationValue = if(cbMailNotification.isChecked) "1" else "0"

        val privacyPolicy = MultipartBody.Part.createFormData(
            "approval_privacypolicy",
            privacyPolicyValue
        )
        val participation = MultipartBody.Part.createFormData(
            "approval_participation",
            participationValue
        )
        val mailNotification = MultipartBody.Part.createFormData(
            "approval_mailnotification",
            mailNotificationValue)

        val retrofit = Retrofit.Builder()
            .baseUrl("https://sumsi.dev.webundsoehne.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(ApiService::class.java)
        val call = service.uploadImage(
            "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJhdWQiOiIxIiwianRpIjoiOGMxY2RmMTJlMGExMzJkYmYxNGZkZDQ0NjI1ZTA3ODFmZjA5MzkwNjZkODU1YzMyZmZhY2FhZDIxODU3ZDM0NTBjNThkZWQ4MmEyNDM2N2EiLCJpYXQiOjE3MDM2MTAwNzQuNzM0Mzk2LCJuYmYiOjE3MDM2MTAwNzQuNzM0Mzk5LCJleHAiOjE3MzUyMzI0NzQuNzI3MzEzLCJzdWIiOiIxIiwic2NvcGVzIjpbXX0.iJ8zGPtlc78E7c15hTFXpwyk8XTj5qcNcEo08H88lsZ8jjeO3f5W0ux4tEYhPOJ5bhkuzKBfTgAjf2fS0vu39rqde-wWiCFcZ-B778RnFxAmFgVf_6FICw33_G4lCUdiTPjRBBRQWtPV78X5KvJMS3SIcPwW6TC9suJF5D49Zu8OtOPENoH4Off_pWTF3p0n8czoLjIf0V4fcFW8fivdq5qhEVHSflW7H5x-3DFnjnueN81CTXgHs2ZlVzgvkhxxlpgz7cNduzj9oCCvMvdNUXI2PWYMzyL8kUwf8BJNEs47EWg2BrVS4bi_rbBzIHDwPGbAy6nsql6QxtzcJW6QgK-tzK7UHjEPNPrAH5Hpj1Ow--IZ4votVFkT8PW2BwDsGBlqmHqIpONb4O7hCMae7zB2TIgLXvCglDr5AzVNmFN9K1XGM-uHBw1CCWmoYc_XfyLi3fcOwEnzGAsfAyv5XbRgHoYg4djbUO7ZiFuM9ixU7HyJoWhBU9PadHm9YEnqLeWOr3BC_VrSLtG6-eerre20WEBZYPMAoSL8psCFhDrySn8eTARKvY2PagKNsYTJCYm9Y3pt2WVlpwA-yLC5rUbOqI9CJLjtjBZj5v6wMjPyyb0aXXPPPHIfLoJGRR_9GBiTgyCDeGaf8TO5yK1OspgCY1YAUQVh_DEr3eIzgWc", // Ersetze YOUR_AUTH_KEY mit deinem tatsächlichen Token
            imagePart,
            legalGuardianFirstName,
            legalGuardianLastName,
            email,
            childFirstName,
            childAge,
            privacyPolicy,
            participation,
            mailNotification
        )

        call.enqueue(object : Callback<SubmissionResponse> {
            override fun onResponse(call: Call<SubmissionResponse>, response: Response<SubmissionResponse>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    responseBody?.let {
                        Toast.makeText(context, "Upload erfolgreich: ${it.message}", Toast.LENGTH_LONG).show()
                        Log.d("UploadSuccess", "Upload erfolgreich: ${it.message}")
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Toast.makeText(context, "Fehler beim Upload: $errorBody", Toast.LENGTH_LONG).show()
                    Log.e("UploadError", "Fehler beim Upload: $errorBody")
                }
            }

            override fun onFailure(call: Call<SubmissionResponse>, t: Throwable) {
                Toast.makeText(context, "Netzwerkfehler oder anderer Fehler: ${t.message}", Toast.LENGTH_LONG).show()
                Log.e("UploadFailure", "Netzwerkfehler oder anderer Fehler: ${t.message}")
            }
        })
    }

    private fun getRealPathFromURI(contentUri: Uri): String {
        val cursor = context?.contentResolver?.query(contentUri, null, null, null, null)
        cursor?.moveToFirst()
        val index = cursor?.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
        val path = index?.let { cursor.getString(it) }
        cursor?.close()
        return path ?: ""
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSION_READ_MEDIA_IMAGES) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startImagePicker()
            } else {
                Toast.makeText(context, "Berechtigung verweigert", Toast.LENGTH_SHORT).show()
            }
        }
    }

}