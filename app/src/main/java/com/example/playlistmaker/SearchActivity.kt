package com.example.playlistmaker

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.Layout
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
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
    }

    var savedSearchText : String? = null

    private val iTunesBaseUrl = "https://itunes.apple.com"

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

        trackAdapter.tracks = tracks

        recyclerView.adapter = trackAdapter

        searchBackButton.setOnClickListener {
            finish()
        }

        clearButton.setOnClickListener {
            queryInput.setText("")
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            val view: View? = this.currentFocus
            inputMethodManager?.hideSoftInputFromWindow(view?.windowToken, 0)
        }

        refreshButton.setOnClickListener {
            findTracks()
        }

        var searchTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.visibility = clearButtonVisibility(s)
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

    fun findTracks() {
        iTunesService.search(queryInput.text.toString())
            .enqueue(object : Callback<TracksResponse> {
                override fun onResponse(
                    call: Call<TracksResponse>,
                    response: Response<TracksResponse>
                ) {
                    if (response.code() == 200) {
                        tracks.clear()
                        if (response.body()?.results?.isNotEmpty() == true) {
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
                    showMessage(getString(R.string.something_went_wrong), t.message.toString())
                    placeholderImage.setImageResource(R.drawable.something_went_wrong)
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
                refreshButton.visibility = View.VISIBLE
            }
        } else {
            placeholderMessage.visibility = View.GONE
            placeholderImage.visibility = View.GONE
            refreshButton.visibility = View.GONE
        }
    }


}