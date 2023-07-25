package com.example.fimae;

import android.app.Activity;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.fimae.activities.MapPicker;
import com.example.fimae.adapters.Report.ReportAdapterItem;
import com.example.fimae.bottomdialogs.DatingBottomSheetPicker;
import com.example.fimae.components.ListTile;
import com.example.fimae.models.dating.DatingProfileDefaultValue;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;

public class DatingProfile extends Fragment {
    ArrayList<ReportAdapterItem> distanceOptions = new ArrayList<ReportAdapterItem>() {
        {
            for (int i = 100; i <= DatingProfileDefaultValue.distance; i += 100) {
                add(new ReportAdapterItem(String.valueOf(i), i + " km"));
            }
        }
    };
    ArrayList<ReportAdapterItem> genderOptions = new ArrayList<ReportAdapterItem>() {
        {
            add(new ReportAdapterItem("1", "Không quan trọng"));
            add(new ReportAdapterItem("2", "Nam"));
            add(new ReportAdapterItem("3", "Nữ"));

        }
    };
    ArrayList<ReportAdapterItem> relationshipOptions = new ArrayList<ReportAdapterItem>() {
        {
            add(new ReportAdapterItem("1", "Không quan trọng"));
            add(new ReportAdapterItem("2", "Người trò chuyện"));
            add(new ReportAdapterItem("3", "Quan hệ bạn bè"));
            add(new ReportAdapterItem("4", "Kiểu hẹn hò không ràng buộc"));
            add(new ReportAdapterItem("5", "Mối quan hệ lâu dài"));
        }
    };
    ArrayList<ReportAdapterItem> educationLevelOptions = new ArrayList<ReportAdapterItem>() {
        {
            add(new ReportAdapterItem("1", "Không quan trọng"));
            add(new ReportAdapterItem("2", "Bằng trung học"));
            add(new ReportAdapterItem("3", "Bằng cao đẳng/đại học"));
            add(new ReportAdapterItem("4", "Bằng cao học"));
        }
    };
    ListTile location, distance, gender, age, relationship, height, education;

    public DatingProfile() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    ActivityResultLauncher<Intent> mapPickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    assert result.getData() != null;
                    LatLng selectedLatLng = result.getData().getParcelableExtra("selectedLatLng");


                    Geocoder geocoder = new Geocoder(requireContext());
                    ArrayList<Address> addresses = null;

                    try {
                        addresses = (ArrayList<Address>) geocoder.getFromLocation(selectedLatLng.latitude, selectedLatLng.longitude, 1);
                        //Set subtile is City name
                        if (!addresses.isEmpty())
                            location.setSubtitle(addresses.get(0).getLocality());
                    } catch (IOException e) {
                        Toast.makeText(getContext(), "Lỗi: Không xác định được vị trí", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }
            });

    public void setProfileLocation() {
        Intent intent = new Intent(getActivity(), MapPicker.class);
        mapPickerLauncher.launch(intent);
    }

    public void setProfileDistance() {
        DatingBottomSheetPicker.builder()
                .setTitle("Chọn khoảng cách")
                .setSubtitle("Khoảng cách giữa bạn và đối phương")
                .setReportAdapterItems(distanceOptions, 0)
                .setOnDatingtDialogListener(new DatingBottomSheetPicker.OnDatingtDialogListener() {
                    @Override
                    public void onDatingRadioSubmit(ReportAdapterItem reportAdapterItem) {
                        distance.setSubtitle("Trong vòng" + reportAdapterItem.getTitle());
                    }

                    @Override
                    public void onDatingRangeSliderSubmit(int min, int max) {

                    }
                })
                .build().show(getChildFragmentManager(), "DatingBottomSheetPicker");
    }

    public void setProfileGender() {
        DatingBottomSheetPicker.builder()
                .setTitle("Chọn giới tính")
                .setSubtitle("Giới tính của đối phương")
                .setReportAdapterItems(genderOptions, 0)
                .setOnDatingtDialogListener(new DatingBottomSheetPicker.OnDatingtDialogListener() {
                    @Override
                    public void onDatingRadioSubmit(ReportAdapterItem reportAdapterItem) {
                        distance.setSubtitle( reportAdapterItem.getTitle());
                    }

                    @Override
                    public void onDatingRangeSliderSubmit(int min, int max) {

                    }
                })
                .build().show(getChildFragmentManager(), "DatingBottomSheetPicker");
    }

    public void setProfileAge() {
        DatingBottomSheetPicker.builder().setTitle("Chọn độ tuổi")
                .setUpRangeSlider(DatingProfileDefaultValue.minAge, DatingProfileDefaultValue.maxAge, "tuổi", "Tuổi", 18, 22)
                .setOnDatingtDialogListener(new DatingBottomSheetPicker.OnDatingtDialogListener() {
                    @Override
                    public void onDatingRadioSubmit(ReportAdapterItem reportAdapterItem) {

                    }

                    @Override
                    public void onDatingRangeSliderSubmit(int min, int max) {
                        age.setSubtitle(min + " - " + max + " tuổi");
                    }
                }).build().show(getChildFragmentManager(), "DatingBottomSheetPicker");
    }

    public void setProfileRelationship() {
        DatingBottomSheetPicker.builder()
                .setTitle("Chọn mối quan hệ")
                .setSubtitle("Mối quan hệ mà bạn mong muốn")
                .setReportAdapterItems(relationshipOptions, 1)
                .setOnDatingtDialogListener(new DatingBottomSheetPicker.OnDatingtDialogListener() {
                    @Override
                    public void onDatingRadioSubmit(ReportAdapterItem reportAdapterItem) {
                        distance.setSubtitle(reportAdapterItem.getTitle());
                    }

                    @Override
                    public void onDatingRangeSliderSubmit(int min, int max) {

                    }
                })
                .build().show(getChildFragmentManager(), "DatingBottomSheetPicker");
    }

    public void setProfileHeight() {
        DatingBottomSheetPicker.builder().setTitle("Chọn chiều cao")
                .setUpRangeSlider(DatingProfileDefaultValue.minHeight, DatingProfileDefaultValue.maxHeight, "cm", "Chiều cao", 160, 180)
                .setOnDatingtDialogListener(new DatingBottomSheetPicker.OnDatingtDialogListener() {
                    @Override
                    public void onDatingRadioSubmit(ReportAdapterItem reportAdapterItem) {

                    }

                    @Override
                    public void onDatingRangeSliderSubmit(int min, int max) {
                        height.setSubtitle(min + " - " + max + " cm");
                    }
                }).build().show(getChildFragmentManager(), "DatingBottomSheetPicker");
    }

    public void setProfileEducation() {
        DatingBottomSheetPicker.builder()
                .setTitle("Chọn trình độ học vấn")
                .setSubtitle("Trình độ học vấn của đối phương")
                .setReportAdapterItems(educationLevelOptions, 0)
                .setOnDatingtDialogListener(new DatingBottomSheetPicker.OnDatingtDialogListener() {
                    @Override
                    public void onDatingRadioSubmit(ReportAdapterItem reportAdapterItem) {
                        distance.setSubtitle(reportAdapterItem.getTitle());
                    }

                    @Override
                    public void onDatingRangeSliderSubmit(int min, int max) {

                    }
                })
                .build().show(getChildFragmentManager(), "DatingBottomSheetPicker");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_date_profile, container, false);
        location = view.findViewById(R.id.lt_location);
        distance = view.findViewById(R.id.lt_distance);
        gender = view.findViewById(R.id.lt_gender);
        age = view.findViewById(R.id.lt_age);
        relationship = view.findViewById(R.id.lt_relationship);
        height = view.findViewById(R.id.lt_height);
        education = view.findViewById(R.id.lt_education);
        location.setOnClickListener(v -> setProfileLocation());
        distance.setOnClickListener(v -> setProfileDistance());
        gender.setOnClickListener(v -> setProfileGender());
        age.setOnClickListener(v -> setProfileAge());
        relationship.setOnClickListener(v -> setProfileRelationship());
        height.setOnClickListener(v -> setProfileHeight());
        education.setOnClickListener(v -> setProfileEducation());
        return view;
    }
}