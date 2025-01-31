package com.example.notesappcursor.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject

class ContentModerationService {
    private val client = OkHttpClient()
    private val apiKey = "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"
    private val url = "https://api-inference.huggingface.co/models/facebook/roberta-hate-speech-dynabench-r4-target"
    


    suspend fun checkContent(text: String): Pair<Boolean, String?> = withContext(Dispatchers.IO) {
        try {
            val mediaType = "application/json; charset=utf-8".toMediaType()
            val requestBody = JSONObject()
                .put("inputs", text)
                .toString()
                .toRequestBody(mediaType)

            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .addHeader("Authorization", "Bearer $apiKey")
                .build()

            val response = client.newCall(request).execute()
            val responseString = response.body?.string()
            if (responseString != null) {
                val jsonArray = JSONArray(responseString)
                if (jsonArray.length() > 0) {
                    val results = jsonArray.getJSONArray(0)
                    for (i in 0 until results.length()) {
                        val result = results.getJSONObject(i)
                        val label = result.getString("label")
                        val score = result.getDouble("score")
                        if ((label == "hate" || label == "offensive") && score > 0.5) {
                            return@withContext Pair(true, "Warning: This content may contain inappropriate language.")
                        }
                    }
                }
            }
            Pair(false, null)
        } catch (e: Exception) {
            e.printStackTrace()
            Pair(false, null)
        }
    }
} 