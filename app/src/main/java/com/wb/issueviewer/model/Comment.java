package com.wb.issueviewer.model;


public class Comment {
    private String mUsername;
    private String mBody;

    public Comment(String body) {
        mBody = body;
    }

    public void setUsername(String username) {
        mUsername = username;
    }

    public String getUsername() {
        return mUsername;
    }

    public String getBody() {
        return mBody;
    }
}
