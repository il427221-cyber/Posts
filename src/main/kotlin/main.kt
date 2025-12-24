data class Post(
    val id: Int = 0, val ownerId: Int = 10, val friendsOnly: Int = 1,
    val text: String = "Новый пост", val likes: Likes,
    val canDelete: Boolean = true, val canEdit: Boolean = true, val views: Views,
    val isPinned: Boolean = true, val markedAsAds: Boolean = false
)

class Likes(
    val count: Int, val userLikes: Boolean = true,
    val canLike: Boolean = true, val canPublish: Boolean = true
)

class Views(val count: Int = 3)

object WallService {
    var posts = emptyArray<Post>()
     var nextUniqueId = 1

    fun add(post: Post): Post {
        val newId = nextUniqueId++
        val postWithId = post.copy(id = newId)
        posts += postWithId

        return postWithId
    }

    fun update(post: Post): Boolean {
        for ((index, existingPost) in posts.withIndex()) {
            if (existingPost.id == post.id) {
                posts[index] = existingPost.copy(
                    ownerId = post.ownerId,
                    friendsOnly = post.friendsOnly,
                    text = post.text,
                    likes = post.likes,
                    canDelete = post.canDelete,
                    canEdit = post.canEdit,
                    views = post.views,
                    isPinned = post.isPinned,
                    markedAsAds = post.markedAsAds
                )
                return true
            }
        }
        return false
    }

    fun clear() {
        posts = emptyArray()
    }

}

fun main() {
    val likes = Likes(20)
    val views = Views()
    val post = Post(likes = likes, views = views)
}
