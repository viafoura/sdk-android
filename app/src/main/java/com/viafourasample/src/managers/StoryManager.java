package com.viafourasample.src.managers;

import com.viafourasample.src.model.Story;

import java.util.ArrayList;
import java.util.List;

public class StoryManager {
    private static StoryManager instance;

    public List<Story> storyList = new ArrayList<>();

    public static StoryManager getInstance(){
        if (instance == null) {
            instance = new StoryManager();
        }
        return instance;
    }

    public StoryManager() {
        storyList.add(new Story("https://www.datocms-assets.com/55856/1636753460-information-overload.jpg?crop=focalpoint&fit=crop&fm=webp&fp-x=0.86&fp-y=0.47&h=428&w=856", "Moving Staff to Cover the Coronavirus", "Here Are What Media Companies Are Doing to Deal With COVID-19 Information Overload", "Norman Phillips", "ECONOMIA", "https://viafoura-mobile-demo.vercel.app/posts/here-are-what-media-companies-are-doing-with-covid-19-overload", "101113541"));
        storyList.add(new Story("https://www.datocms-assets.com/55856/1636663477-blognewheights.jpg?fit=crop&fm=webp&h=428&w=856", "Grow civility", "Don't shut out your community, instead guide them towards civility", "Tom Hardington", "ECONOMIA", "https://viafoura-mobile-demo.vercel.app/posts/dont-shut-out-your-community-guide-them-to-civility", "101113531"));
        storyList.add(new Story("https://www.datocms-assets.com/55856/1639925068-brexit-to-cost-the-uk.jpg?fit=crop&fm=webp&h=428&w=856", "Brexit cost", "Brexit to cost £1,200 for each person in UK", "Tom Hardington", "ECONOMIA", "https://viafoura-mobile-demo.vercel.app/posts/brexit-to-cost-gbp1-200-for-each-person-in-uk", "101113509"));
    }

    public Story getStory(String containerId){
        for(Story story: storyList){
            if(story.getContainerId().equals(containerId)){
                return story;
            }
        }

        return null;
    }

    public List<Story> getStoryList(){
        return storyList;
    }
}
