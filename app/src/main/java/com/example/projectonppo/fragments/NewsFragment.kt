package com.example.projectonppo.fragments

import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projectonppo.R
import com.example.projectonppo.adapters.NewsFeedAdapter
import com.example.projectonppo.listeners.SettingsLoader
import com.example.projectonppo.models.NewsRSS
import com.example.projectonppo.parsers.XMLparser
import kotlinx.android.synthetic.main.fragment_news.*
import org.xmlpull.v1.XmlPullParserException
import java.lang.Exception
import java.net.URL


class NewsFragment: Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        hiddenKeyboard()
        return inflater.inflate(R.layout.fragment_news, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //headerSite.text = arguments?.getString("currentUrl")
        var urlLink =  arguments?.getString("currentUrl")
        var urls: ArrayList<NewsRSS>? = null
        val progressDialog = ProgressDialog(context)
        progressDialog.setMessage(resources.getText(R.string.load_news))
        progressDialog.setCancelable(false)

        SettingsLoader(object : SettingsLoader.LoadListener
        {
            override fun onPreExecute() {
                if(!urlLink?.startsWith("http://")!! && !urlLink!!.startsWith("https://")) {
                    urlLink = "https://$urlLink"
                }
                progressDialog.show()
            }

            override fun onPostExecute() {
                val adapter = NewsFeedAdapter()
                adapter.links = urls!!
                recyclerNewsFeed.adapter = adapter
                recyclerNewsFeed.layoutManager = LinearLayoutManager(context)

                progressDialog.dismiss()
            }

            override fun doInBackground() {
                try {
                    val url = URL(urlLink)
                    val inputStream = url.openConnection().getInputStream()
                    urls = XMLparser.pars(inputStream)
                }
                catch (e:Exception){
                    progressDialog.dismiss()
                    Toast.makeText(context, "Exception: " + e.toString(), Toast.LENGTH_LONG).show()
                }
            }
        }).execute()
    }

    private fun hiddenKeyboard(){
        val inputManager = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(activity?.currentFocus?.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
    }
}