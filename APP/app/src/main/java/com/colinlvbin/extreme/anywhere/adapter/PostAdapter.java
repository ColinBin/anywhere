package com.colinlvbin.extreme.anywhere.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.colinlvbin.extreme.anywhere.R;
import com.colinlvbin.extreme.anywhere.model.Post;

import java.util.List;

/**
 * Created by Colin on 2016/6/11.
 */
public class PostAdapter extends ArrayAdapter<Post>{
    private int resourceId;
    public PostAdapter(Context context,int textViewResourceId,
                       List<Post> objects){
        super(context,textViewResourceId,objects);
        resourceId=textViewResourceId;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Post currentPost=getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId, null);
        ((TextView)view.findViewById(R.id.post_title_post_item)).setText(currentPost.getTitle());
        ((TextView)view.findViewById(R.id.post_creator_post_item)).setText(currentPost.getUser_id());
        ((TextView)view.findViewById(R.id.post_style_post_item)).setText(String.valueOf(currentPost
                .getStyle()));
        ((TextView)view.findViewById(R.id.post_comment_number_post_item)).setText(String
                .valueOf(currentPost
                        .getComment_number()));
        ((TextView)view.findViewById(R.id.post_like_number_post_item)).setText(String.valueOf(currentPost
                .getLike_number()));
        ImageView hasCipherImage=(ImageView)view.findViewById(R.id.has_cipher_image_post_item);
        if(currentPost.getHas_cipher()==1) {
            hasCipherImage.setImageResource(R.drawable.has_cipher);
        }else{
            hasCipherImage.setImageResource(R.drawable.not_has_cipher);
        }

        return view;

    }

}
