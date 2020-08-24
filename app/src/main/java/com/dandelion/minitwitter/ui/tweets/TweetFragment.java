package com.dandelion.minitwitter.ui.tweets;
// [Imports]
import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dandelion.minitwitter.R;
import com.dandelion.minitwitter.common.CommonItems;
import com.dandelion.minitwitter.data.TweetViewModel;
import com.dandelion.minitwitter.retrofit.AuthTwitterClient;
import com.dandelion.minitwitter.retrofit.AuthTwitterService;
import com.dandelion.minitwitter.retrofit.response.Tweet;
import java.util.List;

// [Tweet Fragment]
public class TweetFragment extends Fragment {
    //[Vars]
    private int tweetListType = 1;
    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView recyclerView;
    MyTweetRecyclerViewAdapter tweetAdapter;
    List<Tweet> tweetList;
    TweetViewModel tweetViewModel;
    AuthTwitterService authTwitterService;
    AuthTwitterClient authTwitterClient;

    // (Constructor): default constructor
    public TweetFragment() {

    }

    // (Constructor): customized by list type
    public static TweetFragment newInstance(int tweetListType) {
        TweetFragment fragment = new TweetFragment();
        Bundle args = new Bundle();
        args.putInt(CommonItems.TWEET_LIST_TYPE, tweetListType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Creates a new instance of ViewModel for LiveData representation
        ViewModelProvider viewModelProvider = new ViewModelProvider(getActivity());
        tweetViewModel = viewModelProvider.get(TweetViewModel.class);
        // Arguments management
        if (getArguments() != null) {
            tweetListType = getArguments().getInt(CommonItems.TWEET_LIST_TYPE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tweet_list, container, false);

        // Sets SwipeRefreshLayout container instance and defines onRefreshLayout event
        swipeRefreshLayout = view.findViewById(R.id.SwipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorMiniTwitterBlue));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true); // Sets status to refreshing
                // Gets Tweets data depending on type selected (All or Favorites)
                if (tweetListType == CommonItems.TWEET_LIST_ALL) {
                    loadLiveTweetData();    // All > Performs data reload in order to refresh list
                } else if (tweetListType == CommonItems.TWEET_LIST_FAV) {
                    loadLiveFavTweetData();    // Favorite > Performs data reload in order to refresh list
                }

            }
        });

        // Sets list controls and behaviour based on items count
        Context context = view.getContext();

        // RecyclerView setup (LayoutManager && Adapter)
        recyclerView = view.findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        tweetAdapter = new MyTweetRecyclerViewAdapter(getActivity(), tweetList);    // Sets source list for Adapter
        recyclerView.setAdapter(tweetAdapter);                                      // Sets Adapter used in view to represent data

        // Gets Tweets data depending on type selected (All or Favorites)
        if (tweetListType == CommonItems.TWEET_LIST_ALL) {
            loadTweetData();    // All > Gets tweets data and links adapter with recycler view
        } else if (tweetListType == CommonItems.TWEET_LIST_FAV) {
            loadFavTweetData();    // Favorites > Gets tweets data and links adapter with recycler view
        }


        return view;
    }

    // [Methods]: custom methods

    // Gets -All- Tweets data from repository (without refresh from server)
    private void loadTweetData() {
        // Retrieves Tweets data from repository by using view model
        // ** Sets a data observer in order to get reflected changes
        tweetViewModel.getTweets().observe(getActivity(), tweets -> {
            tweetList = tweets;                         // Updates Tweets data from repository
            tweetAdapter.updateTweetData(tweetList);    // Updates Tweets data in Adapter
        });
    }

    // Gets -All- Tweets data from repository by requesting updated data to server
    private void loadLiveTweetData() {
        // Retrieves Tweets data from repository by using view model
        // ** Sets a data observer in order to get reflected changes
        // ** Data is get from server as updated data by using repository
        tweetViewModel.getLiveTweets().observe(getActivity(), new Observer<List<Tweet>>() {
            @Override
            public void onChanged(List<Tweet> tweets) {
                tweetList = tweets;                         // Updates Tweets data from repository
                tweetAdapter.updateTweetData(tweetList);    // Updates Tweets data in Adapter
                swipeRefreshLayout.setRefreshing(false);    // Sets refreshing status to false
                tweetViewModel.getLiveTweets().removeObserver(this);    // Removes Observer after list is updated
            }
        });
    }

    // Gets -Favorite- Tweets data from repository (without refresh from server)
    private void loadFavTweetData() {
        tweetViewModel.getFavTweets().observe(getActivity(), tweets -> {
            tweetList = tweets;                         // Updates Tweets data from repository
            tweetAdapter.updateTweetData(tweetList);    // Updates Tweets data in Adapter
        });
    }

    // Gets -Favorite- Tweets data from repository by requesting updated data to server
    private void loadLiveFavTweetData() {
        tweetViewModel.getLiveFavTweets().observe(getActivity(), new Observer<List<Tweet>>() {
            @Override
            public void onChanged(List<Tweet> tweets) {
                tweetList = tweets;                         // Updates Tweets data from repository
                tweetAdapter.updateTweetData(tweetList);    // Updates Tweets data in Adapter
                swipeRefreshLayout.setRefreshing(false);    // Sets refreshing status to false
                tweetViewModel.getLiveTweets().removeObserver(this);    // Removes Observer after list is updated
            }
        });
    }
}