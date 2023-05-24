package com.example.fimae.repository;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.fimae.models.FimaeUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ConnectRepo {

    private static ConnectRepo INSTANCE = new ConnectRepo();

    // other instance variables can be here

    private ConnectRepo() {
    };

    public static ConnectRepo getInstance() {
        return(INSTANCE);
    }

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();;

    FimaeUser userLocal;
    FimaeUser userRemote;

    public ArrayList<FimaeUser> listUsersOnline = new ArrayList<>();

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
                    FimaeUser user = dataSnapshot.getValue(FimaeUser.class);
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
                deleteUserOnl(getUserRemote(), tableName);
            }
        } else {
            if(userLocal != null)
                addUserOnl(userLocal, tableName);
        }
    }

    public void addUserOnl(FimaeUser user, String tableName) {
        String id = "user" + user.getId();
        databaseReference.child(tableName).child(id).setValue(user);
        listUsersOnline.add(user);
    }

    public void deleteUserOnl(FimaeUser user, String tableName) {
        String id = "user" + user.getId();
        // delete user in list
        databaseReference.child(tableName).child(id).removeValue();
        listUsersOnline.remove(user);
    }

    public FimaeUser getFirstUserOnl() {
        return listUsersOnline.get(0);
    }

    public FimaeUser getUserLocal() {
        return userLocal;
    }

    public void setUserLocal(FimaeUser userLocal) {
        this.userLocal = userLocal;
    }

    public FimaeUser getUserRemote() {
        return userRemote;
    }

    public void setUserRemote(FimaeUser userRemote) {
        this.userRemote = userRemote;
    }

    public void setUserRemoteByName(String name) {
        List<FimaeUser> users = Arrays.asList(FimaeUser.dummy);
        for(FimaeUser user : users){
            if(user.getName().equals(name)){
                setUserRemote(user);
                return;
            }
        }
    }
}
