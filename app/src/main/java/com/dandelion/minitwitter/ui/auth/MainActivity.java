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
import com.dandelion.minitwitter.retrofit.request.RequestLogin;
import com.dandelion.minitwitter.retrofit.response.ResponseAuth;
import com.dandelion.minitwitter.ui.DashboardActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// [Login]: user login activity
public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    // [Vars]
    Button buttonLogin;
    TextView textViewSignup;
    EditText editTextMail, editTextPassword;
    MiniTwitterClient miniTwitterClient;
    MiniTwitterService miniTwitterService;

    // [Methods]: overridable
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Hides Action Bar
        getSupportActionBar().hide();

        // Makes Retrofit instance creation
        retrofitInit();

        // Prepares controls && events setup
        findViews();
        makeEvents();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonLogin:
                goToLogin();
                break;
            case R.id.textViewGoSignUp:
                goToSignup();
                break;
        }
    }

    // [Methods]: custom methods

    // Makes an instance of Retrofit (API management class)
    private void retrofitInit() {
        miniTwitterClient = MiniTwitterClient.getInstance();            // Singleton Pattern instance
        miniTwitterService = miniTwitterClient.getMiniTwitterService(); // API Service interface
    }

    // Moves to SignUp activity
    private void goToSignup() {
        Intent goToSingUp = new Intent(MainActivity.this, SignUpActivity.class);
        startActivity(goToSingUp);
        finishAffinity();
    }

    // Finds all views used in this activity
    private void findViews() {
        buttonLogin = findViewById(R.id.buttonLogin);
        textViewSignup = findViewById(R.id.textViewGoSignUp);
        editTextMail = findViewById(R.id.editTextMail);
        editTextPassword = findViewById(R.id.editTextPassword);

    }

    // Makes events used in this activity
    private void makeEvents() {
        buttonLogin.setOnClickListener(this);
        textViewSignup.setOnClickListener(this);
    }

    // Goes to login Activity
    private void goToLogin() {
        // Gets login parameters
        String strMail = editTextMail.getText().toString();
        String strPassword = editTextPassword.getText().toString();
        // Verifies login data
        if (strMail.isEmpty()) {
            String errMailValidation = getResources().getString(R.string.email_validation_failed_by_empty);
            editTextMail.setError(errMailValidation);
        } else if (strPassword.isEmpty()) {
            String errPassValidation = getResources().getString(R.string.password_validation_failed_by_empty);
            editTextPassword.setError(errPassValidation);
        } else {
            // Disables login button to prevent recalling
            buttonLogin.setEnabled(false);
            // Performs login request
            RequestLogin requestLogin = new RequestLogin(strMail,strPassword);      // Request object for login
            Call<ResponseAuth> caller = miniTwitterService.doLogin(requestLogin);   // Makes a call for login
            caller.enqueue(new Callback<ResponseAuth>() {                           // Performs Async call
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

                        // Session is granted so moves on next Activity
                        Intent modeToDashboard = new Intent(MainActivity.this, DashboardActivity.class);
                        startActivity(modeToDashboard);
                        // ** in order to avoid return to login Activity is destroyed
                        finish();
                    } else {
                        // Authentication has failed!!
                        String strAuthFailed = getResources().getString(R.string.login_failed);;
                        Toast.makeText(GlobalApp.getContext(),strAuthFailed,Toast.LENGTH_LONG).show();
                        // Enables login button
                        buttonLogin.setEnabled(true);
                    }
                }

                @Override
                public void onFailure(Call<ResponseAuth> call, Throwable t) {
                    // Request failed!!
                    String strRequestFailed = getResources().getString(R.string.connection_failure);
                    Toast.makeText(GlobalApp.getContext(), strRequestFailed, Toast.LENGTH_LONG).show();
                    // Enables login button
                    buttonLogin.setEnabled(true);
                }
            });
        }
    }
}