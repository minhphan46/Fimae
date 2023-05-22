package com.example.fimae.repository;

import android.annotation.SuppressLint;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.example.fimae.models.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.stringee.messaging.User;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class ConnectRepo {

    private static ConnectRepo INSTANCE = new ConnectRepo();

    // other instance variables can be here

    private ConnectRepo() {
    };

    public static ConnectRepo getInstance() {
        return(INSTANCE);
    }

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();;

    UserInfo userLocal;
    UserInfo userRemote;

    public ArrayList<UserInfo> listUsersOnline = new ArrayList<>();

    public static String table_chat_name = "USERS_CHAT_ONLINE";
    public static String table_call_voice_name = "USERS_CALL_ONLINE";
    public static String table_call_video_name = "USERS_VIDEO_ONLINE";

    public void fetchData(String tableName) {
        databaseReference.child(tableName).addValueEventListener(
        new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!listUsersOnline.isEmpty()) listUsersOnline.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    UserInfo user = dataSnapshot.getValue(UserInfo.class);
                    if(user != null) {
                        Log.d("TAG", user.toString());
                        listUsersOnline.add(user);
                    }
                    else break;
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
    public void checkUser(String tableName) {
        // set local user
        // when click button call
        // fetch all user in list
        // if return null => wait to connect
        // if has datas
        // get first user
        // delete user in database
        // return this user
        if(!listUsersOnline.isEmpty()){
            // if user remote != userLocal
            if(!getFirstUserOnl().getId().equals(userLocal.getId())){
                userRemote = getFirstUserOnl();
            }
        } else {
            if(userLocal != null)
                addUserOnl(userLocal, tableName);
        }
    }

    public void addUserOnl(UserInfo user, String tableName) {
        String id = "user" + user.getId();
        databaseReference.child(tableName).child(id).setValue(user);
        listUsersOnline.add(user);
    }

    public void deleteUserOnl(UserInfo user, String tableName) {
        String id = "user" + user.getId();
        // delete user in list
        databaseReference.child(tableName).child(id).removeValue();
        listUsersOnline.remove(user);
    }

    public UserInfo getFirstUserOnl() {
        return listUsersOnline.get(0);
    }

    public UserInfo getUserLocal() {
        return userLocal;
    }

    public void setUserLocal(UserInfo userLocal) {
        this.userLocal = userLocal;
    }

    public UserInfo getUserRemote() {
        return userRemote;
    }

    public void setUserRemote(UserInfo userRemote) {
        this.userRemote = userRemote;
    }
}
