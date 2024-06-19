package amirlabs.sapasemua.ui.menu.profile

import amirlabs.sapasemua.R
import amirlabs.sapasemua.base.DevFragment
import amirlabs.sapasemua.data.model.User
import amirlabs.sapasemua.databinding.FragmentProfileBinding
import amirlabs.sapasemua.ui.menu.MenuContainerFragmentDirections
import amirlabs.sapasemua.utils.PrefsKey
import amirlabs.sapasemua.utils.prefs
import android.util.Base64
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.bumptech.glide.Glide

class ProfileFragment : DevFragment<FragmentProfileBinding>(R.layout.fragment_profile) {
    private val mainNavController by lazy { activity?.findNavController(R.id.nav_host_fragment_main) }
    private val menuNavController: NavController? by lazy { activity?.findNavController(R.id.nav_host_fragment_menu) }
    private val user = prefs().getObject("user", User::class.java)
    override fun initData() {

    }

    override fun initUI() {
        binding.tvName.text = user?.name
        if (user?.avatar != null) {
//            val image: ByteArray = Base64.decode(user.avatar, Base64.DEFAULT)
            Glide.with(this)
//                .asBitmap()
                .load(user.avatar)
                .error(R.drawable.ic_profile)
                .into(binding.ivProfile)
        }
    }

    override fun initAction() {
        binding.btnSignOut.setOnClickListener {
            prefs().clear()
            prefs().setBoolean(PrefsKey.IS_WALKTHROUGH, false)
            mainNavController?.navigate(MenuContainerFragmentDirections.actionMenuContainerFragmentToAuthContainerFragment())
        }
        binding.btnProfile.setOnClickListener {
            menuNavController?.navigate(ProfileFragmentDirections.actionProfileFragmentToProfileDetailFragment(
                user?.id ?:""
            ))
        }
        binding.btnAboutUs.setOnClickListener {
            menuNavController?.navigate(ProfileFragmentDirections.actionProfileFragmentToAboutUsFragment())
        }
    }

    override fun initObserver() {

    }
}
