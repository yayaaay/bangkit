package amirlabs.sapasemua.ui.menu.module.submodule

import amirlabs.sapasemua.R
import amirlabs.sapasemua.base.DevFragment
import amirlabs.sapasemua.databinding.FragmentEditSubmoduleBinding
import amirlabs.sapasemua.utils.DevState
import amirlabs.sapasemua.utils.getViewModel
import amirlabs.sapasemua.utils.logDebug
import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaDataSource
import android.net.Uri
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.core.app.ActivityCompat
import androidx.core.net.toUri
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DataSpec
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.datasource.FileDataSource
import androidx.media3.datasource.FileDataSource.FileDataSourceException
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.extractor.DefaultExtractorsFactory
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

@UnstableApi
class EditSubmoduleFragment : DevFragment<FragmentEditSubmoduleBinding>(R.layout.fragment_edit_submodule) {
    override val vm: SubModuleViewModel by getViewModel()
    private val args: EditSubmoduleFragmentArgs by navArgs()
    private val menuNavController: NavController? by lazy { activity?.findNavController(R.id.nav_host_fragment_menu) }
    private lateinit var pickVideo: ActivityResultLauncher<PickVisualMediaRequest>
    private var video: File? = null
    private var player: ExoPlayer?= null
    private val dataSourceFactory: DataSource.Factory = DefaultHttpDataSource.Factory()
    private val fileDataSourceFactory: DataSource.Factory = DataSource.Factory { FileDataSource() }
    override fun initData() {
        if (!checkPermissions()) {
            requestPermissions()
        }

        pickVideo = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                lifecycleScope.launch {
                    val f = File(requireContext().cacheDir, System.currentTimeMillis().toString())
                    withContext(Dispatchers.IO){
                        f.createNewFile()
                        val inputStream = activity?.contentResolver?.openInputStream(uri)
                        var fos: FileOutputStream? = null
                        try {
                            fos = FileOutputStream(f)
                        } catch (e: FileNotFoundException) {
                            e.printStackTrace()
                        }
                        try {
                            fos?.write(inputStream?.readBytes())
                            fos?.flush()
                            fos?.close()
                            inputStream?.close()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                        video = f
                        withContext(Dispatchers.Main){
                            releasePlayer()
                            initFilePlayer(f)
                            binding.btnSubmit.isEnabled = isVerified()
                        }
                    }
                }
            }
        }
    }

    override fun initUI() {
    }

    override fun initAction() {
        binding.btnBack.setOnClickListener {
            menuNavController?.popBackStack()
        }
        binding.etTitle.editText?.doAfterTextChanged {
            verifyTitle()
            binding.btnSubmit.isEnabled = isVerified()
        }
        binding.etDuration.editText?.doAfterTextChanged {
            verifyDuration()
            binding.btnSubmit.isEnabled = isVerified()
        }
        binding.ivModule.setOnClickListener {
            if (checkPermissions()) {
                pickVideo.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly))
            } else {
                requestPermissions()
            }
        }
        binding.btnAddImage.setOnClickListener {
            if (checkPermissions()) {
                pickVideo.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly))
            } else {
                requestPermissions()
            }
        }
        vm.getLessonById(args.submoduleId)
        binding.btnSubmit.setOnClickListener {
            vm.editSubmodule(
                args.submoduleId,
                video,
                binding.etTitle.editText?.text.toString(),
                binding.etDuration.editText?.text.toString()
            )
        }
        binding.btnDelete.setOnClickListener {
            vm.deleteSubmodule(args.submoduleId)
        }
    }

    override fun initObserver() {
        vm.lesson.observe(viewLifecycleOwner) {
            when (it) {
                is DevState.Loading -> {
                }

                is DevState.Success -> {
                    binding.etTitle.editText?.setText(it.data.name)
                    binding.etDuration.editText?.setText(it.data.duration.toString())
                    initPlayer(it.data.video?:"")
                }

                is DevState.Failure -> {
                }

                else -> {}
            }
        }
        vm.editSubmoduleResult.observe(viewLifecycleOwner){
            when(it){
                is DevState.Success->{
                    menuNavController?.popBackStack()
                }
                else->{
                    
                }
            }
        }
        vm.deleteSubmoduleResult.observe(viewLifecycleOwner){
            when(it){
                is DevState.Success->{
                    menuNavController?.popBackStack()
                }
                else->{

                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        pause()
    }

    override fun onResume() {
        super.onResume()
        play()
    }

    override fun onDestroy() {
        super.onDestroy()
        releasePlayer()
    }


    @OptIn(UnstableApi::class) private fun initPlayer(mediaUrl: String){
        player = ExoPlayer.Builder(requireContext())
            .build()
            .apply {
                setMediaSource(getProgressiveMediaSource(mediaUrl))
                prepare()
                addListener(playerListener)
            }
    }

    @OptIn(UnstableApi::class) private fun initFilePlayer(file: File){
        player = ExoPlayer.Builder(requireContext())
            .build()
            .apply {
                setMediaSource(getFileMediaSource(file))
                prepare()
                addListener(playerListener)
            }
    }
    @OptIn(UnstableApi::class) private fun getProgressiveMediaSource(mediaUrl: String): MediaSource {
        // Create a Regular media source pointing to a playlist uri.
        return ProgressiveMediaSource.Factory(dataSourceFactory)
            .createMediaSource(MediaItem.fromUri(Uri.parse(mediaUrl)))
    }

    @OptIn(UnstableApi::class) private fun getFileMediaSource(file:File): MediaSource {
        val dataSpec = DataSpec(file.toUri())
        val fileDataSource = FileDataSource()
        try {
            fileDataSource.open(dataSpec)
        } catch (e: FileDataSourceException) {
            e.printStackTrace()
        }

        val factory = DataSource.Factory { fileDataSource }
        return ProgressiveMediaSource.Factory(fileDataSourceFactory).createMediaSource(MediaItem.fromUri(file.toURI().toString()))
    }
    private val playerListener = object: Player.Listener {
        @OptIn(UnstableApi::class) override fun onPlaybackStateChanged(playbackState: Int) {
            super.onPlaybackStateChanged(playbackState)
            when(playbackState){
                Player.STATE_ENDED -> restartPlayer()
                Player.STATE_READY -> {
                    binding.ivModule.player = player
                    binding.ivModule.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                    play()
                }
            }
        }
    }
    private fun releasePlayer(){
        player?.apply {
            playWhenReady = false
            release()
        }
        player = null
    }
    private fun pause(){
        player?.playWhenReady = false
    }
    private fun play(){
        player?.playWhenReady = true
    }
    private fun restartPlayer(){
        player?.seekTo(0)
        player?.playWhenReady = true
    }

    private fun isVerified(): Boolean {
        return binding.etTitle.editText?.error == null && binding.etDuration.editText?.error == null ||
                video != null
    }

    private fun verifyTitle() {
        if (binding.etTitle.editText?.text.toString().isEmpty()) {
            binding.etTitle.error = "Judul tidak boleh kosong"
        } else {
            binding.etTitle.error = null
        }
    }

    private fun verifyDuration() {
        if (binding.etDuration.editText?.text.toString().isEmpty()) {
            binding.etDuration.error = "Durasi tidak boleh kosong"
        } else {
            binding.etDuration.error = null
        }
    }

    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED&&
            ActivityCompat.checkSelfPermission(
                requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            requireActivity(), arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE), 1
        )
    }
}