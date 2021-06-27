package com.android.example.projectarcamera.viewmodel;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.Toast;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import static com.android.example.projectarcamera.NetworkConnected.isConnected;

public class CameraViewModel extends ViewModel {

    //for observing if image to be captured
    public MutableLiveData<Boolean> imageCapture = new MutableLiveData<>();

    //for observing if navigation to gallery to be done
    public MutableLiveData<Boolean> isGalleryCLicked = new MutableLiveData<>();

    ////for observing if image to be uploaded on firebase
    public MutableLiveData<Boolean> isUploadCLicked = new MutableLiveData<>();

    //for observing the status of the upload
    public MutableLiveData<Boolean> isSuccessfullyUploaded = new MutableLiveData<>();

    //called when capture image is clicked
    public void captureImage() {
        imageCapture.setValue(true);
    }

    //called if capture is done or failed
    public void captureDoneOrFailed() {
        imageCapture.setValue(false);
        uploadDoneOrFailed();
    }

    //called if gallery is clicked
    public void galleryClicked() {
        isGalleryCLicked.setValue(true);
    }

    //called when navigation to gallery is done
    public void doneGalleryNavigation() {
        isGalleryCLicked.setValue(false);
    }

    //called when upload image button is clicked
    public void uploadImage() {
        isUploadCLicked.setValue(true);
    }

    //called if upload is done or failed
    public void uploadDoneOrFailed() {
        isUploadCLicked.setValue(false);
    }

    //called to upload the bitmap to the firebase
    public void uploadImageBitmap(Bitmap bitmap, Context context) {

        //if network is connected
        if (isConnected(context)) {

            //get the instance of firebase database and storage along with the required references
            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            DatabaseReference databaseReference = firebaseDatabase.getReference().child("image_url");

            FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
            String date = new SimpleDateFormat("yyyyMMddHHmmss", java.util.Locale.getDefault()).format(new Date());
            StorageReference storageReference = firebaseStorage.getReference().child("images").child(date);

            // Get the data from an ImageView as bytes
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] data = stream.toByteArray();

            //upload task to upload the image and monitor the successful or unsuccessful upload
            UploadTask uploadTask = storageReference.putBytes(data);
            uploadTask
                    .addOnFailureListener(exception -> Toast.makeText(context, "Upload Failed", Toast.LENGTH_SHORT).show())
                    .addOnSuccessListener(taskSnapshot -> {
                        storageReference.getDownloadUrl().addOnSuccessListener((uri) ->
                                databaseReference.push().setValue(uri.toString()));
                        isSuccessfullyUploaded.setValue(true);
                    });
        } else
            isSuccessfullyUploaded.setValue(false);
    }
}