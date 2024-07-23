package com.example.dicodingstory.ui.liststory

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.dicodingstory.data.local.entity.StoryEntity
import com.example.dicodingstory.databinding.ItemStoryBinding

class StoryAdapter : PagingDataAdapter<StoryEntity, StoryAdapter.StoryViewHolder>(DIFF_CALLBACK) {

    private var onItemClickCallback: OnItemClickCallback? = null

    fun setOnItemClickCallback (onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    inner class StoryViewHolder(private val binding: ItemStoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(storyEntity: StoryEntity) {
            binding.root.setOnClickListener{
                onItemClickCallback?.onItemClicked(storyEntity)
            }
            binding.apply {
                tvItemName.text = storyEntity.name
                Glide.with(itemView.context)
                    .load(storyEntity.photoUrl)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(ivItemPhoto)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val binding = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        val storyEntity = getItem(position)
        if (storyEntity != null) {
            holder.bind(storyEntity)
        }
    }

    interface OnItemClickCallback {
        fun onItemClicked(storyEntity: StoryEntity)
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<StoryEntity>() {
            override fun areItemsTheSame(oldItem: StoryEntity, newItem: StoryEntity): Boolean {
                return oldItem.id == newItem.id
            }
            override fun areContentsTheSame(oldItem: StoryEntity, newItem: StoryEntity): Boolean {
                return oldItem == newItem
            }
        }
    }
}