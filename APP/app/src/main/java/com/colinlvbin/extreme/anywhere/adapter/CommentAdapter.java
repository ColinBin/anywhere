package com.colinlvbin.extreme.anywhere.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.colinlvbin.extreme.anywhere.R;
import com.colinlvbin.extreme.anywhere.model.Comment;

import java.util.List;

/**
 * Created by Colin on 2016/6/11.
 */
public class CommentAdapter extends ArrayAdapter<Comment>{
    private int resourceId;
    public CommentAdapter(Context context, int textViewResourceId,
                       List<Comment> objects){
        super(context,textViewResourceId,objects);
        resourceId=textViewResourceId;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Comment currentComment=getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId, null);
        ((TextView)view.findViewById(R.id.comment_creator_comment_item)).setText(currentComment
                .getUser_id());
        ((TextView)view.findViewById(R.id.comment_content_comment_item)).setText(currentComment
                .getContent());
        ((TextView)view.findViewById(R.id.comment_create_time_comment_item)).setText
                (currentComment.getCreate_time());
        return view;

    }

}
