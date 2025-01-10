package com.example.cnscompiler.codeView

import android.content.Context
import android.graphics.Color
import android.text.Editable
import android.text.Spannable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import java.util.regex.Pattern


class CustomCodeView(context: Context, attrs: AttributeSet) : AppCompatEditText(context, attrs) {

    init {
        // Add a text watcher to dynamically apply syntax highlighting
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(editable: Editable?) {
                editable?.let {
                    removeTextChangedListener(this) // Prevent recursive updates
                    val highlighted = highlightSyntax(it.toString())
                    setText(highlighted) // Set the highlighted text
                    setSelection(highlighted.length) // Retain cursor position
                    addTextChangedListener(this) // Reattach listener
                }
            }
        })
    }

    fun setCode(code: String) {
        setText(highlightSyntax(code))
    }

    private fun highlightSyntax(code: String): SpannableString {
        val spannable = SpannableString(code)

        // Example: Highlight 'fun' keyword in blue
        val keywordsPattern = Pattern.compile("\\b(fun|val|var|class|if|else|for|while)\\b")
        val matcher = keywordsPattern.matcher(code)

        while (matcher.find()) {
            spannable.setSpan(
                ForegroundColorSpan(Color.BLUE),
                matcher.start(),
                matcher.end(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        // Example: Highlight strings in green
        val stringPattern = Pattern.compile("\".*?\"")
        val stringMatcher = stringPattern.matcher(code)

        while (stringMatcher.find()) {
            spannable.setSpan(
                ForegroundColorSpan(Color.GREEN),
                stringMatcher.start(),
                stringMatcher.end(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        // Example: Highlight comments in gray
        val commentPattern = Pattern.compile("//.*")
        val commentMatcher = commentPattern.matcher(code)

        while (commentMatcher.find()) {
            spannable.setSpan(
                ForegroundColorSpan(Color.GRAY),
                commentMatcher.start(),
                commentMatcher.end(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        return spannable
    }
}
