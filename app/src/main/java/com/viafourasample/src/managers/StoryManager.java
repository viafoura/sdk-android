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
        storyList.add(new Story("https://www.datocms-assets.com/55856/1636753460-information-overload.jpg?crop=focalpoint&fit=crop&fm=webp&fp-x=0.86&fp-y=0.47&h=428&w=856", "Moving Staff to Cover the Coronavirus", "Here Are What Media Companies Are Doing to Deal With COVID-19 Information Overload", "Norman Phillips", "ECONOMY", "https://viafoura-mobile-demo.vercel.app/posts/here-are-what-media-companies-are-doing-with-covid-19-overload", "72c86bde-e162-11ee-b6d0-e3e7190ad965", Story.StoryType.comments));
        storyList.add(new Story("https://www.datocms-assets.com/55856/1636663477-blognewheights.jpg?fit=crop&fm=webp&h=428&w=856", "Grow civility", "Don't shut out your community, instead guide them towards civility", "Tom Hardington", "ECONOMY", "https://viafoura-mobile-demo.vercel.app/posts/dont-shut-out-your-community-guide-them-to-civility", "101113531", Story.StoryType.comments));
        storyList.add(new Story("https://www.datocms-assets.com/67251/1701970811-tacos.jpg?fit=crop&fm=webp&h=428&w=856", "Korean Fusion Delight", "Homemade Bulgogi Tacos Recipe", "Tom Hardington", "RECIPE", "https://viafoura-mobile-demo.vercel.app/posts/brexit-to-cost-gbp1-200-for-each-person-in-uk", "1231293123", Story.StoryType.reviews));
    }

    public Story getStory(String containerId){
        for(Story story: storyList){
            if(story.getContainerId().equals(containerId)){
                return story;
            }
        }

        return Story.randomStory(containerId);
    }

    public List<Story> getStoryList(){
        return storyList;
    }
}
