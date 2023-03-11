package com.example.playlistmaker

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView

class SearchActivity : AppCompatActivity() {

    companion object {
        const val SEARCH_LINE = "SEARCH_LINE"
    }

    var savedSearchText : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val searchBackButton = findViewById<ImageView>(R.id.search_back)
        val searchEditText = findViewById<EditText>(R.id.search_edit_text)
        val clearButton = findViewById<ImageView>(R.id.clear_button)

        searchBackButton.setOnClickListener {
            finish()
        }

        clearButton.setOnClickListener {
            searchEditText.setText("")
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            val view: View? = this.currentFocus
            inputMethodManager?.hideSoftInputFromWindow(view?.windowToken, 0)
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

        searchEditText.addTextChangedListener(searchTextWatcher)
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



}