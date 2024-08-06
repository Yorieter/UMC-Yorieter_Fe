package com.example.yorieter.post

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.yorieter.R
import com.example.yorieter.databinding.ItemRecipeBinding

class RecipeRVAdapter(private val recipelist : ArrayList<RecipeData>) : RecyclerView.Adapter<RecipeRVAdapter.MyViewHolder>() {

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
        fun onItemClick(recipeData: RecipeData)
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
        fun bind(recipeData: RecipeData) {
            //binding.itemRecipeImg.=recipeData.recipe_image
            binding.itemRecipeImg.setImageResource(recipeData.recipe_image!!)
            binding.titleTv.text = recipeData.recipe_title
            binding.dateTv.text = recipeData.recipe_date
        }
//        val coverImg : ImageView = binding.itemRecipeImg
//        val title : TextView = binding.titleTv
//        val date : TextView = binding.dateTv

    }
}

