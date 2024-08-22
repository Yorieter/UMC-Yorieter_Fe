package com.example.yorieter.mypage.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.yorieter.R
import com.example.yorieter.databinding.ItemMylikeBinding
import com.example.yorieter.mypage.dataclass.Mylike
import com.example.yorieter.mypage.viewModel.MyLikeViewModel
import com.example.yorieter.post.CommunitylikeResponse
import com.example.yorieter.post.PostRetrofitItf
import com.example.yorieter.post.PostRetrofitObj
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MylikeRVAdapter(private val context: Context, private val mylikeList: ArrayList<Mylike>): RecyclerView.Adapter<MylikeRVAdapter.ViewHolder>() {

    //private var mylikeList: List<Mylike> = emptyList()
    interface OnItemClickListener { // 클릭리스너 인터페이스 생성
        fun onItemClick(view: View, position: Int)
    }

    var itemClickListner: OnItemClickListener? = null

    private fun getToken(): String? {
        val sharedPref = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        return sharedPref.getString("token", null)
    }

    private fun saveLikeState(recipeId: Int, isLiked: Boolean) {
        val sharedPref = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putBoolean("liked_$recipeId", isLiked)
            apply()
            android.util.Log.d("SharedPreferences", "Saved like state: recipeId=$recipeId, isLiked=$isLiked")
        }
    }

    private fun getLikeState(recipeId: Int): Boolean {
        val sharedPref = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val isLiked = sharedPref.getBoolean("liked_$recipeId", false)
        Log.d("SharedPreferences", "Retrieved like state: recipeId=$recipeId, isLiked=$isLiked")
        return isLiked
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MylikeRVAdapter.ViewHolder {
        val binding: ItemMylikeBinding = ItemMylikeBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MylikeRVAdapter.ViewHolder, position: Int) {
        holder.bind(mylikeList[position])
    }

    override fun getItemCount(): Int = mylikeList.size

    inner class ViewHolder(val binding: ItemMylikeBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(mylike: Mylike){
            Glide.with(binding.itemMylikeIv.context)
                .load(mylike.coverImg)
                .error(R.drawable.mypage_ic_yorieter_profile)
                .into(binding.itemMylikeIv)

            // 제목과 날짜 설정
            binding.itemMylikeTitleTv.text = mylike.title
            binding.mylikeDate.text = mylike.date

            // 아이템 클릭 리스너 설정
            itemView.setOnClickListener {
                itemClickListner?.onItemClick(it, adapterPosition)
            }

            // 초기 좋아요 상태 설정
            val isLiked = getLikeState(mylike.recipeId) // 좋아요된 상태
            updateLikeUI(isLiked)

            binding.mypageLikeIv.setOnClickListener {
                if(mylike.mypagelike){
                    //updateLikeUI(false)
                    unlikeRecipe(mylike.recipeId, mylike) // 좋아요 취소
                } else {
                    //updateLikeUI(true)
                    likeRecipe(mylike.recipeId, mylike) // 좋아요
                }
            }
        }

        // 좋아요 UI 업데이트 함수
        private fun updateLikeUI(isLiked: Boolean){
            binding.mypageLikeIv.setImageResource(
                if (isLiked) R.drawable.recipe_like else R.drawable.recipe_like_new
            )
        }

        private fun likeRecipe(recipeId: Int, mylike: Mylike){
            val likeService = PostRetrofitObj.getRetrofit().create(PostRetrofitItf::class.java)
            val token = getToken()

            if (token != null) {
                likeService.recipesLike("Bearer $token", recipeId).enqueue(object :
                    Callback<CommunitylikeResponse> {
                    override fun onResponse(call: Call<CommunitylikeResponse>, response: Response<CommunitylikeResponse>) {
                        if (response.isSuccessful) {
                            mylike.mypagelike = true
                            saveLikeState(recipeId, true)
                            updateLikeUI(true)
                            Log.d("좋아요/SUCCESS", "좋아요 성공")
                        } else {
                            handleLikeError(response, mylike)
                        }
                    }

                    override fun onFailure(call: Call<CommunitylikeResponse>, t: Throwable) {
                        Log.e("좋아요 API/FAILURE", "Network failure: ${t.message}")
                    }
                })
            }
        }

        private fun unlikeRecipe(recipeId: Int, mylike: Mylike){
            val likeService = PostRetrofitObj.getRetrofit().create(PostRetrofitItf::class.java)
            val token = getToken()

            if (token != null) {
                likeService.recipeDelete("Bearer $token", recipeId).enqueue(object : Callback<CommunitylikeResponse> {
                    override fun onResponse(call: Call<CommunitylikeResponse>, response: Response<CommunitylikeResponse>) {
                        if (response.isSuccessful) {
                            mylike.mypagelike = false
                            saveLikeState(recipeId, false)
                            updateLikeUI(false)
                            Log.d("좋아요/SUCCESS", "좋아요 취소 성공")
                        } else {
                            handleLikeError(response, mylike)
                        }
                    }

                    override fun onFailure(call: Call<CommunitylikeResponse>, t: Throwable) {
                        Log.e("좋아요 API/FAILURE", "Network failure: ${t.message}")
                    }
                })
            }
        }

        private fun handleLikeError(response: Response<CommunitylikeResponse>, mylike: Mylike){
            val errorBody = response.errorBody()?.string()
            if (errorBody != null) {
                val errorJson = JSONObject(errorBody)
                val errorCode = errorJson.getString("code")

                when (errorCode) {
                    "RECIPE409" -> {
                        // 이미 상태가 일치하는 경우
                        Log.e("좋아요/ERROR", "이미 좋아요 상태가 맞습니다.")
                        mylike.mypagelike = !mylike.mypagelike
                        saveLikeState(mylike.recipeId, mylike.mypagelike)
                        updateLikeUI(mylike.mypagelike)
                    }
                    else -> {
                        Log.e("좋아요/ERROR", "Error: $errorBody")
                    }
                }
            }
        }


    }
}