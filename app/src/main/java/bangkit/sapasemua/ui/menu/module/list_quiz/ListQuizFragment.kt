package amirlabs.sapasemua.ui.menu.module.list_quiz

import amirlabs.sapasemua.R
import amirlabs.sapasemua.base.DevFragment
import amirlabs.sapasemua.databinding.FragmentListQuizBinding
import amirlabs.sapasemua.utils.DevState
import amirlabs.sapasemua.utils.getViewModel
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs

class ListQuizFragment : DevFragment<FragmentListQuizBinding>(R.layout.fragment_list_quiz) {
    override val vm: ListQuizViewModel by getViewModel()
    private val args: ListQuizFragmentArgs by navArgs()
    private val menuNavController: NavController? by lazy { activity?.findNavController(R.id.nav_host_fragment_menu) }
    private lateinit var adapter: ListQuizAdapter
    override fun initData() {
        adapter = ListQuizAdapter(
            onItemClick = {
                if (it.id == null) return@ListQuizAdapter
                menuNavController?.navigate(ListQuizFragmentDirections.actionListQuizFragmentToEditQuizFragment(it.id))
            },
            onItemDelete = { item, position ->
                if (item.id == null) return@ListQuizAdapter
                vm.deleteQuiz(item.id)
            }
        )
    }

    override fun initUI() {
        with(binding) {
            rvListQuiz.adapter = adapter
        }
    }

    override fun initAction() {
        with(binding) {
            btnBack.setOnClickListener {
                menuNavController?.popBackStack()
            }
            btnAddQuiz.setOnClickListener {
                menuNavController?.navigate(ListQuizFragmentDirections.actionListQuizFragmentToAddQuizFragment(args.moduleId))
            }
        }
        vm.getQuizByModule(args.moduleId) // "6538d002add450709e219ac5"
    }

    override fun initObserver() {
        vm.quiz.observe(viewLifecycleOwner){
            when(it){
                is DevState.Loading -> {
                }
                is DevState.Failure -> {
                    Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                }
                is DevState.Success -> {
                    adapter.updateList(it.data)
                }
                is DevState.Default -> {}
                is DevState.Empty -> {}
            }
        }
        vm.deleteQuiz.observe(viewLifecycleOwner){
            when(it){
                is DevState.Loading -> {
                }
                is DevState.Failure -> {
                    Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                }
                is DevState.Success -> {
                    Toast.makeText(context, "Delete Quiz Success", Toast.LENGTH_SHORT).show()
                    vm.getQuizByModule(args.moduleId)
                }
                is DevState.Default -> {}
                is DevState.Empty -> {}
            }
        }
    }
}