package com.example.iiitbazaar.utils

import android.content.Context
import android.graphics.Bitmap
import android.util.Base64
import android.widget.Toast
import okhttp3.*
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.IOException

fun uploadImageToImgur(context: Context, bitmap: Bitmap, callback: (String?) -> Unit) {
    val scaledBitmap = Bitmap.createScaledBitmap(bitmap, 800, 800, true)
    val outputStream = ByteArrayOutputStream()
    scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream)

    val imageBytes = outputStream.toByteArray()
    val base64Image = Base64.encodeToString(imageBytes, Base64.NO_WRAP)

    val requestBody = MultipartBody.Builder()
        .setType(MultipartBody.FORM)
        .addFormDataPart("image", base64Image)
        .build()

    val request = Request.Builder()
        .url("https://api.imgur.com/3/image")
        .addHeader("Authorization", "Client-ID 029882ed0685ddf") // Replace with your actual Client-ID
        .post(requestBody)
        .build()

    OkHttpClient().newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            callback(null)
        }

        override fun onResponse(call: Call, response: Response) {
            val responseBody = response.body?.string()
            if (!response.isSuccessful || responseBody == null) {
                callback(null)
                return
            }

            try {
                val json = JSONObject(responseBody)
                val link = json.getJSONObject("data").getString("link")
                // Show "Posted" toast on main thread
                (context as? android.app.Activity)?.runOnUiThread {
                    Toast.makeText(context, "Image posted successfully!", Toast.LENGTH_SHORT).show()
                    callback(link)
                }
            }  catch (e: Exception) {
                callback(null)
            }
        }
    })
}
