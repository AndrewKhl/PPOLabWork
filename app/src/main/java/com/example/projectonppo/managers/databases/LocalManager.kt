package com.example.projectonppo.managers.databases

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.widget.Toast
import com.example.projectonppo.models.NewsRSS
import java.io.BufferedReader
import java.io.InputStreamReader

class LocalManager (context: Context?, tableName: String? = null) : SQLiteOpenHelper(context, "ApplicationCash", null, 1) {
    private var TABLE_NAME = tableName?.split(':')?.get(1)?.split('/')?.get(2)?.split('.')?.get(0) ?: ""
    private val COL_TITLE = "title"
    private val COL_DATE = "date"
    private val COL_DESCRIPTION = "description"
    private val COL_IMAGE = "image"
    private val COL_URL = "url"
    private val fileName = "urls"

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        createNewTable(db)
    }

    override fun onCreate(db: SQLiteDatabase?) {
        createNewTable(db)
    }

    private fun createNewTable(db: SQLiteDatabase?){
        val createTable = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                COL_URL + " VARCHAR(256)," +
                COL_TITLE  + " VARCHAR(256)," +
                COL_DATE + " VARCHAR(256)," +
                COL_DESCRIPTION + " TEXT," +
                COL_IMAGE + " VARCHAR(256))"
        db?.execSQL(createTable)
    }

    private fun insertRssNew(title: String, date: String, description: String, image: String, url:String) {
        val db = this.writableDatabase
        val content = getContentValues(title, date, description, image, url)
        db.insert(TABLE_NAME, null, content)
        db.close()
    }

    fun writeRssNews(news: ArrayList<NewsRSS>) {
        for (new in news) {
            insertRssNew(new.title, new.date, new.description, new.images.toString(), new.link)
        }
    }

    private fun getContentValues(title: String, date: String, description: String,
                                 image: String, url: String): ContentValues {
        val content = ContentValues()
        content.put(COL_TITLE, title)
        content.put(COL_DATE, date)
        content.put(COL_DESCRIPTION, description)
        content.put(COL_IMAGE, image)
        content.put(COL_URL, url)
        return  content
    }

    fun clearDatabase(){
        val db = this.writableDatabase
        db.execSQL("DELETE FROM $TABLE_NAME")
        //db.execSQL("DROP TABLE $TABLE_NAME")
        db.close()
    }

    fun clearAllDatabases(activity: Activity){
        val tableNames = loadUrlsWithFile(activity)
        for (name in tableNames) {
            this.TABLE_NAME = name.split('/')[0].split('.')[0]
            clearDatabase()
        }
    }

    fun readRssNews(): ArrayList<NewsRSS>{
        val news = ArrayList<NewsRSS>()
        val db = this.readableDatabase
        this.onUpgrade(db, 1, 1)
        val query = "SELECT * FROM $TABLE_NAME"
        val result = db.rawQuery(query, null)

        if (result.moveToFirst()) {
            while (!result.isAfterLast) {
                val title = result.getString(result.getColumnIndex(COL_TITLE))
                val date = result.getString(result.getColumnIndex(COL_DATE))
                val description = result.getString(result.getColumnIndex(COL_DESCRIPTION))
                val image = result.getString(result.getColumnIndex(COL_IMAGE))
                val url = result.getString(result.getColumnIndex(COL_URL))
                val rssNew = NewsRSS(title = title, date = date, description = description, images = image, link = url)
                news.add(rssNew)
                result.moveToNext()
            }
        }

        result.close()
        db.close()
        return news
    }

    private fun loadUrlsWithFile(activity: Activity): ArrayList<String>{
        val urls: ArrayList<String> = ArrayList()
        try {
            val inputStream = activity.openFileInput(fileName)
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
            Toast.makeText(activity.applicationContext, "Exception: " + e.toString(), Toast.LENGTH_LONG).show()
        }

        return urls
    }
}