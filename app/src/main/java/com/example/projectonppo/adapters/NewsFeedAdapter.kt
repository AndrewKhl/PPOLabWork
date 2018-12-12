package com.example.projectonppo.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.projectonppo.R
import com.example.projectonppo.models.NewsRSS
import androidx.databinding.adapters.CompoundButtonBindingAdapter.setChecked
import com.example.projectonppo.WebActivity


class NewsFeedAdapter() : RecyclerView.Adapter<ViewHolder>() {

    var links: ArrayList<NewsRSS> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val cardItem = LayoutInflater.from(parent.context).inflate(R.layout.card_news, parent, false)
        return ViewHolder(cardItem, parent.context)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentLink = links[position]
        holder.updateWithPage(currentLink)

    }

    override fun getItemCount(): Int {
        return links.size
    }
}

class ViewHolder constructor(view: View, var context: Context) : RecyclerView.ViewHolder(view), View.OnClickListener {
    override fun onClick(v: View) {
        val intent = Intent(context, WebActivity::class.java)
        intent.putExtra("link", this.linkView.text.toString())
        startActivity(context, intent,null)
    }

    init {
        view.setOnClickListener(this)
    }

    private val linkView:TextView = view.findViewById(R.id.cardTime)
    private val titleView: TextView = view.findViewById(R.id.cardTitle)
    private val descriptionView: TextView = view.findViewById(R.id.cardContent)

    fun updateWithPage(news: NewsRSS){

        linkView.text = news.link
        titleView.text = news.title
        descriptionView.text = news.description
    }
}
