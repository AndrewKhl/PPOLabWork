package com.example.projectonppo.parsers

import java.io.InputStream
import org.xmlpull.v1.XmlPullParser
import android.util.Xml
import com.example.projectonppo.models.NewsRSS
import org.xmlpull.v1.XmlPullParserException

class XMLparser{
    companion object {
        fun pars(inputStream: InputStream): ArrayList<NewsRSS> {
            var title: String? = null
            var link: String? = null
            var description: String? = null
            var isItem = false
            val items: ArrayList<NewsRSS> = ArrayList()

            try {
                val xmlPullParser = Xml.newPullParser()
                xmlPullParser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
                xmlPullParser.setInput(inputStream, null)

                xmlPullParser.nextTag()
                while (xmlPullParser.next() != XmlPullParser.END_DOCUMENT) {
                    val eventType = xmlPullParser.eventType

                    val name = xmlPullParser.name ?: continue

                    if (eventType == XmlPullParser.END_TAG) {
                        if (name.equals("item", ignoreCase = true)) {
                            isItem = false
                        }
                        continue
                    }

                    if (eventType == XmlPullParser.START_TAG) {
                        if (name.equals("item", ignoreCase = true)) {
                            isItem = true
                            continue
                        }
                    }

                    var result = ""
                    if (xmlPullParser.next() == XmlPullParser.TEXT) {
                        result = xmlPullParser.text
                        xmlPullParser.nextTag()
                    }

                    when {
                        name.equals("title", ignoreCase = true) -> title = result
                        name.equals("link", ignoreCase = true) -> link = result
                        name.equals("description", ignoreCase = true) -> description = result
                    }

                    if (title != null && link != null && description != null) {
                        if (isItem) {
                            val item = NewsRSS(title = title, link = link, description = description)
                            items.add(item)
                        }

                        title = null
                        link = null
                        description = null
                        isItem = false
                    }
                }

                return items
            } finally {
                inputStream.close()
            }
        }
    }
}