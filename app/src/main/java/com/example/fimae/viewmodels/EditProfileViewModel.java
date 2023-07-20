package com.example.fimae.viewmodels;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.fimae.models.Fimaers;
import com.example.fimae.repository.FimaerRepository;
import com.google.android.gms.tasks.Task;

import java.text.SimpleDateFormat;

public class EditProfileViewModel extends ViewModel {

    private MutableLiveData<Fimaers> user;

    public MutableLiveData<Fimaers> getUser() {
        return user;
    }

    private FimaerRepository userRepo;


    public EditProfileViewModel(){

        userRepo = FimaerRepository.getInstance();
        fetchUser();
    }

    public MutableLiveData<Fimaers> fetchUser()
    {
        if(user == null)
        {
            user = userRepo.getCurrentUser();
        }
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
