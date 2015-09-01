package com.wb.issueviewer;

import android.app.DialogFragment;
import android.app.LoaderManager;
import android.content.Loader;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.wb.issueviewer.adapters.CommentListAdapter;
import com.wb.issueviewer.loaders.CommentListLoader;
import com.wb.issueviewer.model.Comment;

import java.util.ArrayList;
import java.util.List;

public class CommentsDialogFragment extends DialogFragment implements LoaderManager.LoaderCallbacks<List<Comment>> {

    private List<Comment> mComments;

    private ListView mListView;
    private ArrayAdapter mAdapter;
    private long mIssueId;

    /**
     * Create a new instance of CommentsDialogFragment, providing "num"
     * as an argument.
     */
    static CommentsDialogFragment newInstance(long issueId) {
        CommentsDialogFragment f = new CommentsDialogFragment();
        f.setIssue(issueId);

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mComments = new ArrayList<>();

    }

    private void setIssue(long issueId) {
        mIssueId = issueId;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.comments_layout, null, false);
        mListView = (ListView) v.findViewById(R.id.listview_comments);

        mAdapter = new CommentListAdapter(getActivity(), R.layout.issue_row_layout, mComments);
        mListView.setAdapter(mAdapter);

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        // Prepare the loader.  Either re-connect with an existing one,
        // or start a new one.  Only start if current List of comments is null/empty
        if(mComments == null || mComments.isEmpty())
            getLoaderManager().initLoader(0, null, this);

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public Loader<List<Comment>> onCreateLoader(int i, Bundle bundle) {
        Loader<List<Comment>> loader = new CommentListLoader(getActivity(), mIssueId);
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<List<Comment>> listLoader, List<Comment> commentsList) {
        mComments.addAll(commentsList);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<List<Comment>> listLoader) {

    }
}