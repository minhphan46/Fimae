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

    private FimaerRepository userRepo;

    public Boolean hasChange = false;

    String[] tinhCach = {
            "Hài hước",
            "Ga lăng",
            "Dễ gần",
            "Dễ thương",
            "Lãng mạn",
            "Truyền thống",
            "Gen Z",
            "Thể thao",
            "Đeo bám",
            "Cú đêm",
            "Cô đơn",
            "Ngầu",
            "Xấu hổ",
            "Yên tĩnh",
            "Thích ở nhà",
            "Hoạt ngôn",
            "Nhạt nhẽo"
    };
    String[] chomSao = {
            "Bạch Dương",
            "Kim Ngưu",
            "Song Tử",
            "Cự Giải",
            "Sư Tử",
            "Xử Nữ",
            "Thiên Bình",
            "Bọ Cạp",
            "Nhân Mã",
            "Ma Kết",
            "Bảo Bình",
            "Song Ngư"
    };
    String[] thuCung = {
            "Chó",
            "Mèo",
            "Hamster",
            "Thỏ",
            "Cá",
            "Chim",
            "Rùa",
            "Nhím",
            "Bò sát",
            "Khỉ",
            "Côn trùng",
    };
    String[] amNhac = {
            "Guitar",
            "K-pop",
            "J-pop",
            "Nhạc pop",
            "Nhạc rock",
            "Nhạc đồng quê",
            "Nhạc jazz",
            "Nhạc không lời",
            "R&B",
            "Rap",
            "Metal",
            "Nhạc cổ điển",
            "Nhạc điện tử",
    };
    String[] theThao = {
            "Bóng đá",
            "Bóng rổ",
            "Cầu lông",
            "Bơi lội",
            "Bắn cung",
            "Bóng chuyền",
            "Điền kinh",
            "Đua xe",
            "Tenis",
            "Bóng bàn",
            "Muay Thái",
            "Karate",
            "Thể dục dụng cụ",
            "Leo núi",
            "Thể hình",
            "Võ thuật",
            "Trượt băng",
            "Nhảy cao",
            "Cờ vua",
            "Cờ tướng",
            "Taekwondo",
            "Bi-a",
            "Đấu vật",
            "Bóng ném",
            "Quần vợt"
    };
    MutableLiveData<ArrayList<String>> chips = new MutableLiveData<>(new ArrayList<>(Arrays.asList(tinhCach)));


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
                list = tinhCach;
                break;
            case 1:
                list = chomSao;
                break;
            case 2:
                list =thuCung;
                break;
            case 3:
                list = amNhac;
                break;
            case 4:
                list = theThao;
                break;
            default:
                list = tinhCach;

        }
        chips.setValue(new ArrayList<>(Arrays.asList(list)));

    }

    public void chipClick(String text, boolean b)
    {
        if(b)
        {
            user.getValue().addChip(text);
            Log.i("TAG", "chipClick: " + text);
        }
        else
        {
            user.getValue().removeChip(text);
            Log.i("TAG", "chipUncheck: "+ text );
        }
    }

    public Task<Void> updateUser()
    {
        return userRepo.updateProfile(user.getValue());
    }


}
