package amirlabs.sapasemua.ui.menu.module.quiz

import amirlabs.sapasemua.data.model.Quiz
import amirlabs.sapasemua.databinding.ItemQuizResultBinding
import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class QuizResultAdapter : RecyclerView.Adapter<QuizResultAdapter.ViewHolder>() {
    val listData = ArrayList<Quiz>()
    private val listAnswers = ArrayList<String>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemQuizResultBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount()= listData.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(list: List<Quiz>, answers: List<String>) {
        listAnswers.clear()
        listAnswers.addAll(answers)
        listData.clear()
        listData.addAll(list)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listData[position], position)
    }
    inner class ViewHolder(val binding: ItemQuizResultBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Quiz, position: Int) {
            with(binding) {
                cardResult.setCardBackgroundColor(
                    if (data.answer == listAnswers[position]) {
                        itemView.resources.getColor(android.R.color.holo_green_light, null)
                    } else {
                        itemView.resources.getColor(android.R.color.holo_red_light, null)
                    }
                )
                tvAnswer.text = listAnswers[position]
            }
        }
    }
}