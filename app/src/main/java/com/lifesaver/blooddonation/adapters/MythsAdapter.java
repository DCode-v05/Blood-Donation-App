package com.lifesaver.blooddonation.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lifesaver.blooddonation.R;
import com.lifesaver.blooddonation.constants.BloodGroups;

import java.util.List;

public class MythsAdapter extends RecyclerView.Adapter<MythsAdapter.VH> {

    private final List<BloodGroups.MythFact> myths;

    public MythsAdapter(List<BloodGroups.MythFact> myths) { this.myths = myths; }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_myth, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        BloodGroups.MythFact mf = myths.get(position);
        h.myth.setText(mf.myth);
        h.fact.setText(mf.fact);
    }

    @Override public int getItemCount() { return myths.size(); }

    static class VH extends RecyclerView.ViewHolder {
        final TextView myth, fact;
        VH(@NonNull View v) {
            super(v);
            myth = v.findViewById(R.id.text_myth);
            fact = v.findViewById(R.id.text_fact);
        }
    }
}
