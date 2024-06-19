package amirlabs.sapasemua.ui.menu.forum.detail

import amirlabs.sapasemua.R
import amirlabs.sapasemua.databinding.ItemCommentBinding
import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class ForumDetailAdapter(private val onItemClick: (Comment) -> Unit) : RecyclerView.Adapter<ForumDetailAdapter.ViewHolder>() {
    val listData = ArrayList<Comment>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: ItemCommentBinding = ItemCommentBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(view)
    }

    override fun getItemCount() = listData.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listData[position], position)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(list: List<Comment>) {
        this.listData.clear()
        this.listData.addAll(list)
        notifyDataSetChanged()
    }
    fun addFirst(data: Comment) {
        this.listData.add(0, data)
        notifyItemInserted(0)
    }
    inner class ViewHolder(private val binding: ItemCommentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(data: Comment, position: Int) {
            with(binding) {
                tvForumCreatorName.text = "${data.creator?.name}-(${data.creator?.role})"
                tvForumDesc.text = data.title

//                val image: ByteArray = Base64.decode(data.creator?.avatar ?:"", Base64.DEFAULT)
                Glide.with(binding.root.context)
//                    .asBitmap()
                    .load(data.creator?.avatar)
                    .error(R.drawable.ic_profile)
                    .into(ivForumAvatar)

                binding.root.setOnClickListener { onItemClick(data) }
            }
        }
    }

}