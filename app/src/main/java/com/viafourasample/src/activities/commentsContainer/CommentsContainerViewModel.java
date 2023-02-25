package com.viafourasample.src.activities.commentsContainer;

import com.viafourasample.src.managers.StoryManager;
import com.viafourasample.src.model.Story;

public class CommentsContainerViewModel {
    private Story story;

    public CommentsContainerViewModel(String containerId) {
        this.story = StoryManager.getInstance().getStory(containerId);
    }
    public Story getStory() {
        return story;
    }
}
