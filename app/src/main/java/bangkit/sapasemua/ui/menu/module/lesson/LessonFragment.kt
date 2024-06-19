package amirlabs.sapasemua.ui.menu.module.lesson

import amirlabs.sapasemua.R
import amirlabs.sapasemua.base.DevFragment
import amirlabs.sapasemua.databinding.FragmentLessonBinding
import amirlabs.sapasemua.utils.DevState
import amirlabs.sapasemua.utils.getViewModel
import android.net.Uri
import androidx.annotation.OptIn
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.Player.STATE_ENDED
import androidx.media3.common.Player.STATE_READY
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2

class LessonFragment : DevFragment<FragmentLessonBinding>(R.layout.fragment_lesson) {
    override val vm: LessonViewModel by getViewModel()
    private val args: LessonFragmentArgs by navArgs()
    private val menuNavController: NavController? by lazy { activity?.findNavController(R.id.nav_host_fragment_menu) }
    private lateinit var adapter: LessonAdapter

    override fun initData() {
        adapter = LessonAdapter{

        }
    }

    override fun initUI() {
        binding.vpLesson.adapter = adapter
    }

    override fun initAction() {
        binding.vpLesson.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                if (position == adapter.itemCount - 1) {
                    binding.btnNext.text = "Selesai"
                } else {
                    binding.btnNext.text = "Selanjutnya"
                }
                binding.btnPrev.isEnabled = position != 0
                binding.btnNext.isEnabled = position != adapter.itemCount - 1
            }
        })
        binding.btnBack.setOnClickListener {
            menuNavController?.popBackStack()
        }
        binding.btnNext.setOnClickListener {
            if (binding.vpLesson.currentItem < adapter.itemCount) {
//            binding.vpLesson.currentItem += 1
                binding.vpLesson.setCurrentItem(binding.vpLesson.currentItem + 1, true)
            }
        }
        binding.btnPrev.setOnClickListener {
            if (binding.vpLesson.currentItem > 0) {
//            binding.vpLesson.currentItem -= 1
                binding.vpLesson.setCurrentItem(binding.vpLesson.currentItem - 1, true)
            }
        }
        vm.getLessons(args.moduleId)
    }

    override fun initObserver() {
        vm.lesson.observe(viewLifecycleOwner){
            when(it){
                is DevState.Success -> {
                    adapter.updateList(it.data)
                    binding.vpLesson.setCurrentItem(args.position, true)
                }
                is DevState.Failure -> {
                }
                is DevState.Loading -> {
                }
                else -> {}
            }
        }
    }
}