package com.viafourasample.src.fragments.home;

import com.viafourasample.src.managers.StoryManager;
import com.viafourasample.src.model.Story;

import java.util.ArrayList;
import java.util.List;

public class HomeFragmentViewModel {
    public List<Story> storyList = new ArrayList<>();

    public HomeFragmentViewModel() {
        storyList = StoryManager.getInstance().getStoryList();
    }
}
