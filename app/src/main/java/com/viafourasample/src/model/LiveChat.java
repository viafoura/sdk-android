package com.viafourasample.src.model;

public class LiveChat {
    private String title;
    private int image;

    public LiveChat(String title, int image) {
        this.title = title;
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public int getImage() {
        return image;
    }
}
