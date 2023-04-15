package com.example.playlistmaker

import android.content.SharedPreferences
import com.google.gson.Gson

const val SEARCH_HISTORY_KEY = "search_history"

class SearchHistory(val sharedPrefs : SharedPreferences) {
    var trackList : MutableList<Track> = getTracks().toMutableList()

    fun getTracks(): Array<Track> {
        val json = sharedPrefs.getString(SEARCH_HISTORY_KEY, null) ?: return emptyArray()
        return Gson().fromJson(json, Array<Track>::class.java)
    }

    fun add(track: Track){
        trackList.remove(track)
        trackList.add(0, track)

        if (trackList.size > 10) trackList.removeAt(10)

        val json = Gson().toJson(trackList)
        sharedPrefs.edit()
            .putString(SEARCH_HISTORY_KEY, json)
            .apply()
    }

    fun clear(){
        trackList.clear()
        sharedPrefs.edit()
            .remove(SEARCH_HISTORY_KEY)
            .apply()
    }
}