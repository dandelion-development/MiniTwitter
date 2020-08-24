package com.dandelion.minitwitter.ui.tweets;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.dandelion.minitwitter.R;
import com.dandelion.minitwitter.common.CommonItems;
import com.dandelion.minitwitter.data.TweetViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.navigation.NavigationView;

public class BottomModalTweetFragment extends BottomSheetDialogFragment {
    // [Vars]
    private TweetViewModel tweetViewModel;
    private int tweetDeletionId;
    // (Instance)
    public static BottomModalTweetFragment newInstance(int tweetId) {
        BottomModalTweetFragment bottomModalTweetFragment = new BottomModalTweetFragment();
        Bundle args = new Bundle();                     // Makes a bundle for pass arguments
        args.putInt(CommonItems.ARG_TWEET_ID,tweetId);
        bottomModalTweetFragment.setArguments(args);
        return bottomModalTweetFragment;
    }
    // [Methods]: overridable
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // On having arguments
        if (getArguments() != null) {
            tweetDeletionId = getArguments().getInt(CommonItems.ARG_TWEET_ID);
        }
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_modal_tweet_fragment,
                                                         container, false);
        // Makes an instance of navigation menu control && sets up onClickItem
        final NavigationView navigationView = view.findViewById(R.id.navViewBottomTweet);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                // Performs action depending on menu item selected
                if (id == R.id.action_delete_tweet) {
                    tweetViewModel.deleteTweet(tweetDeletionId);
                    getDialog().dismiss();
                    return true;    // Menu item has been clicked
                }
                return false;       // Menu item not clicked
            }
        });
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ViewModelProvider viewModelProvider = new ViewModelProvider(getActivity());
        //mViewModel = ViewModelProviders.of(this).get(TweetViewModel.class);
        tweetViewModel = viewModelProvider.get(TweetViewModel.class);
    }

}