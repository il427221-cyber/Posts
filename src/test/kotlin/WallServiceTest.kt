import org.junit.Assert
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class WallServiceTest {

    @Before
    fun clearBeforeTest() {
        WallService.clear()
    }

    @Test
    fun add() {
        val initialPost = Post(text = "Stories", likes = Likes(100), views = Views(32), id = 4)
        val addedPost = WallService.add(initialPost)

        assertNotEquals(0, addedPost.id)
    }

    @Test
    fun updateExisting_returnsTrue() {
        val addedPost =
            WallService.add(Post(ownerId = 10, text = "Изначальный пост", likes = Likes(30), views = Views(5), id = 10))

        val postToUpdate = Post(
            id = addedPost.id,
            ownerId = 10,
            text = "Обновленный текст поста",
            likes = Likes(70),
            views = Views(20)

        )
        val result = WallService.update(postToUpdate)

        assertTrue(result)
    }

    @Test
    fun updateNotExisting_returnsFalse() {
        val service = WallService
        service.add(Post(ownerId = 10, text = "Добавленный пост", likes = Likes(30), views = Views(5), id = 3))

        val update = Post(
            id = 5,
            ownerId = 10,
            text = "Добавленный пост",
            likes = Likes(30),
            views = Views(5)

        )
        val result = service.update(update)

        assertFalse(result)
    }

    //Добавлены тесты на исключение и проверку добавленного комментария по id
    @Test(expected = PostNotFoundException::class)
    fun shouldThrow() {
        val comment = Comment(57, 111, "Новый комментарий", 222, 12, noteId = 2)
        val postId = 44
        val newPost = WallService.createComment(44, comment)

        println(PostNotFoundException("Нет поста с таким $postId"))
    }

    @Test
    fun addingComment() {
        val initialPost = Post(0, text = "Перезалив лекции", likes = Likes(0), views = Views(5))
        val newPost = WallService.add(initialPost)
        val commentToCreate = Comment(1, 2, "Отличная лекция", 12, 5, noteId = 6)
        val createdComment = WallService.createComment(postId = newPost.id, comment = commentToCreate)

        assertEquals(commentToCreate, createdComment)
    }

    @Test
    fun addNote() {
        val initialNote = Note(0, "Новая заметка", "Note", 5, "https://www.notes.com")
        val addedNote = WallService.add(initialNote)

        assertNotEquals("ID заметки должен быть сгенерирован", 0, addedNote.id)
        assertTrue(WallService.getNotes().contains(addedNote))
    }


    @Test(expected = NoteNotFoundException::class)
    fun createComment_should_throw_NoteNotFoundException() {
        val initialNote = Note(0, "Заметка", "Текст к заметке", 5, "https://www.example.com", false)
        val addedNote = WallService.add(initialNote) // Пусть addedNote.id = 1

        WallService.delete(addedNote.id)
        val initialComment = Comment(0, 2, "Комментарий", 7, 8, false, 7)

        WallService.createComment_toNote(noteId = addedNote.id, comment = initialComment)
    }

    @Test
    fun deleteNote() {
        val initialNote = Note(0, "Заметка", "Текст к заметке", 5, "https://www.example.com", false)
        val newNote = WallService.add(initialNote)

        val deletedNote = WallService.delete(noteId = newNote.id)
        assertTrue(deletedNote)
    }

    @Test(expected = NoteNotFoundException::class)
    fun delete_should_throw_NoteNotFoundException_for_nonExistent_note() {
        val nonExistentNoteId = 777
        WallService.delete(noteId = nonExistentNoteId)
    }

    @Test
    fun deleteComment() {
        val initialNote = Note(0, "Заметка", "Текст к заметке", 5, "https://www.example.com", false)
        val newNote = WallService.add(initialNote)

        val initialComment = Comment(0, 111, "Новый комментарий", 222, 12, false, noteId = 2)
        val addedComment = WallService.createComment_toNote(newNote.id, initialComment)

        val deletedComment = WallService.deleteComment(addedComment.id)
        assertTrue(deletedComment)

    }

    @Test(expected = CommentNotFoundException::class)
    fun deleteComment_should_throw_CommentNotFoundException_for_nonExistent_comment() {
        val nonExistentCommentId = 1000
        WallService.deleteComment(commentId = nonExistentCommentId)
    }

    @Test(expected = CommentNotFoundException::class)
    fun deleteComment_should_throw_CommentNotFoundException_when_note_is_deleted() {
        val initialNote = Note(0, "Заметка", "Текст к заметке", 5, "https://www.example.com", false)
        val newNote = WallService.add(initialNote)

        val initialComment = Comment(0, 111, "Новый комментарий", 222, 12, false, noteId = 2)
        val addedComment = WallService.createComment_toNote(newNote.id, initialComment)

        WallService.delete(noteId = newNote.id)

        WallService.deleteComment(commentId = addedComment.id)
    }

    @Test
    fun editNote() {
        val initialNote = Note(0, "Заметка", "Текст к заметке", 5, "https://www.example.com", false)
        val newNote = WallService.add(initialNote)

        val updateNote = newNote.copy(title = "Отредактированная заметка", text = "Поправки к заметке", comments = 8)
        val result = WallService.edit(updateNote)

        assertTrue("Заметка успешно отредактирована", result)
    }

    @Test(expected = NoteNotFoundException::class)
    fun editNote_should_throw_NoteNotFoundException() {
        val initialNote = Note(0, "Заметка", "Текст к заметке", 5, "https://www.example.com", false)
        WallService.edit(initialNote)
    }

    @Test
    fun editComment() {
        val initialNote = Note(0, "Заметка", "Текст к заметке", 5, "https://www.example.com", false)
        val newNote = WallService.add(initialNote)

        val initialComment = Comment(0, 111, "Новый комментарий", 222, 12, false, noteId = newNote.id)
        val addedComment = WallService.createComment_toNote(newNote.id, initialComment)

        val updateComment = addedComment.copy(text = "Отредактированный комментарий")
        val result = WallService.editComment(updateComment)

        assertTrue("Комментарий к заметке успешно отредактирован", result)
    }

    @Test(expected = CommentNotFoundException::class)
    fun editComment_should_throw_CommentNotFoundException() {
        val initialNote = Note(0, "Заметка", "Текст к заметке", 5, "https://www.example.com", false)
        val initialComment = Comment(0, 111, "Новый комментарий", 222, 12, false, noteId = initialNote.id)
        WallService.editComment(initialComment)
    }

    @Test
    fun getNotes() {
        val initialNote = Note(0, "Заметка", "Текст к заметке", 5, "https://www.example.com", false)
        val initialNote1 = Note(0, "Заметка 1", "Текст к заметке 1", 6, "https://www.example.com", false)
        val initialNote2 = Note(0, "Заметка 2 (удаленная)", "Текст к заметке 2", 6, "https://www.example.com", false)

        val newNote = WallService.add(initialNote)
        val newNote1 = WallService.add(initialNote1)
        val newNote2 = WallService.add(initialNote2)

        val deleteNote = WallService.delete(noteId = newNote2.id)
        newNote2.isDeleted = true
        val notesList = WallService.getNotes()

        assertEquals(2, notesList.size)
    }

    @Test
    fun getNotes_ById() {
        val initialNote = Note(0, "Заметка", "Текст к заметке", 5, "https://www.example.com", false)
        val initialNote1 = Note(0, "Заметка 1", "Текст к заметке 1", 6, "https://www.example.com", false)

        val newNote = WallService.add(initialNote)
        val newNote1 = WallService.add(initialNote1)

        val deleteNote = WallService.delete(noteId = newNote1.id)
        newNote1.isDeleted = true

        val foundNote = WallService.getById(noteId = newNote.id)
        assertEquals(newNote, foundNote)
    }

    @Test(expected = NoteNotFoundException::class)
    fun getNotes_ById_should_throw_NoteNotFoundException() {
        val initialNote = Note(0, "Заметка", "Текст к заметке", 5, "https://www.example.com", false)
        val newNote = WallService.add(initialNote)
        val deleteNote = WallService.delete(noteId = newNote.id)
        newNote.isDeleted = true

        WallService.getById(newNote.id)
    }

    @Test
    fun getComments() {
        val initialNote = Note(0, "Заметка", "Текст к заметке", 5, "https://www.example.com", false)
        val initialNote1 = Note(0, "Заметка 1", "Текст к заметке 1", 6, "https://www.example.com", false)

        val newNote = WallService.add(initialNote)
        val newNote1 = WallService.add(initialNote1)

        val initialComment = Comment(0, 111, "Новый комментарий", 222, 12, false, noteId = newNote.id)
        val initialComment1 = Comment(0, 111, "Новый комментарий", 222, 12, false, noteId = newNote1.id)

        val addedComment = WallService.createComment_toNote(newNote.id, initialComment)
        val addedComment1 = WallService.createComment_toNote(newNote1.id, initialComment1)

        val deleteComment1 = WallService.deleteComment(commentId = addedComment1.id)
        addedComment1.isDeleted = true

        val getComment = WallService.getComments(noteId = addedComment.noteId)
        assertEquals(1, getComment.size)
    }

    @Test(expected = NoteNotFoundException::class)
    fun getComments_should_throw_NoteNotFoundException() {
        val initialNote = Note(0, "Заметка", "Текст к заметке", 5, "https://www.example.com", false)
        val newNote = WallService.add(initialNote)

        val initialComment = Comment(0, 111, "Новый комментарий", 222, 12, false, noteId = newNote.id)
        val addedComment = WallService.createComment_toNote(newNote.id, initialComment)

        val deleteNote = WallService.delete(noteId = newNote.id)
        newNote.isDeleted = true

        WallService.getComments(noteId = addedComment.noteId)
    }

    @Test
    fun restoreComment() {
        val initialNote = Note(0, "Заметка", "Текст к заметке", 5, "https://www.example.com", false)
        val newNote = WallService.add(initialNote)

        val initialComment = Comment(0, 111, "Новый комментарий", 222, 12, false, noteId = newNote.id)
        val addedComment = WallService.createComment_toNote(newNote.id, initialComment)

        val deleteComment = WallService.deleteComment(addedComment.id)
        assertTrue("Комментарий удален", deleteComment)

        val restoredComment = WallService.restoreComment(addedComment.id)
        assertTrue("Комментарий восстановлен", restoredComment)
    }

    @Test(expected = CommentNotFoundException::class)
    fun restoreComment_should_throw_CommentNotFoundException_if_comment_not_found() {
        WallService.restoreComment(54)
    }

    @Test(expected = CommentNotFoundException::class)
    fun restoreComment_should_throw_CommentNotFoundException_if_comment_not_deleted() {
        val initialNote = Note(0, "Заметка", "Текст к заметке", 5, "https://www.example.com", false)
        val newNote = WallService.add(initialNote)

        val initialComment = Comment(0, 111, "Новый комментарий", 222, 12, false, noteId = newNote.id)
        val addedComment = WallService.createComment_toNote(newNote.id, initialComment)

        WallService.restoreComment(addedComment.id)
    }

    @Test(expected = NoteNotFoundException::class)
    fun restoreComment_should_throw_CommentNotFoundException_if_parent_note_is_deleted() {
        val initialNote = Note(0, "Заметка", "Текст к заметке", 5, "https://www.example.com", false)
        val newNote = WallService.add(initialNote)

        val initialComment = Comment(0, 111, "Новый комментарий", 222, 12, false, noteId = newNote.id)
        val addedComment = WallService.createComment_toNote(newNote.id, initialComment)

        WallService.deleteComment(addedComment.id)
        WallService.delete(newNote.id)
        WallService.restoreComment(addedComment.id)
    }
}