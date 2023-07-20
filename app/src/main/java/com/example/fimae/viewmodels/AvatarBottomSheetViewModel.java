package com.example.fimae.viewmodels;

import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.fimae.repository.FimaerRepository;

public class AvatarBottomSheetViewModel extends ViewModel {

    FimaerRepository userRepo = FimaerRepository.getInstance();
    MutableLiveData<String> imageUrl = new MutableLiveData<>("");
    MutableLiveData<Boolean> isChoose = new MutableLiveData<Boolean>(false);

    public void updateAvatar(Uri imageUri, FimaerRepository.UploadAvatarCallback callback)
    {
        userRepo.uploadAvatar(userRepo.getCurrentUser().getValue().getUid(),imageUri,callback);
    }

    public LiveData<String> getImageUrl()
    {
        return imageUrl;
    }
    public void setImageUrl(String url)
    {
        imageUrl.setValue(url);
    }
    public LiveData<Boolean> getisChoose()
    {
        return isChoose;
    }
    public void setisChoose(Boolean value)
    {
        isChoose.setValue(value);
    }
}
