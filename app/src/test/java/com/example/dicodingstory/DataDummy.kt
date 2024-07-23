package com.example.dicodingstory

import com.example.dicodingstory.data.local.entity.StoryEntity

object DataDummy {
    fun generateDummyStories():List<StoryEntity>{
        val stories = ArrayList<StoryEntity>()
        for(i in 0..100){
            val story = StoryEntity(
                id = i.toString(),
                photoUrl ="https://help.dicoding.com/wp-content/uploads/2021/01/dicoding-edit-1.jpg",
                name = "name $i",
                description = "description $i"
            )
            stories.add(story)
        }
        return stories
    }
}