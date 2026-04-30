package com.lifesaver.blooddonation.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lifesaver.blooddonation.R;
import com.lifesaver.blooddonation.constants.BloodGroups;
import com.lifesaver.blooddonation.models.Donation;
import com.lifesaver.blooddonation.utils.DateUtils;

import java.util.ArrayList;
import java.util.List;

public class DonationHistoryAdapter
        extends RecyclerView.Adapter<DonationHistoryAdapter.VH> {

    private final List<Donation> items = new ArrayList<>();

    public void submit(List<Donation> data) {
        items.clear();
        if (data != null) items.addAll(data);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_donation, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        Donation d = items.get(position);
        h.type.setText(prettyType(d.getType()));
        String meta = DateUtils.formatIso(d.getDate());
        if (d.getOrganization() != null && !d.getOrganization().isEmpty()) {
            meta += "  •  " + d.getOrganization();
        } else if (d.getLocation() != null && !d.getLocation().isEmpty()) {
            meta += "  •  " + d.getLocation();
        }
        h.meta.setText(meta);
        h.units.setText(d.getUnits() == null ? "" : d.getUnits());
    }

    @Override public int getItemCount() { return items.size(); }

    private static String prettyType(String type) {
        if (type == null) return "Donation";
        for (BloodGroups.DonationType t : BloodGroups.DONATION_TYPES) {
            if (t.value.equals(type)) return t.label;
        }
        return type;
    }

    static class VH extends RecyclerView.ViewHolder {
        final TextView type, meta, units;
        VH(@NonNull View v) {
            super(v);
            type  = v.findViewById(R.id.text_type);
            meta  = v.findViewById(R.id.text_meta);
            units = v.findViewById(R.id.text_units);
        }
    }
}
