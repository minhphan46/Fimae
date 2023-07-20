package com.example.fimae.repository;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.fimae.models.Fimaers;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConnectRepo {

    private static ConnectRepo INSTANCE = new ConnectRepo();

    // other instance variables can be here

    private ConnectRepo() {
    };

    public static ConnectRepo getInstance() {
        return(INSTANCE);
    }
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    Fimaers userLocal;
    Fimaers userRemote;

    public ArrayList<Fimaers> listUsersOnline = new ArrayList<>();

    public  static  String user_table = "fimaers";
    public static String table_chat_name = "chats";
    public static String table_call_voice_name = "calls";
    public static String table_call_video_name = "videos";



    public void addUserOnl(Fimaers user, String tableName) {
        firestore.collection(tableName.concat("_queue")).document(user.getUid()).set(user.toJson());
    }

    public void deleteUserOnl(Fimaers user, String tableName) {
       firestore.collection(tableName.concat("_queue")).document(user.getUid()).delete();
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

        DocumentReference docRef = firestore.collection(user_table).document(id);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful())
                {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if(documentSnapshot.exists())
                    {
                        Log.e("ConnectRepo", "No document");

                        Fimaers user = documentSnapshot.toObject(Fimaers.class);
                        setUserRemote(user);
                    }
                    else
                    {
                        Log.e("ConnectRepo", "No such document");
                    }
                }else {
                    Log.e("ConnectRepo", "get failed with ", task.getException());

                }
            }
        });
//        List<Fimaers> users = Fimaers.dummy;
//        for(Fimaers user : users){
//            if(user.getUid().equals(id)){
//                setUserRemote(user);
//                return;
//            }
//        }
    }
}
