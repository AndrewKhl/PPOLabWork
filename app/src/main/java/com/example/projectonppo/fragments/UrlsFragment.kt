package com.example.projectonppo.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.projectonppo.NewsActivity
import com.example.projectonppo.R
import com.example.projectonppo.validations.ValidationForUrl
import kotlinx.android.synthetic.main.fragment_urls.*
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter


class UrlsFragment: Fragment() {

    private var urls: ArrayList<String> = ArrayList()
    private val fileName = "urls"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_urls, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        urls.clear()
        //urls.add("https://news.mail.ru/rss/main/91/")
        loadUrlsWithFile()
        val urlsAdapter = ArrayAdapter(context, android.R.layout.simple_list_item_1, urls)
        urlsEdit.addTextChangedListener(ValidationForUrl(urlsEdit))
        urlsList.adapter = urlsAdapter

        btnAddUrls.setOnClickListener {
            val newUrl = urlsEdit.text.toString()
            when {
                newUrl.isNotEmpty() and (urlsEdit.error == null) and (!urls.contains(newUrl)) -> {
                    urls.add(newUrl)
                    urlsAdapter.notifyDataSetChanged();
                    saveNewUrlInFile(newUrl)
                    urlsEdit.setText("")
                }
                newUrl.isEmpty() -> Toast.makeText(context, resources.getText(R.string.emptyURL), Toast.LENGTH_SHORT).show()
                urls.contains(newUrl) -> Toast.makeText(context, resources.getText(R.string.containsURL), Toast.LENGTH_SHORT).show()
                else -> Toast.makeText(context, resources.getText(R.string.not_correctURL), Toast.LENGTH_SHORT).show()
            }
        }

        urlsList.onItemClickListener = AdapterView.OnItemClickListener { parent, v, position, id ->
            /*val bundle = Bundle()
            bundle.putString("currentUrl", urlsAdapter.getItem(position))
            findNavController().navigate(R.id.newsFragment, bundle)*/

            val intent = Intent(context, NewsActivity::class.java)
            intent.putExtra("currentUrl", urlsAdapter.getItem(position))
            startActivity(intent)
        }
    }

    private fun saveNewUrlInFile(newUrl: String){
        try {
            val outputStream = activity?.openFileOutput(fileName, Activity.MODE_APPEND)
            val osw = OutputStreamWriter(outputStream)
            osw.append(newUrl + "\n")
            osw.close()
        } catch (e: Exception) {
            Toast.makeText(context, "Exception: " + e.toString(), Toast.LENGTH_LONG).show()
        }
    }

    private fun loadUrlsWithFile(){
        try {
            val inputStream = activity?.openFileInput(fileName)
            if (inputStream != null) {
                val reader = BufferedReader(InputStreamReader(inputStream))
                while (true) {
                    val line: String? = reader.readLine() ?: break
                    urls.add(line.toString())
                }

                inputStream.close()
            }
        }
        catch (e: Exception) {
            Toast.makeText(context, "Exception: " + e.toString(), Toast.LENGTH_LONG).show()
        }
    }
}