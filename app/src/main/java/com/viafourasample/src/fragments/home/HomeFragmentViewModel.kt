package com.viafourasample.src.fragments.home

import com.viafourasample.src.managers.StoryManager
import com.viafourasample.src.model.Story

class HomeFragmentViewModel {
    val storyList: List<Story> = StoryManager.getInstance().storyList
}
