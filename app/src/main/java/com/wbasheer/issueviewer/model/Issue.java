package com.wbasheer.issueviewer.model;


public class Issue {
    private long mId;
    private long mNumber;
    private String mTitle;
    private String mBody;

    public Issue(String title) {
        mTitle = title;
    }

    public Issue(String title, String body) {
        this(title);
        mBody = body;
    }

    public void setBody(String body) {
        mBody = body;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getBody() {
        return mBody;
    }

    public void setNumber(long issueNumber) {
        mNumber = issueNumber;
    }

    public long getNumber() {
        return mNumber;
    }
}
