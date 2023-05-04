package com.example.fimae.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.fimae.adapters.MessageAdapter;
import com.example.fimae.databinding.FragmentSecondBinding;
import com.example.fimae.models.Message;

public class SecondFragment extends Fragment {

    private FragmentSecondBinding binding;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentSecondBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MessageAdapter adapter = new MessageAdapter(this.getContext(), Message.dummy);
        binding.listMessages.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this.getContext());
        binding.listMessages.setLayoutManager(layoutManager);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}