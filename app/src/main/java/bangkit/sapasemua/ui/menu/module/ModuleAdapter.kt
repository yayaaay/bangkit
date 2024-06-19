package amirlabs.sapasemua.ui.menu.module

import amirlabs.sapasemua.R
import amirlabs.sapasemua.data.model.Module
import amirlabs.sapasemua.data.model.User
import amirlabs.sapasemua.databinding.ItemModuleBinding
import amirlabs.sapasemua.utils.isAdmin
import amirlabs.sapasemua.utils.prefs
import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class ModuleAdapter(private val onItemClick: (Module) -> Unit, private val onDeleteClick: (Module) -> Unit) :
    RecyclerView.Adapter<ModuleAdapter.ViewHolder>() {
    val listData = ArrayList<Module>()
    private val user = prefs().getObject("user", User::class.java)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ModuleAdapter.ViewHolder {
        val view: ItemModuleBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_module,
            parent,
            false
        )
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ModuleAdapter.ViewHolder, position: Int) {
        holder.bind(listData[position])
    }

    override fun getItemCount() = listData.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(list: List<Module>) {
        this.listData.clear()
        this.listData.addAll(list)
        notifyDataSetChanged()
    }

    fun deleteItem(moduleId:String) {
        val index = listData.indexOfFirst { it.id == moduleId }
        if (index != -1) {
            listData.removeAt(index)
            notifyItemRemoved(index)
        }
    }

    inner class ViewHolder(private val binding: ItemModuleBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(data: Module) {
            with(binding) {
                tvModuleTitle.text = data.name
                tvModuleDesc.text = data.description
                tvModuleLevel.text = when(data.level) {
                    1 -> "Basic"
                    2 -> "Intermediate"
                    3 -> "Advanced"
                    else -> "Beginner"
                }
                Glide.with(binding.root.context)
                    .load(data.image)
                    .into(ivModule)

                binding.ivDelete.visibility = if (isAdmin() && user?.id == data.creator) android.view.View.VISIBLE else android.view.View.GONE
                binding.ivDelete.setOnClickListener { onDeleteClick(data) }
                binding.root.setOnClickListener { onItemClick(data) }
            }
        }
    }
}