package com.dandelion.minitwitter.ui;
// [Imports]
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.dandelion.minitwitter.R;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.dandelion.minitwitter.common.CommonItems;
import com.dandelion.minitwitter.common.SharedPreferencesManager;
import com.dandelion.minitwitter.ui.profile.ProfileFragment;
import com.dandelion.minitwitter.ui.tweets.NewTweetDialogFragment;
import com.dandelion.minitwitter.ui.tweets.TweetFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

// [Dashboard]
public class DashboardActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    // [Vars]
    private Fragment tweetFragment;
    private Fragment favoriteFragment;
    private Fragment profileFragment;
    private FloatingActionButton newTweetButton;
    private ImageView imageViewAvatar;

    // [Methods]: overridable
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        // Hides upper default ToolBar
        getSupportActionBar().hide();

        // Gets image avatar from user profile (if exists)
        String profilePhotoUrl = SharedPreferencesManager.getStringValue(CommonItems.DATA_LABEL_PHOTO_URL);
        imageViewAvatar = findViewById(R.id.imageViewToolBarPhoto);
        if (!profilePhotoUrl.isEmpty()) {
            // On having profile image
            Glide.with(this)
                    .load(CommonItems.URL_MINITWITTER_PHOTOS + profilePhotoUrl)
                    .dontAnimate()                              // animations are not recommended when using CircleImageView
                    .diskCacheStrategy(DiskCacheStrategy.NONE)  // Avoids use disk cache data (force to refresh)
                    .skipMemoryCache(true)                      // Skips memory cache usage
                    .centerCrop()                               // Makes photo get in center
                    .into(imageViewAvatar);
        }

        // Makes an instance for new tweet button (FloatingActionButton)
        newTweetButton = (FloatingActionButton) findViewById(R.id.fab);

        // Makes Fragment Instances
        tweetFragment = TweetFragment.newInstance(CommonItems.TWEET_LIST_ALL);

        // Makes onClickListener for new tweet button
        newTweetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewTweetDialogFragment newTweetDialogFragment = new NewTweetDialogFragment();
                newTweetDialogFragment.show(getSupportFragmentManager(),newTweetDialogFragment.DIALOG_FRAGMENT_TAG);
            }
        });

        // Navigation Management
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.nav_view);
        navigation.setOnNavigationItemSelectedListener(this);

        // Fragments Management (sets all tweets fragment type by default)
        replaceTweetFragment(CommonItems.TWEET_LIST_ALL);

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navigation_home:
                // (Tweets All)
                newTweetButton.show();
                replaceTweetFragment(CommonItems.TWEET_LIST_ALL);
                return true;
            case R.id.navigation_tweets:
                // (Tweets Favorites)
                newTweetButton.hide();
                replaceTweetFragment(CommonItems.TWEET_LIST_FAV);
                return true;
            case R.id.navigation_profile:
                // (User Profile)
                newTweetButton.hide();
                displayProfileFragment();
                break;
        }
        return false;
    }


    // [Methods]: custom

    // Replace Tweet Fragment over container with specified fragment type
    private void replaceTweetFragment(int tweetFragmentType) {
        tweetFragment = TweetFragment.newInstance(tweetFragmentType);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer,tweetFragment)
                .commit();
    }

    // Displays user profile fragment over fragment container
    private void displayProfileFragment() {
        profileFragment = new ProfileFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer,profileFragment)
                .commit();
    }
}