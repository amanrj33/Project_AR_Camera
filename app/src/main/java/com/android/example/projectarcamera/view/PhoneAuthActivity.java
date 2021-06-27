package com.android.example.projectarcamera.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import com.android.example.projectarcamera.R;
import com.android.example.projectarcamera.databinding.ActivityPhoneAuthBinding;
import com.android.example.projectarcamera.viewmodel.PhoneAuthViewModel;
import com.google.android.material.textfield.TextInputLayout;
import com.hbb20.CountryCodePicker;

public class PhoneAuthActivity extends AppCompatActivity {

    private TextInputLayout phoneNumberLayout;
    private EditText phoneNumberEditText;
    private CountryCodePicker picker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //binding instance for activity phone auth
        ActivityPhoneAuthBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_phone_auth);

        //view model instance
        PhoneAuthViewModel viewModel = new ViewModelProvider(this).get(PhoneAuthViewModel.class);
        viewModel.context = this;

        //hooking variables and views
        phoneNumberLayout = binding.phoneNo;
        picker = binding.phonePicker;
        phoneNumberEditText = phoneNumberLayout.getEditText();

        //text change listener for edit text
        phoneNumberEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!phoneNumberEditText.getText().toString().trim().isEmpty()) {
                    phoneNumberLayout.setError(null);
                    phoneNumberLayout.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        //on click listener for authenticate button
        binding.authenticateButton.setOnClickListener(view -> {
            binding.authProgressBar.setVisibility(View.VISIBLE);
            String phoneNumber = phoneNumberEditText.getText().toString().trim();

            //validate phone number
            viewModel.isValid(phoneNumber);
        });

        //on click listener for verify button
        binding.verifyBtn.setOnClickListener(view -> {

            //verify the code entered by the user
            viewModel.verifyCode(binding.otpEditText.getText().toString().trim());
        });

        //observer for phone validation
        viewModel.isPhoneValid.observe(this, aBoolean -> {
            binding.authProgressBar.setVisibility(View.GONE);
            if (aBoolean){
                //if phone is valid
                phoneNumberLayout.setError(null);
                phoneNumberLayout.setErrorEnabled(false);

                //get the number with country code
                final String numberWithCode = "+" + picker.getFullNumberWithPlus() + phoneNumberEditText.getText().toString().trim();
                binding.otpTextView.setText(getString(R.string.otp_phone_text, numberWithCode));

                //authenticate number
                viewModel.authenticateNumber(numberWithCode, this);
            } else {
                //if phone is not valid
                phoneNumberLayout.setError(viewModel.errorMessage);
            }
        });

        //observer for is phone number is authenticating or not
        viewModel.isAuthenticating.observe(this, isAuthenticating ->{
            if (isAuthenticating) {
                binding.typeAuthenticate.setVisibility(View.GONE);
                binding.typeVerify.setVisibility(View.VISIBLE);
                binding.verifyBtn.setEnabled(false);
            } else {
                binding.typeAuthenticate.setVisibility(View.VISIBLE);
                binding.typeVerify.setVisibility(View.GONE);
            }
        });

        //observer for auto code retrieval
        viewModel.autoCode.observe(this, code ->{
            if(code != null){
                binding.verifyProgressBar.setVisibility(View.GONE);
                binding.otpEditText.setText(code);
            } else {
                binding.verifyProgressBar.setVisibility(View.GONE);
                binding.verifyBtn.setEnabled(true);
            }
        });

        //observer for phone number authenticated
        viewModel.isAuthenticated.observe(this, isAuthenticated -> {
            if (isAuthenticated) {
                startActivity(new Intent(this, CameraActivity.class));
                finish();
            }
            else {
                binding.verifyProgressBar.setVisibility(View.GONE);
                binding.verifyBtn.setEnabled(true);
            }
        });

    }
}