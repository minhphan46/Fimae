package com.example.fimae.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.example.fimae.R;
import com.example.fimae.adapters.MessageAdapter;
import com.example.fimae.databinding.FragmentSecondBinding;
import com.example.fimae.models.Message;

import java.util.Arrays;

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

        MessageAdapter adapter = new MessageAdapter(this.getContext(), R.layout.fragment_second, Arrays.asList(Message.dummy));
        binding.listMessages.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}