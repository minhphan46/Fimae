package com.example.fimae.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fimae.R;
import com.example.fimae.activities.DetailPostActivity;
import com.example.fimae.activities.PostActivity;
import com.example.fimae.adapters.PostAdapter;
import com.example.fimae.databinding.FragmentFeedBinding;
import com.example.fimae.models.Post;
import com.example.fimae.models.Seed;

import java.util.ArrayList;
import java.util.List;

public class FeedFragment extends Fragment {
    private Seed seed = new Seed();

    private PostAdapter postAdapter;
    private List<Post> postList;
    FragmentFeedBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentFeedBinding.inflate(inflater, container, false);
        binding.postList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        binding.postList.setLayoutManager(linearLayoutManager);
        postList = new ArrayList<>(seed.postseed());
        postAdapter = new PostAdapter(getContext(), postList, new PostAdapter.IClickCardUserListener() {
            @Override
            public void onClickUser(Post post) {
                Intent intent = new Intent(getContext(), DetailPostActivity.class);
                intent.putExtra("id", post.getPostId());
                startActivity(intent);
            }
        });
        binding.postList.setAdapter(postAdapter);

        binding.addPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), PostActivity.class);
                startActivity(intent);
            }
        });

        return binding.getRoot();
    }
}
