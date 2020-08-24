package com.dandelion.minitwitter.ui.profile;
// [Imports]
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.dandelion.minitwitter.R;
import com.dandelion.minitwitter.common.CommonItems;
import com.dandelion.minitwitter.data.ProfileViewModel;
import com.dandelion.minitwitter.retrofit.request.RequestUserProfile;
import com.dandelion.minitwitter.retrofit.response.UserProfile;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.CompositePermissionListener;
import com.karumi.dexter.listener.single.DialogOnDeniedPermissionListener;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.regex.Pattern;

// [Profile Fragment]: user data management fragment
public class ProfileFragment extends Fragment implements PermissionListener {
    // [Vars]
    private ProfileViewModel profileViewModel;
    private LiveData<UserProfile> userProfile;
    private LiveData<String> userPhoto;
    private ImageView imageViewAvatar;
    private EditText editTextUserName;
    private EditText editTextEmail;
    private EditText editTextWebsite;
    private EditText editTextDescription;
    private EditText editTextPassword;
    private Button buttonSave;
    private Button buttonModifyPassword;
    private Boolean hasUpdate;
    private PermissionListener permissionListener;

    // (Instance)
    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }
    // [Methods]: overridable
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewModelProvider viewModelProvider = new ViewModelProvider(getActivity());
        profileViewModel = viewModelProvider.get(ProfileViewModel.class);
        hasUpdate = false;  // Disables update flag (is used to display update message after update action)
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profile_fragment, container, false);
        // Gets an instance of fragment controls
        imageViewAvatar = view.findViewById(R.id.imageViewAvatar);
        editTextUserName = view.findViewById(R.id.editTextUserName);
        editTextEmail = view.findViewById(R.id.editTextEmail);
        editTextWebsite = view.findViewById(R.id.editTextWebsite);
        editTextDescription = view.findViewById(R.id.editTextDescription);
        editTextPassword = view.findViewById(R.id.editTextPassword);
        buttonSave = view.findViewById(R.id.buttonProfileSave);
        buttonModifyPassword = view.findViewById(R.id.buttonModifyPassword);
        // Sets up onClick events
        // (Profile Data):>
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Gets user profile data
                String userName = editTextUserName.getText().toString();
                String userEmail = editTextEmail.getText().toString();
                String userWebsite = editTextWebsite.getText().toString();
                String userDescription = editTextDescription.getText().toString();
                String userPassword = editTextPassword.getText().toString();
                // Profile data validation
                if (validateProfileData(userName,userEmail,userWebsite,userDescription,userPassword)) {
                    // Makes a new user profile request
                    RequestUserProfile requestUserProfile = new RequestUserProfile();
                    requestUserProfile.setUsername(userName);
                    requestUserProfile.setEmail(userEmail);
                    requestUserProfile.setWebsite(userWebsite);
                    requestUserProfile.setDescripcion(userDescription);
                    requestUserProfile.setPassword(userPassword);
                    // Updates user profile data
                    profileViewModel.updateUserProfile(requestUserProfile);
                    String actionMessage = getResources().getString(R.string.profile_action_message_updating);
                    Toast.makeText(getContext(),actionMessage,Toast.LENGTH_LONG).show();    // Displays update message
                    buttonSave.setEnabled(false);                                           // Disables update button
                    hasUpdate = true;                                                       // Sets update flag to on
                                                                                            // ** will show update message over onChange event
                }
            }
        });
        // (Password Update):>
        buttonModifyPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(),"Modify Password",Toast.LENGTH_LONG).show();
            }
        });
        // (Password Update):>
        imageViewAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Verifies permissions (in order to grant access to storage resources)
                checkPermissions();
                // Invokes photo selection dialog (from device storage)

            }
        });
        // User Profile Data (gets profile instance && defines observer)
        userProfile = profileViewModel.getUserProfile();
        userProfile.observe(getActivity(), new Observer<UserProfile>() {
            @Override
            public void onChanged(UserProfile userProfile) {
                // Sets loaded data over form controls
                editTextUserName.setText(userProfile.getUsername());
                editTextEmail.setText(userProfile.getEmail());
                editTextWebsite.setText(userProfile.getWebsite());
                editTextDescription.setText(userProfile.getDescripcion());
                // On having user profile photo
                String photoFile = userProfile.getPhotoUrl();
                if (!photoFile.isEmpty()) {
                    // Refresh profile photo
                    refreshPhoto(photoFile);
                }
                // Verifies if data has been updated
                if (hasUpdate) {
                    String actionMessage = getResources().getString(R.string.profile_action_message_updated);
                    Toast.makeText(getContext(),actionMessage,Toast.LENGTH_LONG).show();
                    hasUpdate = false;  // Disables update data flag
                }
            }
        });
        // User Profile Photo (gets profile photo instance && defines observer)
        userPhoto = profileViewModel.getUserProfilePhoto();
        userPhoto.observe(getActivity(), new Observer<String>() {
            @Override
            public void onChanged(String photo) {
                // On having photo changed
                if (!photo.isEmpty()) {
                    // Refresh profile photo
                    refreshPhoto(photo);
                }
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // On having result
        if (requestCode != getActivity().RESULT_CANCELED) {
            // On having result for photo selection
            if (requestCode == CommonItems.REQUEST_CODE_FOR_PHOTO_SELECTION) {
                // On having data as response
                if (data != null) {
                    Uri uriPhoto = data.getData();                    // Gets photo data as uri
                    String photoPath = getPhotoPathFromUri(uriPhoto); // Gets source photo path from uri data
                    // Performs user profile photo update
                    profileViewModel.updateUserProfilePhoto(photoPath);
                }
            }
        }
    }

    // (Permission): methods applied to permissions

    @Override
    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
        Toast.makeText(getActivity(),"Permission Granted",Toast.LENGTH_LONG).show();
        // Invokes implicit intent in order to access to device resource
        Intent chooseProfilePhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(chooseProfilePhoto,CommonItems.REQUEST_CODE_FOR_PHOTO_SELECTION);
    }

    @Override
    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
        // On permission denied shows informative message
        String accessDeniedMessage = getResources().getString(R.string.photo_selection_access_denied);
        Toast.makeText(getActivity(),accessDeniedMessage,Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {

    }

    // [Methods]: custom

    // Refresh photo by replacing with source parameter photo
    private void refreshPhoto(String photo) {
        Glide.with(getActivity())
                .load(CommonItems.URL_MINITWITTER_PHOTOS + photo)
                .dontAnimate()                              // animations are not recommended when using CircleImageView
                .diskCacheStrategy(DiskCacheStrategy.NONE)  // Avoids use disk cache data (force to refresh)
                .skipMemoryCache(true)                      // Skips memory cache usage
                .centerCrop()                               // Makes photo get in center
                .into(imageViewAvatar);
    }

    // Validates user profile data
    private Boolean validateProfileData(String paramUserName,
                                        String paramUserEmail,
                                        String paramUserWebsite,
                                        String paramUserDescription,
                                        String paramUserPassword) {
        Boolean validationResult = true;
        // Validates user profile data (empty data)
        if (paramUserName.isEmpty()) {
            validationResult = false;
            editTextUserName.setError(getResources().getString(R.string.profile_validation_user_name_empty));
        } else if (paramUserEmail.isEmpty()) {
            validationResult = false;
            editTextEmail.setError(getResources().getString(R.string.profile_validation_user_email_empty));
        } else if (paramUserPassword.isEmpty()) {
            validationResult = false;
            editTextPassword.setError(getResources().getString(R.string.profile_validation_user_password_empty));
        }
        // Validates user profile data (data format)
        if (validationResult) {
            if (!validUserName(paramUserName)) {
                validationResult = false;
                editTextUserName.setError(getResources().getString(R.string.profile_validation_user_name_wrong_format));
            } else if (!validUserEmail(paramUserEmail)) {
                validationResult = false;
                editTextEmail.setError(getResources().getString(R.string.profile_validation_user_email_wrong_format));
            } else if (!validUserPassword(paramUserPassword)) {
                validationResult = false;
                editTextPassword.setError(getResources().getString(R.string.profile_validation_user_password_wrong_format));
            }
        }

        // Returns validation result
        return  validationResult;
    }

    // Validates user name format
    private Boolean validUserName(String paramUserName) {
        Boolean validationResult = Pattern.matches(CommonItems.PATTERN_VALIDATION_FOR_USER_NAME,paramUserName);
        return validationResult;
    }

    // Validates user email format
    private Boolean validUserEmail(String paramUserEmail) {
        Boolean validationResult = Pattern.matches(CommonItems.PATTERN_VALIDATION_FOR_USER_EMAIL,paramUserEmail);
        return validationResult;
    }

    // Validates user password format
    private Boolean validUserPassword(String paramUserPassword) {
        Boolean validationResult = Pattern.matches(CommonItems.PATTERN_VALIDATION_FOR_USER_PASSWORD,paramUserPassword);
        return validationResult;
    }

    // Verifies permissions in order to grant access to device resources
    private void checkPermissions() {
        // Permission dialog parameters
        String dialogTitle = getResources().getString(R.string.dialog_permission_check_title);
        String dialogMessage = getResources().getString(R.string.dialog_permission_check_message);
        String dialogButtonAccept = getResources().getString(R.string.dialog_permission_check_button_accept);
        String dialogButtonCancel = getResources().getString(R.string.dialog_permission_check_button_cancel);
        // Makes permission dialog instance
        PermissionListener dialogOnPermissionDeniedListener = DialogOnDeniedPermissionListener
                .Builder
                .withContext(getActivity())
                .withTitle(dialogTitle)
                .withMessage(dialogMessage)
                .withButtonText(dialogButtonAccept)
                .withIcon(R.mipmap.ic_launcher)
                .build();
        // Makes an instance of PermissionListener in order respond to permission management actions
        permissionListener = new CompositePermissionListener((PermissionListener) this,dialogOnPermissionDeniedListener);

        // Sets permission check having previous elements (listener && dialog) tied to verification process
        // - Dexter library manages permission verification
        // - Dialog is launched when permissions are denied or have not required permissions
        // - Permission listener takes care of trigger actions depending on user interaction
        Dexter.withContext(getActivity())
        .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
        .withListener(permissionListener)
        .check();
    }

    // Gets data structure from uri response
    private String getPhotoPathFromUri(Uri sourceDataUri) {
        String photoPath = null;
        String[] mediaDataSchema = {MediaStore.Images.Media.DATA};
        Cursor cursor = getActivity().getContentResolver()
                .query(sourceDataUri,mediaDataSchema,null,null,null);
        if (cursor != null) {
            cursor.moveToFirst();
            int pathColIndex = cursor.getColumnIndex(mediaDataSchema[0]);
            photoPath = cursor.getString(pathColIndex);
            cursor.close();
        }
        return photoPath;
    }
}