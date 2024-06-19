package amirlabs.sapasemua.ui.menu.module.edit_module

import amirlabs.sapasemua.R
import amirlabs.sapasemua.data.model.SubModule
import amirlabs.sapasemua.databinding.ItemCreateVideoBinding
import amirlabs.sapasemua.databinding.ItemSubmoduleBinding
import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView

class EditModuleAdapter(private val onItemClick: (SubModule, Int) -> Unit, private val onAddClick: () -> Unit)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val listData = ArrayList<SubModule>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == 0) {
            val view: ItemCreateVideoBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_create_video, parent, false)
            LastViewHolder(view)
        }else{
            val view: ItemSubmoduleBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_submodule, parent, false)
            ViewHolder(view)
        }
    }

    override fun getItemCount() = listData.size+1

    override fun getItemViewType(position: Int): Int {
        if(position == listData.size) return 0
        return 1
    }
    fun add(data:SubModule){
        this.listData.add(data)
        notifyItemInserted(listData.lastIndex)
    }
    fun updateOne(data:SubModule, position: Int){
            listData[position] = data
            notifyItemChanged(position)
    }
    @SuppressLint("NotifyDataSetChanged")
    fun updateList(list: List<SubModule>) {
        this.listData.clear()
        this.listData.addAll(list)
        notifyDataSetChanged()
    }
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if(getItemViewType(position) == 1) {
            (holder as ViewHolder).bind(listData[position], position)
        } else {
            (holder as LastViewHolder).bind()
        }
    }

    inner class ViewHolder(private val binding: ItemSubmoduleBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(data: SubModule, position: Int) {
            with(binding) {
                tvSubmoduleTitle.text = data.name
                tvSubmoduleTime.text = "${data.duration ?: 10} minutes"
                binding.root.setOnClickListener { onItemClick(data, position) }
            }
        }
    }

    inner class LastViewHolder(private val binding: ItemCreateVideoBinding) :
        RecyclerView.ViewHolder(binding.root){
        fun bind(){
            with(binding){
                root.setOnClickListener { onAddClick() }
            }
        }
    }
}