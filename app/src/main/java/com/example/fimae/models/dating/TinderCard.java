package com.example.fimae.models.dating;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.fimae.R;
import com.example.fimae.repository.DatingRepository;
import com.mindorks.placeholderview.SwipePlaceHolderView;
import com.mindorks.placeholderview.annotations.Layout;
import com.mindorks.placeholderview.annotations.Resolve;
import com.mindorks.placeholderview.annotations.View;
import com.mindorks.placeholderview.annotations.swipe.SwipeCancelState;
import com.mindorks.placeholderview.annotations.swipe.SwipeIn;
import com.mindorks.placeholderview.annotations.swipe.SwipeInState;
import com.mindorks.placeholderview.annotations.swipe.SwipeOut;
import com.mindorks.placeholderview.annotations.swipe.SwipeOutState;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

@Layout(R.layout.adapter_tinder_card)
public class TinderCard {

    @View(R.id.profileImageView)
     ImageView profileImageView;

    @View(R.id.nameAgeTxt)
     TextView nameAgeTxt;

    @View(R.id.locationNameTxt)
     TextView locationNameTxt;

    private DatingProfile mProfile;

    private  DatingProfile currentProfile;
    private Context mContext;
    private SwipePlaceHolderView mSwipeView;

    public TinderCard(Context context, DatingProfile profile, SwipePlaceHolderView swipeView, DatingProfile currentProfile) {
        mContext = context;
        mProfile = profile;
        mSwipeView = swipeView;
        this.currentProfile = currentProfile;
    }

    @Resolve
    public void onResolved(){
        Glide.with(mContext).load(mProfile.getImages().get(0)).into(profileImageView);
        nameAgeTxt.setText(mProfile.getName() + ", " + mProfile.getAge());
        try {
            Geocoder geocoder = new Geocoder(mContext);
            ArrayList<Address> addresses = null;
            LatLng latLng = mProfile.getLocation();
            addresses = (ArrayList<Address>) geocoder.getFromLocation(latLng.getLatitude(), latLng.getLongitude(), 1);

            if (!addresses.isEmpty())
            {
                String distance = "";
                if(mProfile.getDistanceFromYou() >=1000)
                {
                    double distanceInKm = (mProfile.getDistanceFromYou()/1000);
                    //Set subtile is City name
                    DecimalFormat decimalFormat = new DecimalFormat("#.00");
                    distance = decimalFormat.format(distanceInKm) + "Km";
                }
                else
                {
                    distance = String.valueOf(mProfile.getDistanceFromYou()) + "m";
                }

                locationNameTxt.setText(addresses.get(0).getLocality() + " " + distance);
            }
            else {
                locationNameTxt.setText("Không xác định được vị trí");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @SwipeOut
    public void onSwipedOut(){
        Log.d("EVENT", "onSwipedOut");
        currentProfile.addUserDislike(mProfile.getUid());
        DatingRepository.getInstance().updateUserDislike(currentProfile.getDislikedUsers());
        mSwipeView.addView(this);
    }

    @SwipeCancelState
    public void onSwipeCancelState(){
        Log.d("EVENT", "onSwipeCancelState");
    }

    @SwipeIn
    public void onSwipeIn(){
        currentProfile.addUserLike(mProfile.getUid());
        DatingRepository.getInstance().updateUserLike(currentProfile.getLikedUsers());
        Log.d("EVENT", "onSwipedIn");
    }

    @SwipeInState
    public void onSwipeInState(){
        Log.d("EVENT", "onSwipeInState");
    }

    @SwipeOutState
    public void onSwipeOutState(){
        Log.d("EVENT", "onSwipeOutState");
    }
}