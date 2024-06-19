package amirlabs.sapasemua.ui.menu.module.list_quiz

import amirlabs.sapasemua.R
import amirlabs.sapasemua.data.model.Quiz
import amirlabs.sapasemua.databinding.ItemListQuizBinding
import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import java.io.File

class ListQuizAdapter(private val onItemClick: (Quiz) -> Unit, private val onItemDelete: (Quiz, Int) -> Unit) :
    RecyclerView.Adapter<ListQuizAdapter.ViewHolder>() {
    val listData = ArrayList<Quiz>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: ItemListQuizBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_list_quiz, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = listData.size
    fun add(data:Quiz){
        this.listData.add(data)
        notifyItemInserted(listData.lastIndex)
    }
    fun updateOne(data:Quiz, position: Int){
        listData[position] = data
        notifyItemChanged(position)
    }
    fun remove(position: Int){
        listData.removeAt(position)
        notifyItemRemoved(position)
    }
    @SuppressLint("NotifyDataSetChanged")
    fun updateList(list: List<Quiz>) {
        this.listData.clear()
        this.listData.addAll(list)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listData[position], position)
    }

    inner class ViewHolder(private val binding: ItemListQuizBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(data: Quiz, position: Int) {
            with(binding) {
                tvTitle.text = data.question
                binding.ivDelete.setOnClickListener { onItemDelete(data, position) }
                binding.root.setOnClickListener { onItemClick(data) }
            }
        }
    }
}