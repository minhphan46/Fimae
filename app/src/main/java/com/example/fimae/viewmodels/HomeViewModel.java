package com.example.fimae.viewmodels;

import androidx.annotation.NonNull;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.ObservableField;

import com.example.fimae.BR;
import com.stringee.StringeeClient;
import com.stringee.call.StringeeCall;
import com.stringee.call.StringeeCall2;
import com.stringee.exception.StringeeError;
import com.stringee.listener.StringeeConnectionListener;

import org.json.JSONObject;


public class HomeViewModel extends BaseObservable {

    private String address;
    public ObservableField<String> status = new ObservableField<>();



    public HomeViewModel(@NonNull android.content.Context context){
        //initStringeeConnection(context);
    }

    @Bindable
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
        notifyPropertyChanged(BR.address);
    }

    public void onClickCall(){
        // when click call
        status.set("Connecting...");
    }

}
