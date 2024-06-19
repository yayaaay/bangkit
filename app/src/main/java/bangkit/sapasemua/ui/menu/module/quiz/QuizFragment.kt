package amirlabs.sapasemua.ui.menu.module.quiz

import amirlabs.sapasemua.R
import amirlabs.sapasemua.base.DevFragment
import amirlabs.sapasemua.data.model.Quiz
import amirlabs.sapasemua.databinding.FragmentQuizBinding
import amirlabs.sapasemua.utils.DevState
import amirlabs.sapasemua.utils.getViewModel
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2


class QuizFragment : DevFragment<FragmentQuizBinding>(R.layout.fragment_quiz) {
    override val vm: QuizViewModel by getViewModel()
    private val args: QuizFragmentArgs by navArgs()
    private val menuNavController: NavController? by lazy { activity?.findNavController(R.id.nav_host_fragment_menu) }
    private lateinit var adapter: QuizAdapter
    private var quizAnswer = ArrayList<Quiz>()
    override fun initData() {
        adapter = QuizAdapter(
            onItemClick = {
            },
            onOption1Clicked = { quiz, position ->
                quizAnswer[position].answer = quiz.option1
            },
            onOption2Clicked = { quiz, position ->
                quizAnswer[position].answer = quiz.option2
            },
            onOption3Clicked = { quiz, position ->
                quizAnswer[position].answer = quiz.option3
            },
            onOption4Clicked = { quiz, position ->
                quizAnswer[position].answer = quiz.option4
            }
        )
    }

    override fun initUI() {
        binding.vpQuiz.adapter = adapter
    }

    override fun initAction() {
        binding.vpQuiz.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                if (position == adapter.itemCount - 1) {
                    binding.btnNext.text = "Selesai"
                } else {
                    binding.btnNext.text = "Selanjutnya"
                }
                binding.btnPrev.isEnabled = position != 0
            }
        })
        binding.btnBack.setOnClickListener {
            menuNavController?.popBackStack()
        }
        binding.btnPrev.setOnClickListener {
            if (binding.vpQuiz.currentItem > 0) {
                binding.vpQuiz.currentItem -= 1
            }
        }
        binding.btnNext.setOnClickListener {
            if (binding.vpQuiz.currentItem < adapter.itemCount - 1) {
                binding.vpQuiz.currentItem += 1
            } else {
                vm.submitQuiz(args.moduleId, quizAnswer)
            }
        }
        vm.getQuizByModule(args.moduleId)
    }

    override fun initObserver() {
        vm.quiz.observe(viewLifecycleOwner){
            when(it){
                is DevState.Loading -> {
                }
                is DevState.Failure -> {
                }
                is DevState.Success -> {
                    adapter.updateList(it.data)
                    quizAnswer.addAll(it.data.map { quiz -> Quiz(quiz.id, quiz.question, null, quiz.attachment, quiz.option1, quiz.option2, quiz.option3, quiz.option4, quiz.createdAt, quiz.updatedAt) })
                }
                is DevState.Default -> {}
                is DevState.Empty -> {}
            }
        }
        vm.submitResult.observe(viewLifecycleOwner){
            when(it){
                is DevState.Loading -> {
                }
                is DevState.Failure -> {
                }
                is DevState.Success -> {
                    menuNavController?.navigate(QuizFragmentDirections.actionQuizFragmentToQuizResultFragment(it.data._id ?: ""))
                }
                is DevState.Default -> {}
                is DevState.Empty -> {}
            }
        }
    }
}