package amirlabs.sapasemua.ui.menu.forum.create_discussion

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import amirlabs.sapasemua.R
import amirlabs.sapasemua.base.DevFragment
import amirlabs.sapasemua.databinding.FragmentCreateDiscussionBinding
import amirlabs.sapasemua.ui.menu.forum.detail.ForumDetailFragmentArgs
import amirlabs.sapasemua.ui.menu.forum.detail.ForumDetailViewModel
import amirlabs.sapasemua.utils.DevState
import amirlabs.sapasemua.utils.getViewModel
import android.util.Base64
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide

class CreateDiscussionFragment : DevFragment<FragmentCreateDiscussionBinding>(R.layout.fragment_create_discussion) {
    override val vm: CreateDiscussionViewModel by getViewModel()
    private val menuNavController: NavController? by lazy { activity?.findNavController(R.id.nav_host_fragment_menu) }
    override fun initData() {

    }

    override fun initUI() {
        binding.btnSubmit.isEnabled = isVerified()
        binding.tvForumCreatorName.text = "${vm.user?.name}-(${vm.user?.role})"
//        val image: ByteArray = Base64.decode(vm.user?.avatar ?:"", Base64.DEFAULT)
        Glide.with(binding.root.context)
//            .asBitmap()
            .load(vm.user?.avatar)
            .into(binding.ivForumAvatar)
    }

    override fun initAction() {
        binding.btnBack.setOnClickListener {
            menuNavController?.popBackStack()
        }
        binding.etAddTitle.editText?.doAfterTextChanged {
            verifyTitle()
            binding.btnSubmit.isEnabled = isVerified()
        }
        binding.etAddDesc.editText?.doAfterTextChanged {
            verifyDescription()
            binding.btnSubmit.isEnabled = isVerified()
        }
        binding.btnSubmit.setOnClickListener {
            vm.submitForum(
                binding.etAddTitle.editText?.text.toString(),
                binding.etAddDesc.editText?.text.toString()
            )
        }
    }

    override fun initObserver() {
        vm.forumResult.observe(viewLifecycleOwner){
            when(it){
                is DevState.Loading -> {
//                    binding.shimmerViewContainer.startShimmer()
                }
                is DevState.Success -> {
//                    binding.shimmerViewContainer.stopShimmer()
//                    binding.shimmerViewContainer.hideShimmer()
//                    binding.etAddComment.editText?.setText("")
//                    vm.getForumDetail(args.forumId)
                    menuNavController?.popBackStack()
                    Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show()
                }
                is DevState.Failure -> {
//                    binding.shimmerViewContainer.stopShimmer()
//                    binding.shimmerViewContainer.hideShimmer()
                }
                else->{}
            }
        }
    }

    private fun verifyTitle() {
        if (binding.etAddTitle.editText?.text.toString().isEmpty()) {
            binding.etAddTitle.error = "Judul tidak boleh kosong"
        } else {
            binding.etAddTitle.error = null
        }
    }

    private fun verifyDescription() {
        if (binding.etAddDesc.editText?.text.toString().isEmpty()) {
            binding.etAddDesc.error = "Deskripsi tidak boleh kosong"
        } else {
            binding.etAddDesc.error = null
        }
    }
    private fun isVerified(): Boolean {
        return binding.etAddTitle.editText?.error == null &&
                binding.etAddDesc.editText?.error == null &&
                binding.etAddTitle.editText?.text.toString().isNotEmpty() &&
                binding.etAddDesc.editText?.text.toString().isNotEmpty()
    }
}