package com.novaagritech.agriclinic.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;


import com.novaagritech.agriclinic.R;
import com.novaagritech.agriclinic.modals.Comments;

import java.util.List;

/**
 * Created by anil on 21/02/18.
 */

public class MessageAdapter extends ArrayAdapter<Comments.CommentDetails> {
    public MessageAdapter(Context context, int resource, List<Comments.CommentDetails> objects) {
        super(context, resource, objects);
    }

    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.item_message, parent, false);
        }


        TextView messageTextView = (TextView) convertView.findViewById(R.id.messageTextView);
        TextView authorTextView = (TextView) convertView.findViewById(R.id.nameTextView);

        Comments.CommentDetails message = getItem(position);
        assert message != null;
        messageTextView.setText(message.getComment());

        authorTextView.setText("Posted by : "+message.getUser_name());

        return convertView;
    }
}
