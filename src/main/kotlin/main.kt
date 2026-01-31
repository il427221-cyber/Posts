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

class NoteNotFoundException(message: String) : RuntimeException()

class CommentNotFoundException(message: String) : RuntimeException()

object WallService : NoteService<Note, Comment> {

    private var notes = mutableListOf<Note>()
    private var posts = mutableListOf<Post>()
    private var comments = mutableListOf<Comment>()
    var nextUniqueId = 1
    var nextNoteId = 1
    var nextCommentId = 1

    fun createComment(postId: Int, comment: Comment): Comment {
        for (post in posts) {
            if (post.id == postId) {
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
        notes.clear()
        posts.clear()
        comments.clear()
        nextNoteId = 1
        nextCommentId = 1
        nextUniqueId = 1
    }

    override fun add(note: Note): Note {
        val noteNewId = nextNoteId++
        val noteWithId = note.copy(id = noteNewId)
        notes.add(noteWithId)

        return noteWithId
    }

    override fun createComment_toNote(noteId: Int, comment: Comment): Comment {
        val existingNote = notes.find { it.id == noteId && !it.isDeleted }
            ?: throw NoteNotFoundException("Заметка с id=$noteId не найдена или удалена")
        val newComment = comment.copy(id = nextCommentId++, noteId = noteId)

        comments.add(newComment)
        return newComment
    }

    override fun delete(noteId: Int): Boolean {
        val noteToDelete = notes.find { it.id == noteId && !it.isDeleted }
            ?: throw NoteNotFoundException("Заметка с id=$noteId не найдена или уже удалена")

        noteToDelete.isDeleted = true

        comments.filter { it.noteId == noteId && !it.isDeleted }.forEach { it.isDeleted = true }
        return true
    }

    override fun deleteComment(commentId: Int): Boolean {
        val commentToDelete = comments.find { it.id == commentId && !it.isDeleted }
            ?: throw CommentNotFoundException("Комментарий с id=$commentId не найден или уже удален")

        val parentNote = notes.find { it.id == commentToDelete.noteId && !it.isDeleted }
            ?: throw NoteNotFoundException("Заметка, к которой относится комментарий (id=${commentToDelete.noteId}), удалена")

        commentToDelete.isDeleted = true
        return true

    }

    override fun edit(note: Note): Boolean {
        val index = notes.indexOfFirst { it.id == note.id && !it.isDeleted }

        if (index == -1) {
            throw NoteNotFoundException("Заметка с id=${note.id} не найдена или удалена")
        }
        notes[index] = notes[index].copy(
            title = note.title,
            text = note.text
        )
        return true
    }

    override fun editComment(comment: Comment): Boolean {
        val index = comments.indexOfFirst { it.id == comment.id && !it.isDeleted }

        if (index == -1) {
            throw CommentNotFoundException("Комментарий с id=${comment.id} не найден или удален")
        }
        val existingComment = comments[index]

        val noteToBeCommented = notes.find { it.id == existingComment.noteId && !it.isDeleted }
            ?: throw NoteNotFoundException("Заметка, к которой относится комментарий (id=${existingComment.noteId}), удалена")

        comments[index] = existingComment.copy(
            text = comment.text
        )
        return true
    }

    override fun getNotes(): List<Note> {
        return notes.filter { !it.isDeleted }
    }

    override fun getById(noteId: Int): Note {
        return notes.find { it.id == noteId && !it.isDeleted }
            ?: throw NoteNotFoundException("Заметка с id=$noteId не найдена или уже удалена")
    }

    override fun getComments(noteId: Int): List<Comment> {
        val note = notes.find { it.id == noteId && !it.isDeleted }
            ?: throw NoteNotFoundException("Заметка с id=$noteId не найдена или удалена")
        return comments.filter { it.id == noteId && !it.isDeleted }
    }

    override fun restoreComment(commentId: Int): Boolean {
        val commentToRestore = comments.find { it.id == commentId && it.isDeleted }
            ?: throw CommentNotFoundException("Комментарий с id=$commentId не найден или не был удален")

        val parentNote = notes.find { it.id == commentToRestore.noteId && !it.isDeleted }
            ?: throw NoteNotFoundException("Невозможно восстановить комментарий: Заметка к нему удалена")

        commentToRestore.isDeleted = false
        return true

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
}