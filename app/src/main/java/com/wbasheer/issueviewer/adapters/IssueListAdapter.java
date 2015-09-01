package com.wbasheer.issueviewer.adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.wbasheer.issueviewer.R;
import com.wbasheer.issueviewer.model.Issue;

import java.util.List;

public class IssueListAdapter extends ArrayAdapter<Issue> {

    private List<Issue> mIssues;
    private Context mContext;

    private final int MAX_BODY_LENGTH = 140;

    public IssueListAdapter(Context context, int resource, List<Issue> issues) {
        super(context, resource);
        mContext = context;

        mIssues = issues;
    }

    @Override
    public int getCount() {
        return mIssues.size();
    }

    @Override
    public Issue getItem(int position) {
        return mIssues.get(position);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if(convertView==null) {

            LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.issue_row_layout, parent, false);

            ViewHolder viewHolder = new ViewHolder();

            viewHolder.titleTextView = (TextView) convertView.findViewById(R.id.txtview_issue_title);
            viewHolder.bodyTextView = (TextView) convertView.findViewById(R.id.txtview_issue_body);
            convertView.setTag(viewHolder);
        }

        Issue issue = getItem(position);
        ViewHolder holder = (ViewHolder) convertView.getTag();
        holder.titleTextView.setText(issue.getTitle());
        String body = issue.getBody();
        if(body != null && body.length() > MAX_BODY_LENGTH) {
            body = body.substring(0, MAX_BODY_LENGTH-1) + "...";
        }
        holder.bodyTextView.setText(body);

        return convertView;
    }

    private class ViewHolder {
        public TextView titleTextView;
        public TextView bodyTextView;
    }
}
