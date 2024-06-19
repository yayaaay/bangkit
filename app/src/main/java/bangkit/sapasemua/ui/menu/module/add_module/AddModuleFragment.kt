package amirlabs.sapasemua.ui.menu.module.add_module

import amirlabs.sapasemua.R
import amirlabs.sapasemua.base.DevFragment
import amirlabs.sapasemua.data.model.SubModule
import amirlabs.sapasemua.databinding.FragmentAddModuleBinding
import amirlabs.sapasemua.utils.DevState
import amirlabs.sapasemua.utils.DialogUtils
import amirlabs.sapasemua.utils.getViewModel
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
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
import com.bumptech.glide.Glide
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException


class AddModuleFragment : DevFragment<FragmentAddModuleBinding>(R.layout.fragment_add_module) {
    override val vm: AddModuleViewModel by getViewModel()
    private val menuNavController: NavController? by lazy { activity?.findNavController(R.id.nav_host_fragment_menu) }
    private lateinit var pickImage: ActivityResultLauncher<PickVisualMediaRequest>
    private lateinit var pickVideo: ActivityResultLauncher<PickVisualMediaRequest>
    private lateinit var adapter: AddModuleAdapter
    private var image: File? = null
    private var video: ArrayList<File> = arrayListOf()
    private var submodule: ArrayList<Map<String, Any>> = arrayListOf()
    private var level = 1
    override fun initData() {
        adapter = AddModuleAdapter({ it, position ->
            DialogUtils.showAddModuleDialog(requireContext(),
                saveButtonClicked = { title, duration ->
                    if (title?.isNotEmpty() == true && duration != -1) {
                        it.name = title
                        it.duration = duration
                        adapter.updateOne(it, position)
                        submodule[position] = mapOf("name" to title, "duration" to duration!!)
                    }
                })
        }, {
            pickVideo.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly))
        })
        if (!checkPermissions()) {
            requestPermissions()
        }

        pickImage = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                lifecycleScope.launch {
                    val f = File(requireContext().cacheDir, System.currentTimeMillis().toString())
                    withContext(Dispatchers.IO) {
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
                        withContext(Dispatchers.Main) {
                            Glide.with(binding.root.context)
                                .load(image)
                                .into(binding.ivModule)
                            binding.btnSubmit.isEnabled = isVerified()
                        }
                    }
                }
            }
        }
        pickVideo = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                lifecycleScope.launch {
                    val f = File(requireContext().cacheDir, System.currentTimeMillis().toString())
                    withContext(Dispatchers.IO) {
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
                        video.add(f)
                    }
                }
                adapter.add(SubModule(name = "Video", duration = 10))
                submodule.add(mapOf("name" to "Video", "duration" to 10))
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
            binding.btnSubmit.isEnabled = isVerified()
        }
        binding.etAddDesc.editText?.doAfterTextChanged {
            verifyDescription()
            binding.btnSubmit.isEnabled = isVerified()
        }
        binding.rgDifficulty.setOnCheckedChangeListener { _, checkedId ->
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
        }
        binding.btnSubmit.setOnClickListener {
            vm.createModule(
                image!!,
                video,
                submodule,
                binding.etAddSubject.editText?.text.toString(),
                binding.etAddDesc.editText?.text.toString(),
                level
            )
        }
    }

    override fun initObserver() {
        vm.createResult.observe(viewLifecycleOwner) {
            when (it) {
                is DevState.Loading -> {
                    binding.etAddSubject.isEnabled = false
                    binding.etAddDesc.isEnabled = false
                    binding.btnSubmit.isClickable = false
                    binding.ivModule.isClickable = false
                    binding.btnSubmit.startAnimation()
                }

                is DevState.Success -> {
                    binding.btnSubmit.revertAnimation {
                        binding.btnSubmit.background =
                            ResourcesCompat.getDrawable(resources, R.drawable.sample, null)
                    }
                    menuNavController?.navigate(
                        AddModuleFragmentDirections.actionAddModuleFragmentToListQuizFragment(
                            it.data.id ?: ""
                        )
                    )
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
                    Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                }

                is DevState.Default -> {}
                is DevState.Empty -> {}
            }
        }
    }

    private fun isVerified(): Boolean {
        return binding.etAddSubject.editText?.error == null && binding.etAddDesc.editText?.error == null && image != null
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
            ) == PackageManager.PERMISSION_GRANTED &&
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

    @Suppress("DEPRECATION")
    private fun getPickedImage(selectedPhotoUri: Uri): Bitmap {
        val bitmap = when {
            Build.VERSION.SDK_INT < 28 -> MediaStore.Images.Media.getBitmap(
                activity?.contentResolver,
                selectedPhotoUri
            )

            else -> {
                val source =
                    ImageDecoder.createSource(requireActivity().contentResolver, selectedPhotoUri)
                ImageDecoder.decodeBitmap(source)
            }
        }
        return bitmap
    }
}