interface NoteService<T, C> {// T - заметка, C - комментарий

    fun add(note: T): T
    fun createComment_toNote(noteId: Int, comment: C): C
    fun delete(noteId: Int): Boolean
    fun deleteComment(commentId: Int): Boolean
    fun edit(note: T): Boolean
    fun editComment(comment: C): Boolean
    fun getNotes(): List<T>
    fun getById(noteId: Int): T
    fun getComments(noteId: Int): List<C>
    fun restoreComment(commentId: Int): Boolean
}