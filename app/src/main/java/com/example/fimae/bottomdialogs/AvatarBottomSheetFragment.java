package com.example.fimae.bottomdialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import com.example.fimae.R;
import com.example.fimae.databinding.FragmentAvatarBottomSheetBinding;
import com.example.fimae.repository.FimaerRepository;
import com.example.fimae.service.FirebaseService;
import com.example.fimae.utils.FileUtils;
import com.example.fimae.viewmodels.AvatarBottomSheetViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class AvatarBottomSheetFragment extends BottomSheetDialogFragment {

    public static AvatarBottomSheetFragment newInstance(String url, String uid) {
        AvatarBottomSheetFragment frag = new AvatarBottomSheetFragment();
        Bundle args = new Bundle();
        args.putString("url", url);
        args.putString("uid", uid);
        frag.setArguments(args);
        return frag;
    }
    PickImageBottomSheetFragment.PickImageCallBack callBack;

    public void setCallBack(PickImageBottomSheetFragment.PickImageCallBack callBack) {
        this.callBack = callBack;
    }

    private FragmentAvatarBottomSheetBinding binding;
    private Uri imageUri;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        AvatarBottomSheetViewModel viewModel = new ViewModelProvider(this).get(AvatarBottomSheetViewModel.class);
        View view = inflater.inflate(R.layout.fragment_avatar_bottom_sheet, container, false);
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_avatar_bottom_sheet,container,false);
        view = binding.getRoot();
        ImageView avatar = view.findViewById(R.id.avatarBtn);
        TextView text = view.findViewById(R.id.saveTextView);
        Button editAvabtn = view.findViewById(R.id.editAvaBtn);
        text.setVisibility(View.GONE);
        String uid = getArguments() != null ? getArguments().getString("uid") : null;

                text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setCancelable(false);
                FirebaseService firebaseService = FirebaseService.getInstance();
                String imagePath = FileUtils.getFilePathFromContentUri(getContext(),imageUri);
                firebaseService.uploadFile("avatar/" + uid, Uri.parse(imagePath))
                        .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                if(task.isSuccessful())
                                {
                                    Toast.makeText(getContext(), "Image Uploaded Successfully", Toast.LENGTH_SHORT).show();
                                    Task<Uri> downloadUri = task.getResult().getStorage().getDownloadUrl();
                                    downloadUri.addOnCompleteListener(new OnCompleteListener<Uri>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Uri> task) {
                                            if(task.isSuccessful()){
                                                callBack.pickImageComplete(task.getResult());
                                            }
                                        }
                                    });
                                }
                            }
                        });
                viewModel.updateAvatar(imageUri, new FimaerRepository.UploadAvatarCallback() {
                    @Override
                    public void onUploadSuccess(Uri uri) {
                        dismiss();
                    }

                    @Override
                    public void onUploadError(String errorMessage) {

                    }
                });
            }
        });
        editAvabtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PickImageBottomSheetFragment pickImageFragment = new PickImageBottomSheetFragment();
                pickImageFragment.setCallBack(new PickImageBottomSheetFragment.PickImageCallBack() {
                    @Override
                    public void pickImageComplete(Uri uri) {
                        Log.i("TAG", "pickImageComplete: " + uri);
                        imageUri = uri;
                        Picasso.get().load(uri).into(avatar);
                        text.setVisibility(View.VISIBLE);
                    }
                });
                FragmentManager fragmentManager = getChildFragmentManager(); // For fragments
                pickImageFragment.show(fragmentManager, "pick_image_bottom_sheet");
            }
        });
        binding.setLifecycleOwner(this.getViewLifecycleOwner());
        String url = getArguments() != null ? getArguments().getString("url") : null;
        viewModel.setImageUrl(url);
        binding.setUrl(url);

        return view;
    }
}
