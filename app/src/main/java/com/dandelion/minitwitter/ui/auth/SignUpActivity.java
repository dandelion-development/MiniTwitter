package com.dandelion.minitwitter.ui.auth;
// [Imports]
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dandelion.minitwitter.R;
import com.dandelion.minitwitter.common.CommonItems;
import com.dandelion.minitwitter.common.GlobalApp;
import com.dandelion.minitwitter.common.SharedPreferencesManager;
import com.dandelion.minitwitter.retrofit.MiniTwitterClient;
import com.dandelion.minitwitter.retrofit.MiniTwitterService;
import com.dandelion.minitwitter.retrofit.request.RequestSignup;
import com.dandelion.minitwitter.retrofit.response.ResponseAuth;
import com.dandelion.minitwitter.ui.DashboardActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// [Sign Up]: user sign up activity
public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {
    // [Vars]
    Button buttonSignup;
    TextView textViewGoToLogin;
    EditText editTextUser,editTextMail,editTextPassword;
    MiniTwitterClient miniTwitterClient;
    MiniTwitterService miniTwitterService;
    // [Methods]: overridable
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Hides Action Bar
        getSupportActionBar().hide();
        // Makes Retrofit instance for access to service
        retrofitInit();
        // Gets an instance of controls
        findViews();
        // Makes events for control actions
        makeEvents();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonSignup:
                goToSignUp();
                break;
            case R.id.textViewGoLogin:
                goToLogin();
                break;
        }
    }

    // [Methods]: custom methods

    private void goToSignUp() {
        // Gets Sign up parameters
        String strUSer = editTextUser.getText().toString();
        String strMail = editTextMail.getText().toString();
        String strPass = editTextPassword.getText().toString();
        // Verifies given parameters
        if (strUSer.isEmpty()) {
            String errUserValidation = getResources().getString(R.string.user_name_validation_failed_by_empty);
            editTextUser.setError(errUserValidation);
        } else if (strMail.isEmpty()) {
            String errMailValidation = getResources().getString(R.string.email_validation_failed_by_empty);
            editTextMail.setError(errMailValidation);
        } else if (strPass.isEmpty()) {
            String errPassValidation = getResources().getString(R.string.password_validation_failed_by_empty);
            editTextPassword.setError(errPassValidation);
        } else if (strPass.length() < 4) {
            String errPassValidation = getResources().getString(R.string.password_validation_failed_by_format);
            editTextPassword.setError(errPassValidation);
        } else {
            // Makes a request for sign up (by using before created POJOs)
            RequestSignup requestSignup = new RequestSignup(strUSer,strMail,strMail, CommonItems.API_UDEMY_CODE);
            Call<ResponseAuth> caller = miniTwitterService.doSignUp(requestSignup);
            caller.enqueue(new Callback<ResponseAuth>() {
                @Override
                public void onResponse(Call<ResponseAuth> call, Response<ResponseAuth> response) {
                    if (response.isSuccessful()) {
                        // Stores session data in persistent file (over SharedPreferences)
                        SharedPreferencesManager.saveStringValue(   // TOKEN
                                CommonItems.DATA_LABEL_TOKEN,
                                response.body().getToken());
                        SharedPreferencesManager.saveStringValue(   // USER NAME
                                CommonItems.DATA_LABEL_USER_NAME,
                                response.body().getUsername());
                        SharedPreferencesManager.saveStringValue(   // EMAIL
                                CommonItems.DATA_LABEL_EMAIL,
                                response.body().getEmail());
                        SharedPreferencesManager.saveStringValue(   // PHOTO URL
                                CommonItems.DATA_LABEL_PHOTO_URL,
                                response.body().getPhotoUrl());
                        SharedPreferencesManager.saveStringValue(   // CREATION DATE
                                CommonItems.DATA_LABEL_CREATED,
                                response.body().getCreated());
                        SharedPreferencesManager.saveBoolValue(   // ACTIVE
                                CommonItems.DATA_LABEL_ACTIVE,
                                response.body().getActive());

                        // On sing up success moves to dashboard
                        Intent goToDashboard = new Intent(SignUpActivity.this, DashboardActivity.class);
                        startActivity(goToDashboard);
                        finish();
                    } else {
                        // Registration has failed!!
                        String strRegistrationFailed = getResources().getString(R.string.registration_failed);
                        Toast.makeText(GlobalApp.getContext(),strRegistrationFailed,Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseAuth> call, Throwable t) {
                    String strRequestFailed = getResources().getString(R.string.request_callback_failure);
                    Toast.makeText(GlobalApp.getContext(), strRequestFailed, Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private void goToLogin() {
        Intent goToLogin = new Intent(SignUpActivity.this, MainActivity.class);
        startActivity(goToLogin);
        finish();
    }

    private void retrofitInit() {
        miniTwitterClient = MiniTwitterClient.getInstance();
        miniTwitterService = miniTwitterClient.getMiniTwitterService();
    }

    private void findViews() {
        buttonSignup = findViewById(R.id.buttonSignup);
        textViewGoToLogin = findViewById(R.id.textViewGoLogin);
        editTextUser = findViewById(R.id.editTextUser);
        editTextMail = findViewById(R.id.editTextMail);
        editTextPassword = findViewById(R.id.editTextPassword);
    }

    private void makeEvents() {
        buttonSignup.setOnClickListener(this);
        textViewGoToLogin.setOnClickListener(this);
    }
}