package com.example.coursesmanager.ui.player

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.MediaController
import androidx.appcompat.app.AppCompatActivity
import com.example.coursesmanager.R
import com.example.coursesmanager.databinding.ActivityVideoPlayerBinding
import com.example.coursesmanager.util.ToastUtil
import java.util.regex.Pattern

class VideoPlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVideoPlayerBinding
    private var mediaController: MediaController? = null

    private val videoUrl: String by lazy {
        intent.getStringExtra(EXTRA_VIDEO_URL).orEmpty()
    }

    private val videoTitle: String by lazy {
        intent.getStringExtra(EXTRA_VIDEO_TITLE) ?: getString(R.string.title_video_trailer)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvPlayerTitle.text = videoTitle

        binding.ivClosePlayer.setOnClickListener {
            finish()
        }

        setupPlayback()
    }

    private fun setupPlayback() {
        if (videoUrl.isBlank()) {
            ToastUtil.show(this, getString(R.string.err_trailer_invalid))
            finish()
            return
        }

        if (isYouTubeUrl(videoUrl)) {
            // Trường hợp 1: URL YouTube -> Phát bằng WebView iframe player tương thích
            setupYouTubePlayback(videoUrl)
        } else {
            // Trường hợp 2: URL MP4 trực tiếp -> Phát bằng native VideoView
            setupMp4Playback(videoUrl)
        }
    }

    private fun isYouTubeUrl(url: String): Boolean {
        val lower = url.lowercase()
        return lower.contains("youtube.com") || lower.contains("youtu.be")
    }

    private fun extractYouTubeVideoId(url: String): String? {
        val pattern = "(?<=watch\\?v=|/videos/|embed/|youtu.be/|/v/|/e/|watch\\?feature=player_embedded&v=|%2Fvideos%2F|embed\\?video_id=)[^#&?\\n]*"
        val compiledPattern = Pattern.compile(pattern)
        val matcher = compiledPattern.matcher(url)
        return if (matcher.find()) matcher.group() else null
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupYouTubePlayback(url: String) {
        binding.videoView.visibility = View.GONE
        binding.webViewPlayer.visibility = View.VISIBLE
        binding.pbVideoLoading.visibility = View.VISIBLE

        val videoId = extractYouTubeVideoId(url)
        val embedUrl = if (!videoId.isNullOrBlank()) {
            "https://www.youtube.com/embed/$videoId?autoplay=1&playsinline=1"
        } else {
            url
        }

        binding.webViewPlayer.apply {
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.mediaPlaybackRequiresUserGesture = false
            settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW

            webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    binding.pbVideoLoading.visibility = View.GONE
                }
            }

            webChromeClient = WebChromeClient()

            val htmlData = """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0">
                    <style>
                        * { margin: 0; padding: 0; box-sizing: border-box; }
                        body, html { width: 100%; height: 100%; background-color: #000; overflow: hidden; display: flex; justify-content: center; align-items: center; }
                        iframe { width: 100%; height: 100%; border: none; }
                    </style>
                </head>
                <body>
                    <iframe 
                        src="$embedUrl" 
                        allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture" 
                        allowfullscreen>
                    </iframe>
                </body>
                </html>
            """.trimIndent()

            loadDataWithBaseURL("https://www.youtube.com", htmlData, "text/html", "UTF-8", null)
        }
    }

    private fun setupMp4Playback(url: String) {
        binding.webViewPlayer.visibility = View.GONE
        binding.videoView.visibility = View.VISIBLE
        binding.pbVideoLoading.visibility = View.VISIBLE

        try {
            val uri = Uri.parse(url)
            mediaController = MediaController(this).apply {
                setAnchorView(binding.videoView)
            }

            binding.videoView.apply {
                setMediaController(mediaController)
                setVideoURI(uri)

                setOnPreparedListener { mediaPlayer ->
                    binding.pbVideoLoading.visibility = View.GONE
                    mediaPlayer.isLooping = false
                    start()
                }

                setOnErrorListener { _, what, extra ->
                    binding.pbVideoLoading.visibility = View.GONE
                    ToastUtil.showLong(
                        this@VideoPlayerActivity,
                        "${getString(R.string.err_video_play_failed)} (Mã lỗi: $what, $extra)"
                    )
                    true
                }
            }
        } catch (e: Exception) {
            binding.pbVideoLoading.visibility = View.GONE
            ToastUtil.showLong(this, "${getString(R.string.err_video_play_failed)}: ${e.localizedMessage}")
        }
    }

    override fun onPause() {
        super.onPause()
        if (binding.videoView.isPlaying) {
            binding.videoView.pause()
        }
        binding.webViewPlayer.onPause()
    }

    override fun onResume() {
        super.onResume()
        binding.webViewPlayer.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.videoView.stopPlayback()
        mediaController = null
        binding.webViewPlayer.destroy()
    }

    companion object {
        const val EXTRA_VIDEO_URL = "extra_video_url"
        const val EXTRA_VIDEO_TITLE = "extra_video_title"
    }
}
