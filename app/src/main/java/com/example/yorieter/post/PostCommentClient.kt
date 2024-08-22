package com.example.yorieter.post

import com.google.gson.annotations.SerializedName

data class PostCommentResponse(
    @SerializedName(value = "code") var code: String,
    @SerializedName(value = "message") var message: String,
    @SerializedName(value = "result") var result: PostComment,
    @SerializedName(value = "isSuccess") var isSuccess: Boolean
)

data class PostComment(
    @SerializedName(value = "id") var id: Int,
    @SerializedName(value = "content") var content: String,
    @SerializedName(value = "createdAt") var createdAt: String,
    @SerializedName(value = "updatedAt") var updatedAt: String,
    @SerializedName(value = "memberId") var memberId: Int,
    @SerializedName(value = "recipeId") var recipeId: Int,
    @SerializedName(value = "parentCommentId") var parentCommentId: Int,
    @SerializedName(value = "childComments") var childComments: List<String>,
    @SerializedName(value = "memberNickname") var memberNickname: String,
    @SerializedName(value = "memberProfile") var memberProfile: String
)