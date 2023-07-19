package com.example.fimae.viewmodels;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.fimae.BR;
import com.example.fimae.models.Fimaers;
import com.example.fimae.repository.FimaerRepository;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class ProfileViewModel  extends ViewModel {

    private MutableLiveData<Fimaers> user;

    private String uid;

    public MutableLiveData<Fimaers> getUser() {
        return user;
    }

    private FimaerRepository userRepo;


    public ProfileViewModel(){
        userRepo = FimaerRepository.getInstance();
        uid = userRepo.getCurrentUserUid();
        fetchUser();
    }

    @Nullable
    public String getUid() {
        return uid;
    }

    public void setUid(String value)
    {
        uid = value;
        fetchUser(value);
    }

    public MutableLiveData<Fimaers> fetchUser()
    {
        Log.i("PROFILEVM", "fetchUser: ");
        if(user == null)
        {
            user = userRepo.getCurrentUser();
        }
        return user;
    }
    public MutableLiveData<Fimaers> fetchUser(String uid)
    {
        Log.i("PROFILEVM", "fetchUser: ");
        userRepo.getFimaerById(uid).addOnCompleteListener(new OnCompleteListener<Fimaers>() {
                @Override
                public void onComplete(@NonNull Task<Fimaers> task) {
                    if(task.isSuccessful())
                    {
                        user.setValue(task.getResult());
                    }
                }
            });
        return user;
    }

    public void setName(String value)
    {
        Fimaers tset = user.getValue();
        tset.setName(value);
        user.setValue(tset);
    }


    public Task<Void> updateUser()
    {
        return userRepo.updateProfile(user.getValue());
    }
}