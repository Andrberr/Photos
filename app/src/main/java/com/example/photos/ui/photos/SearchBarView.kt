package com.example.photos.ui.photos

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.inputmethod.EditorInfo
import android.widget.LinearLayout
import androidx.core.view.isVisible
import com.example.photos.databinding.SearchBarLayoutBinding

class SearchBarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : LinearLayout(context, attrs, defStyleAttr) {

    private var queryText: String = ""
    private val handler = Handler(Looper.getMainLooper())
    private var searchRunnable: Runnable? = null
    private val binding = SearchBarLayoutBinding.inflate(LayoutInflater.from(context), this)

    var onSearchListener: ((String) -> Unit)? = null
    var onClearListener: (() -> Unit)? = null

    init {
        initViews()
    }

    private fun initViews() {
        with(binding) {
            editText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?, start: Int, count: Int, after: Int,
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    searchRunnable?.let { handler.removeCallbacks(it) }
                    queryText = s.toString()
                    searchRunnable = Runnable {
                        if (queryText != "") {
                            clearIcon.isVisible = true
                            onSearchListener?.invoke(queryText)
                        }
                    }
                    searchRunnable?.let { handler.postDelayed(it, DEBOUNCE_DELAY) }
                }

                override fun afterTextChanged(s: Editable?) {}
            })

            editText.setOnEditorActionListener { _, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_DONE || (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)) {
                    onSearchListener?.invoke(queryText)
                    true
                } else {
                    false
                }
            }

            clearIcon.setOnClickListener {
                clearText()
                onClearListener?.invoke()
            }
        }
    }

    private fun clearText() {
        binding.editText.text.clear()
        binding.clearIcon.isVisible = false
    }

    companion object {
        private const val DEBOUNCE_DELAY = 1000L
    }
}