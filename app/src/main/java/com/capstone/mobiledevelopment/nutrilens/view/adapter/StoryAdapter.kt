//package com.capstone.mobiledevelopment.nutrilens.view.adapter
//
//import android.content.Intent
//import android.view.LayoutInflater
//import android.view.ViewGroup
//import androidx.recyclerview.widget.DiffUtil
//import androidx.recyclerview.widget.ListAdapter
//import androidx.recyclerview.widget.RecyclerView
//import com.bumptech.glide.Glide
//import com.capstone.mobiledevelopment.nutrilens.data.reponse.ListStoryItem
//import com.capstone.mobiledevelopment.nutrilens.databinding.StoryCardBinding
//import com.capstone.mobiledevelopment.nutrilens.view.detail.DetailActivity
//
//class StoryAdapter : ListAdapter<ListStoryItem, StoryAdapter.MyViewHolder>(DIFF_CALLBACK) {
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
//        val binding = StoryCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
//        return MyViewHolder(binding)
//    }
//
//    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
//        val story = getItem(position)
//        holder.bind(story)
//    }
//
//    class MyViewHolder(private val binding: StoryCardBinding) :
//        RecyclerView.ViewHolder(binding.root) {
//        fun bind(story: ListStoryItem) {
//            binding.tvItemName.text = story.name
//            binding.tvItemDescript.text = story.description
//            Glide.with(binding.root.context).load(story.photoUrl).into(binding.ivItemPhoto)
//            binding.root.setOnClickListener {
//                val intentDetail = Intent(binding.root.context, DetailActivity::class.java).apply {
//                    putExtra("story", story)
//                }
//                binding.root.context.startActivity(intentDetail)
//            }
//        }
//    }
//
//    companion object {
//        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>() {
//            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
//                return oldItem.id == newItem.id
//            }
//
//            override fun areContentsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
//                return oldItem == newItem
//            }
//        }
//    }
//}