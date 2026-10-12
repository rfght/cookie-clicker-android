package com.rfght.cookieclicker

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.view.KeyEvent
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private val gameUrl = "https://rfght.github.io/cookie-clicker/game/index.html"

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        webView = WebView(this)
        setContentView(webView)

        webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            databaseEnabled = true
            cacheMode = WebSettings.LOAD_NO_CACHE
            mediaPlaybackRequiresUserGesture = false
            useWideViewPort = true
            loadWithOverviewMode = true
            builtInZoomControls = false
            displayZoomControls = false
            allowFileAccess = false
            allowContentAccess = false
        }

        webView.isLongClickable = false
        webView.isHapticFeedbackEnabled = false
        webView.setOnLongClickListener { true }

        webView.webViewClient = object : WebViewClient() {

            // Ловим ошибку при загрузке главной страницы
            override fun onReceivedError(
                view: WebView,
                request: WebResourceRequest,
                error: WebResourceError
            ) {
                if (request.isForMainFrame) {
                    showOfflinePage()
                }
            }

            // Устаревший callback для Android < 6.0
            @Deprecated("Deprecated in Java")
            override fun onReceivedError(
                view: WebView,
                errorCode: Int,
                description: String?,
                failingUrl: String?
            ) {
                if (failingUrl == gameUrl) {
                    showOfflinePage()
                }
            }

            override fun onPageStarted(view: WebView, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
            }
        }

        webView.loadUrl(gameUrl)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (webView.canGoBack()) {
                    webView.goBack()
                } else {
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        })
    }

    // Экран "Нет интернета" — HTML прямо в Kotlin
    private fun showOfflinePage() {
        val html = """
            <!DOCTYPE html>
            <html lang="ru">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <style>
                    * { margin: 0; padding: 0; box-sizing: border-box; }
                    body {
                        font-family: -apple-system, 'Segoe UI', Arial, sans-serif;
                        background: linear-gradient(135deg, #0f0c29, #1a0f3a, #2d1b4e);
                        color: #fff;
                        min-height: 100vh;
                        display: flex;
                        flex-direction: column;
                        align-items: center;
                        justify-content: center;
                        text-align: center;
                        padding: 30px;
                    }
                    .icon { font-size: 90px; margin-bottom: 20px; animation: shake 3s ease-in-out infinite; }
                    @keyframes shake {
                        0%, 100% { transform: rotate(0deg); }
                        25% { transform: rotate(-10deg); }
                        75% { transform: rotate(10deg); }
                    }
                    h1 {
                        font-size: 24px;
                        font-weight: 700;
                        background: linear-gradient(135deg, #f5c068, #ffd700);
                        -webkit-background-clip: text;
                        background-clip: text;
                        -webkit-text-fill-color: transparent;
                        margin-bottom: 12px;
                    }
                    p { font-size: 14px; color: rgba(255,255,255,0.6); max-width: 280px; line-height: 1.6; margin-bottom: 30px; }
                    button {
                        background: linear-gradient(135deg, #e8a54b, #f5c068);
                        color: #1a0f05;
                        border: none;
                        border-radius: 16px;
                        padding: 15px 40px;
                        font-size: 16px;
                        font-weight: bold;
                        cursor: pointer;
                        transition: transform 0.2s ease;
                        box-shadow: 0 6px 20px rgba(232,165,75,0.4);
                    }
                    button:active { transform: scale(0.96); }
                </style>
            </head>
            <body>
                <div class="icon">🍪</div>
                <h1>Нет интернета</h1>
                <p>Проверь подключение к сети и попробуй снова. Печеньки ждут!</p>
                <button onclick="retry()">Повторить</button>
                <script>
                    function retry() {
                        window.location.href = '$gameUrl';
                    }
                </script>
            </body>
            </html>
        """.trimIndent()

        webView.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_F12) return true
        if (event.isCtrlPressed && keyCode == KeyEvent.KEYCODE_U) return true
        if (event.isCtrlPressed && event.isShiftPressed &&
            (keyCode == KeyEvent.KEYCODE_I || keyCode == KeyEvent.KEYCODE_J)) return true
        return super.onKeyDown(keyCode, event)
    }
}