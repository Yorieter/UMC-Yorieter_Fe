package com.example.yorieter.mypage.api.ResponseData

import com.google.gson.annotations.SerializedName

// 내가 작성한 댓글 목록 조회 응답 클래스
data class GetMyCommentResponse(
    @SerializedName(value = "code") var code: String,
    @SerializedName(value = "message") var message: String,
    @SerializedName(value = "result") var result: GetCommentResult,
    @SerializedName(value = "isSuccess") var isSuccess: Boolean
)

data class GetCommentResult(
    @SerializedName(value = "commentList") var commentList: List<CommentList>,
    @SerializedName(value = "listSize") var listSize: Int,
    @SerializedName(value = "totalPage") var totalPage: Int,
    @SerializedName(value = "totalElements") var totalElements: Int,
    @SerializedName(value = "isFirst") var isFirst: Boolean,
    @SerializedName(value = "isLast") var isLast: Boolean
)

data class CommentList(
    @SerializedName(value = "username") var username: String,
    @SerializedName(value = "content") var content: String,
    @SerializedName(value = "createdAt") var createdAt: String
)
