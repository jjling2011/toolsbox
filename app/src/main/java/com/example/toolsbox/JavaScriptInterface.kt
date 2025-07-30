package com.example.toolsbox

import android.content.Context
import android.view.inputmethod.InputMethodManager
import android.webkit.JavascriptInterface
import android.webkit.WebView

class JavaScriptInterface(private val context: Context, private val webView: WebView) {

    @JavascriptInterface
    fun showIme() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        imm!!.showSoftInput(webView, InputMethodManager.SHOW_IMPLICIT)
    }
}