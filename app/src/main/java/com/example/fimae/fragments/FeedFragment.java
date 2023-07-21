package com.example.fimae.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

import com.example.fimae.activities.DetailPostActivity;
import com.example.fimae.activities.PostActivity;
import com.example.fimae.adapters.PostAdapter;
import com.example.fimae.adapters.ShortFragmentPageAdapter;
import com.example.fimae.databinding.FragmentFeedBinding;
import com.example.fimae.models.Post;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FeedFragment extends Fragment {
    public static int REQUEST_CREATEPOST_CODE = 1;
    private PostAdapter postAdapter;
    private List<Post> posts = new ArrayList<>();
    FragmentFeedBinding binding;

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
        CollectionReference postRef = FirebaseFirestore.getInstance().collection("posts");
        postRef.orderBy("timeCreated", Query.Direction.DESCENDING).addSnapshotListener((value, error) -> {
            if (error != null) {
                return;
            }
            for (DocumentChange dc : value.getDocumentChanges()) {
                Post post = dc.getDocument().toObject(Post.class);
                switch (dc.getType()) {
                    case ADDED:
                        posts.add(post);
                        if(posts.size() <=1){
                            postAdapter.addUpdate();
                        }
                        else postAdapter.notifyItemInserted(posts.size() - 1);
                        break;
                    case MODIFIED:
                        for(Post item : posts){
                            if(item.getPostId().equals(post.getPostId())){
                                if(!post.getContent().equals(item.getContent()) || post.getPostImages().size() != item.getPostImages().size()){
                                    posts.set(posts.indexOf(item), post);
//                                    item = post;
//                                    postAdapter.notifyItemChanged(posts.indexOf(item));
//                                    postAdapter.notifyItemChanged(posts.indexOf(item) + 1);
                                    postAdapter.notifyItemChanged(posts.indexOf(item) + 1);
                                }
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
            mStartForResult.launch(intent);
        });

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


}
