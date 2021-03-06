package com.example.projectonppo.parsers

import android.util.Xml
import com.example.projectonppo.models.NewsRSS
import org.xmlpull.v1.XmlPullParser
import java.io.InputStream

class XMLparser{
    companion object {
        fun pars(inputStream: InputStream): ArrayList<NewsRSS>? {
            var title: String? = null
            var link: String? = null
            var description: String? = null
            var imageLink: String? = null
            var pubDate: String? = null
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
                            if (title != null && link != null && description != null) {
                                if (isItem) {
                                    val item = NewsRSS(title = title, link = link, description = description, images = imageLink, date = pubDate ?: "")
                                    items.add(item)
                                }

                                title = null
                                link = null
                                imageLink = null
                                description = null
                                pubDate = null
                            }
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
                        name.equals("enclosure", ignoreCase = true)-> imageLink = xmlPullParser.getAttributeValue(0)
                        name.equals("pubDate", ignoreCase = true) -> pubDate = result
                    }
                }
                inputStream.close()
                return items
            }
            catch (e :Exception){
                inputStream.close()
                return null
            }
        }
    }
}