package com.example.fimae;

import static com.example.fimae.models.dating.DatingProfileDefaultValue.*;
import android.app.Activity;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.fimae.activities.MapPicker;
import com.example.fimae.adapters.Report.ReportAdapterItem;
import com.example.fimae.bottomdialogs.DatingBottomSheetPicker;
import com.example.fimae.components.ListTile;
import com.example.fimae.models.dating.DatingProfile;
import com.example.fimae.models.dating.DatingProfileDefaultValue;
import com.example.fimae.models.dating.EducationalLevel;
import com.example.fimae.models.dating.GenderOptions;
import com.example.fimae.models.dating.LatLng;
import com.example.fimae.models.dating.RelationshipType;
import com.example.fimae.repository.DatingRepository;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;

public class DatingProfileFragment extends Fragment {

    ListTile location, distance, gender, age, relationship, height, education;

    DatingProfile datingProfile;

    public DatingProfileFragment(DatingProfile profile) {
        super();
        this.datingProfile = profile;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        setValue();
        return view;
    }

    void setValue(){
        updateListTileLocation(datingProfile.getLocation());
        updateListTileDistance(datingProfile.getDistance());
        updateListTileGender(datingProfile.getGenderOptions());
        updateListTileAge(datingProfile.getMinAge(), datingProfile.getMaxAge());
        updateListTileRelationship(datingProfile.getRelationshipType());
        updateListTileHeight(datingProfile.getMinHeight(), datingProfile.getMaxHeight());
        updateListTileEducation(datingProfile.getEducationalLevel());
    }

    private void updateListTileEducation(EducationalLevel educationalLevel) {
        education.setSubtitle(educationalLevel.getDisplayName());
    }

    private void updateListTileRelationship(RelationshipType relationshipType) {
        relationship.setSubtitle(relationshipType.getDisplayName());
    }

    private void updateListTileGender(GenderOptions genderOptions) {
        gender.setSubtitle(genderOptions.getDisplayName());
    }

    void updateListTileLocation(LatLng latLng){
        try {
            Geocoder geocoder = new Geocoder(requireContext());
            ArrayList<Address> addresses = null;
            addresses = (ArrayList<Address>) geocoder.getFromLocation(latLng.getLatitude(), latLng.getLongitude(), 3);
            //Set subtile is City name
            if (addresses != null && !addresses.isEmpty()){
                for (int i = 0; i< addresses.size(); i++){
                    String s = addresses.get(0).getLocality();
                    if(s != null && !s.isEmpty()){
                        location.setSubtitle(s);
                        return;
                    }
                }
            }
            else {
                location.setSubtitle("Không xác định được vị trí");
            }

        } catch (Exception e) {
            location.setSubtitle("Không xác định được vị trí");
            e.printStackTrace();
        }
    }
    void updateListTileAge(int minAge, int maxAge){
        age.setSubtitle(minAge + " - " + maxAge + " tuổi");
    }
    void updateListTileHeight(int minHeight, int maxHeight){
        height.setSubtitle(minHeight + " - " + maxHeight + " cm");
    }
    void updateListTileDistance(int distance){
        this.distance.setSubtitle("Trong vòng " + distance + " km");
    }
    ActivityResultLauncher<Intent> mapPickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    assert result.getData() != null;
                    LatLng selectedLatLng = (LatLng) result.getData().getSerializableExtra("selectedLatLng");
                    if(selectedLatLng == null){
                        Toast.makeText(getContext(), "Lỗi: Không thể lấy vị trí", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    updateListTileLocation(selectedLatLng);
                    DatingRepository.getInstance().updateLocation(selectedLatLng).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (!task.isSuccessful()) {
                                {
                                    Toast.makeText(getContext(), "Lỗi: Không thể cập nhật vị trí", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });

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
                        int calDis = Integer.parseInt(reportAdapterItem.getId());
                        updateListTileDistance(calDis);
                        DatingRepository.getInstance().updateDistance(calDis).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (!task.isSuccessful()) {
                                    {
                                        updateListTileDistance(datingProfile.getDistance());
                                        Toast.makeText(getContext(), "Lỗi: Không thể cập nhật khoảng cách", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        });
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

                        GenderOptions genderOptions;
                        if (reportAdapterItem.getId() == "1") {
                            genderOptions = GenderOptions.UNIMPORTANT;
                        } else if (reportAdapterItem.getId() == "2") {
                            genderOptions = GenderOptions.MALE;
                        } else if (reportAdapterItem.getId() == "3") {
                            genderOptions = GenderOptions.FEMALE;
                        } else {
                            genderOptions = GenderOptions.UNIMPORTANT;
                        }
                        updateListTileGender(genderOptions);
                        DatingRepository.getInstance().updateGenderOptions(genderOptions).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (!task.isSuccessful()) {
                                    updateListTileGender(datingProfile.getGenderOptions());
                                    Toast.makeText(getContext(), "Lỗi: Không thể cập nhật giới tính", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });


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
                        updateListTileAge(min, max);

                        DatingRepository.getInstance().updateMinAgeAndMaxAge(min, max).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (!task.isSuccessful()) {
                                    updateListTileAge(datingProfile.getMinAge(), datingProfile.getMaxAge());
                                    Toast.makeText(getContext(), "Lỗi: Không thể cập nhật độ tuổi", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
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
                        RelationshipType relationshipType;
                        switch (reportAdapterItem.getId()) {
                            case "2":
                                relationshipType = RelationshipType.CHAT;
                                break;
                            case "3":
                                relationshipType = RelationshipType.FRIENDS;
                                break;
                            case "4":
                                relationshipType = RelationshipType.CASUAL_DATING;
                                break;
                            case "5":
                                relationshipType = RelationshipType.LONG_TERM;
                                break;
                            default:
                                relationshipType = RelationshipType.UNIMPORTANT;
                                break;
                        }
                        updateListTileRelationship(relationshipType);
                        DatingRepository.getInstance().updateRelationshipType(relationshipType).addOnCompleteListener(
                                new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (!task.isSuccessful()) {
                                            updateListTileRelationship(datingProfile.getRelationshipType());
                                            Toast.makeText(getContext(), "Lỗi: Không thể cập nhật mối quan hệ", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                        );

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
                        updateListTileHeight(min, max);
                        DatingRepository.getInstance().updateMinHeightAndMaxHeight(min, max).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (!task.isSuccessful()) {
                                    updateListTileHeight(datingProfile.getMinHeight(), datingProfile.getMaxHeight());
                                    Toast.makeText(getContext(), "Lỗi: Không thể cập nhật chiều cao", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
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
                        EducationalLevel educationalLevel;
                        switch (reportAdapterItem.getId()) {
                            case "2":
                                educationalLevel = EducationalLevel.HIGH_SCHOOL;
                                break;
                            case "3":
                                educationalLevel = EducationalLevel.COLLEGE;
                                break;
                            case "4":
                                educationalLevel = EducationalLevel.GRADUATE;
                                break;
                            default:
                                educationalLevel = EducationalLevel.UNIMPORTANT;
                                break;
                        }
                        updateListTileEducation(educationalLevel);
                        DatingRepository.getInstance().updateEducationalLevel(educationalLevel).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (!task.isSuccessful()) {
                                    updateListTileEducation(datingProfile.getEducationalLevel());
                                    Toast.makeText(getContext(), "Lỗi: Không thể cập nhật trình độ học vấn", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }

                    @Override
                    public void onDatingRangeSliderSubmit(int min, int max) {

                    }
                })
                .build().show(getChildFragmentManager(), "DatingBottomSheetPicker");
    }


}