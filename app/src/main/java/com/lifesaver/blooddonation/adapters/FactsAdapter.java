package com.lifesaver.blooddonation.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lifesaver.blooddonation.R;

import java.util.Arrays;
import java.util.List;

public class FactsAdapter extends RecyclerView.Adapter<FactsAdapter.VH> {

    private final List<String> facts;

    public FactsAdapter(String[] facts) { this.facts = Arrays.asList(facts); }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_fact, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        h.text.setText(facts.get(position));
    }

    @Override public int getItemCount() { return facts.size(); }

    static class VH extends RecyclerView.ViewHolder {
        final TextView text;
        VH(@NonNull View v) {
            super(v);
            text = v.findViewById(R.id.text_fact);
        }
    }
}
