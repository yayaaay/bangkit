package amirlabs.sapasemua.ui.menu.module

import amirlabs.sapasemua.R
import amirlabs.sapasemua.base.DevFragment
import amirlabs.sapasemua.databinding.FragmentListQuizResultBinding
import amirlabs.sapasemua.utils.DevState
import amirlabs.sapasemua.utils.getViewModel
import amirlabs.sapasemua.utils.logError
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.findNavController

class ListQuizResultFragment : DevFragment<FragmentListQuizResultBinding>(R.layout.fragment_list_quiz_result) {
    override val vm: ModuleViewModel by getViewModel()
    private val menuNavController: NavController? by lazy { activity?.findNavController(R.id.nav_host_fragment_menu) }
    private lateinit var adapter: ListQuizResultAdapter

    override fun initData() {
        adapter = ListQuizResultAdapter {
            if(it._id == null) return@ListQuizResultAdapter
            menuNavController?.navigate(
                ModuleFragmentDirections.actionModuleFragmentToQuizResultFragment(
                    it._id
                )
            )
        }
    }

    override fun initUI() {
        binding.rvQuizResult.adapter = adapter
    }

    override fun initAction() {
        vm.getAllQuizResult()
    }

    override fun initObserver() {
        vm.quizResults.observe(viewLifecycleOwner) {
            when (it) {
                is DevState.Loading -> {
                    binding.msvQuizResult.showLoadingLayout()
                }

                is DevState.Success -> {
                    binding.msvQuizResult.showDefaultLayout()
                    adapter.updateList(it.data)
                }

                is DevState.Failure -> {
                    binding.msvQuizResult.showErrorLayout()
                    Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                    logError(it.message)
                }

                is DevState.Default -> {}
                is DevState.Empty -> {}
            }
        }
    }
}