package com.wireless.allien.bloggerapi.m3

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class BlogPostAdapter(private val context: Context, private var blogPosts: MutableList<BlogPost>, private val onBlogPostClickListener: OnBlogPostClickListener) :
    RecyclerView.Adapter<BlogPostAdapter.BlogPostViewHolder>() {

    interface OnBlogPostClickListener {
        fun onBlogPostClick(blogPost: BlogPost)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setBlogPosts(blogPosts: MutableList<BlogPost>) {
        this.blogPosts = blogPosts
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlogPostViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.blog_post_item, parent, false)
        return BlogPostViewHolder(view)
    }

    override fun onBindViewHolder(holder: BlogPostViewHolder, position: Int) {
        val blogPost = blogPosts[position]
        holder.bind(blogPost)
        holder.itemView.setOnClickListener { onBlogPostClickListener.onBlogPostClick(blogPost) }
    }

    override fun getItemCount(): Int = blogPosts.size

    inner class BlogPostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val titleTextView: TextView = itemView.findViewById(R.id.titleView)
        private val contentTextView: TextView = itemView.findViewById(R.id.contentView)
        private val imageView: ImageView = itemView.findViewById(R.id.imageView)

        fun bind(blogPost: BlogPost) {
            titleTextView.text = blogPost.title

            // Remove HTML tags from the content using regex
            val contentWithoutHtmlTags = blogPost.content.replace(Regex("<.*?>"), "")

            // Show only the first 100 characters of the content
            val contentPreview = contentWithoutHtmlTags.substring(0, minOf(contentWithoutHtmlTags.length, 100))

            contentTextView.text = contentPreview
            Glide.with(context)
                .load(blogPost.firstImageUrl)
                .placeholder(R.drawable.picture_images_gallery_selection_icon)
                .into(imageView)
        }

    }
}
