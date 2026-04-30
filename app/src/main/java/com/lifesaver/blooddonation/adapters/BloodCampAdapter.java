package com.lifesaver.blooddonation.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lifesaver.blooddonation.R;
import com.lifesaver.blooddonation.models.BloodCamp;
import com.lifesaver.blooddonation.utils.DateUtils;
import com.lifesaver.blooddonation.utils.DistanceUtils;

import java.util.ArrayList;
import java.util.List;

public class BloodCampAdapter extends RecyclerView.Adapter<BloodCampAdapter.VH> {

    public interface OnCampClick { void onClick(BloodCamp camp); }

    private final List<BloodCamp> items = new ArrayList<>();
    private final OnCampClick listener;

    public BloodCampAdapter(OnCampClick listener) { this.listener = listener; }

    public void submit(List<BloodCamp> data) {
        items.clear();
        if (data != null) items.addAll(data);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_blood_camp, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        BloodCamp c = items.get(position);
        h.name.setText(c.getName());
        h.organizer.setText("By " + (c.getOrganizer() == null ? "—" : c.getOrganizer()));
        h.address.setText(c.getAddress());

        String date = DateUtils.formatIso(c.getDate());
        if (c.getStartTime() != null) date += "  •  " + c.getStartTime();
        if (c.getEndTime()   != null) date += " — " + c.getEndTime();
        h.date.setText(date);

        if (c.getDistanceKm() >= 0) {
            h.distance.setVisibility(View.VISIBLE);
            h.distance.setText(DistanceUtils.format(c.getDistanceKm()));
        } else {
            h.distance.setVisibility(View.GONE);
        }

        h.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onClick(c);
        });
    }

    @Override public int getItemCount() { return items.size(); }

    static class VH extends RecyclerView.ViewHolder {
        final TextView name, organizer, date, address, distance;
        VH(@NonNull View v) {
            super(v);
            name      = v.findViewById(R.id.text_name);
            organizer = v.findViewById(R.id.text_organizer);
            date      = v.findViewById(R.id.text_date);
            address   = v.findViewById(R.id.text_address);
            distance  = v.findViewById(R.id.text_distance);
        }
    }
}
