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
        val initialPost = Post(text = "Stories", likes = Likes(100),views = Views(32))
        val addedPost = WallService.add(initialPost)

        assertNotEquals(0, addedPost.id)
    }

    @Test
    fun updateExisting_returnsTrue() {
    val addedPost = WallService.add(Post(ownerId = 10, text = "Изначальный пост",likes = Likes(30),views = Views(5)))

        val postToUpdate = Post(
            id = addedPost.id,
            ownerId = 10,
            text = "Обновленный текст поста",
            likes = Likes(70),
            views = Views (20)

        )
        val result = WallService.update(postToUpdate)

        assertTrue(result)
    }

    @Test
    fun updateNotExisting_returnsFalse() {
        val service = WallService
        service.add(Post(ownerId = 10, text = "Добавленный пост",likes = Likes(30),views = Views(5)))

        val update = Post(
            id = 5,
            ownerId = 10,
            text = "Добавленный пост",
            likes = Likes(30),
            views = Views (5)

        )
        val result = service.update(update)

        assertFalse(result)
    }

}

