package amirlabs.sapasemua.ui.auth.register

import amirlabs.sapasemua.R
import amirlabs.sapasemua.base.DevFragment
import amirlabs.sapasemua.base.DevViewModel
import amirlabs.sapasemua.databinding.FragmentRegisterBinding
import amirlabs.sapasemua.ui.auth.AuthContainerFragmentDirections
import amirlabs.sapasemua.utils.DevState
import amirlabs.sapasemua.utils.getViewModel
import amirlabs.sapasemua.utils.logError
import android.os.Handler
import android.os.Looper
import android.util.Patterns
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.doAfterTextChanged
import androidx.navigation.NavController
import androidx.navigation.findNavController

class RegisterFragment: DevFragment<FragmentRegisterBinding>(R.layout.fragment_register){
    override val vm: RegisterViewModel by getViewModel()
    private val authNavController: NavController? by lazy { activity?.findNavController(R.id.nav_host_fragment_auth) }
    private val mainNavController: NavController? by lazy { activity?.findNavController(R.id.nav_host_fragment_main) }
    override fun initData() {
    }

    override fun initUI() {
        binding.btnRegister.isEnabled = isVerified()
    }

    override fun initAction() {
        binding.etName.editText?.doAfterTextChanged {
            verifyFullName()
            binding.btnRegister.isEnabled = isVerified()
        }
        binding.etEmail.editText?.doAfterTextChanged {
            verifyEmail()
            binding.btnRegister.isEnabled = isVerified()
        }
        binding.etPassword.editText?.doAfterTextChanged { text ->
            verifyNewPassword(text.toString())
            verifyConfirmPassword()

            binding.btnRegister.isEnabled = isVerified()
        }
        binding.etConfirmPassword.editText?.doAfterTextChanged {
            verifyConfirmPassword()
            binding.btnRegister.isEnabled = isVerified()
        }
        binding.tabLogin.setOnClickListener {
            authNavController?.popBackStack()
        }
        binding.btnRegister.setOnClickListener {
            binding.btnRegister.startAnimation()
            Handler(Looper.getMainLooper()).postDelayed({
            vm.performRegister(
                binding.etName.editText?.text.toString(),
                binding.etEmail.editText?.text.toString(),
                binding.etPassword.editText?.text.toString()
            )
            },1200)
        }
    }

    override fun initObserver() {
        vm.registerStatus.observe(viewLifecycleOwner){
            when (it) {
                is DevState.Success -> {
                    binding.btnRegister.revertAnimation()
                    Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show()
                    mainNavController?.navigate(AuthContainerFragmentDirections.actionAuthContainerFragmentToMenuContainerFragment())
                }

                is DevState.Failure -> {
                    binding.btnRegister.revertAnimation {
                        binding.btnRegister.background =
                            ResourcesCompat.getDrawable(resources, R.drawable.sample, null)
                    }
                    binding.etName.isEnabled = true
                    binding.etEmail.isEnabled = true
                    binding.etPassword.isEnabled = true
                    binding.etConfirmPassword.isEnabled = true
                    binding.tabRegister.isEnabled = true
                    binding.tabLogin.isEnabled = true
                    binding.btnRegister.isEnabled = true
                    Toast.makeText(context,it.message,Toast.LENGTH_SHORT).show()
                    logError(it.message)
                }

                is DevState.Loading -> {
                    binding.etName.isEnabled = false
                    binding.etEmail.isEnabled = false
                    binding.etPassword.isEnabled = false
                    binding.etConfirmPassword.isEnabled = false
                    binding.tabRegister.isEnabled = false
                    binding.tabLogin.isEnabled = false
                    binding.btnRegister.isEnabled = false
                }

                else -> {}
            }
        }
    }


    private fun verifyFullName() {
        if ((binding.etName.editText?.text?.length ?: 0) < 3) {
            binding.etName.error = "Minimal terdiri dari 3 huruf"
        } else {
            binding.etName.error = null
        }
    }

    private fun verifyEmail() {
        if (!Patterns.EMAIL_ADDRESS.matcher(binding.etEmail.editText?.text.toString()).matches()) {
            binding.etEmail.error = "Format Email tidak sesuai"
        } else {
            binding.etEmail.error = null
        }
    }

    private fun verifyNewPassword(text: String) {
        if (text.length < 8 || text == text.lowercase() || text == text.uppercase()) {
            binding.etPassword.error = "Password terdiri dari 8 kata, huruf kecil dan besar"
        } else {
            binding.etPassword.error = null
        }
    }

    private fun verifyConfirmPassword() {
        if (binding.etConfirmPassword.editText?.text.toString() == binding.etPassword.editText?.text.toString()) {
            binding.etConfirmPassword.error = null
        } else {
            binding.etConfirmPassword.error = "Password tidak sama"
        }
    }

    private fun isVerified(): Boolean {
        return binding.etName.error == null && binding.etName.editText?.text?.isNotEmpty() == true &&
                binding.etEmail.error == null && binding.etEmail.editText?.text?.isNotEmpty() == true &&
                binding.etPassword.error == null && binding.etPassword.editText?.text?.isNotEmpty() == true &&
                binding.etConfirmPassword.error == null && binding.etConfirmPassword.editText?.text?.isNotEmpty() == true
    }
}