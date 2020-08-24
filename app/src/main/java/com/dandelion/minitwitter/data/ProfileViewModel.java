package com.dandelion.minitwitter.data;
// [Imports]
import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.dandelion.minitwitter.retrofit.request.RequestUserProfile;
import com.dandelion.minitwitter.retrofit.response.UserProfile;

// [User Profile View Model]: user profile view model that
public class ProfileViewModel extends AndroidViewModel {
    // [Vars]
    private ProfileRepository profileRepository;
    private LiveData<UserProfile> userProfile;
    private LiveData<String> photoProfile;

    // (Constructor)
    public ProfileViewModel(@NonNull Application application) {
        super(application);
        profileRepository = new ProfileRepository();            // Makes an instance of repository (data provider)
        userProfile = profileRepository.getUserProfile();       // Gets user profile data
        photoProfile = profileRepository.getUserProfilePhoto(); // Gets user profile photo
    }

    // [Methods]

    // Gets user profile data
    public LiveData<UserProfile> getUserProfile() {
        return userProfile;
    }

    // Gets user profile photo
    public LiveData<String> getUserProfilePhoto() {
        return photoProfile;
    }

    // Updates user profile
    public void updateUserProfile(RequestUserProfile requestUserProfile) {
        profileRepository.updateUserProfile(requestUserProfile);
    }

    // Updates user profile photo
    public void updateUserProfilePhoto(String photo) {
        profileRepository.uploadPhoto(photo);
    }
}