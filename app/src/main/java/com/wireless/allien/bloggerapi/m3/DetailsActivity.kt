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
import com.android.volley.RequestQueue
import com.android.volley.VolleyLog.TAG
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide


class DetailsActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private lateinit var titleTextView: TextView
    private lateinit var contentTextView: TextView
    private lateinit var requestQueue: RequestQueue
    private lateinit var progressBar: ProgressBar



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        imageView = findViewById(R.id.imageView)
        titleTextView = findViewById(R.id.textViewTitle)
        contentTextView = findViewById(R.id.textViewContent)
        requestQueue = Volley.newRequestQueue(this)
        progressBar = findViewById(R.id.progressBar)



        val blogPostId = intent.getStringExtra("blogPostId")
        if (blogPostId != null) {
            loadBlogPost(blogPostId)
        }
    }

    private fun loadBlogPost(blogPostId: String) {
        progressBar.visibility = View.VISIBLE
        val blogId = "8185842066247672738"
        val apiKey = "AIzaSyB-JPDWhkTLKljmpbcqO1tpGsicrZpnEKU"
        val url = "https://www.googleapis.com/blogger/v3/blogs/$blogId/posts/$blogPostId?key=$apiKey"
        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, url, null,
            { response ->
                progressBar.visibility = View.GONE
                val id = response.getString("id")
                val title = response.getString("title")
                val content = response.getString("content")

                // Get the first image URL from the content
                val pattern = "<img[^>]+src\\s*=\\s*['\"]([^'\"]+)['\"][^>]*>".toRegex()
                val firstImageUrl = pattern.find(content)?.groupValues?.get(1) ?: ""

                // Remove HTML tags from the content
                val contentWithoutHtmlTags = content.replace(Regex("<.*?>"), "")

                val blogPost = BlogPost(id, title, contentWithoutHtmlTags, "", firstImageUrl)
                displayBlogPost(blogPost)
            },
            { error ->
                progressBar.visibility = View.GONE
                Log.e(TAG, "Error loading blog post", error)
                Toast.makeText(this, "Error loading blog post", Toast.LENGTH_SHORT).show()
            })
        requestQueue.add(jsonObjectRequest)
    }

    private fun displayBlogPost(blogPost: BlogPost) {
        titleTextView.text = blogPost.title
        contentTextView.text = blogPost.content
        Glide.with(this)
            .load(blogPost.firstImageUrl)
            .placeholder(R.drawable.picture_images_gallery_selection_icon)
            .into(imageView)
    }
}

