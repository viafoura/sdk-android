package com.viafourasample.src.model;

public class Story {
    private String pictureUrl;
    private String title;
    private String description;
    private String author;
    private String category;
    private String link;
    private String containerId;
    private StoryType storyType;


    public enum StoryType {
        comments, reviews
    }

    public Story(String pictureUrl, String title, String description, String author, String category, String link, String containerId, StoryType storyType) {
        this.pictureUrl = pictureUrl;
        this.title = title;
        this.description = description;
        this.author = author;
        this.category = category;
        this.link = link;
        this.containerId = containerId;
        this.storyType = storyType;
    }

    public StoryType getStoryType() {
        return storyType;
    }

    public void setStoryType(StoryType storyType) {
        this.storyType = storyType;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getAuthor() {
        return author;
    }

    public String getCategory() {
        return category;
    }

    public String getLink() {
        return link;
    }

    public String getContainerId() {
        return containerId;
    }

    public static Story randomStory(String containerId){
        return new Story("https://www.datocms-assets.com/55856/1636753460-information-overload.jpg?crop=focalpoint&fit=crop&fm=webp&fp-x=0.86&fp-y=0.47&h=428&w=856", "Moving Staff to Cover the Coronavirus", "Here Are What Media Companies Are Doing to Deal With COVID-19 Information Overload", "Norman Phillips", "ECONOMY", "https://viafoura-mobile-demo.vercel.app/posts/here-are-what-media-companies-are-doing-with-covid-19-overload", containerId, Story.StoryType.comments);
    }
}
