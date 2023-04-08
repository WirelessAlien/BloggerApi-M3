package com.wireless.allien.bloggerapi.m3

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.VolleyLog.TAG
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var blogPostAdapter: BlogPostAdapter
    private lateinit var blogPosts: MutableList<BlogPost>
    private lateinit var requestQueue: RequestQueue
    private var nextPageToken: String? = null
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var progressBar: ProgressBar

    private val onBlogPostClickListener = object : BlogPostAdapter.OnBlogPostClickListener {
        override fun onBlogPostClick(blogPost: BlogPost) {
            val intent = Intent(this@MainActivity, DetailsActivity::class.java)
            intent.putExtra("blogPostId", blogPost.id)
            startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.navigation_view)
        progressBar = findViewById(R.id.progressBar)


        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_example -> {
                    startActivity(Intent(this, AboutActivity::class.java))
                    true
                }
                R.id.nav_about -> {
                    startActivity(Intent(this, AboutActivity::class.java))
                    true
                }
                R.id.nav_privacy -> {
                    startActivity(Intent(this, PrivacyActivity::class.java))
                    true
                }
                else -> false
            }
        }

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_menu)
        }

        recyclerView = findViewById(R.id.postRecyclerView)
        blogPosts = mutableListOf()
        blogPostAdapter = BlogPostAdapter(this, blogPosts, onBlogPostClickListener)
        recyclerView.adapter = blogPostAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        requestQueue = Volley.newRequestQueue(this)

        loadBlogPosts()
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                if (visibleItemCount + firstVisibleItemPosition >= totalItemCount && firstVisibleItemPosition >= 0) {
                    loadBlogPosts()
                }
            }
        })
    }

    private var isLoading = false
    private fun loadBlogPosts() {
        if (isLoading) {
            return
        }
        val blogId = "8185842066247672738"
        val apiKey = "AIzaSyB-JPDWhkTLKljmpbcqO1tpGsicrZpnEKU"
        var url = "https://www.googleapis.com/blogger/v3/blogs/$blogId/posts?key=$apiKey&maxResults=10"
        if (nextPageToken != null) {
            url += "&pageToken=$nextPageToken"
        }
        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, url, null,
            { response ->
                isLoading = false // Reset the flag
                val jsonArray = response.getJSONArray("items")
                for (i in 0 until jsonArray.length()) {
                    val jsonObject = jsonArray.getJSONObject(i)
                    val id = jsonObject.getString("id")
                    val title = jsonObject.getString("title")
                    val content = jsonObject.getString("content")
                    val published = jsonObject.getString("published")

                    // Get the first image URL from the content
                    val pattern = "<img[^>]+src\\s*=\\s*['\"]([^'\"]+)['\"][^>]*>".toRegex()
                    val firstImageUrl = pattern.find(content)?.groupValues?.get(1) ?: ""

                    val blogPost = BlogPost(id, title, content, published, firstImageUrl)
                    if (!blogPosts.contains(blogPost)) {
                        blogPosts.add(blogPost)
                    }
                }

                nextPageToken = response.optString("nextPageToken", null.toString())

                blogPostAdapter.setBlogPosts(blogPosts)

                // Check if there are no more posts
                if (nextPageToken == null && blogPosts.isEmpty()) {
                    Toast.makeText(this, "No More Posts", Toast.LENGTH_SHORT).show()
                } else if (nextPageToken == null) {
                    Toast.makeText(this, "End of Posts", Toast.LENGTH_SHORT).show()
                }

                // Hide progress bar
                progressBar.visibility = View.GONE
            },
            { error ->
                isLoading = false // Reset the flag
                Log.e(TAG, "Error loading blog posts", error)
                Toast.makeText(this, "Error loading blog posts", Toast.LENGTH_SHORT).show()

                // Hide progress bar
                progressBar.visibility = View.GONE
            })

        isLoading = true // Set the flag
        requestQueue.add(jsonObjectRequest)

        // Show progress bar
        progressBar.visibility = View.VISIBLE
        if (nextPageToken == null) {
            Toast.makeText(this, "Please wait, loading", Toast.LENGTH_SHORT).show()
        }
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                drawerLayout.openDrawer(GravityCompat.START)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
