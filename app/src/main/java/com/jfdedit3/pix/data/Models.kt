package com.jfdedit3.pix.data

data class Artwork(
    val id: Long,
    val title: String,
    val author: String,
    val tags: List<String>,
    val likes: Int,
    val bookmarked: Boolean = false
)

object DemoData {
    val feed = listOf(
        Artwork(1, "City Lights", "Aster", listOf("original", "night", "city"), 12450, true),
        Artwork(2, "Spring Breeze", "Nami", listOf("illustration", "girl", "flowers"), 9321),
        Artwork(3, "Skyline Echo", "Reon", listOf("landscape", "blue", "clouds"), 5102)
    )

    val ranking = listOf(
        Artwork(10, "Top Daily 1", "Mika", listOf("ranking", "daily"), 55112, true),
        Artwork(11, "Top Daily 2", "Sora", listOf("ranking", "trending"), 49821),
        Artwork(12, "Top Daily 3", "Lune", listOf("ranking", "fantasy"), 47210)
    )

    val bookmarks = feed.filter { it.bookmarked } + ranking.filter { it.bookmarked }
}
