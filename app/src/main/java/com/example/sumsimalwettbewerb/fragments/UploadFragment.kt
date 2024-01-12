package com.example.sumsimalwettbewerb.fragments

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
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
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Build
import android.provider.Settings
import android.widget.ImageView
import com.example.sumsimalwettbewerb.ApiService
import com.example.sumsimalwettbewerb.PhotoAdapter
import com.example.sumsimalwettbewerb.R
import com.example.sumsimalwettbewerb.RetrofitClient
import com.example.sumsimalwettbewerb.SettingsResponse
import com.example.sumsimalwettbewerb.SubmissionResponse

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


    companion object {
        private const val REQUEST_CODE_IMAGE_PICK = 1000
        private const val REQUEST_PERMISSION_READ_EXTERNAL_STORAGE = 2000
        private const val REQUEST_PERMISSION_READ_MEDIA_IMAGES = 3000
    }

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
                Toast.makeText(context, getString(R.string.toast), Toast.LENGTH_SHORT).show()
            }
        }

        view.findViewById<Button>(R.id.btnSubmit).setOnClickListener {
            selectedImageUri?.let { uri ->
                uploadImageToServer(uri)
            } ?: run {
                Toast.makeText(context, getString(R.string.toast2), Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun openGalleryForImage() {
        val permission: String
        val requestCode: Int

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permission = Manifest.permission.READ_MEDIA_IMAGES
            requestCode = REQUEST_PERMISSION_READ_MEDIA_IMAGES
        } else {
            permission = Manifest.permission.READ_EXTERNAL_STORAGE
            requestCode = REQUEST_PERMISSION_READ_EXTERNAL_STORAGE
        }

        when {
            ContextCompat.checkSelfPermission(requireContext(), permission) == PackageManager.PERMISSION_GRANTED -> {
                startImagePicker()
            }
            shouldShowRequestPermissionRationale(permission) -> {
                showRationaleDialog(permission, requestCode)
            }
            else -> {
                showSettingsDialog()
            }
        }
    }

    private fun showSettingsDialog() {
        val alertDialog = AlertDialog.Builder(requireContext())
        alertDialog.apply {
            setTitle((getString(R.string.alertDialog_setTitle)))
            setMessage(getString(R.string.alertDialog_setMessage))
            setPositiveButton(getString(R.string.alertDialog_setPositiveButton)) { _, _ ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package", requireActivity().packageName, null)
                intent.data = uri
                startActivity(intent)
            }
            setNegativeButton(getString(R.string.alertDialog_RationaleDialog_setNegativeButton)) { dialog, _ ->
                dialog.dismiss()
            }
            create().show()
        }
    }

    private fun showRationaleDialog(permission: String, requestCode: Int) {
        val alertDialog = AlertDialog.Builder(requireContext())
        alertDialog.apply {
            setTitle(getString(R.string.alertDialog_RationaleDialog_setTitle))
            setMessage(getString(R.string.alertDialog_RationaleDialog_setMessage))
            setPositiveButton(getString(R.string.alertDialog_RationaleDialog_setPositiveButton)) { _, _ ->
                requestPermissions(arrayOf(permission), requestCode)
            }
            setNegativeButton(getString(R.string.alertDialog_RationaleDialog_setNegativeButton)) { dialog, _ ->
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
        fetchSettings { settings ->
            Log.d("UploadImage", "Settings response: $settings")
            if (settings?.data?.submission_open == true) {
                Log.d("UploadImage", "Submission is open, performing upload.")
                performImageUpload(imageUri)
            } else {
                Log.d("UploadImage", "Submission is closed, cannot upload.")
                Toast.makeText(context, "Das Hochladen von Bildern ist derzeit nicht möglich.", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun performImageUpload(imageUri: Uri) {
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
        val authToken = RetrofitClient.getStoredAuthToken(requireContext())
        Log.d("PhotoAdapter", "AuthToken: $authToken")

        val call = service.uploadImage(
            "Bearer $authToken",
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

        fun clearInputFields() {
            etLegalGuardianFirstName.text.clear()
            etLegalGuardianLastName.text.clear()
            etEmail.text.clear()
            etChildFirstName.text.clear()
            etChildAge.text.clear()
            cbTerms.isChecked = false
            cbParticipation.isChecked = false
            cbMailNotification.isChecked = false
            selectedImageView.setImageURI(null)
            selectedImageView.visibility = View.GONE
            selectedImageUri = null
        }

        call.enqueue(object : Callback<SubmissionResponse> {
            @SuppressLint("StringFormatInvalid")
            override fun onResponse(call: Call<SubmissionResponse>, response: Response<SubmissionResponse>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    responseBody?.let {
                        Toast.makeText(context, getString(R.string.toast_Upload_erfolgreich, it.message), Toast.LENGTH_LONG).show()
                        clearInputFields()
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Toast.makeText(context, getString(R.string.toas_Fehlerbeim_Upload, errorBody), Toast.LENGTH_LONG).show()

                }
            }

            override fun onFailure(call: Call<SubmissionResponse>, t: Throwable) {
                val context = requireContext()
                if (PhotoAdapter.isOnline(context)) {
                    Toast.makeText(context, getString(R.string.toast_ProE_MailAdresse), Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(context, getString(R.string.toast_Sie_sind_offline), Toast.LENGTH_LONG).show()
                }
            }
        })
    }

    private fun fetchSettings(callback: (SettingsResponse?) -> Unit) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://sumsi.dev.webundsoehne.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(ApiService::class.java)

        val authToken = RetrofitClient.getStoredAuthToken(requireContext())
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
        if ((requestCode == REQUEST_PERMISSION_READ_EXTERNAL_STORAGE || requestCode == REQUEST_PERMISSION_READ_MEDIA_IMAGES) && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startImagePicker()
        } else {
            Toast.makeText(context, getString(R.string.toast_Berechtigung_verweigert), Toast.LENGTH_SHORT).show()
        }
    }
}
