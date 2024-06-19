package amirlabs.sapasemua.ui.menu.forum

import amirlabs.sapasemua.databinding.ItemForumBinding
import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class ForumAdapter(private val onItemClick: (Forum) -> Unit) :
    RecyclerView.Adapter<ForumAdapter.ViewHolder>() {
    val listData = ArrayList<Forum>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: ItemForumBinding = ItemForumBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(view)
    }

    override fun getItemCount() = listData.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(list: List<Forum>) {
        this.listData.clear()
        this.listData.addAll(list)
        notifyDataSetChanged()
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listData[position], position)
    }

    inner class ViewHolder(private val binding: ItemForumBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(data: Forum, position: Int) {
            with(binding) {
                tvForumTitle.text = data.title
                tvForumDesc.text = data.description

//                val image: ByteArray = Base64.decode(data.creator?.avatar ?:"", Base64.DEFAULT)
                Glide.with(binding.root.context)
//                    .asBitmap()
                    .load(data.creator?.avatar)
                    .into(ivForumAvatar)

                binding.root.setOnClickListener { onItemClick(data) }
            }
        }
    }
}