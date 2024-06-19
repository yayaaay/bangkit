package amirlabs.sapasemua.ui.menu.forum.list

import amirlabs.sapasemua.databinding.ItemForumBinding
import amirlabs.sapasemua.databinding.LayoutLoadingBinding
import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class ForumListAdapter(private val onItemClick: (Forum?) -> Unit, private val onLoadMore: () -> Unit) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val listData = ArrayList<Forum?>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == 1) {
            val view: LayoutLoadingBinding = LayoutLoadingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return LoadingViewHolder(view)
        }
        val view: ItemForumBinding = ItemForumBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = listData.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(list: List<Forum>) {
        this.listData.clear()
        this.listData.addAll(list)
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addAll(list: List<Forum>) {
        this.listData.addAll(list)
        notifyDataSetChanged()
    }

    fun addLoadingView() {
        Looper.myLooper()?.let {
            Handler(it).post {
                listData.add(null)
                notifyItemInserted(listData.size - 1)
            }
        }
    }
    fun removeLoadingView() {
        //Remove loading item
        listData.removeAt(listData.size - 1)
        notifyItemRemoved(listData.size)
    }
    override fun getItemViewType(position: Int): Int {
        if (position == listData.size-1) {
            return 1
        }
        return 0
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is LoadingViewHolder) {
            holder.bind(listData[position], position)
        }else if (holder is ViewHolder) {
            holder.bind(listData[position], position)
        }
    }

    inner class ViewHolder(private val binding: ItemForumBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(data: Forum?, position: Int) {
            with(binding) {
                tvForumTitle.text = data?.title
                tvForumDesc.text = data?.description

//                val image: ByteArray = Base64.decode(data?.creator?.avatar ?:"", Base64.DEFAULT)
                Glide.with(binding.root.context)
//                    .asBitmap()
                    .load(data?.creator?.avatar)
                    .into(ivForumAvatar)

                binding.root.setOnClickListener { onItemClick(data) }
            }
        }
    }

    inner class LoadingViewHolder(private val binding: LayoutLoadingBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(data: Forum?, position: Int) {
            with(binding) {
//                if (!binding.progressIndicator.isVisible) onLoadMore()
            }
        }
    }
}