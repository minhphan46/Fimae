package com.example.fimae.repository;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.fimae.models.Fimaers;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ConnectRepo {

    private static ConnectRepo INSTANCE = new ConnectRepo();

    // other instance variables can be here

    private ConnectRepo() {
    };

    public static ConnectRepo getInstance() {
        return(INSTANCE);
    }

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    Fimaers userLocal;
    Fimaers userRemote;

    public ArrayList<Fimaers> listUsersOnline = new ArrayList<>();

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
                    Fimaers user = dataSnapshot.getValue(Fimaers.class);
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
            if(!getFirstUserOnl().getUid().equals(userLocal.getUid())){
                userRemote = getFirstUserOnl();
                deleteUserOnl(getUserRemote(), tableName);
            }
        } else {
            if(userLocal != null)
                addUserOnl(userLocal, tableName);
        }
    }

    public void addUserOnl(Fimaers user, String tableName) {
        String id = "user" + user.getUid();
        databaseReference.child(tableName).child(id).setValue(user);
        listUsersOnline.add(user);
    }

    public void deleteUserOnl(Fimaers user, String tableName) {
        String id = "user" + user.getUid();
        // delete user in list
        databaseReference.child(tableName).child(id).removeValue();
        listUsersOnline.remove(user);
    }

    public Fimaers getFirstUserOnl() {
        return listUsersOnline.get(0);
    }

    public Fimaers getUserLocal() {
        return userLocal;
    }

    public void setUserLocal(Fimaers userLocal) {
        this.userLocal = userLocal;
    }

    public Fimaers getUserRemote() {
        return userRemote;
    }

    public void setUserRemote(Fimaers userRemote) {
        this.userRemote = userRemote;
    }

    public void setUserRemoteById(String id) {
        List<Fimaers> users = Fimaers.dummy;
        for(Fimaers user : users){
            if(user.getUid().equals(id)){
                setUserRemote(user);
                return;
            }
        }
    }
}
