package com.example.fimae.viewmodels;

import android.net.Uri;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.fimae.activities.CreateProfileActivity;
import com.example.fimae.models.Fimaers;
import com.example.fimae.repository.FimaerRepository;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.Arrays;

public class CreateProfileViewModel extends ViewModel {
    public Fimaers user = new Fimaers();
    FimaerRepository repository = FimaerRepository.getInstance();
    private MutableLiveData<String> toastMessage = new MutableLiveData<>("");

    public LiveData<String> getMessage()
    {
        return toastMessage;
    }

    MutableLiveData<ArrayList<String>> chips = new MutableLiveData<>(new ArrayList<>(Arrays.asList(ChipData.tinhCach)));

    public CreateProfileViewModel(){
        Log.i("TAG", "CreateProfileViewModel: ");
        user.setUid(repository.getCurrentUserUid());
    }
    public void uploadAvatar(Uri uri, FimaerRepository.UploadAvatarCallback callback)
    {
        repository.uploadAvatar(user.getUid(), uri, callback);
    }
    public Task<Void> updateUser()
    {
        return repository.updateProfile(user);
    }
    public MutableLiveData<ArrayList<String>> getChip()
    {
        return chips;
    }

    public void setChip(int pos)
    {
        String[] list;
        switch (pos)
        {
            case 0:
                list = ChipData.tinhCach;
                break;
            case 1:
                list = ChipData.chomSao;
                break;
            case 2:
                list = ChipData.thuCung;
                break;
            case 3:
                list = ChipData.amNhac;
                break;
            case 4:
                list = ChipData.theThao;
                break;
            default:
                list = ChipData.tinhCach;

        }
        chips.setValue(new ArrayList<>(Arrays.asList(list)));

    }

    public boolean chipClick(String text, boolean b)
    {
        if(b)
        {
            if(user.getChip()== null) {
                user.setChip(new ArrayList<>());

            }
            if(user.getChip().size() < 9)
            {
                user.addChip(text);
            }else
            {
                b = false;
                toastMessage.setValue("Tối đa 9 tag");
            }
        }
        else
        {
            user.removeChip(text);
        }
        return b;
    }
}
