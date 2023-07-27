package com.example.fimae.adapters.MediaSliderAdapter;

public class MediaSliderItem {
    private String url;
    private MediaSliderItemType type;

    public MediaSliderItem(String url, MediaSliderItemType type) {
        this.url = url;
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public MediaSliderItemType getType() {
        return type;
    }

    public void setType(MediaSliderItemType type) {
        this.type = type;
    }
}
