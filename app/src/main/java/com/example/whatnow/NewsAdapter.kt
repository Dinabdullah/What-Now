package com.example.whatnow

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ShareCompat
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.whatnow.databinding.ArticalListItemBinding

class NewsAdapter(
    private val a: Activity,
    private val articles: MutableList<Article>,
) :
    RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

    private val favoritesDb = FavoritesDatabase(a)

    class NewsViewHolder(val binding: ArticalListItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val b = ArticalListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NewsViewHolder(b)
    }

    override fun getItemCount() = articles.size

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val article = articles[position]
        holder.binding.articaleText.text = article.title

        Glide
            .with(holder.binding.articaleImage.context)
            .load(article.urlToImage)
            .error(R.drawable.broken_image)
            .transition(DrawableTransitionOptions.withCrossFade(1000))
            .into(holder.binding.articaleImage)

        val url = article.url
        holder.binding.articaleContainer.setOnClickListener {
            val i = Intent(Intent.ACTION_VIEW, url.toUri())
            a.startActivity(i)
        }

        holder.binding.shareFab.setOnClickListener {
            ShareCompat
                .IntentBuilder(a)
                .setType("text/plain")
                .setChooserTitle("Share article with: ")
                .setText(url)
                .startChooser()
        }

        // Update favorite button state
        article.isFavorite = favoritesDb.isFavorite(article.url)
        updateFavoriteButtonState(holder.binding, article.isFavorite)

        holder.binding.favFab.setOnClickListener {
            if (article.isFavorite) {
                if (favoritesDb.removeFavorite(article.url)) {
                    article.isFavorite = false
                    // If we're in favorites view, remove the item
                    if (articles.all { favoritesDb.isFavorite(it.url) }) {
                        articles.removeAt(position)
                        notifyItemRemoved(position)
                        notifyItemRangeChanged(position, articles.size)
                    }
                }
            } else {
                if (favoritesDb.addFavorite(article)) {
                    article.isFavorite = true
                }
            }
            updateFavoriteButtonState(holder.binding, article.isFavorite)
        }
    }

    private fun updateFavoriteButtonState(binding: ArticalListItemBinding, isFavorite: Boolean) {
        binding.favFab.setImageResource(
            if (isFavorite) R.drawable.ic_favorite_filled
            else R.drawable.ic_favorite_border
        )
    }

    fun updateNews(newArticles: List<Article>) {
        articles.clear()
        articles.addAll(newArticles)
        // Update favorite status for all articles
        articles.forEach { article ->
            article.isFavorite = favoritesDb.isFavorite(article.url)
        }
        notifyDataSetChanged()
    }

}