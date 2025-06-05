package com.example.iiitbazaar.utils

import android.content.Context
import android.net.Uri
import android.util.Base64
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

fun uploadImageToImgur(context: Context, imageUri: Uri, callback: (String?) -> Unit) {
    val contentResolver = context.contentResolver
    val inputStream = contentResolver.openInputStream(imageUri)
    val imageBytes = inputStream?.readBytes()
    inputStream?.close()

    val base64Image = Base64.encodeToString(imageBytes, Base64.DEFAULT)

    val client = OkHttpClient()
    val requestBody = FormBody.Builder()
        .add("image", base64Image)
        .build()

    val request = Request.Builder()
        .url("https://api.imgur.com/3/image")
        .post(requestBody)
        .addHeader("Authorization", "Client-ID XXXXXXXXX")
        .build()

    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            e.printStackTrace()
            callback(null)
        }

        override fun onResponse(call: Call, response: Response) {
            val json = JSONObject(response.body?.string() ?: "")
            val imageUrl = json.getJSONObject("data").getString("link")
            callback(imageUrl)
        }
    })
}
