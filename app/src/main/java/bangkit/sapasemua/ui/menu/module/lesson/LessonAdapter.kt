package amirlabs.sapasemua.ui.menu.module.lesson

import amirlabs.sapasemua.data.model.SubModule
import amirlabs.sapasemua.databinding.ItemFragmentLessonBinding
import amirlabs.sapasemua.utils.logError
import android.annotation.SuppressLint
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.recyclerview.widget.RecyclerView

class LessonAdapter(
    private val onItemClick: (SubModule) -> Unit
) : RecyclerView.Adapter<LessonAdapter.ViewHolder>() {
    val listData = ArrayList<SubModule>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemFragmentLessonBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listData[position], position)
    }

    override fun getItemCount(): Int = listData.size

    override fun onViewAttachedToWindow(holder: ViewHolder) {
        super.onViewAttachedToWindow(holder)
        holder.initPlayer(listData[holder.bindingAdapterPosition].video ?: "")
    }

    override fun onViewDetachedFromWindow(holder: ViewHolder) {
        super.onViewDetachedFromWindow(holder)
        holder.releasePlayer()
    }
    @SuppressLint("NotifyDataSetChanged")
    fun updateList(list: List<SubModule>) {
        listData.clear()
        listData.addAll(list)
        notifyDataSetChanged()
    }

    inner class ViewHolder(val binding: ItemFragmentLessonBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private var player: ExoPlayer?= null
        private val dataSourceFactory: DataSource.Factory = DefaultHttpDataSource.Factory()

        fun bind(data: SubModule, position: Int) {
            with(binding) {
                tvLessonTitle.text = data.name
                binding.root.setOnClickListener{
                    onItemClick(data)
                }
//                logError(data.video ?: "")
//                initPlayer(data.video ?: "")
            }
        }

        @OptIn(UnstableApi::class) fun initPlayer(mediaUrl: String){
            player = ExoPlayer.Builder(binding.root.context)
                .build()
                .apply {
                    setMediaSource(getProgressiveMediaSource(mediaUrl))
                    prepare()
                    addListener(playerListener)
                }
        }
        @OptIn(UnstableApi::class) private fun getProgressiveMediaSource(mediaUrl: String): MediaSource {
            // Create a Regular media source pointing to a playlist uri.
            return ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(MediaItem.fromUri(Uri.parse(mediaUrl)))
        }
        val playerListener = object: Player.Listener {
            @OptIn(UnstableApi::class) override fun onPlaybackStateChanged(playbackState: Int) {
                super.onPlaybackStateChanged(playbackState)
                when(playbackState){
                    Player.STATE_ENDED -> restartPlayer()
                    Player.STATE_READY -> {
                        binding.videoLesson.player = player
                        binding.videoLesson.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH
                        play()
                    }
                }
            }
        }
        fun releasePlayer(){
            player?.apply {
                playWhenReady = false
                release()
            }
            player = null
        }
        fun pause(){
            player?.playWhenReady = false
        }
        fun play(){
            player?.playWhenReady = true
        }
        fun restartPlayer(){
            player?.seekTo(0)
            player?.playWhenReady = true
        }
    }

}