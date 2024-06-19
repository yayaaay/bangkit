package amirlabs.sapasemua.ui.menu.forum.list

import amirlabs.sapasemua.R
import amirlabs.sapasemua.base.DevFragment
import amirlabs.sapasemua.databinding.FragmentForumListBinding
import amirlabs.sapasemua.ui.menu.forum.ForumAdapter
import amirlabs.sapasemua.ui.menu.forum.ForumFragmentDirections
import amirlabs.sapasemua.utils.DevState
import amirlabs.sapasemua.utils.RecyclerViewLoadMoreScroll
import amirlabs.sapasemua.utils.getViewModel
import amirlabs.sapasemua.utils.logDebug
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ForumListFragment : DevFragment<FragmentForumListBinding>(R.layout.fragment_forum_list) {
    override val vm: ForumListViewModel by getViewModel()
    private val menuNavController: NavController? by lazy { activity?.findNavController(R.id.nav_host_fragment_menu) }

    private lateinit var adapter: ForumListAdapter
    private lateinit var adapter2: ForumAdapter
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var scrollListener: RecyclerViewLoadMoreScroll
    private var isLoadedAllItems: Boolean = false
    private var pageNo: Int = 0
    override fun initData() {
        adapter = ForumListAdapter ({
            menuNavController?.navigate(ForumFragmentDirections.actionForumFragmentToForumDetailFragment(it?.id?:""))
        },{
            loadMoreForum()
        })
        layoutManager = LinearLayoutManager(context)
        layoutManager.orientation = RecyclerView.VERTICAL
        scrollListener = RecyclerViewLoadMoreScroll(layoutManager)
        scrollListener.setOnLoadMoreListener(object : RecyclerViewLoadMoreScroll.OnLoadMoreListener {
            override fun onLoadMore() {
                logDebug("onLoadMore")
                loadMoreForum()
            }
        })
        adapter2 = ForumAdapter {
            menuNavController?.navigate(ForumListFragmentDirections.actionForumListFragmentToForumDetailFragment(it.id?:""))
        }
    }

    override fun initUI() {
        binding.rvForumList.layoutManager = layoutManager
        binding.rvForumList.adapter = adapter2
//        binding.rvForumList.addOnScrollListener(scrollListener)
    }

    override fun initAction() {
        vm.getAllForum()
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
                    adapter2.updateList(it.data)
                }
                is DevState.Failure -> {
//                    binding.shimmerViewContainer.stopShimmer()
//                    binding.shimmerViewContainer.hideShimmer()
                }
                else->{}
            }
        }
//        vm.pagedForum.observe(viewLifecycleOwner){
//            when(it){
//                is DevState.Loading -> {
////                    if (pageNo == 1) {
////                        binding.shimmerViewContainer.startShimmer()
////                    }
//                }
//                is DevState.Success -> {
//                    if (pageNo == 1) {
////                        binding.shimmerViewContainer.stopShimmer()
////                        binding.shimmerViewContainer.hideShimmer()
////                        adapter.removeLoadingView()
//                        adapter.addAll(it.data)
//                    } else {
//                        adapter.removeLoadingView()
//                        adapter.addAll(it.data)
//                    }
//                    if (it.data.isEmpty()) {
//                        onListFinished()
//                    }
//                }
//                is DevState.Failure -> {
////                    if (pageNo == 1) {
////                        binding.shimmerViewContainer.stopShimmer()
////                        binding.shimmerViewContainer.hideShimmer()
////                    } else {
//                        adapter.removeLoadingView()
////                    }
//                }
//                else->{}
//            }
//        }
    }

    private fun loadMoreForum() {
        if (!isLoadedAllItems) {
            pageNo++
            adapter.addLoadingView()
            vm.getPagedForum(pageNo)
        }
    }
    private fun onListFinished() {
        isLoadedAllItems = true
        if (pageNo > 1) {
            adapter.removeLoadingView()
        }
        scrollListener.setLoaded()
    }
}