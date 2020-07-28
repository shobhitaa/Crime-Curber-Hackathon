package com.example.crimecurber;

public class uploadinfo {
    public String videoLocation;
    public String videourl;
    public uploadinfo(){}

    public uploadinfo(String name, String url) {
        this.videoLocation = name;
        this.videourl = url;
    }

    public String getVideoLocation() {
        return videoLocation;
    }
    public String getVideourl() {
        return videourl;
    }
}
