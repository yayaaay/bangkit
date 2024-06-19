package amirlabs.sapasemua.ui.menu.forum

import amirlabs.sapasemua.R
import amirlabs.sapasemua.base.DevFragment
import amirlabs.sapasemua.databinding.FragmentForumBinding
import amirlabs.sapasemua.utils.DevState
import amirlabs.sapasemua.utils.getViewModel
import androidx.navigation.NavController
import androidx.navigation.findNavController

class ForumFragment : DevFragment<FragmentForumBinding>(R.layout.fragment_forum) {
    override val vm: ForumViewModel by getViewModel()
    private val menuNavController: NavController? by lazy { activity?.findNavController(R.id.nav_host_fragment_menu) }
    private lateinit var adapter: ForumAdapter
    override fun initData() {
        adapter = ForumAdapter {
            menuNavController?.navigate(ForumFragmentDirections.actionForumFragmentToForumDetailFragment(it.id?:""))
        }
    }

    override fun initUI() {
        binding.rvForum.adapter = adapter
    }

    override fun initAction() {
        binding.tvViewAll.setOnClickListener {
            menuNavController?.navigate(ForumFragmentDirections.actionForumFragmentToForumListFragment())
        }
        binding.btnCreateReport.setOnClickListener{
            menuNavController?.navigate(ForumFragmentDirections.actionForumFragmentToCreateDiscussionFragment())
        }
        vm.getLastFiveForum()
    }

    override fun initObserver() {
        vm.forum.observe(viewLifecycleOwner){
            when(it){
                is DevState.Loading -> {
//                    binding.shimmerViewContainer.startShimmer()
                }
                is DevState.Success -> {
//                    binding.shimmerViewContainer.stopShimmer()
//                    binding.shimmerViewContainer.hideShimmer()
                    adapter.updateList(it.data)
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