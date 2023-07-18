package com.viafourasample.src.model;

public class LiveChat {
    private String title;
    private int image;
    private String containerId;
    private boolean isVideo;

    public LiveChat(String title, int image, String containerId, boolean isVideo){
        this.title = title;
        this.image = image;
        this.containerId = containerId;
        this.isVideo = isVideo;
    }

    public String getTitle() {
        return title;
    }

    public boolean isVideo() {
        return isVideo;
    }

    public String getContainerId() {
        return containerId;
    }

    public int getImage() {
        return image;
    }
}
