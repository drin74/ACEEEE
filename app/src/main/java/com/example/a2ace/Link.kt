package com.example.a2ace


data class Link(
    val id: String,
    val title: String,
    val aceStreamId: String,
    val iconResId: Int = 0
) {
    fun getFullUrl(): String {
        return if (aceStreamId.startsWith("acestream://")) {
            aceStreamId
        } else {
            "acestream://$aceStreamId"
        }
    }
}