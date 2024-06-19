package amirlabs.sapasemua.ui.menu.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import amirlabs.sapasemua.R
import amirlabs.sapasemua.base.DevFragment
import amirlabs.sapasemua.databinding.FragmentAboutUsBinding
import amirlabs.sapasemua.utils.toBulletedList
import androidx.navigation.NavController
import androidx.navigation.findNavController

class AboutUsFragment : DevFragment<FragmentAboutUsBinding>(R.layout.fragment_about_us) {
    private val menuNavController: NavController? by lazy { activity?.findNavController(R.id.nav_host_fragment_menu) }
    override fun initData() {

    }

    override fun initUI() {
        binding.tvGoal1.text = listOf("Memudahkan seseorang yang ingin belajar bahasa isyarat dengan mudah tanpa datang ketempat.\n",
            "Membantu temanSapa untuk mengenal bahasa isyarat dengan interaktif dan intuitif.\n", "Menjadi wadah teman-teman tuli untuk saling berinteraksi sesama.").toBulletedList()
    }

    override fun initAction() {
        binding.btnBack.setOnClickListener {
            menuNavController?.popBackStack()
        }
    }

    override fun initObserver() {

    }

}