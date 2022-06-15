package pk.mosafir.travsol.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerFullScreenListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.ui.DefaultPlayerUiController
import pk.mosafir.travsol.databinding.ActivityVideoFullScreenBinding

class VideoFullScreen : AppCompatActivity() {
    private lateinit var _binding: ActivityVideoFullScreenBinding
    private val binding get() = _binding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityVideoFullScreenBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        binding.youtubeKahaniView.apply {
            addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
                override fun onReady(youTubePlayer: YouTubePlayer) {
                    val defaultPlayerUiController =
                        DefaultPlayerUiController(binding.youtubeKahaniView, youTubePlayer)
                    binding.youtubeKahaniView.setCustomPlayerUi(defaultPlayerUiController.rootView)
                    val videoId = "S0Q4gqBUs7c"
                    youTubePlayer.loadVideo(videoId, 0f)
                }
            })
            addFullScreenListener(object : YouTubePlayerFullScreenListener {
                override fun onYouTubePlayerEnterFullScreen() {
                }

                override fun onYouTubePlayerExitFullScreen() {
                    finish()
                }
            })
            enterFullScreen()
        }
    }

    override fun onBackPressed() {
        finish()
    }
}