package amirlabs.sapasemua.ui.menu.module

import amirlabs.sapasemua.R
import amirlabs.sapasemua.base.DevFragment
import amirlabs.sapasemua.data.model.User
import amirlabs.sapasemua.databinding.FragmentListModuleBinding
import amirlabs.sapasemua.utils.DevState
import amirlabs.sapasemua.utils.DialogUtils
import amirlabs.sapasemua.utils.getViewModel
import amirlabs.sapasemua.utils.isAdmin
import amirlabs.sapasemua.utils.logError
import amirlabs.sapasemua.utils.prefs
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.findNavController

class ListModuleFragment : DevFragment<FragmentListModuleBinding>(R.layout.fragment_list_module) {
    override val vm: ModuleViewModel by getViewModel()
    private val menuNavController: NavController? by lazy { activity?.findNavController(R.id.nav_host_fragment_menu) }
    private val user = prefs().getObject("user", User::class.java)
    private lateinit var adapter: ModuleAdapter

    override fun initData() {
        adapter = ModuleAdapter({
            if (isAdmin() && user?.id == it.creator){
                menuNavController?.navigate(ModuleFragmentDirections.actionModuleFragmentToEditModuleFragment(it.id!!))
            }else {
                menuNavController?.navigate(ModuleFragmentDirections.actionModuleFragmentToSubModuleFragment(it.id))
            }
        }, { item ->
            DialogUtils.showAlertDialog(requireContext(), "Apakah anda yakin ingin menghapus modul ?", "Ya", "Tidak", {
                item.id?.let { vm.deleteModule(it) }
            })
        })

    }

    override fun initUI() {
        binding.rvModule.adapter = adapter
        binding.btnAddModule.visibility = if (isAdmin()) android.view.View.VISIBLE else android.view.View.GONE
    }

    override fun initAction() {
        binding.btnAddModule.setOnClickListener {
            menuNavController?.navigate(ModuleFragmentDirections.actionModuleFragmentToAddModuleFragment())
        }
        vm.getAllModule()
    }

    override fun initObserver() {
        vm.modules.observe(viewLifecycleOwner) {
            when (it) {
                is DevState.Loading -> {
                    binding.msvModule.showLoadingLayout()
                }

                is DevState.Empty -> {
                    binding.msvModule.showEmptyLayout()
                }

                is DevState.Success -> {
                    binding.msvModule.showDefaultLayout()
                    adapter.updateList(it.data)
                }

                is DevState.Failure -> {
                    binding.msvModule.showErrorLayout()
                    Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                    logError(it.message)
                }

                is DevState.Default -> {

                }
            }
        }
        vm.deleteModule.observe(viewLifecycleOwner) {
            when (it) {
                is DevState.Loading -> {
                    binding.msvModule.showLoadingLayout()
                }

                is DevState.Empty -> {
                    binding.msvModule.showEmptyLayout()
                }

                is DevState.Success -> {
                    binding.msvModule.showDefaultLayout()
                    it.data.id?.let { it1 -> adapter.deleteItem(it1) }
                }

                is DevState.Failure -> {
                    binding.msvModule.showErrorLayout()
                    Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                    logError(it.message)
                }

                is DevState.Default -> {

                }
            }
        }
    }
}