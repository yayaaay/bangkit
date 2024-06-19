package amirlabs.sapasemua.ui.menu.forum.detail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import amirlabs.sapasemua.R
import amirlabs.sapasemua.base.DevFragment
import amirlabs.sapasemua.databinding.FragmentForumDetailBinding
import amirlabs.sapasemua.ui.menu.forum.ForumAdapter
import amirlabs.sapasemua.ui.menu.forum.ForumFragmentDirections
import amirlabs.sapasemua.ui.menu.forum.ForumViewModel
import amirlabs.sapasemua.utils.DevState
import amirlabs.sapasemua.utils.getViewModel
import android.util.Base64
import androidx.core.widget.doAfterTextChanged
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide

class ForumDetailFragment : DevFragment<FragmentForumDetailBinding>(R.layout.fragment_forum_detail) {
    override val vm: ForumDetailViewModel by getViewModel()
    private val args: ForumDetailFragmentArgs by navArgs()
    private val menuNavController: NavController? by lazy { activity?.findNavController(R.id.nav_host_fragment_menu) }
    private lateinit var adapter: ForumDetailAdapter
    override fun initData() {
        adapter = ForumDetailAdapter {

        }
    }

    override fun initUI() {
        binding.rvComments.adapter = adapter
    }

    override fun initAction() {
        binding.etAddComment.editText?.doAfterTextChanged {
            binding.btnSendComment.isEnabled = it?.isNotEmpty()?:false
        }
        binding.btnSendComment.setOnClickListener {
            vm.addComment(args.forumId, binding.etAddComment.editText?.text.toString())
        }
        binding.btnBack.setOnClickListener {
            menuNavController?.popBackStack()
        }
        vm.getForumDetail(args.forumId)
    }

    override fun initObserver() {
        vm.forumDetail.observe(viewLifecycleOwner){
            when(it){
                is DevState.Loading -> {
//                    binding.shimmerViewContainer.startShimmer()
                }
                is DevState.Success -> {
//                    binding.shimmerViewContainer.stopShimmer()
//                    binding.shimmerViewContainer.hideShimmer()
                    binding.tvForumCreatorName.text = "${it.data.creator?.name}-(${it.data.creator?.role})"
                    binding.tvForumTitle.text = it.data.title
                    binding.tvForumDesc.text = it.data.description
                    adapter.updateList(it.data.comments)
//                    val image: ByteArray = Base64.decode(it.data.creator?.avatar ?:"", Base64.DEFAULT)
                    Glide.with(binding.root.context)
//                        .asBitmap()
                        .load(it.data.creator?.avatar)
                        .into(binding.ivForumAvatar)
                }
                is DevState.Failure -> {
//                    binding.shimmerViewContainer.stopShimmer()
//                    binding.shimmerViewContainer.hideShimmer()
                }
                else->{}
            }
        }
        vm.addComment.observe(viewLifecycleOwner){
            when(it){
                is DevState.Loading -> {
//                    binding.shimmerViewContainer.startShimmer()
                }
                is DevState.Success -> {
//                    binding.shimmerViewContainer.stopShimmer()
//                    binding.shimmerViewContainer.hideShimmer()
                    binding.etAddComment.editText?.setText("")
                    vm.getForumDetail(args.forumId)
                }
                is DevState.Failure -> {
//                    binding.shimmerViewContainer.stopShimmer()
//                    binding.shimmerViewContainer.hideShimmer()
                }
                else->{}
            }
        }
    }
}