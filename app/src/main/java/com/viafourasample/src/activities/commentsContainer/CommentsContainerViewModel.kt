package com.viafourasample.src.activities.commentsContainer

import com.viafourasample.src.managers.StoryManager
import com.viafourasample.src.model.Story

class CommentsContainerViewModel(containerId: String) {
    val story: Story = StoryManager.getInstance().getStory(containerId)
}
