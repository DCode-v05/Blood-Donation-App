package com.lifesaver.blooddonation.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lifesaver.blooddonation.R;
import com.lifesaver.blooddonation.models.BloodRequest;
import com.lifesaver.blooddonation.utils.DateUtils;

import java.util.ArrayList;
import java.util.List;

public class BloodRequestAdapter extends RecyclerView.Adapter<BloodRequestAdapter.VH> {

    public interface OnRequestClick { void onClick(BloodRequest request); }

    private final List<BloodRequest> items = new ArrayList<>();
    private final OnRequestClick listener;

    public BloodRequestAdapter(OnRequestClick listener) { this.listener = listener; }

    public void submit(List<BloodRequest> data) {
        items.clear();
        if (data != null) items.addAll(data);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_blood_request, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        BloodRequest r = items.get(position);
        h.bloodGroup.setText(r.getBloodGroup());
        h.patientName.setText(r.getPatientName());
        h.hospital.setText(r.getHospital());
        h.priority.setText(r.getPriority() == null ? "" : r.getPriority().toUpperCase());

        switch (r.getPriority() == null ? "" : r.getPriority()) {
            case BloodRequest.PRIORITY_NORMAL:
                h.priority.setBackgroundResource(R.drawable.bg_priority_normal);
                break;
            case BloodRequest.PRIORITY_URGENT:
                h.priority.setBackgroundResource(R.drawable.bg_priority_urgent);
                break;
            case BloodRequest.PRIORITY_EMERGENCY:
            default:
                h.priority.setBackgroundResource(R.drawable.bg_priority_emergency);
        }

        String meta = r.getUnitsNeeded() + " units needed  •  required by "
                + DateUtils.formatIso(r.getRequiredDate());
        h.meta.setText(meta);

        h.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onClick(r);
        });
    }

    @Override public int getItemCount() { return items.size(); }

    static class VH extends RecyclerView.ViewHolder {
        final TextView bloodGroup, patientName, hospital, priority, meta;
        VH(@NonNull View v) {
            super(v);
            bloodGroup  = v.findViewById(R.id.text_blood_group);
            patientName = v.findViewById(R.id.text_patient_name);
            hospital    = v.findViewById(R.id.text_hospital);
            priority    = v.findViewById(R.id.text_priority);
            meta        = v.findViewById(R.id.text_meta);
        }
    }
}
