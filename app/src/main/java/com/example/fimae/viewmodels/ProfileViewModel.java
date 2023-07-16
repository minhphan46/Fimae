package com.example.fimae.viewmodels;

import android.util.Log;

import androidx.annotation.NonNull;
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

public class ProfileViewModel extends ViewModel {

    private MutableLiveData<Fimaers> user = new MutableLiveData<>();

    private MutableLiveData<String> toastMessage = new MutableLiveData<>();
    private FimaerRepository userRepo;

    public MutableLiveData<String> getToastMessage() {
        return toastMessage;
    }

    public void setToastMessage(String value)
    {
        toastMessage.setValue(value);
    }

    public ProfileViewModel(){
        userRepo = FimaerRepository.getInstance();
    }

    public MutableLiveData<Fimaers> getUser()
    {
        if(user.getValue() == null)
        {
            userRepo.getCurrentUser(new FimaerRepository.GetUserCallback() {
                @Override
                public void onGetUserSuccess(Fimaers currentUser) {
                    user.setValue(currentUser);
                    Log.i("USER", "onGetUserSuccess: " + user.getValue().getName());
                }

                @Override
                public void onGetUserError(String errorMessage) {
                    Log.e("USER", "onGetUserError: " + errorMessage );
                    setToastMessage(errorMessage);
                }
            });
        }
        return user;
    }
}
