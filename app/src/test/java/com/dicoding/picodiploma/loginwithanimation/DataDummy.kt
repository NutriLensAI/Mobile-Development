package com.dicoding.picodiploma.loginwithanimation

import com.dicoding.picodiploma.loginwithanimation.data.reponse.ListStoryItem

object DataDummy {
    fun generateDummyStoriesResponse(): List<ListStoryItem> {
        val items: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0 until 100) {
            val quote = ListStoryItem(
                id = i.toString(),
                name = "author $i",
                description = "quote $i",
                photoUrl = "https://dummyimage.com/600x400/000/fff&text=Photo+$i",
                createdAt = "2023-05-20T12:00:00Z",
                lat = -6.200000 + (i * 0.01),
                lon = 106.816666 + (i * 0.01)
            )
            items.add(quote)
        }
        return items
    }
}