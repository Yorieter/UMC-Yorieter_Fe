package com.example.yorieter.post

import com.google.gson.annotations.SerializedName

data class CommentsResponse(
    @SerializedName("code") val code: String,
    @SerializedName("message") val message: String,
    @SerializedName("result") val result: List<Comment>?,
    @SerializedName("isSuccess") val isSuccess: Boolean
)

data class CommentRequest(
    @SerializedName("content") val content: String
)

data class Comment(
    @SerializedName("id") val id: Int,
    @SerializedName("content") val content: String,
    @SerializedName("createdAt") val createdAt: String,
    @SerializedName("updatedAt") val updatedAt: String,
    @SerializedName("memberId") val memberId: Int,
    @SerializedName("recipeId") val recipeId: Int,
    @SerializedName("parentCommentId") val parentCommentId: Int,
    @SerializedName("childComments") val childComments: List<String>
)