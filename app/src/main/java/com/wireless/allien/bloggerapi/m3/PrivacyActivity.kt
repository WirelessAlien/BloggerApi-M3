package com.wireless.allien.bloggerapi.m3

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.VolleyLog
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley

class PrivacyActivity : AppCompatActivity() {

    private lateinit var titleTextView: TextView
    private lateinit var contentTextView: TextView
    private lateinit var progressBar: ProgressBar

    private val blogId = "8185842066247672738"
    private val apiKey = "AIzaSyB-JPDWhkTLKljmpbcqO1tpGsicrZpnEKU"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_privacy)

        titleTextView = findViewById(R.id.page_title_text_view)
        contentTextView = findViewById(R.id.pageContentTextView)
        progressBar = findViewById(R.id.page_progressbar)

        // Replace "PAGE_ID" with the ID of the page you want to retrieve
        val pageId = "3883143225383529530"
        val url = "https://www.googleapis.com/blogger/v3/blogs/$blogId/pages/$pageId?key=$apiKey"

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                // Extract the title, content, and first image URL from the page JSON object
                val title = response.getString("title")
                val content = response.getString("content")
                val contentWithoutHtmlTags = content.replace(Regex("<.*?>"), "")

                // Update the UI with the retrieved page content
                titleTextView.text = title
                contentTextView.text = content
                contentTextView.text = contentWithoutHtmlTags

                // Hide the progress bar
                progressBar.visibility = View.GONE
            },
            { error ->
                Log.e(VolleyLog.TAG, "Error loading page content", error)
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
