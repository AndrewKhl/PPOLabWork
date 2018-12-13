package com.example.projectonppo.fragments

import android.app.ProgressDialog
import android.content.Context
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.projectonppo.R
import com.example.projectonppo.adapters.NewsFeedAdapter
import com.example.projectonppo.listeners.SettingsLoader
import com.example.projectonppo.managers.databases.LocalManager
import com.example.projectonppo.models.NewsRSS
import com.example.projectonppo.parsers.XMLparser
import kotlinx.android.synthetic.main.fragment_news.*
import java.net.URL


class NewsFragment: Fragment(), SwipeRefreshLayout.OnRefreshListener {

    private val adapter = NewsFeedAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        hiddenKeyboard()
        return inflater.inflate(R.layout.fragment_news, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val urlLink = getCurrentUrl()

        val localManager = LocalManager(context, urlLink)
        val urls = localManager.readRssNews()

        swipeAndRefreshLayout.setOnRefreshListener(this);

        if (urls.count() == 0)
            loadNetworkNews(urlLink);
        else {
            setDateOnRecycler(urls)
            backgroundLoadNews()
        }

        btnRefresh.setOnClickListener{
            setDateOnRecycler(localManager.readRssNews())
            btnRefresh.visibility = View.INVISIBLE
        }
    }

    private fun getCurrentUrl(): String{
        var urlLink = arguments?.getString("currentUrl")

        if(!urlLink?.startsWith("http://")!! && !urlLink.startsWith("https://"))
            urlLink = "https://$urlLink"

        return urlLink
    }

    private fun setDateOnRecycler(arrayOfLinks: ArrayList<NewsRSS>? = null)
    {
        var urls = arrayOfLinks
        if (urls == null){
            val localManager = LocalManager(context, getCurrentUrl())
            urls = localManager.readRssNews()
        }
        adapter.links = urls
        recyclerNewsFeed.adapter = adapter
        recyclerNewsFeed.layoutManager = LinearLayoutManager(context)

        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE)
            recyclerNewsFeed.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
    }

    private fun saveDateOnLocalDB(urlLink: String, arrayOfLinks: ArrayList<NewsRSS>?){
        val localManager = LocalManager(context, urlLink)
        if (arrayOfLinks != null) {
            localManager.clearDatabase()
            localManager.writeRssNews(arrayOfLinks)
        }
    }

    private fun loadNetworkNews(urlLink: String){
        if (!isOnline(context)){
            Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show()
            return
        }

        val progressDialog = ProgressDialog(context)
        progressDialog.setMessage(resources.getText(R.string.load_news))
        progressDialog.setCancelable(false)

        var urls: ArrayList<NewsRSS>? = null

        SettingsLoader(object : SettingsLoader.LoadListener {
            override fun onPreExecute() {
                progressDialog.show()
            }

            override fun onPostExecute() {
                if (urls != null) {
                    setDateOnRecycler(urls!!)
                    saveDateOnLocalDB(urlLink, urls)
                }
                else
                    Toast.makeText(context, "Incorrect RSS code", Toast.LENGTH_SHORT).show()

                progressDialog.dismiss()
            }

            override fun doInBackground() {
                val url = URL(urlLink)
                val inputStream = url.openConnection().getInputStream()
                urls = XMLparser.pars(inputStream)
            }
        }).execute()
    }

    private fun backgroundLoadNews(doSwipe: Boolean = false){
        val urlLink = getCurrentUrl()
        if (!isOnline(context)){
            Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show()
            return
        }

        var urls: ArrayList<NewsRSS>? = null

        SettingsLoader(object : SettingsLoader.LoadListener {
            override fun onPreExecute() {}

            override fun onPostExecute() {
                if (urls != null) {
                    if (urls!![0].link != adapter.links[0].link ){
                        saveDateOnLocalDB(urlLink, urls)
                        if (!doSwipe){
                            btnRefresh.visibility = View.VISIBLE
                        }
                        else {
                            setDateOnRecycler()
                        }
                    }

                    if (doSwipe)
                        swipeAndRefreshLayout.isRefreshing = false
                }
                else
                    Toast.makeText(context, "Incorrect RSS code", Toast.LENGTH_SHORT).show()
            }

            override fun doInBackground() {
                val url = URL(urlLink)
                val inputStream = url.openConnection().getInputStream()
                urls = XMLparser.pars(inputStream)
            }
        }).execute()
    }

    override fun onRefresh() {
        backgroundLoadNews(doSwipe = true)
        btnRefresh.visibility = View.INVISIBLE
    }

    private fun hiddenKeyboard(){
        val inputManager = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(activity?.currentFocus?.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
    }

    private fun isOnline(context: Context?): Boolean {
        val cm = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = cm.activeNetworkInfo
        return netInfo != null && netInfo.isConnectedOrConnecting
    }
}