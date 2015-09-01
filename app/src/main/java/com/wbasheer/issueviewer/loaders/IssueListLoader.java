package com.wbasheer.issueviewer.loaders;

import android.content.AsyncTaskLoader;
import android.content.Context;

import com.wbasheer.issueviewer.model.Issue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class IssueListLoader extends AsyncTaskLoader<List<Issue>> {

    private List<Issue> mIssueList;
    private Map<String, String> mParameters;

    private String GITHUB_API_BASE_URL = "https://api.github.com/";
    private String GITHUB_API_RAILS_ISSUES_URL = "repos/rails/rails/issues";
    public static final String DEFAULT_CHARSET = java.nio.charset.StandardCharsets.UTF_8.name();

    // Parameters
    public static final String PARAM_STATE = "state";
    public static final String PARAM_SORT = "sort";
    public static final String PARAM_DIRECTION = "direction";
    public static final String PARAM_PAGE = "page";
    public static final String PARAM_PER_PAGE = "per_page";


    // members for JSON parsing of Issues
    private static final String ISSUE_ID = "id";
    private static final String ISSUE_TITLE = "title";
    private static final String ISSUE_BODY = "body";
    private static final String ISSUE_NUMBER = "number";

    public IssueListLoader(Context context) {
        super(context);

        mParameters = new HashMap<>();
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
    public List<Issue> loadInBackground() {

        // Retrieve issues
        String baseRequestUrl = GITHUB_API_BASE_URL + GITHUB_API_RAILS_ISSUES_URL;
        String p = createParameters(mParameters);
        if(p != null && !p.isEmpty())
            baseRequestUrl += "?" + p;

        try {
            URL requestUrl = new URL(baseRequestUrl);
            String response = get(requestUrl);
            if(response != null) {
                mIssueList = parseIssuesJson(response);
                return mIssueList;
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
    public void deliverResult(List<Issue> issues) {
        if (isReset()) {
            // An async query came in while the loader is stopped.  We
            // don't need the result.
            if (issues != null) {
                onReleaseResources(issues);
            }
        }
        List<Issue> oldIssues = mIssueList;
        mIssueList = issues;

        if (isStarted()) {
            // If the Loader is currently started, we can immediately
            // deliver its results.
            super.deliverResult(issues);
        }

        // At this point we can release the resources associated with
        // 'oldIssues' if needed; now that the new result is delivered we
        // know that it is no longer in use.
        if (oldIssues != null) {
            onReleaseResources(oldIssues);
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

    public void addParameter(String key, String value) {
        if(key != null) {
            mParameters.put(key, value);
        }
    }

    public void clearParameters() {
        if(mParameters != null) {
            mParameters.clear();
        }
    }

    private String createParameters(Map<String, String> parameters) {

        String params = "";
        if(parameters != null && parameters.size() > 0) {
            try {
                Iterator it = parameters.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry pair = (Map.Entry)it.next();
                    System.out.println(pair.getKey() + " = " + pair.getValue());
                    params += pair.getKey() + "=" + URLEncoder.encode((String)pair.getValue(), DEFAULT_CHARSET);

                    if(it.hasNext())
                        params += "&";

                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        return params;
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
                e.printStackTrace(); //If you want further info on failure...
            }
        }

        return response;
    }

    public static List<Issue> parseIssuesJson(String json) {
        if(json == null)
            return null;

        List<Issue> issues = null;
        try {
            JSONArray issuesArray = new JSONArray(json);

            issues = new ArrayList<>(issuesArray.length());

            for(int i = 0; i < issuesArray.length(); i++) {

                JSONObject issueJson = issuesArray.getJSONObject(i);
                String title = (String)issueJson.get(ISSUE_TITLE);
                String body = (String)issueJson.get(ISSUE_BODY);
                Object o = issueJson.get(ISSUE_NUMBER);
                long number = Long.valueOf(o.toString());

                Issue issue = new Issue(title, body);
                issue.setNumber(number);
                issues.add(issue);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return issues;
    }

    /**
     * Helper function to take care of releasing resources associated
     * with an actively loaded data set.
     */
    protected void onReleaseResources(List<Issue> issues) {
        // For a simple List<> there is nothing to do.  For something
        // like a Cursor, we would close it here.
    }
}
