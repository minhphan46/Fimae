package com.example.fimae.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager2.widget.ViewPager2;

import com.example.fimae.activities.AuthenticationActivity;
import com.example.fimae.activities.DetailPostActivity;
import com.example.fimae.activities.DisabledUserActivity;
import com.example.fimae.activities.HomeActivity;
import com.example.fimae.activities.PostActivity;
import com.example.fimae.adapters.PostAdapter;
import com.example.fimae.adapters.ShortAdapter.ShortFragmentPageAdapter;
import com.example.fimae.databinding.FragmentFeedBinding;
import com.example.fimae.models.DisableUser;
import com.example.fimae.models.Post;
import com.example.fimae.repository.FollowRepository;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class FeedFragment extends Fragment {
    public static int REQUEST_CREATEPOST_CODE = 1;
    private PostAdapter postAdapter;
    private List<Post> posts = new ArrayList<>();
    FragmentFeedBinding binding;
    private ListenerRegistration postRef;

    private ShortFragmentPageAdapter shortFragmentPageAdapter;

    ActivityResultLauncher<Intent> mStartForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == REQUEST_CREATEPOST_CODE) {
                    Intent intent = result.getData();
                    String postId = intent.getStringExtra("postId");
                }
            });
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        postRef.remove();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentFeedBinding.inflate(inflater, container, false);
//        binding.postList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
//        linearLayoutManager.setReverseLayout(true);
//        linearLayoutManager.setStackFromEnd(true);

        binding.postList.setLayoutManager(linearLayoutManager);
        postAdapter = new PostAdapter();
        posts.clear();
        postAdapter.setData(getContext(), posts, post -> {
            Intent intent = new Intent(getContext(), DetailPostActivity.class);
            intent.putExtra("id", post.getPostId());
            startActivity(intent);
        });
        binding.postList.setAdapter(postAdapter);
        postRef= FirebaseFirestore.getInstance().collection("posts").orderBy("timeCreated", Query.Direction.DESCENDING).addSnapshotListener((value, error) -> {
            if (error != null) {
                return;
            }
            for (DocumentChange dc : value.getDocumentChanges()) {
                Post post = dc.getDocument().toObject(Post.class);
                switch (dc.getType()) {
                    case ADDED:
                        if(post.getIsDeleted() != null && post.getIsDeleted()) {
                            continue;
                        }
                        else {
                            switch (post.getPostMode()){
                                case PRIVATE:
                                    if(post.getPublisher().equals(FirebaseAuth.getInstance().getUid())){
                                        posts.add(post);
                                        if(posts.size() <=1){
                                            postAdapter.addUpdate();
                                        }
                                        else postAdapter.notifyItemInserted(posts.size() - 1);

                                    }

                                    break;
                                case FRIEND:
                                    FollowRepository.getInstance().isFriend(post.getPublisher()).addOnSuccessListener(new OnSuccessListener<Boolean>() {
                                        @Override
                                        public void onSuccess(Boolean aBoolean) {
                                            if (aBoolean){
                                                posts.add(post);
                                                if(posts.size() <=1){
                                                    postAdapter.addUpdate();
                                                }
                                                else postAdapter.notifyItemInserted(posts.size() - 1);
                                            }
                                        }
                                    });
                                    break;
                                case PUBLIC:
                                    posts.add(post);
                                    if(posts.size() <=1){
                                        postAdapter.addUpdate();
                                    }
                                    else postAdapter.notifyItemInserted(posts.size() - 1);
                            }
                        }
                    case MODIFIED:
                        int i =0;
                        for(Post item : posts){
                            if(item.getPostId().equals(post.getPostId())){
                                i = 1;
                                if(post.getIsDeleted() != null && post.getIsDeleted()){
                                    int index = posts.indexOf(item);
                                    posts.remove(item);
                                    postAdapter.notifyItemRemoved(index);
                                }
                                else if(!post.getContent().equals(item.getContent()) || post.getPostImages().size() != item.getPostImages().size()){
                                    posts.set(posts.indexOf(item), post);
                                    postAdapter.notifyItemChanged(posts.indexOf(item) + 1);
                                }
                                break;
                            }
                        }
                        break;
                    case REMOVED:
                        posts.remove(post);
                        postAdapter.notifyItemRemoved(posts.indexOf(post));
                        break;
                }
            }
        });
        binding.addPost.setOnClickListener(view -> {


            Intent intent = new Intent(getContext(), PostActivity.class );
            //            intent.putExtra("uid", "0Ksg5AboSIf7c2mhLcPWiAmczcZ2");
                        mStartForResult.launch(intent);        });
        listenDisable();
        // page shorts
        shortFragmentPageAdapter = new ShortFragmentPageAdapter(getChildFragmentManager(), getLifecycle());

        binding.viewPagerVideo.setAdapter(shortFragmentPageAdapter);
        binding.viewPagerVideo.setUserInputEnabled(false);
        binding.tabLayoutVideo.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(tab != null) {
                    binding.viewPagerVideo.setCurrentItem(tab.getPosition());
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        binding.viewPagerVideo.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                binding.tabLayoutVideo.selectTab(binding.tabLayoutVideo.getTabAt(position));
            }
        });

        return binding.getRoot();
    }
    private void listenDisable(){
        FirebaseFirestore.getInstance().collection("user_disable")
                .document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())+"POST")
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            return;
                        }
                        if (snapshot != null && snapshot.exists()) {
                            // Document exists, check if the user is disabled
                            DisableUser disableUser = snapshot.toObject(DisableUser.class);
                            if (disableUser != null && disableUser.getTimeEnd().after(new Date()) && disableUser.getType().equals("POST")) {
//                                Intent intent = new Intent(HomeActivity.this, AuthenticationActivity.class);
//                                intent.putExtra("signout", true);
//                                startActivity(intent);
                                binding.addPost.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent intent = new Intent(getContext(), DisabledUserActivity.class);
                                        intent.putExtra( "disableId", disableUser.getUserId());
                                        intent.putExtra("type", "POST");
                                        startActivity(intent);
                                    }
                                });
                            }
                            else{
                                binding.addPost.setOnClickListener(view -> {

                                    Intent intent = new Intent(getContext(), PostActivity.class );
                                    mStartForResult.launch(intent);        });

                            }
                        }else{
                            binding.addPost.setOnClickListener(view -> {

                                Intent intent = new Intent(getContext(), PostActivity.class );
                                mStartForResult.launch(intent);        });
                        }
                    }
                });
    }


}
