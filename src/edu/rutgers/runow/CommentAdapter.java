package edu.rutgers.runow;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class CommentAdapter extends ArrayAdapter<Comment> {
	private final Context context;
	private final Comment[] comments;
	
	public CommentAdapter(Context context, Comment[] comments) {
		super(context,R.layout.list_comment,comments);
		this.context = context;
		this.comments = comments;
	}
	
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View commentView = inflater.inflate(R.layout.list_comment, parent, false);
		TextView user = (TextView) commentView.findViewById(R.id.listCommentUser);
		TextView content = (TextView) commentView.findViewById(R.id.listCommentContent);
		//TODO move comments from listview to linearlayout
		Comment temp = comments[position];
		
		user.setText(temp.user);
		content.setText(temp.content);
		
		return commentView;
	}
	
	public Comment getComment(int position) {
		return comments[position];
	}
	
}
