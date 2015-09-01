package com.wb.issueviewer.loaders;


import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import com.wb.issueviewer.model.Comment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class CommentListLoader extends AsyncTaskLoader<List<Comment>> {

    private static final String TAG = CommentListLoader.class.getSimpleName();

    private List<Comment> mCommentList;
    private long mIssueId;

    private String GITHUB_API_BASE_URL = "https://api.github.com/";
    private String GITHUB_API_RAILS_COMMENTS_URL = "repos/rails/rails/issues/%d/comments";
    private final String DEFAULT_CHARSET = java.nio.charset.StandardCharsets.UTF_8.name();

    // members for JSON parsing of comments
    private static final String COMMENT_ID = "id";
    private static final String COMMENT_USER = "user";
    private static final String COMMENT_USERNAME = "login";
    private static final String COMMENT_USER_ID = "id";
    private static final String COMMENT_BODY = "body";

    public CommentListLoader(Context context, long issueId) {
        super(context);

        mIssueId = issueId;
    }

    @Override
    protected void onStartLoading() {


        // When the observer detects a change, it should call onContentChanged()
        // on the Loader, which will cause the next call to takeContentChanged()
        // to return true. If this is ever the case (or if the current data is
        // null), we force a new load.
        forceLoad();

    }


    @Override
    public List<Comment> loadInBackground() {

        Log.i(TAG, "Loading comments with issue ID: " + mIssueId);
        // Retrieve issues
        String baseRequestUrl = GITHUB_API_BASE_URL + String.format(GITHUB_API_RAILS_COMMENTS_URL, mIssueId);

        try {
            URL requestUrl = new URL(baseRequestUrl);
            String response = get(requestUrl);
            if(response != null) {
                mCommentList = parseCommentsJson(response);
                return mCommentList;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Called when there is new data to deliver to the client.  The
     * super class will take care of delivering it; the implementation
     * here just adds a little more logic.
     */
    @Override
    public void deliverResult(List<Comment> comments) {
        if (isReset()) {
            // An async query came in while the loader is stopped.  We
            // don't need the result.
            if (comments != null) {
                onReleaseResources(comments);
            }
        }
        List<Comment> oldComments = mCommentList;
        mCommentList = comments;

        if (isStarted()) {
            // If the Loader is currently started, we can immediately
            // deliver its results.
            super.deliverResult(comments);
        }

        // At this point we can release the resources associated with
        // 'oldIssues' if needed; now that the new result is delivered we
        // know that it is no longer in use.
        if (oldComments != null) {
            onReleaseResources(oldComments);
        }
    }

    @Override
    protected void onStopLoading() {
        // The Loader is in a stopped state, so we should attempt to cancel the
        // current load (if there is one).
        cancelLoad();

        // Note that we leave the observer as is. Loaders in a stopped state
        // should still monitor the data source for changes so that the Loader
        // will know to force a new load if it is ever started again.
    }

    public static String get(URL url) {

        String response = null;
        HttpURLConnection urlConnection = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestProperty("Accept", "application/vnd.github.v3+json");
            urlConnection.connect();
            int status = urlConnection.getResponseCode();

            switch (status) {
                case 200:
                case 201:
                    BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line);
                    }
                    br.close();

                    return sb.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                urlConnection.disconnect();
            } catch (Exception e) {
                e.printStackTrace(); // Further info on failure...
            }
        }

        return response;
    }

    public static List<Comment> parseCommentsJson(String json) {
        if(json == null)
            return null;

        List<Comment> comments = null;
        try {
            JSONArray commentsArray = new JSONArray(json);

            comments = new ArrayList<>(commentsArray.length());

            for(int i = 0; i < commentsArray.length(); i++) {

                JSONObject commentJson = commentsArray.getJSONObject(i);
                JSONObject userJson = commentJson.getJSONObject(COMMENT_USER);
                String username = userJson.getString(COMMENT_USERNAME);
                String body = (String)commentJson.get(COMMENT_BODY);

                Comment comment = new Comment(body);
                comment.setUsername(username);
                comments.add(comment);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return comments;
    }

    /**
     * Helper function to take care of releasing resources associated
     * with an actively loaded data set.
     */
    protected void onReleaseResources(List<Comment> comments) {
        // For a simple List<> there is nothing to do.  For something
        // like a Cursor, we would close it here.
    }
}
