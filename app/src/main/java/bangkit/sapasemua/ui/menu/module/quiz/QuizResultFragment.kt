package amirlabs.sapasemua.ui.menu.module.quiz

import amirlabs.sapasemua.R
import amirlabs.sapasemua.base.DevFragment
import amirlabs.sapasemua.databinding.FragmentQuizResultBinding
import amirlabs.sapasemua.utils.DevState
import amirlabs.sapasemua.utils.getViewModel
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import java.text.SimpleDateFormat
import java.util.Locale

class QuizResultFragment : DevFragment<FragmentQuizResultBinding>(R.layout.fragment_quiz_result) {
    override val vm: QuizViewModel by getViewModel()
    private val args: QuizResultFragmentArgs by navArgs()
    private val menuNavController: NavController? by lazy { activity?.findNavController(R.id.nav_host_fragment_menu) }
    private lateinit var adapter: QuizResultAdapter
    override fun initData() {
        adapter = QuizResultAdapter()
    }

    override fun initUI() {
        binding.rvMark.adapter = adapter
    }

    override fun initAction() {
        binding.btnBack.setOnClickListener {
            menuNavController?.popBackStack()
        }
        vm.getQuizResultById(args.resultId)
    }

    override fun initObserver() {
        vm.quizResult.observe(viewLifecycleOwner){
            when(it){
                is DevState.Loading -> {
//                    binding.progressBar.visibility = View.VISIBLE
                }
                is DevState.Success -> {
//                    binding.progressBar.visibility = View.GONE
                    binding.tvQuizMark.text = "${it.data.score ?: 0}"
                    binding.tvQuestionTitle.text = it.data.module?.name
                    val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale("in", "ID"))
                    val date = sdf.parse(it.data.createdAt ?: "")
                    val sdf2 = SimpleDateFormat("dd MMMM yyyy", Locale("in", "ID"))
                    binding.tvQuestionDate.text = sdf2.format(date ?: "")
                    adapter.updateList(it.data.quiz ?: emptyList(), it.data.answers ?: emptyList())

                }
                is DevState.Failure -> {
//                    binding.progressBar.visibility = View.GONE
//                    binding.tvScore.text = "0"
//                    binding.tvTotal.text = "/0"
                }
                else ->{}
            }
        }
    }
}