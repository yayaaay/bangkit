package amirlabs.sapasemua.ui.menu.module.edit_module

import amirlabs.sapasemua.R
import amirlabs.sapasemua.base.DevFragment
import amirlabs.sapasemua.databinding.FragmentEditModuleBinding
import amirlabs.sapasemua.utils.DevState
import amirlabs.sapasemua.utils.getViewModel
import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

class EditModuleFragment : DevFragment<FragmentEditModuleBinding>(R.layout.fragment_edit_module) {
    override val vm: EditModuleViewModel by getViewModel()
    private val args: EditModuleFragmentArgs by navArgs()
    private val menuNavController: NavController? by lazy { activity?.findNavController(R.id.nav_host_fragment_menu) }
    private lateinit var pickImage: ActivityResultLauncher<PickVisualMediaRequest>
    private lateinit var adapter: EditModuleAdapter
    private var isEditing = false
    private var image: File? = null
    private var level = 1
    override fun initData() {
        adapter = EditModuleAdapter({ it, position ->
            menuNavController?.navigate(
                EditModuleFragmentDirections.actionEditModuleFragmentToEditSubmoduleFragment(it.id ?: "")
            )
        }, {
            menuNavController?.navigate(
                EditModuleFragmentDirections.actionEditModuleFragmentToAddSubmoduleFragment(args.moduleId)
            )
        })
        if (!checkPermissions()) {
            requestPermissions()
        }

        pickImage = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                lifecycleScope.launch {
                    val f = File(requireContext().cacheDir, System.currentTimeMillis().toString())
                    withContext(Dispatchers.IO){
                        f.createNewFile()
                        val inputStream = activity?.contentResolver?.openInputStream(uri)
                        var fos: FileOutputStream? = null
                        try {
                            fos = FileOutputStream(f)
                        } catch (e: FileNotFoundException) {
                            e.printStackTrace()
                        }
                        try {
                            fos?.write(inputStream?.readBytes())
                            fos?.flush()
                            fos?.close()
                            inputStream?.close()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                        image = f
                        withContext(Dispatchers.Main){
                            Glide.with(binding.root.context)
                                .load(image)
                                .into(binding.ivModule)
                        }
                    }
                }
                binding.btnSubmit.isEnabled = isVerified()
            }
        }
    }

    override fun initUI() {
        binding.rgDifficulty.check(R.id.rbBasic)
        binding.rvSubmodule.adapter = adapter
    }

    override fun initAction() {
        binding.btnBack.setOnClickListener {
            menuNavController?.popBackStack()
        }
        binding.btnAddQuiz.setOnClickListener {
            menuNavController?.navigate(EditModuleFragmentDirections.actionEditModuleFragmentToListQuizFragment(args.moduleId))
        }
        binding.ivModule.setOnClickListener {
            if (checkPermissions()) {
                pickImage.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            } else {
                requestPermissions()
            }
        }
        binding.btnAddImage.setOnClickListener {
            if (checkPermissions()) {
                pickImage.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            } else {
                requestPermissions()
            }
        }
        binding.etAddSubject.editText?.doAfterTextChanged {
            verifyName()
            isEditing = true
            binding.btnSubmit.isEnabled = isVerified()
        }
        binding.etAddDesc.editText?.doAfterTextChanged {
            verifyDescription()
            isEditing = true
            binding.btnSubmit.isEnabled = isVerified()
        }
        binding.rgDifficulty.setOnCheckedChangeListener { _, checkedId ->
            isEditing = true
            when (checkedId) {
                R.id.rbBasic -> {
                    level = 1
                    binding.rbBasic.isChecked = true
                    binding.rbIntermediate.isChecked = false
                    binding.rbAdvanced.isChecked = false
                }

                R.id.rbIntermediate -> {
                    level = 2
                    binding.rbBasic.isChecked = false
                    binding.rbIntermediate.isChecked = true
                    binding.rbAdvanced.isChecked = false
                }

                R.id.rbAdvanced -> {
                    level = 3
                    binding.rbBasic.isChecked = false
                    binding.rbIntermediate.isChecked = false
                    binding.rbAdvanced.isChecked = true
                }
            }
            binding.btnSubmit.isEnabled = isVerified()
        }
        binding.btnSubmit.setOnClickListener {
            vm.editModule(
                args.moduleId,
                binding.etAddSubject.editText?.text.toString(),
                binding.etAddDesc.editText?.text.toString(),
                level,
                image
            )
        }

        vm.getOneModule(args.moduleId)
    }

    override fun initObserver() {
        vm.module.observe(viewLifecycleOwner){
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
                    Glide.with(requireContext()).load(it.data.image).into(binding.ivModule)
                    binding.etAddSubject.editText?.setText(it.data.name)
                    binding.etAddDesc.editText?.setText(it.data.description)
                    when (it.data.level) {
                        1 -> {
                            binding.rgDifficulty.check(R.id.rbBasic)
                        }
                        2 -> {
                            binding.rgDifficulty.check(R.id.rbIntermediate)
                        }
                        3 -> {
                            binding.rgDifficulty.check(R.id.rbAdvanced)
                        }
                    }
                    adapter.updateList(it.data.submodule)
                }
                is DevState.Default -> {
                    binding.msvSubmodule.showDefaultLayout()
                }
                is DevState.Empty -> {}
            }
        }
        vm.editResult.observe(viewLifecycleOwner) {
            when (it) {
                is DevState.Loading -> {
                    binding.etAddSubject.isEnabled = false
                    binding.etAddDesc.isEnabled = false
                    binding.btnSubmit.isClickable = false
                    binding.ivModule.isClickable = false
                    binding.btnAddQuiz.isClickable = false
                    binding.btnSubmit.startAnimation()
                }

                is DevState.Success -> {
                    binding.btnSubmit.revertAnimation {
                        binding.btnSubmit.background =
                            ResourcesCompat.getDrawable(resources, R.drawable.sample, null)
                    }
                    Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show()
                    vm.getOneModule(args.moduleId)
                    isEditing = false
                    image = null

                    binding.etAddSubject.isEnabled = true
                    binding.etAddDesc.isEnabled = true
                    binding.btnSubmit.isClickable = true
                    binding.ivModule.isClickable = true
                    binding.btnAddQuiz.isClickable = true
                }

                is DevState.Failure -> {
                    binding.btnSubmit.revertAnimation {
                        binding.btnSubmit.background =
                            ResourcesCompat.getDrawable(resources, R.drawable.sample, null)
                    }
                    binding.etAddSubject.isEnabled = true
                    binding.etAddDesc.isEnabled = true
                    binding.btnSubmit.isClickable = true
                    binding.ivModule.isClickable = true
                    binding.btnAddQuiz.isClickable = true
                    Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                }

                is DevState.Default -> {}
                is DevState.Empty -> {}
            }
        }
    }

    private fun isVerified(): Boolean {
        return (binding.etAddSubject.editText?.error == null && binding.etAddDesc.editText?.error == null) && isEditing
    }

    private fun verifyName() {
        if (binding.etAddSubject.editText?.text.toString().isEmpty()) {
            binding.etAddSubject.error = "Judul tidak boleh kosong"
        } else {
            binding.etAddSubject.error = null
        }
    }

    private fun verifyDescription() {
        if (binding.etAddDesc.editText?.text.toString().isEmpty()) {
            binding.etAddDesc.error = "Deskripsi tidak boleh kosong"
        } else {
            binding.etAddDesc.error = null
        }
    }

    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED&&
            ActivityCompat.checkSelfPermission(
                requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            requireActivity(), arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE), 1
        )
    }

}