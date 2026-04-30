package com.lifesaver.blooddonation.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.lifesaver.blooddonation.R;
import com.lifesaver.blooddonation.adapters.ChatAdapter;
import com.lifesaver.blooddonation.ai.GeminiClient;

public class ChatBotActivity extends AppCompatActivity {

    private RecyclerView recycler;
    private ChatAdapter  adapter;
    private EditText     editMessage;
    private MaterialButton sendButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatbot);

        MaterialToolbar tb = findViewById(R.id.toolbar);
        tb.setNavigationOnClickListener(v -> finish());

        recycler    = findViewById(R.id.recycler_chat);
        editMessage = findViewById(R.id.edit_message);
        sendButton  = findViewById(R.id.button_send);

        adapter = new ChatAdapter();
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(adapter);

        adapter.add(new ChatAdapter.Message(
                "Hello! I'm LifeSaver AI. Ask me anything about blood donation, "
                + "blood types, eligibility, or this app's features.", false));

        sendButton.setOnClickListener(v -> send());
    }

    private void send() {
        String msg = editMessage.getText() == null ? "" : editMessage.getText().toString().trim();
        if (TextUtils.isEmpty(msg)) return;
        adapter.add(new ChatAdapter.Message(msg, true));
        editMessage.setText("");
        recycler.scrollToPosition(adapter.getItemCount() - 1);

        sendButton.setEnabled(false);
        GeminiClient.get().ask(msg, new GeminiClient.ChatCallback() {
            @Override public void onResponse(String text) {
                runOnUiThread(() -> {
                    adapter.add(new ChatAdapter.Message(text, false));
                    recycler.scrollToPosition(adapter.getItemCount() - 1);
                    sendButton.setEnabled(true);
                });
            }
            @Override public void onError(String message) {
                runOnUiThread(() -> {
                    adapter.add(new ChatAdapter.Message(
                            "Sorry, I couldn't reach the AI right now. ("
                            + message + ")", false));
                    sendButton.setEnabled(true);
                });
            }
        });
    }
}
