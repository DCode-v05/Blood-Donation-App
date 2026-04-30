package com.lifesaver.blooddonation.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.lifesaver.blooddonation.R;
import com.lifesaver.blooddonation.activities.ChatBotActivity;
import com.lifesaver.blooddonation.adapters.FactsAdapter;
import com.lifesaver.blooddonation.adapters.MythsAdapter;
import com.lifesaver.blooddonation.constants.BloodGroups;

public class AwarenessFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_awareness, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);

        RecyclerView facts = v.findViewById(R.id.recycler_facts);
        facts.setLayoutManager(new LinearLayoutManager(getContext()));
        facts.setAdapter(new FactsAdapter(BloodGroups.BLOOD_FACTS));

        RecyclerView myths = v.findViewById(R.id.recycler_myths);
        myths.setLayoutManager(new LinearLayoutManager(getContext()));
        myths.setAdapter(new MythsAdapter(BloodGroups.MYTHS));

        ((MaterialButton) v.findViewById(R.id.button_chat))
                .setOnClickListener(b -> startActivity(
                        new Intent(getActivity(), ChatBotActivity.class)));
    }
}
