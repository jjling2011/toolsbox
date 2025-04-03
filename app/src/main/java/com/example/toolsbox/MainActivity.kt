package com.example.toolsbox

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.io.ByteArrayInputStream
import java.net.URLDecoder

private const val WEBSITE = "http://www.toolbox.home/"
private const val TAG = "tools"

class MainActivity : AppCompatActivity() {
    private lateinit var webView: WebView
    private lateinit var textTitle: TextView
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        dbHelper = DatabaseHelper(this)
        dbHelper.initializeDatabase()

        webView = findViewById(R.id.webView)
        textTitle = findViewById(R.id.textTitle)
        setupWebView()
        findViewById<Button>(R.id.btnGreet).setOnClickListener {
            webView.loadUrl(WEBSITE)
        }
    }

    private fun handleDictCnGetRequest(request: WebResourceRequest): WebResourceResponse {
        val mime = Utils.getMimeType("data.json")
        val headers = request.requestHeaders
        val encoded = headers["Keyword"] ?: ""
        val keyword = URLDecoder.decode(encoded, "UTF-8")
        val includeWordDb = headers["Include-Word"] == "true"
        val includeIdiomDb = headers["Include-Idiom"] == "true"
        val includeXhyDb = headers["Include-Xhy"] == "true"

        Log.d(
            TAG,
            "keyword: $keyword, Word: $includeWordDb, Idiom: $includeIdiomDb, Xhy: $includeXhyDb"
        )

        val jsonString =
            dbHelper.searchKeyword(keyword, includeWordDb, includeIdiomDb, includeXhyDb)
        val inputStream = ByteArrayInputStream(jsonString.toByteArray(Charsets.UTF_8))
        return WebResourceResponse(
            mime, "UTF-8", inputStream
        )
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView() {
        val that = this
        webView.webChromeClient = object : WebChromeClient() {
            override fun onReceivedTitle(view: WebView?, title: String?) {
                super.onReceivedTitle(view, title)
                that.textTitle.text = title ?: getString(R.string.app_name)
            }
        }

        webView.webViewClient = object : WebViewClient() {
            override fun shouldInterceptRequest(
                view: WebView, request: WebResourceRequest
            ): WebResourceResponse? {
                val url = request.url
                val path: String =
                    if (url.path != null && url.path != "/") url.path!!.substring(1) else "index.html"
                Log.d(TAG, "url: $url path: $path")
                return try {
                    if (path == "dict-cn/serv.php") {
                        handleDictCnGetRequest(request)
                    } else {
                        // 尝试从assets加载文件
                        val inputStream = assets.open(path)
                        val mimeType = Utils.getMimeType(path)
                        WebResourceResponse(mimeType, "UTF-8", inputStream)
                    }
                } catch (e: Exception) {
                    // 文件不存在，返回404
                    val notFoundHtml = "<html><body><h1>404 Not Found</h1></body></html>"
                    val inputStream = ByteArrayInputStream(notFoundHtml.toByteArray())
                    WebResourceResponse(
                        "text/html", "UTF-8", 404, "Not Found", emptyMap(), inputStream
                    )
                }
            }
        }

        // 启用JavaScript
        val ws = webView.settings
        ws.javaScriptEnabled = true
        ws.domStorageEnabled = true

        // 加载虚拟网址
        webView.loadUrl(WEBSITE)
    }

}