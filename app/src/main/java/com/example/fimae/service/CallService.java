package com.example.fimae.service;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.example.fimae.CallOnChatActivity;
import com.example.fimae.activities.CallActivity;
import com.example.fimae.activities.CallVideoActivity;
import com.example.fimae.activities.WaitingActivity;
import com.example.fimae.repository.AuthRepository;
import com.example.fimae.repository.CommentRepository;
import com.example.fimae.repository.ConnectRepo;
import com.example.fimae.repository.FimaerRepository;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.functions.FirebaseFunctionsException;
import com.stringee.StringeeClient;
import com.stringee.call.StringeeCall;
import com.stringee.call.StringeeCall2;
import com.stringee.exception.StringeeError;
import com.stringee.listener.StringeeConnectionListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CallService {
    public static final String NORMAL = "normal";
    public static final String RANDOM = "random";
    private static CallService callService;
    public static CallService getInstance(){
        if(callService == null) callService = new CallService();
        return callService;
    }

    public StringeeClient client;

    public Map<String, StringeeCall> callMap = new HashMap<>();

    public Map<String, StringeeCall2> call2Map = new HashMap<>();

    public interface CallClientListener {
        void onStatusChange(String status);

        void onIncomingCallVoice(String typeCall, String callId);

        void onIncomingCallVideo(String typeCall, String callId);
    }
    ArrayList<CallClientListener> listeners = new ArrayList<>();
     public void addListener(CallClientListener listener){
         listeners.add(listener);
     }

    public void removeListener(CallClientListener listener){
        listeners.remove(listener);
    }
    public void initStringeeConnection(Context context, FragmentActivity activity){
        client = new StringeeClient(context);
        client.setConnectionListener(new StringeeConnectionListener() {
            @Override
            public void onConnectionConnected(StringeeClient stringeeClient, boolean b) {
                activity.runOnUiThread(()->{
                    for (CallClientListener listener : listeners) {
                        listener.onStatusChange("Đã kết nối");
                    }
                });
            }

            @Override
            public void onConnectionDisconnected(StringeeClient stringeeClient, boolean b) {
                activity.runOnUiThread(()->{
                    for (CallClientListener listener : listeners) {
                        listener.onStatusChange("Mất kết nối");
                    }
                });
            }

            @Override
            public void onIncomingCall(StringeeCall stringeeCall) {
                activity.runOnUiThread(()->{
                    callMap.put(stringeeCall.getCallId(), stringeeCall);
                    // set user remote
                    //ConnectRepo.getInstance().setUserRemoteById(stringeeCall.getFrom());
                    FimaerRepository.getInstance().getFimaerById(stringeeCall.getFrom()).addOnCompleteListener(task -> {
                        if(task.isSuccessful())
                        {
                            ConnectRepo.getInstance().setUserRemote(task.getResult());
                            // navigate to call screen
                            String type = stringeeCall.getCustomDataFromYourServer();
                            for (CallClientListener listener : listeners) {
                                listener.onIncomingCallVoice(type, stringeeCall.getCallId());
                            }
                        }
                    });
                });
            }

            @Override
            public void onIncomingCall2(StringeeCall2 stringeeCall2) {
                activity.runOnUiThread(()->{
                    call2Map.put(stringeeCall2.getCallId(), stringeeCall2);
                    // set user remote
                    ConnectRepo.getInstance().setUserRemoteById(stringeeCall2.getFrom());
                    // navigate to call video screen
                    String type = stringeeCall2.getCustomDataFromYourServer();
                    for (CallClientListener listener : listeners) {
                        listener.onIncomingCallVideo(type, stringeeCall2.getCallId());
                    }
                });
            }

            @Override
            public void onConnectionError(StringeeClient stringeeClient, StringeeError stringeeError) {
                activity.runOnUiThread(()->{
                    Log.e("TAG", "onConnectionError: " + stringeeError.getCode() + stringeeError.getMessage());
                    if(stringeeError.getCode() == 2 || stringeeError.getCode() == 6  || stringeeError.getCode() == 10)
                    {
                        Log.e("TOKEN", "start geting: ");
                        for (CallClientListener listener : listeners) {
                            listener.onStatusChange("Kết nối bị lỗi: " + stringeeError.getMessage());
                        }
                        FimaerRepository.getInstance().refreshToken().addOnCompleteListener(new OnCompleteListener<String>() {
                            @Override
                            public void onComplete(@NonNull Task<String> task) {
                                if(task.isSuccessful())
                                {
                                    Log.e("TOKEN", "onComplete: " + task.getResult());
                                    stringeeClient.connect(task.getResult());
                                }
                                else
                                {
                                    Exception e = task.getException();
                                    if (e instanceof FirebaseFunctionsException) {
                                        FirebaseFunctionsException ffe = (FirebaseFunctionsException) e;
                                        FirebaseFunctionsException.Code code = ffe.getCode();
                                        Object details = ffe.getDetails();
                                        Log.e("TOKEN", "onComplete: " + details.toString() );
                                    }
                                }
                            }
                        });
                    }
                });
            }

            @Override
            public void onRequestNewToken(StringeeClient stringeeClient) {

            }

            @Override
            public void onCustomMessage(String s, JSONObject jsonObject) {

            }

            @Override
            public void onTopicMessage(String s, JSONObject jsonObject) {

            }
        });
        FimaerRepository.getInstance().getFimaerById(FirebaseAuth.getInstance().getUid()).addOnCompleteListener(task -> {
            if(task.isSuccessful())
            {
                String token = task.getResult().getToken();
                client.connect(token);
            }
        });
    }
}
