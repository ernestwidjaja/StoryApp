package com.example.mystoryapp

import com.example.mystoryapp.data.database.Story

object DataDummy {
    fun generateDummyStoryResponse(): List<Story> {
        val items: MutableList<Story> = arrayListOf()
        for (i in 0..100) {
            val story = Story(
                "story + $i",
                "name + $i",
                "description $i",
                "url $i"
            )
            items.add(story)
        }
        return items
    }
}