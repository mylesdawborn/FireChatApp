package com.miliemakes.firechatfirebaseapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class MainChatActivity extends AppCompatActivity {

    private String displayName;
    private ListView chatListView;
    private EditText inputText;
    private ImageButton sendButton;
    private DatabaseReference databaseReference;
    private ChatListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_chat);

        setUpDisplayName();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        // Link the Views in the layout to the Java code
        inputText = findViewById(R.id.messageInput);
        sendButton = findViewById(R.id.sendButton);
        chatListView = findViewById(R.id.chat_list_view);

        inputText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                sendMessage();
                return true;
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });
    }

    private void setUpDisplayName () {
        SharedPreferences prefs = getSharedPreferences(RegisterActivity.CHAT_PREFS, MODE_PRIVATE);
        displayName = prefs.getString(RegisterActivity.DISPLAY_NAME_KEY, null);
        if(displayName == null) displayName = "Anonymous";
    }

    private void sendMessage() {

        Log.d("HYUChat", "I sent a message");
        String input = inputText.getText().toString();
        if (!input.equals("")) {
            InstantMessage chat = new InstantMessage(input, displayName);
            databaseReference.child("messages").push().setValue(chat);
            inputText.setText("");
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter = new ChatListAdapter(this, databaseReference, displayName);
        chatListView.setAdapter(adapter);
    }


    @Override
    public void onStop() {
        super.onStop();

        adapter.cleanup();
    }

}
