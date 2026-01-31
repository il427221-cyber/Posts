data class Comment(
    val id: Int,
    val fromId: Int,
    val text: String,
    val replyToUser: Int,
    val replyToComment: Int,
    var isDeleted: Boolean = false,
    var noteId: Int
)