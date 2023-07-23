package com.example.fimae.fragments;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.fimae.R;
import com.example.fimae.databinding.FragmentCreateTagBinding;
import com.example.fimae.viewmodels.CreateProfileViewModel;
import com.google.android.material.tabs.TabLayout;

public class CreateTagFragment extends Fragment {


    FragmentCreateTagBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_create_tag,container,false);
        CreateProfileViewModel viewModel = new ViewModelProvider(getActivity()).get(CreateProfileViewModel.class);
        binding.setLifecycleOwner(this.getViewLifecycleOwner());
        binding.setViewmodel(viewModel);
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
        binding.tabView.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewModel.setChip(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        binding.nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateMatchProfileFragment profileFragment = new CreateMatchProfileFragment();
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();

                // Replace the contents of the container with the new Fragment
                transaction.replace(R.id.container, profileFragment);

                // Add the transaction to the back stack
                transaction.addToBackStack(null);

                // Commit the FragmentTransaction
                transaction.commit();
            }
        });

        return binding.getRoot();
    }
}