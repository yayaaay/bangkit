package amirlabs.sapasemua.ui.menu.module

import amirlabs.sapasemua.R
import amirlabs.sapasemua.base.DevFragment
import amirlabs.sapasemua.databinding.FragmentModuleBinding
import amirlabs.sapasemua.utils.DevState
import amirlabs.sapasemua.utils.getViewModel
import amirlabs.sapasemua.utils.logError
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.google.android.material.tabs.TabLayoutMediator

class ModuleFragment : DevFragment<FragmentModuleBinding>(R.layout.fragment_module) {
    override val vm: ModuleViewModel by getViewModel()
    private lateinit var tabAdapter: ModuleTabAdapter
    private val menuNavController: NavController? by lazy { activity?.findNavController(R.id.nav_host_fragment_menu) }
    override fun initData() {
        tabAdapter = ModuleTabAdapter(childFragmentManager, lifecycle)
    }

    override fun initUI() {
        binding.vpModule.adapter = tabAdapter
        TabLayoutMediator(binding.tlModule, binding.vpModule) { tab, position ->
            when (position) {
                0 -> tab.text = "Module"
                1 -> tab.text = "Quiz Result"
            }
        }.attach()
    }

    override fun initAction() {}

    override fun initObserver() {}
}