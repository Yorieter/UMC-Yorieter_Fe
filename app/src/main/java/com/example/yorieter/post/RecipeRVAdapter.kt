package com.example.yorieter.post

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.yorieter.R
import com.example.yorieter.databinding.ItemRecipeBinding

class RecipeRVAdapter(private val recipelist : ArrayList<Recipe>) : RecyclerView.Adapter<RecipeRVAdapter.MyViewHolder>() {

    var communityLike: Boolean = false
    //만들어진 뷰홀더 없을때 뷰홀더(레이아웃) 생성하는 함수
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecipeRVAdapter.MyViewHolder {
        val binding: ItemRecipeBinding =
            ItemRecipeBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        // 좋아요 구현
        binding.communityLikeIv.setOnClickListener {
            if (!communityLike) {
                binding.communityLikeIv.setImageResource(R.drawable.like_check)
                communityLike = true
            }
            else {
                binding.communityLikeIv.setImageResource(R.drawable.like_no_check)
                communityLike = false
            }
        }

        return MyViewHolder(binding)
    }

    interface RecipeItemClickListener {
        fun onItemClick(recipe: Recipe)
    }

    var mItemClickListener: RecipeItemClickListener? = null

    fun setRecipeItemClickListener(itemClickListener: RecipeItemClickListener) {
        mItemClickListener = itemClickListener
    }

    override fun onBindViewHolder(holder: RecipeRVAdapter.MyViewHolder, position: Int) {
        holder.bind(recipelist[position])
        holder.itemView.setOnClickListener {
            mItemClickListener?.onItemClick(recipelist[position])
        }
    }

    override fun getItemCount(): Int = recipelist.size

//    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
//        holder.title.text = datalist[position].recipe_title
//        holder.date.text = datalist[position].recipe_date
//        }

    inner class MyViewHolder(val binding: ItemRecipeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(recipe: Recipe) {
            //binding.itemRecipeImg.=recipeData.recipe_image
            recipe.imageUrl?.let {
                Glide.with(binding.root.context)
                    .load(it)
                    .placeholder(R.drawable.food_example02)
                    .into(binding.itemRecipeImg)
            } ?: binding.itemRecipeImg.setImageResource(R.drawable.food_example02) // 이미지가 없는 경우

            binding.titleTv.text = recipe.title
            binding.dateTv.text = recipe.createdAt

            binding.communityLikeIv.setOnClickListener {
                if (!communityLike) {
                    binding.communityLikeIv.setImageResource(R.drawable.like_check)
                    communityLike = true
                } else {
                    binding.communityLikeIv.setImageResource(R.drawable.like_no_check)
                    communityLike = false
                }
            }
        }
//        val coverImg : ImageView = binding.itemRecipeImg
//        val title : TextView = binding.titleTv
//        val date : TextView = binding.dateTv
    }
}

