package pk.mosafir.travsol.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import pk.mosafir.travsol.R
import pk.mosafir.travsol.model.VideoModal
import pk.mosafir.travsol.response.Offer
import pk.mosafir.travsol.webview.WebViewActivity
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer

import androidx.annotation.NonNull

import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerFullScreenListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import com.pierfrancescosoffritti.androidyoutubeplayer.core.ui.DefaultPlayerUiController
import pk.mosafir.travsol.ui.VideoFullScreen

class VideoAdapter(
    private val offers: List<VideoModal>,
) :
    RecyclerView.Adapter<VideoAdapter.OfferViewHolder>() {
    private lateinit var context: Context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoAdapter.OfferViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.video_item_main, parent, false)
        this.context = parent.context
        return OfferViewHolder(view)
    }
    override fun onBindViewHolder(holder:VideoAdapter.OfferViewHolder, position: Int) {
        val offer = offers[position]
        holder.videoName.text = offer.name
        holder.youTubePlayer.apply {
            addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
                override fun onReady(youTubePlayer: YouTubePlayer) {
                    val defaultPlayerUiController =
                        DefaultPlayerUiController(holder.youTubePlayer, youTubePlayer)
                    holder.youTubePlayer.setCustomPlayerUi(defaultPlayerUiController.rootView)
                    val videoId = offer.source
                    youTubePlayer.loadVideo(videoId, 0f)
                }
            })
            addFullScreenListener(object: YouTubePlayerFullScreenListener {
                override fun onYouTubePlayerEnterFullScreen() {
                    val intent = Intent(context, VideoFullScreen::class.java)
                    intent.putExtra("path", offer.source)
                    context.startActivity(intent)
                }
                override fun onYouTubePlayerExitFullScreen() {
                }

            })
        }
    }

    override fun getItemCount(): Int = offers.size

    inner class OfferViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val youTubePlayer = itemView.findViewById<YouTubePlayerView>(R.id.youtube_player_view)!!
        val videoName = itemView.findViewById<TextView>(R.id.video_name)!!
    }
}