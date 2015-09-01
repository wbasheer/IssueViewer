package com.wb.issueviewer;

import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.Loader;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.wb.issueviewer.adapters.IssueListAdapter;
import com.wb.issueviewer.loaders.IssueListLoader;
import com.wb.issueviewer.model.Issue;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ListActivity implements LoaderManager.LoaderCallbacks<List<Issue>> {

    public static final String TAG = MainActivity.class.getSimpleName();
    private Button mFetchDataButton;

    private List<Issue> mIssueList;
    private ArrayAdapter<Issue> mIssueListAdapter;

    // member variables for pagination
    private boolean mIsLoading = false;
    private int mCurrentPage;
    private int mItemsPerPage;
    private int mTotalPages;

    private static final int MAX_RESULTS_PER_REQUEST = 30;
    private static final int ISSUE_LOADER_ID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(mIssueList == null)
            mIssueList = new ArrayList<>();

        // initialize the list adapter
        mIssueListAdapter = new IssueListAdapter(this,
                R.layout.issue_row_layout, mIssueList);

        // assign the list adapter
        setListAdapter(mIssueListAdapter);

        mFetchDataButton = (Button) findViewById(R.id.button_fetch_data);
        mFetchDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // trigger loader to get more data
                loadMoreResults();
            }
        });

        ListView listView = getListView();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                Issue issue = (Issue)adapterView.getItemAtPosition(position);
                showDialog(issue.getNumber());
            }
        });

        mCurrentPage = 1;
        mItemsPerPage = MAX_RESULTS_PER_REQUEST;
        // Prepare the loader.  Either re-connect with an existing one,
        // or start a new one.
        getLoaderManager().initLoader(ISSUE_LOADER_ID, null, this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void loadMoreResults() {
        Loader loader = getLoaderManager().getLoader(ISSUE_LOADER_ID);
        if (loader != null) {
            ((IssueListLoader)loader).addParameter(IssueListLoader.PARAM_PAGE, Integer.toString(mCurrentPage));
            loader.forceLoad();
        }
    }

    @Override
    public Loader onCreateLoader(int id, Bundle bundle) {

        Loader<List<Issue>> loader = new IssueListLoader(this);
        ((IssueListLoader)loader).addParameter(IssueListLoader.PARAM_PAGE, Integer.toString(mCurrentPage));
        ((IssueListLoader)loader).addParameter(IssueListLoader.PARAM_PER_PAGE, Integer.toString(mItemsPerPage));
        ((IssueListLoader)loader).addParameter(IssueListLoader.PARAM_STATE, "open");
        ((IssueListLoader)loader).addParameter(IssueListLoader.PARAM_SORT, "updated");
        ((IssueListLoader)loader).addParameter(IssueListLoader.PARAM_DIRECTION, "desc");
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<List<Issue>> loader, List<Issue> issues) {
        if(issues != null) {
            Log.i(TAG, "Loaded " + issues.size() + " issues");

            mIssueList.addAll(issues);
            mIssueListAdapter.notifyDataSetChanged();

            if(issues.size() >= MAX_RESULTS_PER_REQUEST) {
                mCurrentPage++;
                Log.d(TAG, "Setting current page: " + mCurrentPage);
            }
        }

    }

    @Override
    public void onLoaderReset(Loader loader) {
        mCurrentPage = 1;
        if(loader != null) {
            ((IssueListLoader) loader).addParameter(IssueListLoader.PARAM_PAGE, Integer.toString(mCurrentPage));
            ((IssueListLoader) loader).addParameter(IssueListLoader.PARAM_PER_PAGE, Integer.toString(mItemsPerPage));
            ((IssueListLoader) loader).addParameter(IssueListLoader.PARAM_STATE, "open");
            ((IssueListLoader) loader).addParameter(IssueListLoader.PARAM_SORT, "updated");
            ((IssueListLoader) loader).addParameter(IssueListLoader.PARAM_DIRECTION, "desc");
        }
    }

    private void showDialog(long issueId) {
        // DialogFragment.show() will take care of adding the fragment
        // in a transaction.  We also want to remove any currently showing
        // dialog, so make our own transaction and take care of that here.
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("comment_dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        // Create and show the dialog.
        DialogFragment newFragment = CommentsDialogFragment.newInstance(issueId);
        newFragment.show(ft, "comment_dialog");
    }
}
