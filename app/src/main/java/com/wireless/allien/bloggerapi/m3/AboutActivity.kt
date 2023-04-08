package com.wireless.allien.bloggerapi.m3

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.VolleyLog.TAG
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide

class AboutActivity : AppCompatActivity() {

    private lateinit var titleTextView: TextView
    private lateinit var imageView: ImageView
    private lateinit var contentTextView: TextView
    private lateinit var progressBar: ProgressBar

    private val blogId = "8185842066247672738"
    private val apiKey = "AIzaSyB-JPDWhkTLKljmpbcqO1tpGsicrZpnEKU"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        titleTextView = findViewById(R.id.page_title_text_view)
        imageView = findViewById(R.id.pageimageView)
        contentTextView = findViewById(R.id.pageContentTextView)
        progressBar = findViewById(R.id.page_progressbar)

        // Replace "PAGE_ID" with the ID of the page you want to retrieve
        val pageId = "1635046999056166617"
        val url = "https://www.googleapis.com/blogger/v3/blogs/$blogId/pages/$pageId?key=$apiKey"

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                // Extract the title, content, and first image URL from the page JSON object
                val title = response.getString("title")
                val content = response.getString("content")
                val contentWithoutHtmlTags = content.replace(Regex("<.*?>"), "")

                val pattern = "<img[^>]+src\\s*=\\s*['\"]([^'\"]+)['\"][^>]*>".toRegex()
                val firstImageUrl = pattern.find(content)?.groupValues?.get(1) ?: ""

                // Update the UI with the retrieved page content
                titleTextView.text = title
                contentTextView.text = content
                contentTextView.text = contentWithoutHtmlTags

                Glide.with(this)
                    .load(firstImageUrl)
                    .into(imageView)

                // Hide the progress bar
                progressBar.visibility = View.GONE
            },
            { error ->
                Log.e(TAG, "Error loading page content", error)
                Toast.makeText(this, "Error loading page content", Toast.LENGTH_SHORT).show()

                // Hide the progress bar
                progressBar.visibility = View.GONE
            })

        // Show the progress bar
        progressBar.visibility = View.VISIBLE

        // Add the request to the Volley request queue
        Volley.newRequestQueue(this).add(jsonObjectRequest)
    }
}
