package com.example.projectonppo.adapters

import android.content.Context
import android.content.Intent
import android.media.Image
import android.net.ConnectivityManager
import android.opengl.Visibility
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.projectonppo.R
import com.example.projectonppo.models.NewsRSS
import androidx.databinding.adapters.CompoundButtonBindingAdapter.setChecked
import com.example.projectonppo.WebActivity
import com.squareup.picasso.Picasso
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.min


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
        if (isOnline(context)){
            val intent = Intent(context, WebActivity::class.java)
            intent.putExtra("link", this.link)
            startActivity(context, intent,null)
        }
    }

    init {
        view.setOnClickListener(this)
    }

    private val dateView:TextView = view.findViewById(R.id.cardTime)
    private val titleView: TextView = view.findViewById(R.id.cardTitle)
    private val descriptionView: TextView = view.findViewById(R.id.cardContent)
    private val imageView: ImageView = view.findViewById(R.id.cardImage)
    private var link: String? = null;

    fun updateWithPage(news: NewsRSS){
        link = news.link
        dateView.text = parseDate(news.date)
        titleView.text = news.title

        if (news.description.length < 150){
            descriptionView.text = news.description
        }
        else {
            var description = news.description.substring(0, 150)
            description += if (description.endsWith('.'))
                ".."
            else
                "..."

            descriptionView.text = description
        }


        if((news.images != null) and (news.images != "null"))
            Picasso.with(itemView.context).load(news.images).into(imageView)
        else
            imageView.layoutParams.height = 0
    }

    private fun parseDate(currentDate: String): String {
        var finishDate = currentDate
        var format = SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.ENGLISH)
        try {
            val newDate = format.parse(currentDate)
            format = SimpleDateFormat("dd-MM-yyyy, h:mm a", Locale.ENGLISH)
            format.timeZone = TimeZone.getTimeZone("GMT+3")
            finishDate = format.format(newDate)
        } catch (e: Exception) {
        }

        return finishDate
    }

    private fun isOnline(context: Context?): Boolean {
        val cm = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = cm.activeNetworkInfo
        return netInfo != null && netInfo.isConnectedOrConnecting
    }
}
