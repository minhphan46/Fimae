package com.example.fimae.fragments;

import android.app.DatePickerDialog;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.DatePicker;

import com.example.fimae.R;
import com.example.fimae.databinding.FragmentCreateProfileBinding;
import com.example.fimae.viewmodels.CreateProfileViewModel;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CreateProfileFragment extends Fragment {

    public CreateProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    CreateProfileViewModel viewModel;

    FragmentCreateProfileBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_create_profile,container,false);
        binding.setLifecycleOwner(this.getViewLifecycleOwner());
        viewModel = new ViewModelProvider(getActivity()).get(CreateProfileViewModel.class);
        binding.dobEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDateTimePicker();
            }
        });
        binding.maleChip.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b)
                {
                    viewModel.user.setGender(true);
                }
            }
        });
        binding.femaleChip.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b)
                {
                    viewModel.user.setGender(false);
                }
            }
        });
        binding.buttonFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewModel.user.setName(binding.nameEditText.getText().toString());
                CreateAvatarFragment profileFragment = new CreateAvatarFragment();
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();

                // Replace the contents of the container with the new Fragment
                transaction.replace(R.id.container, profileFragment);

                // Add the transaction to the back stack
                transaction.addToBackStack(null);

                // Commit the FragmentTransaction
                transaction.commit();
            }
        });
        binding.nameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                checkIsComplete();
            }
        });
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        checkIsComplete();
        super.onResume();
    }

    private void showDateTimePicker() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        Date dob = new Date();
        if(binding.dobEditText != null)
        {
            try {
                dob = sdf.parse(binding.dobEditText.getText().toString());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        if (dob != null) {
            calendar.setTime(dob);
        }
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        // Get the Date object from the ViewModel



        // Create a date-time picker dialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this.getContext(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        calendar.set(year, month, day); // Set the selected year, month, and day to the Calendar
                        viewModel.user.setDob(calendar.getTime());
                        binding.dobEditText.setText(sdf.format(calendar.getTime()));
                        checkIsComplete();
                    }
                },
                year,
                month,
                dayOfMonth
        );

        datePickerDialog.show();
    }

    public void checkIsComplete()
    {
        if(!binding.nameEditText.getText().toString().isEmpty() && !binding.dobEditText.getText().toString().isEmpty())
        {
            binding.buttonFinish.setEnabled(true);
        }
        else
        {
            binding.buttonFinish.setEnabled(false);
        }
    }
}