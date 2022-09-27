package com.viafourasample.src.activities.article;

import com.viafourasample.src.model.Story;

public class ArticleViewModel {
    private Story story;

    public ArticleViewModel(Story story) {
        this.story = story;
    }

    public Story getStory() {
        return story;
    }
}
