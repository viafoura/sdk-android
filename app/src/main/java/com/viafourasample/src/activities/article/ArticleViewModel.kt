package com.viafourasample.src.activities.article

import com.viafourasample.src.managers.StoryManager
import com.viafourasample.src.model.Story

class ArticleViewModel(containerId: String) {
    val story: Story = StoryManager.getInstance().getStory(containerId)
}
