package com.example.fimae.fragments;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.fimae.R;
import com.example.fimae.activities.DetailPostActivity;
import com.example.fimae.activities.EditProfileActivity;
import com.example.fimae.activities.SettingActivity;
import com.example.fimae.activities.ShortVideoActivity;
import com.example.fimae.adapters.GridAutoFitLayoutManager;
import com.example.fimae.adapters.PostAdapter;
import com.example.fimae.adapters.ShortAdapter.ShortsReviewAdapter;
import com.example.fimae.adapters.ShortAdapter.ShortsReviewProfileAdapter;
import com.example.fimae.adapters.SpacingItemDecoration;
import com.example.fimae.bottomdialogs.AvatarBottomSheetFragment;
import com.example.fimae.bottomdialogs.PickImageBottomSheetFragment;
import com.example.fimae.databinding.FragmentProfileBinding;
import com.example.fimae.models.Fimaers;
import com.example.fimae.models.Post;
import com.example.fimae.models.shorts.ShortMedia;
import com.example.fimae.repository.FimaerRepository;
import com.example.fimae.repository.ShortsRepository;
import com.example.fimae.service.FirebaseService;
import com.example.fimae.utils.FileUtils;
import com.example.fimae.viewmodels.ProfileViewModel;
import com.example.fimae.viewmodels.ProfileViewModelFactory;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    FimaerRepository userRepo = FimaerRepository.getInstance();
    ImageButton btnEditProfile, copyLitId;
    ImageView backgroundImg;
    CircleImageView avatarBtn;
    TextView bioTextView;
    FragmentProfileBinding binding;
    ProfileViewModel viewModel;

    private List<Post> posts = new ArrayList<>();

    private PostAdapter postAdapter;

    public static ProfileFragment newInstance(String uid) {
        Bundle args = new Bundle();
        args.putString("uid", uid);
        ProfileFragment fragment = new ProfileFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_profile,container,false);
        View view = binding.getRoot();
        if (getArguments() != null) {
            String uid = getArguments().getString("uid");
            ProfileViewModelFactory factory = new ProfileViewModelFactory(uid);
            viewModel = new ViewModelProvider(this, factory).get(ProfileViewModel.class);
        }
        else
        {
            viewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        }
        binding.setLifecycleOwner(this.getViewLifecycleOwner());
        binding.setViewmodel(viewModel);
        posts.clear();

        Log.i("PROFILE", "onCreateView: ");
        viewModel.getUser().observe(getViewLifecycleOwner(), new Observer<Fimaers>() {
            @Override
            public void onChanged(Fimaers fimaers) {
                if(fimaers.getBio() != null)
                    setTextSpan();
                Log.i("PROFILE", "onChanged: " + fimaers.getName());
            }
        });
        TabLayout tabLayout = view.findViewById(R.id.tabView);
        bioTextView = view.findViewById(R.id.bioTxt);
        btnEditProfile = view.findViewById(R.id.editProfileBtn);
        avatarBtn = view.findViewById(R.id.avatarBtn);
        backgroundImg = view.findViewById(R.id.backgroundImage);
        copyLitId = view.findViewById(R.id.copyBtn);
        binding.postList.setHasFixedSize(true);
        postAdapter = new PostAdapter();
        postAdapter.setData(getContext(), posts, post -> {
            Intent intent = new Intent(getContext(), DetailPostActivity.class);
            intent.putExtra("id", post.getPostId());
            startActivity(intent);
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        SpacingItemDecoration itemDecoration = new SpacingItemDecoration(1, 1, 1, 1);
        binding.postList.addItemDecoration(itemDecoration);
        binding.postList.setAdapter(postAdapter);
        binding.postList.setLayoutManager(linearLayoutManager);
        CollectionReference postRef = FirebaseFirestore.getInstance().collection("posts");
        postRef.addSnapshotListener((value, error) -> {
            if (error != null) {
                return;
            }
            for (DocumentChange dc : value.getDocumentChanges()) {
                Post post = dc.getDocument().toObject(Post.class);
                switch (dc.getType()) {
                    case ADDED:
                        if(post.getPublisher().equals(viewModel.getUid()))
                        {
                            posts.add(post);
                            postAdapter.addUpdate();
                        }
                        break;
                    case MODIFIED:
                        for(Post item : posts){
                            if(item.getPostId().equals(post.getPostId())){
                                if(!post.getContent().equals(item.getContent()) || post.getPostImages().size() != item.getPostImages().size()){
                                    posts.set(posts.indexOf(item), post);
                                    postAdapter.notifyItemChanged(posts.indexOf(item));
                                }
                            }
                        }
                        break;
                    case REMOVED:
                        break;
                }
            }
        });
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
        binding.backgroundImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!viewModel.isOther())
                {
                    PickImageBottomSheetFragment pickImageFragment = new PickImageBottomSheetFragment();
                    pickImageFragment.setCallBack(new PickImageBottomSheetFragment.PickImageCallBack() {
                        @Override
                        public void pickImageComplete(Uri uri) {
                            FirebaseService firebaseService = FirebaseService.getInstance();
                            String imagePath = FileUtils.getFilePathFromContentUri(getContext(),uri);
                            firebaseService.uploadFile("background/" + viewModel.getUid(), Uri.parse(imagePath))
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
                                                            viewModel.getUser().getValue().setBackgroundUrl(task.getResult().toString());
                                                            viewModel.updateUser();
                                                        }
                                                    }
                                                });
                                            }
                                        }
                                    });
                        }
                    });
                    FragmentManager fragmentManager = getChildFragmentManager(); // For fragments
                    pickImageFragment.show(fragmentManager, "pick_image_bottom_sheet");
                }
            }
        });
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                if (position == 0) {
                    binding.postList.setAdapter(postAdapter);
                    binding.postList.setLayoutManager(linearLayoutManager);
                } else if (position == 1) {
                    Query shortQuery = ShortsRepository.getInstance().getShortUserQuery(viewModel.getUid());
                    ShortsReviewProfileAdapter shortsReviewProfileAdapter = new ShortsReviewProfileAdapter(
                            shortQuery,
                            new ShortsReviewProfileAdapter.IClickCardListener() {
                                @Override
                                public void onClickUser(ShortMedia video) {
                                    Intent intent = new Intent(getContext(), ShortVideoActivity.class);
                                    intent.putExtra("idVideo", video.getId()); // send id video
                                    intent.putExtra("isProfile", true);
                                    startActivity(intent);
                                }
                            }
                    );
                    binding.postList.setAdapter(shortsReviewProfileAdapter);
                    GridAutoFitLayoutManager gridLayoutManager = new GridAutoFitLayoutManager(getContext(), 120);
                    binding.postList.setLayoutManager(gridLayoutManager);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // Do nothing when tab is unselected
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // Handle tab reselection if needed
            }
        });

        initListener();
        return view;
    }

    private void initListener()
    {
        avatarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!viewModel.isOther())
                {
                    AvatarBottomSheetFragment avatarFragment = AvatarBottomSheetFragment
                            .newInstance(viewModel.getUser().getValue().getAvatarUrl(), viewModel.getUid());
                    avatarFragment.setCallBack(new PickImageBottomSheetFragment.PickImageCallBack() {
                        @Override
                        public void pickImageComplete(Uri uri) {
                            viewModel.getUser().getValue().setAvatarUrl(uri.toString());
                            viewModel.updateUser();
                        }
                    });
                    FragmentManager fragmentManager = getChildFragmentManager(); // For fragments
                    avatarFragment.show(fragmentManager, "avatar_bottom_sheet");
                }
            }
        });
        copyLitId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                copyLitId();
            }
        });
        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), SettingActivity.class);
                startActivity(intent);
            }
        });
    }

    private void copyLitId() {
        // Get the text from the TextView
        String litId = binding.getViewmodel().getUser().getValue().getUid();

        // Copy the text to the clipboard
        ClipboardManager clipboardManager = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("Lit ID", litId);
        if (clipboardManager != null) {
            clipboardManager.setPrimaryClip(clipData);
        }

        // Show a toast message
        Toast.makeText(getActivity(), "Lit ID copied", Toast.LENGTH_SHORT).show();
    }

    private void setTextSpan()
    {
        String text = binding.getViewmodel().getUser().getValue().getBio();
        // Create a SpannableString with the text and the edit icon
        SpannableString spannableString = new SpannableString( text + " "); // Add some extra space after text for the icon
        if(!viewModel.isOther())
        {
            // Get the drawable for the edit icon
            int iconSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 18,
                    getResources().getDisplayMetrics());
            Drawable icon = getResources().getDrawable(R.drawable.ic_edit);
            icon.setBounds(0, 0, iconSize, iconSize);

            // Create an ImageSpan with the edit icon and add it to the SpannableString
            ImageSpan imageSpan = new ImageSpan(icon, ImageSpan.ALIGN_BASELINE);
            spannableString.setSpan(imageSpan, text.length(), text.length() + 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            // Create a ClickableSpan for the icon
            ClickableSpan clickableSpan = new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    // Handle the click event here
                    navToEditProfile();
                }
            };
            // Set the ClickableSpan to the ImageSpan
            spannableString.setSpan(clickableSpan, text.length(), text.length() + 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);

        }

        Log.i("TAG", "setTextSpan: " + spannableString.toString());
        // Set the SpannableString to the TextView
        bioTextView.setText(spannableString);
        bioTextView.setMovementMethod(LinkMovementMethod.getInstance()); // Make the links clickable
    }

    private void navToEditProfile() {
        Intent intent = new Intent(getContext(), EditProfileActivity.class);
        startActivity(intent);
    }

}
