package com.example.playlistmaker

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.Layout
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {

    companion object {
        const val SEARCH_LINE = "SEARCH_LINE"
        const val SEARCH_DEBOUNCE_DELAY = 2000L
        const val CLICK_DEBOUNCE_DELAY = 1000L
    }

    var savedSearchText : String? = null

    private val iTunesBaseUrl = "http://itunes.apple.com"

    private val retrofit = Retrofit.Builder()
        .baseUrl(iTunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val iTunesService = retrofit.create(ITunesApi::class.java)

    private lateinit var searchBackButton : ImageView
    private lateinit var queryInput : EditText
    private lateinit var clearButton : ImageView
    private lateinit var placeholderMessage : TextView
    private lateinit var placeholderImage : ImageView
    private lateinit var refreshButton : Button
    private val trackAdapter = TrackAdapter()
    private val tracks = ArrayList<Track>()
    private val historyAdapter = TrackAdapter()
    private lateinit var searchHistoryLayout: LinearLayout
    private lateinit var searchHistoryRV: RecyclerView
    private lateinit var searchHistoryClearButton: Button
    private lateinit var history: SearchHistory
    private lateinit var progressBar:ProgressBar

    private val searchRunnable = Runnable { findTracks() }
    private val handler = Handler(Looper.getMainLooper())

    private var isClickAllowed = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        val recyclerView: RecyclerView = findViewById(R.id.searchRV)
        recyclerView.layoutManager = LinearLayoutManager(this)

        searchBackButton = findViewById(R.id.search_back)
        queryInput = findViewById(R.id.query_input)
        clearButton = findViewById(R.id.clear_button)
        placeholderMessage = findViewById(R.id.placeholderMessage)
        placeholderImage = findViewById(R.id.placeholderImage)
        refreshButton = findViewById(R.id.refreshButton)
        searchHistoryLayout = findViewById(R.id.searchHistoryLayout)
        searchHistoryRV = findViewById(R.id.TracksHistoryRV)
        searchHistoryClearButton = findViewById(R.id.search_clear_history_button)
        progressBar = findViewById(R.id.progressBarSearchActivity)

        searchHistoryRV.layoutManager = LinearLayoutManager(this)

        history = SearchHistory(getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE))

        searchHistoryLayout.visibility = if (history.trackList.size > 0) View.VISIBLE else View.GONE

        trackAdapter.tracks = tracks
        historyAdapter.tracks = history.trackList

        recyclerView.adapter = trackAdapter
        searchHistoryRV.adapter = historyAdapter

        searchBackButton.setOnClickListener {
            finish()
        }

        clearButton.setOnClickListener {
            queryInput.setText("")
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            val view: View? = this.currentFocus
            inputMethodManager?.hideSoftInputFromWindow(view?.windowToken, 0)
            tracks.clear()
            trackAdapter.notifyDataSetChanged()
        }

        refreshButton.setOnClickListener {
            findTracks()
        }

        searchHistoryClearButton.setOnClickListener{
            history.clear()
            historyAdapter.notifyDataSetChanged()
            searchHistoryLayout.visibility = if (history.trackList.isNotEmpty()) View.VISIBLE else View.GONE

        }

        trackAdapter.onItemClick = { track ->
            if (clickDebounce()) {
                history.add(track)
                historyAdapter.notifyDataSetChanged()
                startActivity( Intent(this, PlayerActivity::class.java).apply {
                    putExtra(TRACK_KEY,track)
                })
            }
        }

        historyAdapter.onItemClick = { track ->
            if (clickDebounce()) {
                startActivity( Intent(this, PlayerActivity::class.java).apply {
                    putExtra(TRACK_KEY,track)
                })
            }
        }

        var searchTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.visibility = clearButtonVisibility(s)
                searchHistoryLayout.visibility = if (queryInput.hasFocus() && s?.isEmpty() == true && history.trackList.isNotEmpty()) View.VISIBLE else View.GONE
                if (s?.isNotEmpty() == true) searchDebounce()
            }

            override fun afterTextChanged(s: Editable?) {
                savedSearchText = s?.toString()
            }
        }

        queryInput.addTextChangedListener(searchTextWatcher)

        queryInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (queryInput.text.isNotEmpty()) {
                    findTracks()
                }
                true
            }
            false
        }
    }

    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    private fun clickDebounce() : Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    fun findTracks() {

        searchHistoryLayout.visibility = View.GONE
        progressBar.visibility = View.VISIBLE

        iTunesService.search(queryInput.text.toString())
            .enqueue(object : Callback<TracksResponse> {
                override fun onResponse(
                    call: Call<TracksResponse>,
                    response: Response<TracksResponse>
                ) {
                    progressBar.visibility = View.GONE
                    if (response.code() == 200) {
                        tracks.clear()
                        refreshButton.visibility = View.GONE
                        if (response.body()?.results?.isNotEmpty() == true) {
                            searchHistoryLayout.visibility = View.VISIBLE
                            tracks.addAll(response.body()?.results!!)
                            trackAdapter.notifyDataSetChanged()
                        }
                        if (tracks.isEmpty()) {
                            showMessage(getString(R.string.nothing_found), "")
                            placeholderImage.setImageResource(R.drawable.nothing_found)
                        } else {
                            showMessage("", "")
                        }
                    } else {
                        showMessage(
                            getString(R.string.something_went_wrong),
                            response.code().toString()
                        )
                        placeholderImage.setImageResource(R.drawable.something_went_wrong)
                    }
                }

                override fun onFailure(call: Call<TracksResponse>, t: Throwable) {
                    progressBar.visibility = View.GONE
                    showMessage(getString(R.string.something_went_wrong), t.message.toString())
                    placeholderImage.setImageResource(R.drawable.something_went_wrong)
                    refreshButton.visibility = View.VISIBLE
                }

            })
    }

    fun clearButtonVisibility(s: CharSequence?) : Int{
        return if(s.isNullOrEmpty()){
            View.GONE
        }else{
            View.VISIBLE
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_LINE, savedSearchText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        savedSearchText = savedInstanceState.getString(SEARCH_LINE, "")
        searchHistoryLayout.visibility = if (history.trackList.size > 0) View.VISIBLE else View.GONE
    }


    private fun showMessage(text: String, additionalMessage: String) {
        if (text.isNotEmpty()) {
            placeholderMessage.visibility = View.VISIBLE
            placeholderImage.visibility = View.VISIBLE
            tracks.clear()
            trackAdapter.notifyDataSetChanged()
            placeholderMessage.text = text
            if (additionalMessage.isNotEmpty()) {
                Toast.makeText(applicationContext, additionalMessage, Toast.LENGTH_LONG)
                    .show()
            }
        } else {
            placeholderMessage.visibility = View.GONE
            placeholderImage.visibility = View.GONE
        }
    }


}