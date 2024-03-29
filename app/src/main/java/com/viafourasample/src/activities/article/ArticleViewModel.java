package com.viafourasample.src.activities.article;

import com.viafourasample.src.managers.StoryManager;
import com.viafourasample.src.model.Story;

public class ArticleViewModel {
    private Story story;

    public ArticleViewModel(String containerId) {
        this.story = StoryManager.getInstance().getStory(containerId);
    }

    public Story getStory() {
        return story;
    }
}
