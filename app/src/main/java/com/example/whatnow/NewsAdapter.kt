package com.example.whatnow

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ShareCompat
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.whatnow.databinding.ArticalListItemBinding

class NewsAdapter(val a: Activity, val articales: ArrayList<Article>) :
    Adapter<NewsAdapter.NewsViewHolder>() {
    class NewsViewHolder(val binding: ArticalListItemBinding) : ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val b = ArticalListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NewsViewHolder(b)
    }

    override fun getItemCount() = articales.size

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {

        Log.d("trace","Link:${articales[position].urlToImage}]")
        holder.binding.articaleText.text = articales[position].title
        Glide
            .with(holder.binding.articaleImage.context)
            .load(articales[position].urlToImage)
            .error(R.drawable.broken_image_24)
            .transition(DrawableTransitionOptions.withCrossFade(1000))
            .into(holder.binding.articaleImage)


        val url = articales[position].url

        holder.binding.articaleContainer.setOnClickListener {

            val i = Intent(Intent.ACTION_VIEW, url.toUri())
            a.startActivity(i)
        }

        holder.binding.shareFab.setOnClickListener {
            ShareCompat
                .IntentBuilder(a)
                .setType("text/plain")
                .setChooserTitle("Share articale with:")
                .setText(url)
                .startChooser()
        }
    }

}