package com.miliemakes.firechatfirebaseapp;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

public class ChatListAdapter extends BaseAdapter{

    private Activity activityID;
    private DatabaseReference databaseReference;
    private String displayName;
    private ArrayList<DataSnapshot> snapshotList;

    private ChildEventListener listenerID = new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            snapshotList.add(dataSnapshot);
            notifyDataSetChanged();
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    //connects to database to retrieve messages and the user who sent them --- continued code to end (appearance included)
    public ChatListAdapter(Activity activity, DatabaseReference ref, String name) {
        activityID = activity;
        displayName = name;
        databaseReference = ref.child("messages");
        databaseReference.addChildEventListener(listenerID);
        snapshotList = new ArrayList<>();
    }

    static class ViewHolder {
        TextView authorName;
        TextView body;
        LinearLayout.LayoutParams params;
    }

    @Override
    public int getCount() {

        return snapshotList.size();
    }

    @Override
    public InstantMessage getItem(int position) {
        DataSnapshot snapshot = snapshotList.get(position);
        return snapshot.getValue(InstantMessage.class);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) activityID.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.chat_msg_row, parent, false);

            final ViewHolder holder = new ViewHolder();
            holder.authorName = convertView.findViewById(R.id.author);
            holder.body = convertView.findViewById(R.id.message);
            holder.params = (LinearLayout.LayoutParams) holder.authorName.getLayoutParams();
            convertView.setTag(holder);
        }
        final InstantMessage message = getItem(position);
        final ViewHolder holder = (ViewHolder) convertView.getTag();

        boolean isMe = message.getAuthor().equals(displayName);
        setChatRowAppearance(isMe, holder);

        String author = message.getAuthor();
        holder.authorName.setText(author);

        String msg = message.getMessage();
        holder.body.setText(msg);

        return convertView;
    }

    private void setChatRowAppearance(boolean isItMe, ViewHolder holder) {
        if (isItMe) {
            holder.params.gravity = Gravity.END;
            holder.authorName.setTextColor(Color.YELLOW);
            holder.body.setBackgroundResource(R.drawable.bubble2);
        } else {
            holder.params.gravity = Gravity.START;
            holder.authorName.setTextColor(Color.BLUE);
            holder.body.setBackgroundResource(R.drawable.bubble1);
        }
        holder.authorName.setLayoutParams(holder.params);
        holder.body.setLayoutParams(holder.params);
    }

    public void cleanup() {
        databaseReference.removeEventListener(listenerID);
    }
}
