package com.wbasheer.issueviewer.adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.wbasheer.issueviewer.R;
import com.wbasheer.issueviewer.model.Comment;
import com.wbasheer.issueviewer.model.Issue;

import java.util.List;

public class CommentListAdapter extends ArrayAdapter<Comment> {

    private List<Comment> mComments;
    private Context mContext;

    public CommentListAdapter(Context context, int resource, List<Comment> comments) {
        super(context, resource);
        mContext = context;

        mComments = comments;
    }

    @Override
    public int getCount() {
        return mComments.size();
    }

    @Override
    public Comment getItem(int position) {
        return mComments.get(position);
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

            viewHolder.usernameTextView = (TextView) convertView.findViewById(R.id.txtview_issue_title);
            viewHolder.bodyTextView = (TextView) convertView.findViewById(R.id.txtview_issue_body);
            convertView.setTag(viewHolder);
        }

        Comment comment = getItem(position);
        ViewHolder holder = (ViewHolder) convertView.getTag();
        holder.usernameTextView.setText(comment.getUsername());
        holder.bodyTextView.setText(comment.getBody());

        return convertView;
    }

    private class ViewHolder {
        public TextView usernameTextView;
        public TextView bodyTextView;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }


}
