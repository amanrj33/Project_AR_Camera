package com.android.example.projectarcamera.viewmodel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class GalleryViewModel extends ViewModel {

    //for observing the list of image urls
    public MutableLiveData<ArrayList<String>> images = new MutableLiveData<>();

    //for observing the error in connectivity
    public MutableLiveData<Boolean> errorNetwork = new MutableLiveData<>();

    //for observing the loader status
    public MutableLiveData<Boolean> showLoader = new MutableLiveData<>();

    //add the image url to the current list
    public void addImage(String image) {
        ArrayList<String> temp = new ArrayList<>(images.getValue());
        temp.add(image);
        images.setValue(temp);
    }

    //set error to be true or false
    public void setErrorNetwork(Boolean b) {
        errorNetwork.setValue(b);
    }

    //read all the image urls from the firebase realtime database
    public void readImages() {
        showLoader.setValue(true);
        images.setValue(new ArrayList<>());

        //instance of firebase database and reference
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference().child("image_url");

        //child event listener to inform on child manipulation
        ChildEventListener childEventListener = new ChildEventListener() {

            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                //add the current image url through the method
                addImage(snapshot.getValue(String.class));
                if (showLoader.getValue())
                    showLoader.setValue(false);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        };
        databaseReference.addChildEventListener(childEventListener);
    }
}