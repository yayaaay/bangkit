package amirlabs.sapasemua.ui.menu.module.submodule

import amirlabs.sapasemua.R
import amirlabs.sapasemua.base.DevFragment
import amirlabs.sapasemua.data.model.User
import amirlabs.sapasemua.databinding.FragmentSubModuleBinding
import amirlabs.sapasemua.utils.DevState
import amirlabs.sapasemua.utils.getViewModel
import amirlabs.sapasemua.utils.isAdmin
import amirlabs.sapasemua.utils.prefs
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide

class SubModuleFragment : DevFragment<FragmentSubModuleBinding>(R.layout.fragment_sub_module) {
    override val vm: SubModuleViewModel by getViewModel()
    private val args: SubModuleFragmentArgs by navArgs()
    private val user:User? = prefs().getObject("user", User::class.java)
    private var editAccess = false
    private val menuNavController: NavController? by lazy { activity?.findNavController(R.id.nav_host_fragment_menu) }
    private lateinit var adapter :SubModuleAdapter

    override fun initData() {
        adapter =  SubModuleAdapter { it, position ->
            if(it.id != null) {
                if(isAdmin() && editAccess){
                    menuNavController?.navigate(SubModuleFragmentDirections.actionSubModuleFragmentToEditSubmoduleFragment(it.id))
                }else {
                    menuNavController?.navigate(
                        SubModuleFragmentDirections.actionSubModuleFragmentToLessonFragment(
                            args.moduleId ?: "",
                            position
                        )
                    )
                }
            }
        }
    }

    override fun initUI() {
        binding.rvSubmodule.adapter = adapter
    }

    override fun initAction() {
        binding.btnBack.setOnClickListener {
            menuNavController?.popBackStack()
        }
        binding.btnQuiz.setOnClickListener {
            menuNavController?.navigate(SubModuleFragmentDirections.actionSubModuleFragmentToQuizFragment(args.moduleId?:""))
        }
        vm.getOneModule(args.moduleId ?: "")
    }

    override fun initObserver() {
        vm.modules.observe(viewLifecycleOwner){
            when(it){
                is DevState.Loading -> {
                    binding.msvSubmodule.showLoadingLayout()
                }
                is DevState.Failure -> {
                    binding.msvSubmodule.showErrorLayout()
                    Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                }
                is DevState.Success -> {
                    binding.msvSubmodule.showDefaultLayout()
//                    val image: ByteArray = Base64.decode(it.data.image, Base64.DEFAULT)
                    Glide.with(requireContext()).load(it.data.image).into(binding.ivModule)
                    binding.tvTitle.text = it.data.name
                    binding.tvDescription.text = it.data.description
                    if (user!=null){
                        editAccess = it.data.creator == user.id
                        adapter.setEditAccess(editAccess)
                    }
                    adapter.updateList(it.data.submodule)
                }
                is DevState.Default -> {
                    binding.msvSubmodule.showDefaultLayout()
                }
                is DevState.Empty -> {}
            }
        }
    }
}