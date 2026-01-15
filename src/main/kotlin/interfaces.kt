interface Attachment {
    val type: String
}

data class PhotoAttachment (
    override val type: String = "photo",
    val photo: Photo
): Attachment

data class Photo (val albumId: Int, val text: String, val userId: Int, val width: Int, val height: Int)

data class VideoAttachment (
    override val type: String = "video",
    val video: Video
): Attachment

data class Video (val title: String, val description: String, val duration: Int, val comments: Int, val platform: String)

data class AudioAttachment (
    override val type: String = "audio",
    val audio: Audio
): Attachment

data class Audio (val artist: String, val title: String, val genreId: Int, val date: Int, val albumId: Int)

data class DocumentAttachment (
    override val type: String = "document",
    val document: Document
): Attachment

data class Document (val id: Int, val title: String, val size: Int, val ext: String, val url: String)

data class NoteAttachment (
    override val type: String = "note",
    val note: Note
): Attachment

data class Note (val id: Int, val title: String, val text: String, val comments: Int, val viewUrl: String)