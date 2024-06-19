package amirlabs.sapasemua.ui.menu.module

import amirlabs.sapasemua.R
import amirlabs.sapasemua.data.model.QuizResult
import amirlabs.sapasemua.data.model.User
import amirlabs.sapasemua.databinding.ItemListQuizResultBinding
import amirlabs.sapasemua.utils.prefs
import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Locale

class ListQuizResultAdapter(private val onItemClick: (QuizResult) -> Unit) : RecyclerView.Adapter<ListQuizResultAdapter.ViewHolder>() {
    val listData = ArrayList<QuizResult>()
    private val user = prefs().getObject("user", User::class.java)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListQuizResultAdapter.ViewHolder {
        val view: ItemListQuizResultBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_list_quiz_result,
            parent,
            false
        )
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListQuizResultAdapter.ViewHolder, position: Int) {
        holder.bind(listData[position], position)
    }

    override fun getItemCount() = listData.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(list: List<QuizResult>) {
        this.listData.clear()
        this.listData.addAll(list)
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val binding: ItemListQuizResultBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(data: QuizResult, position: Int) {
            with(binding) {
                tvTitle.text = data.module?.name
                tvMark.text = data.score.toString()

                val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale("in", "ID"))
                val date = sdf.parse(data.createdAt ?: "")
                val sdf2 = SimpleDateFormat("dd MMMM yyyy", Locale("in", "ID"))
                binding.tvDate.text = sdf2.format(date ?: "")

                binding.root.setOnClickListener { onItemClick(data) }
            }
        }
    }
}