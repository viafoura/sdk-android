package com.viafourasample.src.model;

public class Story {
    private String pictureUrl;
    private String title;
    private String description;
    private String author;
    private String category;
    private String link;
    private String containerId;

    public Story(String pictureUrl, String title, String description, String author, String category, String link, String containerId) {
        this.pictureUrl = pictureUrl;
        this.title = title;
        this.description = description;
        this.author = author;
        this.category = category;
        this.link = link;
        this.containerId = containerId;
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
}
