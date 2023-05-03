package com.example.fimae.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import com.example.fimae.R;
import com.example.fimae.adapters.UserAdapter;
import com.example.fimae.databinding.FragmentFirstBinding;
import com.example.fimae.models.UserInfo;

import java.util.Arrays;

public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;

    private String[] items = {"Item 1", "Item 2", "Item 3", "Item 4", "Item 5"};

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentFirstBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        UserAdapter adapter = new UserAdapter(this.getContext(),
                R.layout.user_chat, Arrays.asList(UserInfo.dummy));
        binding.listView.setAdapter(adapter);
        binding.listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                NavHostFragment.findNavController(FirstFragment.this)
                        .navigate(R.id.SecondFragment);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}