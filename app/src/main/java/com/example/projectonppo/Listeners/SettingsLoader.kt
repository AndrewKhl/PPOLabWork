package com.example.projectonppo.Listeners

import android.os.AsyncTask

class SettingsLoader(private var listener: LoadListener) : AsyncTask<Void, Void, Void?>() {
    interface LoadListener {
        fun onPreExecute()
        fun onPostExecute()
        fun doInBackground()
    }

    override fun onPreExecute() {
        listener.onPreExecute()
        super.onPreExecute()
    }

    override fun doInBackground(vararg voids: Void): Void? {
        listener.doInBackground()
        return null
    }

    override fun onPostExecute(aVoid: Void?) {
        super.onPostExecute(aVoid)
        listener.onPostExecute()
    }
}
