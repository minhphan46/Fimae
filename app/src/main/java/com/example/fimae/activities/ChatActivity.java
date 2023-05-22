package com.example.fimae.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.example.fimae.R;
import com.example.fimae.adapters.UserAdapter;
import com.example.fimae.databinding.ActivityChatBinding;
import com.example.fimae.models.UserInfo;

import java.util.Arrays;

public class ChatActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityChatBinding binding;
    private String[] items = {"Item 1", "Item 2", "Item 3", "Item 4", "Item 5"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        UserAdapter adapter = new UserAdapter(this,
                R.layout.user_chat, Arrays.asList(UserInfo.dummy));
        binding.listView.setAdapter(adapter);
        binding.listView.setOnItemClickListener((parent, view, position, id) -> {

        });
    }

}
