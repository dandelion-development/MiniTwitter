package com.dandelion.minitwitter.data;
// [Imports]

import android.widget.Filter;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.dandelion.minitwitter.R;
import com.dandelion.minitwitter.common.CommonItems;
import com.dandelion.minitwitter.common.GlobalApp;
import com.dandelion.minitwitter.common.SharedPreferencesManager;
import com.dandelion.minitwitter.retrofit.AuthTwitterClient;
import com.dandelion.minitwitter.retrofit.AuthTwitterService;
import com.dandelion.minitwitter.retrofit.request.RequestCreateTweet;
import com.dandelion.minitwitter.retrofit.request.RequestUserProfile;
import com.dandelion.minitwitter.retrofit.response.Like;
import com.dandelion.minitwitter.retrofit.response.Tweet;
import com.dandelion.minitwitter.retrofit.response.TweetDeleted;
import com.dandelion.minitwitter.retrofit.response.UploadPhoto;
import com.dandelion.minitwitter.retrofit.response.UserProfile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// [Tweet Repository]: Tweets data provider

/**
 * Tweet Repository > Get Tweets data by requesting to API (by using Client && Service classes)
 * - Retrieves Tweets data as LiveData that will be observed to reflect changes in Views
 * - Data is given by Client && Service classes (Retrofit) that request data by API methods
 * - This class is used with ModelView (TweetViewModel) classes in order to reflect data changes in real time
 */
public class ProfileRepository {
    // [Vars]
    AuthTwitterClient authTwitterClient;
    AuthTwitterService authTwitterService;
    MutableLiveData<UserProfile> userProfile;
    MutableLiveData<String> photoProfile;

    // (Constructor)

    public ProfileRepository() {
        retrofitInit();         // Initializes Twitter Client && Service
        getUserProfile();       // Gets user profile data
        getUserProfilePhoto();  // Gets user profile photo
    }

    // [Methods]: custom methods

    // Gets user profile data from API
    public MutableLiveData<UserProfile> getUserProfile() {
        if (userProfile == null) {  // Initializes UserProfile object to prevent null exceptions
            userProfile = new MutableLiveData<>();
        }
        // Performs a request for get user profile data
        Call<UserProfile> caller = authTwitterService.getUserProfile();
        caller.enqueue(new Callback<UserProfile>() {
            @Override
            public void onResponse(Call<UserProfile> call, Response<UserProfile> response) {
                // On success
                if (response.isSuccessful()) {
                    // Gets user profile data from response
                    userProfile.setValue(response.body());
                } else {
                    // Request has failed!!
                    String strRequestFailed = GlobalApp
                            .getContext()
                            .getResources()
                            .getString(R.string.request_generic_failure);
                    Toast.makeText(GlobalApp.getContext(),strRequestFailed,Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<UserProfile> call, Throwable t) {
                // Request has failed!!
                String strRequestFailed = GlobalApp
                        .getContext()
                        .getResources()
                        .getString(R.string.request_generic_failure);
                Toast.makeText(GlobalApp.getContext(),strRequestFailed,Toast.LENGTH_LONG).show();
            }
        });
        return userProfile;
    }

    // Updates user profile data
    public MutableLiveData<UserProfile> updateUserProfile(RequestUserProfile requestUserProfile) {
        Call<UserProfile> caller = authTwitterService.updateUserProfile(requestUserProfile);
        caller.enqueue(new Callback<UserProfile>() {
            @Override
            public void onResponse(Call<UserProfile> call, Response<UserProfile> response) {
                // On success
                if (response.isSuccessful()) {
                    // Updates user profile data with response data
                    userProfile.setValue(response.body());
                } else {
                    // Request has failed!!
                    String strRequestFailed = GlobalApp
                            .getContext()
                            .getResources()
                            .getString(R.string.request_generic_failure);
                    Toast.makeText(GlobalApp.getContext(),strRequestFailed,Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<UserProfile> call, Throwable t) {
                // Request has failed!!
                        String strRequestFailed = GlobalApp
                        .getContext()
                        .getResources()
                        .getString(R.string.connection_failure);
                Toast.makeText(GlobalApp.getContext(),strRequestFailed,Toast.LENGTH_LONG).show();
            }
        });
        // Returns user profile
        return userProfile;
    }

    // Gets user profile photo
    public MutableLiveData<String> getUserProfilePhoto() {
        if (photoProfile == null) {
            photoProfile = new MutableLiveData<>();
        }
        return photoProfile;
    }

    // Uploads user profile photo
    public void uploadPhoto(String photoPath) {
        // Verifies that an instance has been created
        if (photoProfile == null) {
            photoProfile = new MutableLiveData<>();
        }
        // Makes a file instance of the photo file
        File photoFile = new File(photoPath);
        // Creates an upload body request for photo image file
        RequestBody requestBody = RequestBody.create(MediaType.parse("image/jpg"),photoFile);
        // Performs upload request (by using authorized service)
        Call<UploadPhoto> caller = authTwitterService.uploadProfilePhoto(requestBody);
        caller.enqueue(new Callback<UploadPhoto>() {
            @Override
            public void onResponse(Call<UploadPhoto> call, Response<UploadPhoto> response) {
                if (response.isSuccessful()) {
                    // Stores photo url in SharedPreferences from response Body
                    SharedPreferencesManager.saveStringValue(
                            CommonItems.DATA_LABEL_PHOTO_URL,
                            response.body().getFilename());
                    // Updates photo url value over observable data variable (in order to communicate changes to application)
                    photoProfile.setValue(response.body().getFilename());
                } else {
                    // Request has failed!!
                    String strRequestFailed = GlobalApp
                            .getContext()
                            .getResources()
                            .getString(R.string.request_generic_failure);
                    Toast.makeText(GlobalApp.getContext(),strRequestFailed,Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<UploadPhoto> call, Throwable t) {
                // Request has failed!!
                String strRequestFailed = GlobalApp
                        .getContext()
                        .getResources()
                        .getString(R.string.connection_failure);
                Toast.makeText(GlobalApp.getContext(),strRequestFailed,Toast.LENGTH_LONG).show();
            }
        });
    }

    // Initializes Twitter Client && Service
    private void retrofitInit() {
        authTwitterClient = authTwitterClient.getInstance();
        authTwitterService = authTwitterClient.getAuthTwitterService();
    }
}
