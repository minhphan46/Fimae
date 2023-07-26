package com.example.fimae;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;

import com.example.fimae.adapters.Dating.DatingImageAdapterItem;
import com.example.fimae.components.ListTile;
import com.example.fimae.models.dating.DatingProfile;
import com.example.fimae.repository.DatingRepository;

import java.util.ArrayList;

public class DatingGeneralSettings extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    DatingProfile datingProfile;
    public DatingGeneralSettings( DatingProfile datingProfile) {
        super();
        this.datingProfile = datingProfile;
    }

    ListTile addImages;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    Switch pauseDating;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dating_general_settings, container, false);
        ListTile listTile = view.findViewById(R.id.lt_safe_tips);


        listTile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), DatingAddImages.class);
                startActivity(intent);
            }
        });
        addImages = view.findViewById(R.id.lt_add_images);
        addImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<DatingImageAdapterItem> images = new ArrayList<>();
                for (int i = 0; i < datingProfile.getImages().size(); i++) {
                    images.add(new DatingImageAdapterItem(datingProfile.getImages().get(i), false));
                }
                Intent intent = new Intent(getContext(), DatingAddImages.class);
                intent.putExtra("uid", datingProfile.getUid());
                intent.putExtra("isCreate", false);
                intent.putExtra("images", images);
                addImagesLauncher.launch(intent);
            }
        });
        pauseDating = view.findViewById(R.id.switch_pause_matching);
        pauseDating.setChecked(datingProfile.isEnable());
        pauseDating.setOnCheckedChangeListener((buttonView, isChecked) -> {
            DatingRepository.getInstance().updateEnable(isChecked);
        });
        return view;
    }
    ActivityResultLauncher<Intent> addImagesLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result != null && result.getResultCode() == -1) {
                    assert result.getData() != null;
                    ArrayList<String> images =  result.getData().getStringArrayListExtra("urls");
                    datingProfile.setImages(images);
                }
            });
}