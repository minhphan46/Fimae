package com.example.fimae.viewmodels;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.fimae.models.Fimaers;
import com.example.fimae.repository.FimaerRepository;
import com.google.android.gms.tasks.Task;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;

public class EditProfileViewModel extends ViewModel {

    private MutableLiveData<Fimaers> user;

    public MutableLiveData<Fimaers> getUser() {
        return user;
    }

    private MutableLiveData<String> toastMessage = new MutableLiveData<>("");

    public LiveData<String> getMessage()
    {
        return toastMessage;
    }

    private FimaerRepository userRepo;

    public Boolean hasChange = false;

    MutableLiveData<ArrayList<String>> chips = new MutableLiveData<>(new ArrayList<>(Arrays.asList(ChipData.tinhCach)));


    public EditProfileViewModel(){

        userRepo = FimaerRepository.getInstance();
        fetchUser();
    }

    public MutableLiveData<Fimaers> fetchUser()
    {
        user = userRepo.getCurrentUser();
        return user;
    }

    public void setName(String value)
    {
        Fimaers tset = user.getValue();
        tset.setName(value);
        user.setValue(tset);
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
            if(user.getValue().getChip()== null) {
                user.getValue().setChip(new ArrayList<>());

            }
            if(user.getValue().getChip().size() < 9)
            {
                user.getValue().addChip(text);
            }else
            {
                b = false;
                toastMessage.setValue("Tối đa 9 tag");
            }
        }
        else
        {
            user.getValue().removeChip(text);
        }
        return b;
    }

    public Task<Void> updateUser()
    {
        return userRepo.updateProfile(user.getValue());
    }


}
