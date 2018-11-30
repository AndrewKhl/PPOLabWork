package com.example.projectonppo.Listeners

import android.os.AsyncTask

import com.example.projectonppo.Manager


class SettingsLoader(internal var listener: LoadListener, var email: String, var password: String) : AsyncTask<Void, Void, Void?>() {
    interface LoadListener {
        fun onPreExecute()
        fun onPostExecute()
    }

    override fun onPreExecute() {
        listener.onPreExecute()
        super.onPreExecute()
    }

    override fun doInBackground(vararg voids: Void): Void? {
        val manager = Manager.dataBase
        manager.signUser(email, password)
        while (true) {
            if (manager.getCurrentUser() != null)
                break
        }
        return null
    }

    override fun onPostExecute(aVoid: Void?) {
        super.onPostExecute(aVoid)
        listener.onPostExecute()
    }
}
