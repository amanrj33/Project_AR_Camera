package com.android.example.projectarcamera.viewmodel;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import java.util.concurrent.TimeUnit;

public class PhoneAuthViewModel extends ViewModel{

    //error message for phone number edit text
    public String errorMessage;

    //for observing phone number is valid or not
    public MutableLiveData<Boolean> isPhoneValid = new MutableLiveData<>();

    //for observing phone number is authenticating or not
    public MutableLiveData<Boolean> isAuthenticating = new MutableLiveData<>();

    //for observing phone number is authenticated or not
    public MutableLiveData<Boolean> isAuthenticated = new MutableLiveData<>();

    //for observing code is auto retrieved or not
    public MutableLiveData<String> autoCode = new MutableLiveData<>();

    private String verificationCodeBySystem;
    @SuppressLint("StaticFieldLeak")
    public Context context;

    //authenticate number and send otp
    public void authenticateNumber(String phoneNumber, Activity activity){
        isAuthenticating.setValue(true);
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(FirebaseAuth.getInstance())
                        .setPhoneNumber(phoneNumber)       // Phone number to verify
                        .setTimeout(30L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(activity)                // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    //OnVerificationStateChangedCallbacks
    private final PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onCodeAutoRetrievalTimeOut(@NonNull String s) {
            super.onCodeAutoRetrievalTimeOut(s);
            autoCode.setValue(null);
        }

        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verificationCodeBySystem = s;
        }

        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            if (code != null) {
               autoCode.setValue(code);
               verifyCode(code);
            }
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            isAuthenticating.setValue(false);
            Toast.makeText(context, "Verification failed", Toast.LENGTH_LONG).show();
        }
    };

    //validate and verify code
    public void verifyCode(String code) {
        if (code.length() != 6) {
            Toast.makeText(context, "Wrong OTP...", Toast.LENGTH_SHORT).show();
        } else {
            isAuthenticating.setValue(true);
            if (verificationCodeBySystem != null) {
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationCodeBySystem, code);
                signInByCredential(credential);
            }
        }
    }

    //sign in using the PhoneAuthCredential
    private void signInByCredential(PhoneAuthCredential credential) {

        final FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.signInWithCredential(credential)
                .addOnCompleteListener((Activity) context, (OnCompleteListener<AuthResult>) task -> {
                    if (task.isSuccessful()) {
                        //if successfully signed in
                        isAuthenticated.setValue(true);
                        Toast.makeText(context, "Successfully Authenticated", Toast.LENGTH_SHORT).show();
                    } else {
                        //otherwise
                        isAuthenticated.setValue(false);
                        Toast.makeText(context, "Wrong OTP...", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    //check whether the phone number is valid or not
    public void isValid(String phoneNumber) {
        if (phoneNumber.isEmpty()) {
            errorMessage = "This field cannot be empty";
            isPhoneValid.setValue(false);
        } else if (phoneNumber.length() != 10) {
            errorMessage = "Phone number is incorrect";
            isPhoneValid.setValue(false);
        } else {
            errorMessage = null;
            isPhoneValid.setValue(true);
        }
    }
}

