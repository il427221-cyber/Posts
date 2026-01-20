import java.lang.RuntimeException

data class Post(
    val id: Int, val ownerId: Int = 10, val friendsOnly: Int = 1,
    val text: String = "Новый пост", val likes: Likes?,
    val canDelete: Boolean = true, val canEdit: Boolean = true, val views: Views?,
    val isPinned: Boolean = true, val markedAsAds: Boolean = false,
    val attachments: Array<Attachment> = emptyArray()
)

class Likes(
    val count: Int, val userLikes: Boolean = true,
    val canLike: Boolean = true, val canPublish: Boolean = true
)

class Views(val count: Int = 3)

class PostNotFoundException(message: String) : RuntimeException()

object WallService {
    private var posts = emptyArray<Post>()
    private var comments = emptyArray<Comment>()
    var nextUniqueId = 1

    //функция добавления комментария
    fun createComment(postId: Int, comment: Comment): Any {
        for (post in posts) {
            if (post.id != postId) {
                comments += comment
                return comments.last()
            }
        }
        throw PostNotFoundException("Нет поста с таким $postId")
    }

    fun getLikesCountForPost(post: Post): Int {
        return post.likes?.count ?: 0
    }

    fun getViewsCountForPost(post: Post): Int {
        return post.views?.count ?: 0
    }

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
    val photo = Photo(10, "Пейзаж", 4, 30, 40)
    val video = Video("Как сделать жизнь проще", "полезные лайфхаки", 10, 55, "YouTube")
    val audio = Audio("Queen", "Богемская Рапсодия", 2, 2026, 6)
    val document = Document(7, "История Российской Империи", 999, "pdf", "https://drive.google.com")
    val note = Note(8, "Погода", "прогноз на неделю", 11, "url_777")

    val attachmentsArray = arrayOf(
        PhotoAttachment(photo = photo),
        VideoAttachment(video = video),
        AudioAttachment(audio = audio),
        DocumentAttachment(document = document),
        NoteAttachment(note = note)
    )

    val likes = Likes(20)
    val views = Views()
    val postWithLikes = Post(likes = likes, views = views, attachments = attachmentsArray, id = 10)
    val postWithoutLikes = Post(likes = null, views = null, attachments = attachmentsArray, id = 5)

}
