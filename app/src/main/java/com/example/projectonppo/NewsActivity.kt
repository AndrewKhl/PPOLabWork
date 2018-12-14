package com.example.projectonppo

import android.app.ProgressDialog
import android.content.Context
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.projectonppo.adapters.NewsFeedAdapter
import com.example.projectonppo.listeners.SettingsLoader
import com.example.projectonppo.managers.databases.LocalManager
import com.example.projectonppo.models.NewsRSS
import com.example.projectonppo.parsers.XMLparser
import kotlinx.android.synthetic.main.activity_news.*
import java.net.URL
import android.content.Intent
import android.view.MenuItem


class NewsActivity: AppCompatActivity(), SwipeRefreshLayout.OnRefreshListener {

    private val adapter = NewsFeedAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news)
        hiddenKeyboard()

        val actionBar = supportActionBar
        actionBar?.setHomeButtonEnabled(true);
        actionBar?.setDisplayHomeAsUpEnabled(true);

        val urlLink = getCurrentUrl()

        val localManager = LocalManager(this, urlLink)
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val myIntent = Intent(applicationContext, MainActivity::class.java)
        startActivityForResult(myIntent, 0)
        return true
    }

    private fun getCurrentUrl(): String{
        val arguments = intent.extras
        var urlLink = arguments?.getString("currentUrl")

        if(!urlLink?.startsWith("http://")!! && !urlLink.startsWith("https://"))
            urlLink = "https://$urlLink"

        return urlLink
    }

    private fun setDateOnRecycler(arrayOfLinks: ArrayList<NewsRSS>? = null)
    {
        var urls = arrayOfLinks
        if (urls == null){
            val localManager = LocalManager(this, getCurrentUrl())
            urls = localManager.readRssNews()
        }
        adapter.links = urls
        recyclerNewsFeed.adapter = adapter
        recyclerNewsFeed.layoutManager = LinearLayoutManager(this)

        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE)
            recyclerNewsFeed.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
    }

    private fun saveDateOnLocalDB(urlLink: String, arrayOfLinks: ArrayList<NewsRSS>?){
        val localManager = LocalManager(this, urlLink)
        if (arrayOfLinks != null) {
            localManager.clearDatabase()
            localManager.writeRssNews(arrayOfLinks)
        }
    }

    private fun loadNetworkNews(urlLink: String){
        if (!isOnline(this)){
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show()
            return
        }

        val progressDialog = ProgressDialog(this)
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
                    Toast.makeText(applicationContext, "Incorrect RSS code", Toast.LENGTH_SHORT).show()

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
        if (!isOnline(this)){
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show()
            return
        }

        if (!doSwipe)
            swipeAndRefreshLayout.isRefreshing = true

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
                        Toast.makeText(applicationContext, "Downloading the latest news is complete", Toast.LENGTH_SHORT).show()
                    }
                    else
                        Toast.makeText(applicationContext, "No latest news", Toast.LENGTH_SHORT).show()
                }
                else
                    Toast.makeText(applicationContext, "Incorrect RSS code", Toast.LENGTH_SHORT).show()

                swipeAndRefreshLayout.isRefreshing = false
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
        val inputManager = applicationContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(this.currentFocus?.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
    }

    private fun isOnline(context: Context?): Boolean {
        val cm = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = cm.activeNetworkInfo
        return netInfo != null && netInfo.isConnectedOrConnecting
    }
}