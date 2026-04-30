package com.lifesaver.blooddonation.adapters;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.lifesaver.blooddonation.R;

import java.util.ArrayList;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.VH> {

    public static class Message {
        public final String text;
        public final boolean fromUser;
        public Message(String text, boolean fromUser) {
            this.text = text;
            this.fromUser = fromUser;
        }
    }

    private final List<Message> messages = new ArrayList<>();

    public void add(Message m) {
        messages.add(m);
        notifyItemInserted(messages.size() - 1);
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chat_message, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        Message m = messages.get(position);
        h.text.setText(m.text);

        LinearLayout root = (LinearLayout) h.itemView;
        if (m.fromUser) {
            root.setGravity(Gravity.END);
            h.text.setTextColor(ContextCompat.getColor(h.itemView.getContext(), R.color.white));
            h.text.setBackgroundResource(R.drawable.bg_priority_normal);
            h.text.setBackgroundTintList(ContextCompat.getColorStateList(
                    h.itemView.getContext(), R.color.primary));
        } else {
            root.setGravity(Gravity.START);
            h.text.setTextColor(ContextCompat.getColor(h.itemView.getContext(), R.color.text_primary));
            h.text.setBackgroundResource(R.drawable.bg_card_white);
            h.text.setBackgroundTintList(null);
        }
    }

    @Override public int getItemCount() { return messages.size(); }

    static class VH extends RecyclerView.ViewHolder {
        final TextView text;
        VH(@NonNull View v) {
            super(v);
            text = v.findViewById(R.id.text_message);
        }
    }
}
