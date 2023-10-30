package com.example.mystoryapp.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.mystoryapp.data.database.Story
import com.example.mystoryapp.databinding.ItemListStoryBinding
import com.example.mystoryapp.ui.detail.DetailActivity
import com.example.mystoryapp.ui.detail.DetailActivity.Companion.loadImage

class ListStoryAdapter : PagingDataAdapter<Story, ListStoryAdapter.MyViewHolder>(
    DIFF_CALLBACK
) {

    class MyViewHolder(private val binding: ItemListStoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(story: Story) {
            with(binding) {
                ivStory.loadImage(story.photoUrl)
                tvTitle.text = story.name
                tvDescription.text = story.description
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val binding = ItemListStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val story = getItem(position)
        if (story != null) {
            holder.bind(story)
        }
        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, DetailActivity::class.java)
            intent.putExtra(DetailActivity.STORY_ID, story?.id)
            holder.itemView.context.startActivity(intent, ActivityOptionsCompat.makeSceneTransitionAnimation(holder.itemView.context as Activity).toBundle())
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Story>() {
            override fun areItemsTheSame(oldItem: Story, newItem: Story): Boolean {
                return oldItem == newItem
            }
            override fun areContentsTheSame(oldItem: Story, newItem: Story): Boolean {
                return oldItem == newItem
            }
        }
    }
}