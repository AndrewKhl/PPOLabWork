package com.example.projectonppo.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.projectonppo.R
import com.example.projectonppo.models.NewsRSS


class NewsFeedAdapter() : RecyclerView.Adapter<ViewHolder>() {

    var links: ArrayList<NewsRSS> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val cardItem = LayoutInflater.from(parent.context).inflate(R.layout.card_news, parent, false)
        return ViewHolder(cardItem)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentLink = links[position]
        holder.updateWithPage(currentLink)
    }

    override fun getItemCount(): Int {
        return links.size
    }
}

class ViewHolder constructor(view: View) : RecyclerView.ViewHolder(view) {
    val linkView:TextView = view.findViewById(R.id.cardTime)
    val titleView: TextView = view.findViewById(R.id.cardTitle)
    val descriptionView: TextView = view.findViewById(R.id.cardContent)

    private var currentPage: NewsRSS? = null

    fun updateWithPage(news: NewsRSS){
        currentPage = news
        linkView.text = news.link
        titleView.text = news.title
        descriptionView.text = news.description
    }
}