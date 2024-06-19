package amirlabs.sapasemua.ui.menu.module

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class ModuleTabAdapter(fragment: FragmentManager, lifecycle: Lifecycle): FragmentStateAdapter(fragment, lifecycle) {
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> ListModuleFragment()
            1 -> ListQuizResultFragment()
            else -> ListModuleFragment()
        }
    }
}